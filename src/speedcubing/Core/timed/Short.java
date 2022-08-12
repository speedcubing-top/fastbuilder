package speedcubing.Core.timed;


import org.bukkit.Bukkit;

public class Short extends TimedMode {
    public Short() {
        super("Short", 1, 76, false, 29, 103, "replay_short");
    }


    public int roomToCoord(int room) {
        return 6 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    public int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 6) - (Math.abs(z / 12) + z / 12) / (z / 6);
    }
}
