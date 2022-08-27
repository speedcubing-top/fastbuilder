package speedcubing.BukkitEventListener;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import speedcubing.Main;
import speedcubing.Member;
import speedcubing.server.speedcubingServer;

public class PlayerQuit implements Listener {
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Member member = Member.getMember(player);
        if (player.getWorld().getName().equals("world"))
            Main.Long.Quit(player);
        else {
            if (player.getWorld().getName().startsWith("replay_")) {
                member.replayPlayer.unwatch();
            }
            player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 1000, 0.5, 0, 0));
        }
        new Thread(() -> {
            int x = member.blockskin;
            speedcubingServer.connection.update("playersdata", "fastbuild_blockskin=" + x, "id=" + member.user.id);
        }).start();
        Member.users.remove(member.user.id);
    }
}
