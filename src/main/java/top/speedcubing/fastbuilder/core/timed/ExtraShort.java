package top.speedcubing.fastbuilder.core.timed;

public class ExtraShort extends TimedMode {
    public static TimedMode instance;

    public ExtraShort() {
        super("map_fb_extrashort", "map_fb_replay_extrashort", "fb_extrashort", 6, 39, false, 17, 101);
        instance = this;
    }

    @Override
    public int roomToCoord(int room) {
        return 6 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    @Override
    public int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 6) - (Math.abs(z / 12) + z / 12) / (z / 6);
    }
}

