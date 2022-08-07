package speedcubing.replay;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import speedcubing.system.speedcubingSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ReplayCompressor {

    public static String a;

    public static void write(Replay replay, UUID owner) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < replay.LocationTicks.size(); i++) {
            s.append(",");
            int a = replay.LocationTicks.indexOf(i);
            if (a != -1) {
                Location location = replay.LocationActions.get(a);
                s.append("0").
                        append(doubleToString(location.getX())).
                        append(doubleToString(location.getY())).
                        append(doubleToString(location.getZ())).
                        append(doubleToString(location.getYaw())).
                        append(doubleToString(location.getPitch()));
                System.out.println(doubleToString(location.getX()));
            }
            a = replay.BlockTicks.indexOf(i);
            if (a != -1) {
                Block block = replay.BlockActions.get(a);
                s.append("1").append(intToString(block.getX(), 2)).
                        append(intToString(block.getY(), 2)).
                        append(intToString(block.getZ(), 2));
            }
            a = replay.BreakTicks.indexOf(i);
            if (a != -1) {
                Block block = replay.BreakActions.get(a);
                s.append("2").append(intToString(block.getX(), 2)).
                        append(intToString(block.getY(), 2)).
                        append(intToString(block.getZ(), 2)).
                        append(intToString(replay.BreakTypes.get(a), 2)).
                        append(replay.BreakDatas.get(a));
            }
            a = replay.SneakTicks.indexOf(i);
            if (a != -1) {
                boolean b = replay.SneakActions.get(a);
                s.append("3").append(b ? 0 : 1);
            }
            a = replay.HandItemTicks.indexOf(i);
            if (a != -1) {
                s.append("4").append(intToString(replay.HandItemActions.get(a), 2)).
                        append(replay.HandItemEnchants.get(a) ? 1 : 0).
                        append(replay.HandItemDatas.get(a));
            }
        }
        s = new StringBuilder(s.substring(2));
        s.append(replay.StartSneaking ? 1 : 0)
                .append(doubleToString(replay.StartLocation.getX()))
                .append(doubleToString(replay.StartLocation.getY()))
                .append(doubleToString(replay.StartLocation.getZ()))
                .append(intToString(replay.StartItem.getTypeId(), 2))
                .append(replay.StartItem.getData().getData());
        a = s.toString();
        File f = new File("/home/andy/test2.ext");
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(s.toString().getBytes(StandardCharsets.US_ASCII));
        } catch (Exception e) {
            e.printStackTrace();
        }
        speedcubingSystem.connection.insert("fbreplay", "id,owner,datas,value,signature,time", "'" + UUID.randomUUID() + "','" + owner + "','" + s + "','" + replay.value + "','" + replay.signature + "'," + (System.currentTimeMillis() / 1000L));
    }

    public static void read(String s) {
        String[] splitted = s.split(",");
        World world = Bukkit.getWorld("world");
        Replay replay = new Replay();
        for (int i = 0; i < splitted.length - 1; i++) {
            String a = splitted[i];
            if (a.charAt(0) == '0') {
                replay.LocationTicks.add(i);
                String x = a.substring(1, 9);
                String y = a.substring(10, 18);
                String z = a.substring(11, 19);
                String yaw = a.substring(20, 28);
                String pitch = a.substring(21, 25);
                System.out.println(x + " " + y + " " + z);
                replay.LocationActions.add(new Location(world, StringToDouble(x), StringToDouble(y), StringToDouble(z), (float) StringToDouble(yaw), (float) StringToDouble(pitch)));
            }
        }
        System.out.println(replay.LocationActions);
    }

    static int StringToInt(String s) {
        return Integer.parseInt(s, 16);
    }

    static String intToString(int i, int size) {
        StringBuilder s = new StringBuilder(Integer.toHexString(i));
        size *= 2;
        while (s.length() < size) {
            s.insert(0, "0");
        }
        return s.toString();
    }

    static String intToString2(int i, int size) {
        byte[] bytes;
        if (size == 4)
            bytes = new byte[]{(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i};
        else bytes = new byte[]{(byte) (i >> 8), (byte) i};
        StringBuilder s = new StringBuilder();
        for (byte bb : bytes) {
            s.append((char) bb);
        }
        return s.toString();
    }

    static int StringToInt2(String s) {
        byte[] bb = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            bb[i] = (byte) s.charAt(i);
        }
        return new BigInteger(bb).intValue();
    }

    static String doubleToString(double d) {
        return intToString((int) (d * 1024), 4);
    }

    static double StringToDouble(String s) {
        return StringToInt(s) / 1024D;
    }
}
