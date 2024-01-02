package top.speedcubing.fastbuilder.Core.timed;

public class InclinedShort extends TimedMode {
    public static TimedMode instance;
    public InclinedShort() {
        super("map_fb_inclinedshort", "map_fb_replay_inclinedshort","fb_inclinedshort_yours", 4, 40, true, 18, 103);
        instance = this;
    }

    public int roomToCoord(int room) {
        return 4 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    public int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 4) - (Math.abs(z / 8) + z / 8) / (z / 4);
    }
}
