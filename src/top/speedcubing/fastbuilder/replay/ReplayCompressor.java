package top.speedcubing.fastbuilder.replay;

import top.speedcubing.fastbuilder.Member;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.system.speedcubingSystem;

import java.sql.Blob;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public class ReplayCompressor {

    public static String a;

    public static void write(Replay replay, UUID owner, boolean TAS) {
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
                Block block = replayTick.blockLocation;
                s.append(intToString(1, 2))
                        .append(intToString(block.getX(), 4)).
                        append(intToString(block.getY(), 4)).
                        append(intToString(block.getZ(), 4));
            }
            if (replayTick.breakLocation != null) {
                Block block = replayTick.breakLocation;
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
        a = s.toString();
        speedcubingSystem.connection.insert("fbreplay", "id,owner,time,datas,name,value,signature,score,mode,tas", "'" + UUID.randomUUID() + "','" + owner + "'," + (System.currentTimeMillis() / 1000L) + ",0x" + s + ",'" + replay.name + "','" + replay.value + "','" + replay.signature + "'," + (replay.replayRecorder.NewTick - 1) + "," + replay.replayRecorder.member.currentMode.sqlId + ",tas=" + (TAS ? 0 : 1));
    }

    public static Replay read(Member member, int slot) {
        ResultSet resultSet = speedcubingSystem.connection.select("fbreplay", "datas,name,value,signature,score", "owner='" + member.user.player.getUniqueId() + "' AND mode=" + member.currentMode.sqlId + " ORDER BY fav DESC, time DESC LIMIT " + slot + ",1");
        StringBuilder s = new StringBuilder();
        String temp;
        String name;
        String value;
        String signature;
        try {
            resultSet.next();
            Blob result = resultSet.getBlob(1);
            name = resultSet.getString(2);
            value = resultSet.getString(3);
            signature = resultSet.getString(4);
            for (int i = 1; i <= result.length(); i++) {
                temp = String.format("%x", result.getBytes(i, 1)[0]);
                if (temp.length() == 1)
                    temp = "0" + temp;
                s.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        World world = Bukkit.getWorld("world");
        Replay replay = new Replay();
        replay.name = name;
        replay.value = value;
        replay.signature = signature;
        replay.StartSneaking = stringToInt(s.substring(0, 2)) == 0;
        replay.StartLocation = new Location(world, StringToDouble(s.substring(2, 10)), StringToDouble(s.substring(10, 18)), StringToDouble(s.substring(18, 26)), (float) StringToDouble(s.substring(26, 34)), (float) StringToDouble(s.substring(34, 42)));
        replay.StartItem = new ItemStack(stringToInt(s.substring(42, 46)), 1, (short) stringToInt(s.substring(46, 48)));
        s = new StringBuilder(s.substring(48));
        ReplayTick replayTick = null;
        while (s.length() != 0) {
            if (s.toString().startsWith("00")) {
                replayTick = new ReplayTick();
                replayTick.location = new Location(world,
                        StringToDouble(s.substring(2, 10)),
                        StringToDouble(s.substring(10, 18)),
                        StringToDouble(s.substring(18, 26)),
                        (float) StringToDouble(s.substring(26, 34)),
                        (float) StringToDouble(s.substring(34, 42)));
                replay.ticks.add(replayTick);
                s = new StringBuilder(s.substring(42));
            } else if (s.toString().startsWith("01")) {
                replayTick.blockLocation = world.getBlockAt(stringToInt(s.substring(2, 6)), stringToInt(s.substring(6, 10)), stringToInt(s.substring(10, 14)));
                s = new StringBuilder(s.substring(14));
            } else if (s.toString().startsWith("02")) {
                replayTick.breakLocation = world.getBlockAt(stringToInt(s.substring(2, 6)), stringToInt(s.substring(6, 10)), stringToInt(s.substring(10, 14)));
                replayTick.breakMaterial = stringToInt(s.substring(14, 18));
                replayTick.breakDatas = (byte) stringToInt(s.substring(18, 20));
                s = new StringBuilder(s.substring(20));
            } else if (s.toString().startsWith("03")) {
                replayTick.sneak = stringToInt(s.substring(2, 4)) == 0;
                s = new StringBuilder(s.substring(4));
            } else if (s.toString().startsWith("04")) {
                replayTick.handMaterial = stringToInt(s.substring(2, 6));
                replayTick.handEchants = stringToInt(s.substring(6, 8)) == 0;
                replayTick.handDatas = (byte) stringToInt(s.substring(8, 10));
                s = new StringBuilder(s.substring(10));
            }
        }
        return replay;
    }

    static int stringToInt(String s) {
        char c = ((int) s.charAt(0)) > 55 ? 'f' : '0';
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() < 8) {
            sBuilder.insert(0, c);
        }
        s = sBuilder.toString();
        return (int) Long.parseLong(s, 16);
    }

    static String intToString(int i, int size) {
        StringBuilder s = new StringBuilder(Integer.toHexString(i));
        while (s.length() < size) {
            s.insert(0, i < 0 ? "f" : "0");
        }
        return s.substring(s.length() - size);
    }

    static String doubleToString(double d) {
        return intToString((int) (d * 1024), 8);
    }

    static double StringToDouble(String s) {
        return stringToInt(s) / 1024D;
    }
}
