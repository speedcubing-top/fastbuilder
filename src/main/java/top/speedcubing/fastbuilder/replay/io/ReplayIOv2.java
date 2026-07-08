package top.speedcubing.fastbuilder.replay.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
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

        // init item
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
                buffer.writeVarInt(block.getZ());
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

        byte[] compressed = ReplayCompressor.compress(buffer.toByteArray());

        long time = SystemUtils.getCurrentSecond();
        String replayId = replayId(compressed, time);
        try {
            writeReplayFile(replayId, 2, compressed);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write FastBuilder replay file", e);
        }

        //hash
        String uuidHex = UUIDUtils.undash(uuid);
        try (SQLConnection connection = Database.getCubing()) {
            connection.prepare("INSERT INTO fbreplay (id,uuid,time,replay_ver,datas,name,value,signature,score,mode,tas) VALUES (UNHEX(?),UNHEX(?),?,2,NULL,?,?,?,?,?,?)")
                    .setString(1, replayId)
                    .setString(2, uuidHex)
                    .setLong(3, time)
                    .setString(4, replay.name)
                    .setString(5, replay.value)
                    .setString(6, replay.signature)
                    .setInt(7, replay.replayRecorder.NewTick - 1)
                    .setInt(8, replay.replayRecorder.member.currentMode.sqlId)
                    .setInt(9, TAS ? 0 : 1)
                    .executeUpdate();
        }
    }

    public Replay read(SQLRow r) {
        String id;
        String name;
        String value;
        String signature;
        int mode;
        try {
            id = DataConversion.byteArrayToHexString(r.getBytes("id"));
            name = r.getString("name");
            value = r.getString("value");
            signature = r.getString("signature");
            mode = r.getInt("mode");

            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(ReplayCompressor.decompress(readReplayFile(2, r))));

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
        } catch (Exception e) {
            return null;
        }
    }
}