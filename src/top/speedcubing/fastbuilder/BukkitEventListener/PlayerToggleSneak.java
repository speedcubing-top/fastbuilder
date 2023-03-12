package top.speedcubing.fastbuilder.BukkitEventListener;

import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import top.speedcubing.fastbuilder.Member;

public class PlayerToggleSneak implements Listener {
    @EventHandler
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent e) {
        Member member = Member.getMember(e.getPlayer());
        if (e.isSneaking() && member.replayPlayer != null && member.replayPlayer.isSpec) {
            member.replayPlayer.isSpec = false;
            member.user.sendPacket(new PacketPlayOutCamera(member.user.toNMS()), new PacketPlayOutGameStateChange(3, 2));
            member.user.bSetAllowFlight(true);
            member.user.bSetFlying(true);
            member.user.bTeleport(new Location(member.user.bGetWorld(), member.replayPlayer.npc.entityPlayer.locX, member.replayPlayer.npc.entityPlayer.locY, member.replayPlayer.npc.entityPlayer.locZ, member.replayPlayer.npc.entityPlayer.yaw, member.replayPlayer.npc.entityPlayer.pitch));
        }
    }
}
