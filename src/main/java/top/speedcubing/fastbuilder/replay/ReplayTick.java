package top.speedcubing.fastbuilder.replay;

import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Location;

public class ReplayTick {
    public Location location;

    public BlockPosition blockLocation;

    public BlockPosition breakLocation;
    public int breakMaterial;
    public byte breakDatas;

    public Boolean sneak;

    public Integer handMaterial;
    public byte handDatas;
    public boolean handEchants;

}
