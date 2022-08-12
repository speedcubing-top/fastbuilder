package speedcubing.replay.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import speedcubing.Member;

public class InventoryClick implements Listener {
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        if (ReplayGUI.fb.contains(inventory.getHolder())) {
            e.setCancelled(true);
            int i = e.getRawSlot();
            if (inventory.getItem(i) != null)
                Member.getMember(e.getWhoClicked()).joinReplay(i);
        }
    }
}
