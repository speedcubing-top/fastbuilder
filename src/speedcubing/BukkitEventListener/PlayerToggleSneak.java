package speedcubing.BukkitEventListener;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import speedcubing.Member;

public class PlayerToggleSneak implements Listener {
    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent e) {
        Member member = Member.getMember(e.getPlayer());
        if (e.isSneaking() && member.replayPlayer != null && member.replayPlayer.isSpec) {
            member.replayPlayer.isSpec = false;
            EntityPlayer npc = member.replayPlayer.npc.entityPlayer;
            ((CraftPlayer) member.player).getHandle().playerConnection.sendPacket(new PacketPlayOutCamera(((CraftPlayer) member.player).getHandle()));
            ((CraftPlayer) member.player).getHandle().playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, 2));
            member.player.setAllowFlight(true);
            member.player.setFlying(true);
            member.player.teleport(new Location(member.player.getWorld(), npc.locX, npc.locY, npc.locZ, npc.yaw, npc.pitch));
        }
    }
}
