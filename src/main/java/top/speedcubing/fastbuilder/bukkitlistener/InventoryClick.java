package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClick implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void InventoryClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();
        int RawSlot = e.getRawSlot();
        if (RawSlot != 999) {
            if (inventory.getName().equals("container.crafting")) {
                if (player.getGameMode() == GameMode.SURVIVAL)
                    e.setCancelled(true);
            }
        }
    }
}
