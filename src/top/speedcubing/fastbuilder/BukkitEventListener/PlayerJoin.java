package top.speedcubing.fastbuilder.BukkitEventListener;

import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        new Member(player);
        player.setGameMode(GameMode.SURVIVAL);
        Main.Long.Join(player, false,true);
    }
}
