package top.speedcubing.fastbuilder.Core.timed;

public class Long extends TimedMode {
    public static TimedMode instance;

    public Long() {
        super("map_fb_long", "map_fb_replay_long", "fb_long_yours", 2, 230, false, 84, 110);
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
