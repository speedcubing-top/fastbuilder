package speedcubing.BukkitEventListener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import speedcubing.All.Chat;
import speedcubing.Core.Long;

public class PlayerJoin implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.isOnline()) {
            player.setGameMode(GameMode.SURVIVAL);
            Long.Join(player, true);
            new Chat().PacketInject(player);
        }
    }
}
