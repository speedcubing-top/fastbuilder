package speedcubing.replay;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ReplayTick {


    public final Location location;

    public final Block blockLocation;

    public final Block breakLocation;
    public final int breakMaterial;
    public final byte breakDatas;

    public final boolean sneak;

    public final int handMaterial;
    public final byte handDatas;
    public final boolean handEchants;

    public ReplayTick(Location location, Block blockLocation, Block breakLocation, int breakMaterial, byte breakDatas, boolean sneak, int handMaterial, byte handDatas, boolean handEchants) {
        this.location = location;
        this.blockLocation = blockLocation;
        this.breakLocation = breakLocation;
        this.breakMaterial = breakMaterial;
        this.breakDatas = breakDatas;
        this.sneak = sneak;
        this.handMaterial = handMaterial;
        this.handDatas = handDatas;
        this.handEchants = handEchants;
    }
}
