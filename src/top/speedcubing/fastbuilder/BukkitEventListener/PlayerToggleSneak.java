package top.speedcubing.fastbuilder.BukkitEventListener;

import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
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
            member.user.bTeleport(member.replayPlayer.npc.entityPlayer.getBukkitEntity());
        }
    }
}
