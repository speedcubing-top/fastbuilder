package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import top.speedcubing.fastbuilder.core.Core;
import top.speedcubing.fastbuilder.core.Custom;

public class PlayerChangedWorld implements Listener {
    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (e.getFrom().getName().equals("Custom")) {
            Custom.Quit(player);
            return;
        }
        if (!e.getFrom().getName().startsWith("map_fb_replay")) {
            Core.getInstance(e.getFrom().getName()).quit(player);
        }
    }
}
