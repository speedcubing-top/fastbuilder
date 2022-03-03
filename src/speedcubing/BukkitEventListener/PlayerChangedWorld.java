package speedcubing.BukkitEventListener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import speedcubing.Core.*;
import speedcubing.Core.Long;
import speedcubing.Core.Short;

public class PlayerChangedWorld implements Listener {
    @EventHandler
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        switch (e.getFrom().getName()) {
            case "world":
                Long.Quit(player);
                break;
            case "Short":
                Short.Quit(player);
                break;
            case "Inclined":
                Inclined.Quit(player);
                break;
            case "Onestack":
                OneStack.Quit(player);
                break;
            case "Inclinedshort":
                InclinedShort.Quit(player);
                break;
            case "Custom":
                Custom.Quit(player);
                break;
        }
    }
}
