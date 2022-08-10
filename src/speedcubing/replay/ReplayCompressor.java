package speedcubing.replay;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import speedcubing.system.speedcubingSystem;

import java.sql.Blob;
import java.sql.ResultSet;
import java.util.UUID;

public class ReplayCompressor {

    public static String a;

    public static void write(Replay replay, UUID owner) {
        StringBuilder s = new StringBuilder();
        s.append(intToString(replay.StartSneaking ? 0 : 1, 2))
                .append(doubleToString(replay.StartLocation.getX()))
                .append(doubleToString(replay.StartLocation.getY()))
                .append(doubleToString(replay.StartLocation.getZ()))
                .append(doubleToString(replay.StartLocation.getYaw()))
                .append(doubleToString(replay.StartLocation.getPitch()))
                .append(intToString(replay.StartItem.getTypeId(), 4))
                .append(intToString(replay.StartItem.getData().getData(), 2));
        for (int i = 0; i < replay.LocationTicks.size(); i++) {
            int a = replay.LocationTicks.indexOf(i);
            if (a != -1) {
                Location location = replay.LocationActions.get(a);
                s.append(intToString(0, 2)).
                        append(doubleToString(location.getX())).
                        append(doubleToString(location.getY())).
                        append(doubleToString(location.getZ())).
                        append(doubleToString(location.getYaw())).
                        append(doubleToString(location.getPitch()));
            }

            a = replay.BlockTicks.indexOf(i);
            if (a != -1) {
                Block block = replay.BlockActions.get(a);
                s.append(intToString(1, 2))
                        .append(intToString(block.getX(), 4)).
                        append(intToString(block.getY(), 4)).
                        append(intToString(block.getZ(), 4));
            }

            a = replay.BreakTicks.indexOf(i);
            if (a != -1) {
                Block block = replay.BreakActions.get(a);
                s.append(intToString(2, 2)).
                        append(intToString(block.getX(), 4)).
                        append(intToString(block.getY(), 4)).
                        append(intToString(block.getZ(), 4)).
                        append(intToString(replay.BreakTypes.get(a), 4)).
                        append(intToString(replay.BreakDatas.get(a), 2));
            }

            a = replay.SneakTicks.indexOf(i);
            if (a != -1) {
                s.append(intToString(3, 2)).append(intToString(replay.SneakActions.get(a) ? 0 : 1, 2));
            }

            a = replay.HandItemTicks.indexOf(i);
            if (a != -1) {
                s.append(intToString(4, 2)).append(intToString(replay.HandItemActions.get(a), 4)).
                        append(intToString(replay.HandItemEnchants.get(a) ? 0 : 1, 2)).
                        append(intToString(replay.HandItemDatas.get(a), 2));
            }
        }
        a = s.toString();
        speedcubingSystem.connection.insert("fbreplay", "id,owner,time,datas,name,value,signature,score,mode", "'" + UUID.randomUUID() + "','" + owner + "'," + (System.currentTimeMillis() / 1000L) + ",0x" + s + ",'" + replay.name + "','" + replay.value + "','" + replay.signature + "'," + (replay.replayRecorder.NewTick - 1) + "," + replay.replayRecorder.member.currentMode.sqlId);
    }

    public static Replay read(ResultSet resultSet, int XDiff) {
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
        replay.StartLocation = new Location(world, StringToDouble(s.substring(2, 10)) + XDiff, StringToDouble(s.substring(10, 18)), StringToDouble(s.substring(18, 26)), (float) StringToDouble(s.substring(26, 34)), (float) StringToDouble(s.substring(34, 42)));
        replay.StartItem = new ItemStack(stringToInt(s.substring(42, 46)), 1, (short) stringToInt(s.substring(46, 48)));
        s = new StringBuilder(s.substring(48));
        int i = -1;
        while (s.length() != 0) {
            if (s.toString().startsWith("00")) {
                i += 1;
                replay.LocationTicks.add(i);
                replay.LocationActions.add(new Location(world,
                        StringToDouble(s.substring(2, 10)) + XDiff,
                        StringToDouble(s.substring(10, 18)),
                        StringToDouble(s.substring(18, 26)),
                        (float) StringToDouble(s.substring(26, 34)),
                        (float) StringToDouble(s.substring(34, 42))));
                s = new StringBuilder(s.substring(42));
            } else if (s.toString().startsWith("01")) {
                replay.BlockTicks.add(i);
                replay.BlockActions.add(world.getBlockAt(stringToInt(s.substring(2, 6)) + XDiff, stringToInt(s.substring(6, 10)), stringToInt(s.substring(10, 14))));
                s = new StringBuilder(s.substring(14));
            } else if (s.toString().startsWith("02")) {
                replay.BreakTicks.add(i);
                replay.BreakActions.add(world.getBlockAt(stringToInt(s.substring(2, 6)) + XDiff, stringToInt(s.substring(6, 10)), stringToInt(s.substring(10, 14))));
                replay.BreakTypes.add(stringToInt(s.substring(14, 18)));
                replay.BreakDatas.add((byte) stringToInt(s.substring(18, 20)));
                s = new StringBuilder(s.substring(20));
            } else if (s.toString().startsWith("03")) {
                replay.SneakTicks.add(i);
                replay.SneakActions.add(stringToInt(s.substring(2, 4)) == 0);
                s = new StringBuilder(s.substring(4));
            } else if (s.toString().startsWith("04")) {
                replay.HandItemTicks.add(i);
                replay.HandItemActions.add(stringToInt(s.substring(2, 6)));
                replay.HandItemEnchants.add(stringToInt(s.substring(6, 8)) == 0);
                replay.HandItemDatas.add((byte) stringToInt(s.substring(8, 10)));
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
