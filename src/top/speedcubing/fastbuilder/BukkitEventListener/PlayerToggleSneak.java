package top.speedcubing.fastbuilder.BukkitEventListener;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import top.speedcubing.fastbuilder.Member;

public class PlayerToggleSneak implements Listener {
    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent e) {
        Member member = Member.getMember(e.getPlayer());
        if (e.isSneaking() && member.replayPlayer != null && member.replayPlayer.isSpec) {
            member.replayPlayer.isSpec = false;
            EntityPlayer npc = member.replayPlayer.npc.entityPlayer;
            ((CraftPlayer) member.user.player).getHandle().playerConnection.sendPacket(new PacketPlayOutCamera(((CraftPlayer) member.user.player).getHandle()));
            ((CraftPlayer) member.user.player).getHandle().playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, 2));
            member.user.bSetAllowFlight(true);
            member.user.bSetFlying(true);
            member.user.bTeleport(new Location(member.user.bGetWorld(), npc.locX, npc.locY, npc.locZ, npc.yaw, npc.pitch));
        }
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            Member.getMember(e.getPlayer()).Blocks.add(e.getBlock());
        }
    }
}
