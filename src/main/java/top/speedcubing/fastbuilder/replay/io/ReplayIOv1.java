package top.speedcubing.fastbuilder.replay.io;

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
import top.speedcubing.lib.utils.bytes.DataConversion;

import java.util.List;
import java.util.UUID;

public class ReplayIOv1 extends ReplayIO {

    //upload replay object to db
    public void write(Replay replay, UUID uuid, boolean TAS) {
        StringBuilder s = new StringBuilder();
        s.append(intToString(replay.StartSneaking ? 0 : 1, 2))
                .append(doubleToString(replay.StartLocation.getX()))
                .append(doubleToString(replay.StartLocation.getY()))
                .append(doubleToString(replay.StartLocation.getZ()))
                .append(doubleToString(replay.StartLocation.getYaw()))
                .append(doubleToString(replay.StartLocation.getPitch()))
                .append(intToString(replay.StartItem.getTypeId(), 4))
                .append(intToString(replay.StartItem.getData().getData(), 2));
        List<ReplayTick> ticks = replay.ticks;
        for (int i = 0; i < ticks.size(); i++) {
            ReplayTick replayTick = replay.ticks.get(i);
            Location location = replayTick.location;
            s.append(intToString(0, 2)).
                    append(doubleToString(location.getX())).
                    append(doubleToString(location.getY())).
                    append(doubleToString(location.getZ())).
                    append(doubleToString(location.getYaw())).
                    append(doubleToString(location.getPitch()));
            if (replayTick.blockLocation != null) {
                BlockPosition block = replayTick.blockLocation;
                s.append(intToString(1, 2))
                        .append(intToString(block.getX(), 4)).
                        append(intToString(block.getY(), 4)).
                        append(intToString(block.getZ(), 4));
            }
            if (replayTick.breakLocation != null) {
                BlockPosition block = replayTick.breakLocation;
                s.append(intToString(2, 2)).
                        append(intToString(block.getX(), 4)).
                        append(intToString(block.getY(), 4)).
                        append(intToString(block.getZ(), 4)).
                        append(intToString(replayTick.breakMaterial, 4)).
                        append(intToString(replayTick.breakDatas, 2));
            }
            if (replayTick.sneak != null) {
                s.append(intToString(3, 2)).append(intToString(replayTick.sneak ? 0 : 1, 2));
            }
            if (replayTick.handMaterial != null) {
                s.append(intToString(4, 2)).append(intToString(replayTick.handMaterial, 4)).
                        append(intToString(replayTick.handEchants ? 0 : 1, 2)).
                        append(intToString(replayTick.handDatas, 2));
            }
        }
        String hex = s.toString();

        //not sure if the hex is the proper size.
        if (hex.length() % 2 == 1) {
            hex = "0" + hex;
        }

        long time = SystemUtils.getCurrentSecond();
        byte[] data = DataConversion.hexStringToByteArray(hex);
        String replayId = replayId(data, time);
        String replayPath;
        try {
            replayPath = writeReplayFile(replayId, 1, data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write FastBuilder replay file", e);
        }

        //hash
        String uuidHex = UUIDUtils.undash(uuid);
        try (SQLConnection connection = Database.getCubing()) {
            connection.prepare("INSERT INTO fbreplay (id,uuid,time,replay_ver,datas,name,value,signature,score,mode,tas) VALUES (UNHEX(?),UNHEX(?),?,1,NULL,?,?,?,?,?,?,?)")
                    .setString(1, replayId)
                    .setString(2, uuidHex)
                    .setLong(3, time)
                    .setString(4, replayPath)
                    .setString(5, replay.name)
                    .setString(6, replay.value)
                    .setString(7, replay.signature)
                    .setInt(8, replay.replayRecorder.NewTick - 1)
                    .setInt(9, replay.replayRecorder.member.currentMode.sqlId)
                    .setInt(10, TAS ? 0 : 1)
                    .executeUpdate();
        }
    }

    public Replay read(SQLRow r) {

        ReplayReader reader;
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


            reader = new ReplayReader(DataConversion.byteArrayToHexString(readReplayFile(1, r)));
        } catch (Exception e) {
            return null;
        }

        Replay replay = new Replay(id, mode);
        replay.name = name;
        replay.value = value;
        replay.signature = signature;
        replay.StartSneaking = reader.readInt() == 0;
        replay.StartLocation = new Location(null,
                reader.readDouble(),
                reader.readDouble(),
                reader.readDouble(),
                reader.readFloat(),
                reader.readFloat());
        replay.StartItem = new ItemStack(
                reader.readShort(),
                1,
                (short) reader.readInt());
        ReplayTick replayTick = null;
        while (reader.hasRemaining()) {
            int type = reader.readInt();
            if (type == 0) {
                replayTick = new ReplayTick();
                replayTick.location = new Location(null,
                        reader.readDouble(),
                        reader.readDouble(),
                        reader.readDouble(),
                        (float) reader.readDouble(),
                        (float) reader.readDouble());
                replay.ticks.add(replayTick);
            } else if (type == 1) {
                replayTick.blockLocation = new BlockPosition(
                        reader.readShort(),
                        reader.readShort(),
                        reader.readShort());
            } else if (type == 2) {
                replayTick.breakLocation = new BlockPosition(
                        reader.readShort(),
                        reader.readShort(),
                        reader.readShort());
                replayTick.breakMaterial = reader.readShort();
                replayTick.breakDatas = reader.readByte();
            } else if (type == 3) {
                replayTick.sneak = reader.readByte() == 0;
            } else if (type == 4) {
                replayTick.handMaterial = reader.readShort();
                replayTick.handEchants = reader.readByte() == 0;
                replayTick.handDatas = reader.readByte();
            }
        }
        replay.tickSize = replay.ticks.size();
        return replay;
    }

    private String intToString(int i, int size) {
        StringBuilder s = new StringBuilder(Integer.toHexString(i));
        while (s.length() < size) {
            s.insert(0, i < 0 ? "f" : "0");
        }
        return s.substring(s.length() - size);
    }

    private String doubleToString(double d) {
        return intToString((int) (d * 1024), 8);
    }
}

class ReplayReader {
    private final String data;
    private int index;

    ReplayReader(String data) {
        System.out.println("data: " + data);
        this.data = data;
        this.index = 0;
    }

    boolean hasRemaining() {
        return index < data.length();
    }

    byte readByte() {
        return (byte) readInt();
    }

    int readShort() {
        String s = data.substring(index, index + 4);
        char c = ((int) s.charAt(0)) > 55 ? 'f' : '0';
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() < 8) {
            sBuilder.insert(0, c);
        }
        s = sBuilder.toString();
        index += 4;
        System.out.println("read short, index=" + index);
        return (int) Long.parseLong(s, 16);
    }

    int readInt() {
        String s = data.substring(index, index + 2);
        char c = ((int) s.charAt(0)) > 55 ? 'f' : '0';
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() < 8) {
            sBuilder.insert(0, c);
        }
        s = sBuilder.toString();
        index += 2;
        System.out.println("read int, index=" + index);
        return (int) Long.parseLong(s, 16);
    }

    double readDouble() {
        return readInt() / 1024D;
    }

    float readFloat() {
        return (float) readDouble();
    }
}