package speedcubing.BukkitEventListener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import speedcubing.Core.Long;
import speedcubing.Member;

public class PlayerJoin implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.isOnline()) {
            player.setGameMode(GameMode.SURVIVAL);
            new Member(player.getUniqueId());
            Long.Join(player, true);
        }
    }
}
