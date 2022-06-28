package speedcubing.replay;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import speedcubing.Main;
import speedcubing.lib.bukkit.entity.NPC;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Replay {
    public Location StartLocation;

    public List<Integer> LocationTicks = new ArrayList<>();
    public List<Location> LocationActions = new ArrayList<>();
    public int Locationindex = 0;
    public int nextLocation = 0;

    public List<Integer> BlockTicks = new ArrayList<>();
    public List<BlockPosition> BlockActions = new ArrayList<>();
    public int Blockindex = 0;
    public int nextBlock = 0;

    public int tick = 0;
    public NPC npc;
    public final Player bridger;
    public final World world;
    public final String value;
    public final String signature;

    public Replay(World world, Player bridger) {
        this.bridger = bridger;
        this.world = world;
        Property profile = ((CraftPlayer) bridger).getProfile().getProperties().get("textures").iterator().next();
        this.value = profile.getValue();
        this.signature = profile.getSignature();
    }

    public void reset() {
        StartLocation = null;
        LocationTicks.clear();
        LocationActions.clear();
        BlockTicks.clear();
        BlockActions.clear();
        Locationindex = 0;
        nextLocation = 0;
    }

    public void spawnReplay(Player viewer) {
        nextLocation = LocationTicks.get(0);
        npc = new NPC(bridger.getName(), UUID.randomUUID(), true)
                .setSkin(value, signature, false)
                .setLocation(StartLocation.getX(), StartLocation.getY(), StartLocation.getZ(), StartLocation.getYaw(), StartLocation.getPitch());
        npc.listener.add(((CraftPlayer) viewer).getHandle().playerConnection);
        npc.spawn();
    }


    public void start() {
        int locationlength = LocationTicks.size();
        int blocklength = BlockTicks.size();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            while (tick == nextLocation) {
                Location loc = LocationActions.get(Locationindex);
                npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                npc.updateNpcLocation();
                Locationindex++;
                nextLocation = Locationindex == locationlength ? 0 : LocationTicks.get(Locationindex);
            }
            while (tick == nextBlock) {
                BlockPosition p = BlockActions.get(Blockindex);
                world.getBlockAt(p.getX(), p.getY(), p.getZ()).setType(Material.SANDSTONE);
                npc.animation(0);
                Blockindex++;
                nextBlock = Blockindex == blocklength ? 0 : BlockTicks.get(Blockindex);
            }
            tick++;
        }, 0, 1);
    }
}
