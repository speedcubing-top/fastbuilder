package top.speedcubing.fastbuilder.BukkitEventListener;


import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;
import top.speedcubing.fastbuilder.*;
import top.speedcubing.fastbuilder.Core.timed.Normal;

public class PlayerQuit implements Listener {
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Member member = Member.getMember(player);
        if (player.getWorld().equals(Normal.instance.world))
            Normal.instance.Quit(player);
        else {
            if (member.isInReplay()) {
                member.replayPlayer.unwatch();
            }
            player.teleport(new Location(Normal.instance.world, 0.5, 1000, 0.5, 0, 0));
        }
        member.user.dbUpdate("fastbuild_blockskin=" + member.blockskin + ",fb_blockplace=fb_blockplace+" + member.placedBlock);
        Member.users.remove(member.user.id);
    }
}
