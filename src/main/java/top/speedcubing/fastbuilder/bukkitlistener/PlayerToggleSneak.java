package top.speedcubing.fastbuilder.bukkitlistener;

import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.Replay;

public class PlayerToggleSneak implements Listener {

    @EventHandler
    public void playerToggleSneak(PlayerToggleSneakEvent e) {
        Member member = Member.getMember(e.getPlayer());
        Replay replay = member.replay;
        if (replay != null) {
            replay.replayRecorder.playerToggleSneakEvent(e.isSneaking());
            return;
        }

        if (e.isSneaking() && member.replayViewer != null && member.replayViewer.isSpec) {
            member.replayViewer.isSpec = false;
            member.user.sendPacket(new PacketPlayOutCamera(member.user.toNMS()), new PacketPlayOutGameStateChange(3, 2));
            member.user.bSetAllowFlight(true);
            member.user.bSetFlying(true);
            member.user.bTeleport(new Location(member.user.bGetWorld(), member.replayViewer.npc.entityPlayer.locX, member.replayViewer.npc.entityPlayer.locY, member.replayViewer.npc.entityPlayer.locZ, member.replayViewer.npc.entityPlayer.yaw, member.replayViewer.npc.entityPlayer.pitch));
        }
    }
}
