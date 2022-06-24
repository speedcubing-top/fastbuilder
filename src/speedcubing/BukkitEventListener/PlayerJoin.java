package speedcubing.BukkitEventListener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import speedcubing.Core.timed.Long;
import speedcubing.Main;
import speedcubing.Member;

public class PlayerJoin implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        new Member(player.getUniqueId());
        player.setGameMode(GameMode.SURVIVAL);
        Main.Long.Join(player, true);
    }
}
