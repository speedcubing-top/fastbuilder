package top.speedcubing.fastbuilder.replay.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.common.database.Database;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.fastbuilder.replay.ReplayTick;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.lib.utils.SQL.SQLRow;
import top.speedcubing.lib.utils.SystemUtils;
import top.speedcubing.lib.utils.UUIDUtils;
import top.speedcubing.lib.utils.bytes.ByteArrayBuffer;
import top.speedcubing.lib.utils.bytes.DataConversion;
import top.speedcubing.lib.utils.bytes.IOUtils;

public class ReplayIOv2 extends ReplayIO {

    //upload replay object to db
    public void write(Replay replay, UUID uuid, boolean TAS) {
        ByteArrayBuffer buffer = new ByteArrayBuffer();
        try {
            buffer.writeBoolean(replay.StartSneaking);
            buffer.writeDouble(replay.StartLocation.getX());
            buffer.writeDouble(replay.StartLocation.getY());
            buffer.writeDouble(replay.StartLocation.getZ());
            buffer.writeFloat(replay.StartLocation.getYaw());
            buffer.writeFloat(replay.StartLocation.getPitch());
            buffer.writeVarInt(replay.StartItem.getTypeId());
            buffer.writeByte(replay.StartItem.getData().getData());


            List<ReplayTick> ticks = replay.ticks;
            for (int i = 0; i < ticks.size(); i++) {
                ReplayTick replayTick = replay.ticks.get(i);
                Location location = replayTick.location;

                buffer.write(0);
                buffer.writeDouble(location.getX());
                buffer.writeDouble(location.getY());
                buffer.writeDouble(location.getZ());
                buffer.writeFloat(location.getYaw());
                buffer.writeFloat(location.getPitch());

                if (replayTick.blockLocation != null) {
                    BlockPosition block = replayTick.blockLocation;
                    buffer.write(1);
                    buffer.writeVarInt(block.getX());
                    buffer.writeVarInt(block.getY());
                    buffer.writeVarInt(block.getZ());
                }
                if (replayTick.breakLocation != null) {
                    BlockPosition block = replayTick.breakLocation;
                    buffer.write(2);
                    buffer.writeVarInt(block.getX());
                    buffer.writeVarInt(block.getY());
                    buffer.writeVarInt(replayTick.breakMaterial);
                    buffer.writeByte(replayTick.breakDatas);
                }
                if (replayTick.sneak != null) {
                    buffer.write(3);
                    buffer.writeBoolean(replayTick.sneak);
                }
                if (replayTick.handMaterial != null) {
                    buffer.write(4);
                    buffer.writeVarInt(replayTick.handMaterial);
                    buffer.writeBoolean(replayTick.handEchants);
                    buffer.writeByte(replayTick.handDatas);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        byte[] compressed = ReplayCompressor.compress(buffer.toByteArray());
        String dataHex = DataConversion.byteArrayToHexString(compressed);

        long time = SystemUtils.getCurrentSecond();

        //hash
        String uuidHex = UUIDUtils.undash(uuid);
        try (SQLConnection connection = Database.getCubing()) {
            connection.insert("fbreplay", "id,uuid,time,replay_ver,datas,name,value,signature,score,mode,tas", "UNHEX(MD5(CONCAT(X'" + dataHex + "'," + time + "))),X'" + uuidHex + "'," + time + ",2,X'" + dataHex + "','" + replay.name + "','" + replay.value + "','" + replay.signature + "'," + (replay.replayRecorder.NewTick - 1) + "," + replay.replayRecorder.member.currentMode.sqlId + "," + (TAS ? 0 : 1));
        }
    }

    public Replay read(SQLRow r) {
        Blob datas;
        byte[] bytes;
        String id;
        String name;
        String value;
        String signature;
        int mode;
        try {
            id = DataConversion.byteArrayToHexString(r.getBytes("id"));
            datas = r.getBlob("datas");
            name = r.getString("name");
            value = r.getString("value");
            signature = r.getString("signature");
            mode = r.getInt("mode");

            bytes = datas.getBytes(1, (int) datas.length());
            bytes = ReplayCompressor.decompress(bytes);
            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(bytes));

            Replay replay = new Replay(id, mode);
            replay.name = name;
            replay.value = value;
            replay.signature = signature;

            replay.StartSneaking = inputStream.readBoolean();
            double x = inputStream.readDouble();
            double y = inputStream.readDouble();
            double z = inputStream.readDouble();
            float yaw = inputStream.readFloat();
            float pitch = inputStream.readFloat();
            replay.StartLocation = new Location(null, x, y, z, yaw, pitch);
            replay.StartItem = new ItemStack(IOUtils.readVarInt(inputStream), 1, inputStream.readByte());
            ReplayTick replayTick = null;
            byte b;
            while ((b = (byte) inputStream.read()) != -1) {
                if (b == 0) {
                    replayTick = new ReplayTick();
                    replayTick.location = new Location(null, inputStream.readDouble(), inputStream.readDouble(), inputStream.readDouble(), inputStream.readFloat(), inputStream.readFloat());
                    replay.ticks.add(replayTick);
                } else if (b == 1) {
                    replayTick.blockLocation = new BlockPosition(IOUtils.readVarInt(inputStream), IOUtils.readVarInt(inputStream), IOUtils.readVarInt(inputStream));
                } else if (b == 2) {
                    replayTick.breakLocation = new BlockPosition(IOUtils.readVarInt(inputStream), IOUtils.readVarInt(inputStream), IOUtils.readVarInt(inputStream));
                    replayTick.breakMaterial = IOUtils.readVarInt(inputStream);
                    replayTick.breakDatas = inputStream.readByte();
                } else if (b == 3) {
                    replayTick.sneak = inputStream.readBoolean();
                } else if (b == 4) {
                    replayTick.handMaterial = IOUtils.readVarInt(inputStream);
                    replayTick.handEchants = inputStream.readBoolean();
                    replayTick.handDatas = inputStream.readByte();
                }
            }
            replay.tickSize = replay.ticks.size();
            return replay;
        } catch (SQLException | IOException e) {
            return null;
        }
    }
}