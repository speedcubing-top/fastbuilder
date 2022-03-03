package speedcubing.Core;

import net.minecraft.server.v1_8_R3.BlockPosition;

import java.util.*;

public class Core {
    public static Set<UUID> waiting = new HashSet<>();
    public static Map<UUID, Integer[]> integerCache = new HashMap<>();

    public static Map<UUID, Integer> times = new HashMap<>();
    public static Map<UUID, Integer> MainTimer = new HashMap<>();
    public static Map<UUID, Integer> finishedtask = new HashMap<>();
    public static Map<UUID, Set<BlockPosition>> Blocks = new HashMap<>();
    public static final Map<UUID, Integer> PlayerCoord = new HashMap<>();

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
}
