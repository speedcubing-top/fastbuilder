package top.speedcubing.fastbuilder.replay.gui;

public class FastBuilderUtils {
    public static String tickToTime(int tick) {
        return String.format("%.3f", tick * 0.05);
    }
}
