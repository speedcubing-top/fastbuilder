package top.speedcubing.fastbuilder.BukkitEventListener;

import top.speedcubing.fastbuilder.Core.Core;
import top.speedcubing.fastbuilder.Core.Custom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangedWorld implements Listener {
    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (e.getFrom().getName().equals("Custom"))
            Custom.Quit(player);
        else if (!e.getFrom().getName().startsWith("map_fb_replay"))
            Core.getInstance(e.getFrom().getName()).Quit(player);
    }
}
