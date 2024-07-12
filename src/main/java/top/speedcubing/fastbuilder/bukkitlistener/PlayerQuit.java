package top.speedcubing.fastbuilder.bukkitlistener;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import top.speedcubing.fastbuilder.core.timed.Normal;
import top.speedcubing.fastbuilder.Member;

public class PlayerQuit implements Listener {
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Member member = Member.getMember(player);
        if (player.getWorld().equals(Normal.instance.world))
            Normal.instance.Quit(player);
        else {
            if (member.isInReplay()) {
                member.replayViewer.unwatch();
            }
            player.teleport(new Location(Normal.instance.world, 0.5, 1000, 0.5, 0, 0));
        }
        member.user.dbUpdate("fastbuild_blockskin=" + member.blockskin + ",fb_blockplace=fb_blockplace+" + member.placedBlock);
        member.user.dbUpdate("fastbuild_animation=" + member.animation);
        Member.users.remove(member.user.id);
    }
}
