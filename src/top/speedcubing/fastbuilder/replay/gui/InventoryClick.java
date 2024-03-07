package top.speedcubing.fastbuilder.replay.gui;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.server.database.Database;

public class InventoryClick implements Listener {
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e) {
        if (e.getRawSlot() != -999) {
            Inventory inventory = e.getInventory();
            if (ReplayGUI.fb.contains(inventory.getHolder())) {
                e.setCancelled(true);
                int i = e.getRawSlot();
                ItemStack s = inventory.getItem(i);
                if (s != null)
                    switch (e.getAction()) {
                        case PICKUP_ALL:
                            Member.getMember(e.getWhoClicked()).joinReplay(i);
                            break;
                        case PICKUP_HALF:
                            if (i > 0) {
                                Member member = Member.getMember(e.getWhoClicked());
                                Database.systemConnection.update("fbreplay", "fav=" + (s.getType().equals(Material.EMPTY_MAP) ? 0 : 1), "id='" + member.replays.get(i - 1) + "'");
                                ReplayGUI.openFastBuilderReplay(member, member.currentMode.sqlId);
                            } else
                                Member.getMember(e.getWhoClicked()).joinReplay(i);
                            break;
                    }
            }
        }
    }
}
