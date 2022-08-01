package speedcubing.BukkitEventListener;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import speedcubing.Main;
import speedcubing.Member;
import speedcubing.replay.ReplayPlayer;
import speedcubing.server.speedcubingServer;

public class PlayerQuit implements Listener {
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equals("world"))
            Main.Long.Quit(player);
        else player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 1000, 0.5, 0, 0));
        Member member = Member.getMember(player);
        new Thread(() -> {
            int[] x = member.integerCache;
            speedcubingServer.connection.update("playersdata",
                    "fastbuild_blockskin=" + x[0] +
                            ",fastbuild_falldelay=" + x[1]
                    , "id=" + member.id);
        }).start();
        Member.users.remove(member.id);
    }
}
