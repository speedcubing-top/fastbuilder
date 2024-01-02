package top.speedcubing.fastbuilder.BukkitEventListener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import top.speedcubing.fastbuilder.*;
import top.speedcubing.fastbuilder.Core.timed.Normal;

public class PlayerJoin implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        new Member(player);
        player.setGameMode(GameMode.SURVIVAL);
        Normal.instance.Join(player, false, true);
    }
}
