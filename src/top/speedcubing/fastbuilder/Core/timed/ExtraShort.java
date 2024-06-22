package top.speedcubing.fastbuilder.Core.timed;

public class ExtraShort  extends TimedMode {
    public static TimedMode instance;
    public ExtraShort() {
        super("map_fb_extrashort", "map_fb_replay_extrashort","fb_extrashort", 6, 74, false, 17, 101);
        instance = this;
    }

    @Override
    public int roomToCoord(int room) {
        return 4 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    @Override
    public int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 4) - (Math.abs(z / 8) + z / 8) / (z / 4);
    }
}

