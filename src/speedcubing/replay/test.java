package speedcubing.replay;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import cubing.lib.bukkit.EntityPlayerUtils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import speedcubing.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class test implements Listener {
    public static String[] StartSkin;
    public static Location StartLocation;

    public static List<Integer> LocationTicks = new ArrayList<>();
    public static List<Location> LocationActions = new ArrayList<>();
    public static int Locationindex = 0;
    public static int nextLocation = 0;

    public static List<Integer> BlockTicks = new ArrayList<>();
    public static List<BlockPosition> BlockActions = new ArrayList<>();
    public static int Blockindex = 0;
    public static int nextBlock = 0;

    public static int tick = 0;
    public static EntityPlayer entityPlayer;
    public static PacketPlayOutAnimation swing ;

    public void reset() {
        StartSkin = null;
        StartLocation = null;
        LocationTicks.clear();
        LocationActions.clear();
        BlockTicks.clear();
        BlockActions.clear();
        Locationindex = 0;
        nextLocation = 0;
        entityPlayer = null;
    }

    WorldServer world = ((CraftWorld) Bukkit.getWorld("world")).getHandle();

    public void init(Player player) {
        nextLocation = LocationTicks.get(0);
        entityPlayer = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), world, new GameProfile(UUID.randomUUID(), player.getName()), new PlayerInteractManager(world));
        entityPlayer.getProfile().getProperties().put("textures", new Property("textures", StartSkin[0], StartSkin[1]));
        entityPlayer.setLocation(StartLocation.getX(), StartLocation.getY(), StartLocation.getZ(), StartLocation.getYaw(), StartLocation.getPitch());
        EntityPlayerUtils.enableOuterLayer(entityPlayer.getDataWatcher());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (StartLocation.getPitch() / 360 * 256)));
        swing= new PacketPlayOutAnimation(entityPlayer, (byte) 0);
    }


    public void start(Player player) {
        int locationlength = LocationTicks.size();
        int blocklength = BlockTicks.size();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            while (tick == nextLocation) {
                Location loc = LocationActions.get(Locationindex);
                teleport(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), entityPlayer, ((CraftPlayer) player).getHandle().playerConnection);
                Locationindex++;
                nextLocation = Locationindex == locationlength ? 0 : LocationTicks.get(Locationindex);
            }
            while (tick == nextBlock) {
                BlockPosition p = BlockActions.get(Blockindex);
                world.setTypeAndData(p, Blocks.SANDSTONE.getBlockData(), 3);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(swing);
                Blockindex++;
                nextBlock = Blockindex == blocklength ? 0 : BlockTicks.get(Blockindex);
            }
            tick++;
        }, 0, 1);
    }

    public void teleport(double x, double y, double z, float yaw, float pitch, EntityPlayer entityPlayer, PlayerConnection connection) {
        connection.sendPacket(new PacketPlayOutEntityTeleport(entityPlayer.getId(), MathHelper.floor(x * 32), MathHelper.floor(y * 32), MathHelper.floor(z * 32), (byte) ((int) (yaw * 256 / 360)), (byte) ((int) (pitch * 256 / 360)), true));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), (byte) ((int) (yaw * 256F / 360F)), (byte) ((int) (pitch * 256F / 360F)), true));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((int) (yaw * 256F / 360F))));
    }
}
