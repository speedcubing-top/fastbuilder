package speedcubing.BukkitEventListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import speedcubing.Core.Core;
import speedcubing.Core.Custom;

public class PlayerChangedWorld implements Listener {
    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (e.getFrom().getName().equals("Custom"))
            Custom.Quit(player);
        else if (!e.getFrom().getName().startsWith("replay_"))
            Core.getInstance(e.getFrom().getName()).Quit(player);
    }
}
