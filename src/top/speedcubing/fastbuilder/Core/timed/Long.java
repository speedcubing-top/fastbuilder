package top.speedcubing.fastbuilder.Core.timed;

public class Long extends TimedMode {
    public Long() {
        super("long", 2, 230, false, 84, 110, "replay_long");
    }

    public int roomToCoord(int room) {
        return 6 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    public int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 6) - (Math.abs(z / 12) + z / 12) / (z / 6);
    }
}
