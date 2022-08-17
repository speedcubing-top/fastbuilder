package speedcubing.Core.timed;

public class InclinedShort extends TimedMode {
    public InclinedShort() {
        super("InclinedShort", 4, 40, true, 18, 103, "replay_inclinedshort");
    }

    public int roomToCoord(int room) {
        return 4 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    public int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 4) - (Math.abs(z / 8) + z / 8) / (z / 4);
    }
}
