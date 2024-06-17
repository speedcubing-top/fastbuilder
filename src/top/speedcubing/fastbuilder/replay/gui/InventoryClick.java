package top.speedcubing.fastbuilder.replay.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClick implements Listener {
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e) {
        if (e.getRawSlot() != -999) {
            Inventory inventory = e.getInventory();
        }
    }
}
