package top.speedcubing.fastbuilder.core.timed;

public class OneStack extends TimedMode {
    public static TimedMode instance;
    public OneStack() {
        super("map_fb_onestack", "map_fb_replay_onestack","fb_onestack_yours",5, 220, false, 29, 128);
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
