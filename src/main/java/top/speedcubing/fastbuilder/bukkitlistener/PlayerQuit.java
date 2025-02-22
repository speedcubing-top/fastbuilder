package top.speedcubing.fastbuilder.bukkitlistener;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.core.Core;
import top.speedcubing.fastbuilder.core.Custom;

public class PlayerQuit implements Listener {
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Member member = Member.getMember(player);
        if (member.isInReplay()) {
            member.replayViewer.unwatch();
        } else if (player.getWorld().getName().equals("Custom")) {
            Custom.Quit(player);
        } else {
            Core.getInstance(player.getWorld().getName()).quit(player);
        }
        member.user.dbUpdate("fastbuild_blockskin=" + member.blockskin + ",fb_blockplace=fb_blockplace+" + member.placedBlock);
        member.user.dbUpdate("fastbuild_animation=" + member.animation);
        Member.users.remove(member.user.id);
    }
}
