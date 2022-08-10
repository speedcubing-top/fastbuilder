package speedcubing.Core.timed;


public class Inclined extends TimedMode {
    public Inclined() {
        super("Inclined", 3, 110, true, 29, 106, null);
    }

    public int roomToCoord(int room) {
        return 4 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    public int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 4) - (Math.abs(z / 8) + z / 8) / (z / 4);
    }
}
