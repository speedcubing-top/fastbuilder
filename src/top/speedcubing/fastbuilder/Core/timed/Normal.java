package top.speedcubing.fastbuilder.Core.timed;


public class Normal extends TimedMode {
    public static TimedMode instance;
    public Normal() {
        super("map_fb_normal", "map_fb_replay_normal", "fb_normal_yours",0, 143, false,52,106);
        instance = this;
    }

    public int roomToCoord(int room) {
        return 6 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    public int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 6) - (Math.abs(z / 12) + z / 12) / (z / 6);
    }
}
