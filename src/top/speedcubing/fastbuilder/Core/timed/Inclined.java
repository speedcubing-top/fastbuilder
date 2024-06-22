package top.speedcubing.fastbuilder.Core.timed;


public class Inclined extends TimedMode {
    public static TimedMode instance;
    public Inclined() {
        super("map_fb_inclined", "map_fb_replay_inclined","fb_inclined_yours", 3, 103, true, 29, 106);
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
