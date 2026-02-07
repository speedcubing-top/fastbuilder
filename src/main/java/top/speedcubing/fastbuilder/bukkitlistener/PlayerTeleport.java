package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleport implements Listener {
    @EventHandler
    public void playerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            if (e.getFrom().getWorld() != e.getTo().getWorld()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("Cancelled.");
            }
        }
    }
}
