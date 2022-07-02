package speedcubing.Core;

import speedcubing.Core.timed.TimedMode;
import speedcubing.Main;
import speedcubing.Member;
import speedcubing.server.speedcubingServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Core {
    public static String msToTime(int ms) {
        return String.format("%.3f", ms * 0.01);
    }

    public static String tickToTime(int tick) {
        return String.format("%.3f", tick * 0.05);
    }

    public static int cal(int oldroom, boolean left, boolean[] b) {
        int a = left ? 0 : 1;
        int l = b.length - (a * -1 + 2);
        while (oldroom != l) {
            oldroom += (oldroom == a ? 1 : (oldroom % 2) * 4 - 2) * (a * -2 + 1);
            if (b[oldroom])
                return oldroom;
        }
        return -1;
    }

    public static List<String> getSideBar(int i, List<Member> session, Map<Member, Integer> score) {
        List<String> result = new ArrayList<>();
        int size = session.size();
        for (int j = i; j < 3; j++) {
            if (size <= j)
                result.add("§7-,---");
            else {
                Member ses = session.get(j);
                int sc = score.get(ses);
                if (sc == 0)
                    result.add("§7-,---");
                else
                    result.add(speedcubingServer.getFormat(ses.rank)[1] + ses.player.getName() + "§8: §e" + tickToTime(sc));
            }
        }
        return result;
    }

    public static TimedMode getInstance(String world) {
        switch (world.toLowerCase()) {
            case "world":
                return Main.Long;
            case "short":
                return Main.Short;
            case "inclined":
                return Main.Inclined;
            case "onestack":
                return Main.OneStack;
            case "inclinedshort":
                return Main.InclinedShort;
        }
        return null;
    }
}
