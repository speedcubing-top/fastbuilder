package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlock implements Listener {
    @EventHandler
    public void EntityChangeBlockEvent(EntityChangeBlockEvent e) {
        if (e.getEntityType() == EntityType.FALLING_BLOCK && e.getTo().isBlock()) {
            e.setCancelled(true);
        }
    }

}
