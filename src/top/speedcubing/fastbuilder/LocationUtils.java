package top.speedcubing.fastbuilder;

import org.bukkit.Location;

public class LocationUtils {
    public static boolean sameCoord(Location loc1,Location loc2){
        return loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ() && loc1.getYaw() == loc2.getYaw() && loc1.getPitch() == loc2.getPitch();
    }
}
