package speedcubing.BukkitEventListener;


import speedcubing.Member;
import speedcubing.server.speedcubingServer;
import speedcubing.server.listeners.PlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import speedcubing.Core.Core;
import speedcubing.Core.Long;

import java.util.UUID;

public class PlayerQuit implements Listener {
    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (PlayerJoin.JoinPackets.containsKey(uuid)) {
            if (player.getWorld().getName().equals("world"))
                Long.Quit(player);
            else player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 1000, 0.5, 0, 0));
            new Thread(() -> {
                Integer[] x = Core.integerCache.get(uuid);
                speedcubingServer.connection.update("playersdata",
                        "fastbuild_blockskin=" + x[0] +
                                ",fastbuild_falldelay=" + x[1]
                        , "uuid='" + uuid + "'");
                Core.integerCache.remove(uuid);
            }).start();
            Core.Blocks.remove(uuid);
            Core.MainTimer.remove(uuid);
            Core.finishedtask.remove(uuid);
            Core.PlayerCoord.remove(uuid);
            Core.times.remove(uuid);
            Member.users.remove(uuid);
        }
    }
}
