package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import top.speedcubing.fastbuilder.core.BlockEditor;
import top.speedcubing.fastbuilder.core.Custom;
import top.speedcubing.fastbuilder.list.ItemStackList;
import top.speedcubing.fastbuilder.list.StringList;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.lib.bukkit.SideBar;

public class InventoryClick implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void InventoryClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();
        int RawSlot = e.getRawSlot();
        InventoryHolder holder = inventory.getHolder();
        if (RawSlot != 999) {
            if (inventory.getName().equals("container.crafting")) {
                if (player.getGameMode() == GameMode.SURVIVAL)
                    e.setCancelled(true);
            } else if (holder == ItemStackList.settings) {
                e.setCancelled(true);
                switch (RawSlot) {
                    case 12:
                        BlockEditor.createMainPage(player);
                        break;
                    case 14:
                        if (!player.getWorld().getName().equals("Custom"))
                            player.openInventory(ItemStackList.ResetConfirm[Member.getMember(player).user.lang]);
                        else if (player.getInventory().getItem(5) != null) {
                            Member member = Member.getMember(player);
                            int[] center = member.Center;
                            boolean Loop = member.hits.isEmpty();
                            if (Loop)
                                Custom.AddBB(center[0], center[1], member.PlayerCoord, member);
                            else
                                member.hits.clear();
                            for (int z = -1; z <= 1; z++) {
                                Main.setTypeCheck(player.getWorld().getBlockAt(center[0] - 1, center[1] + 100, member.PlayerCoord + z), Loop ? Material.AIR : Material.GOLD_PLATE, 22);
//                                player.getWorld().getBlockAt(center[0] - 1, center[1] + 100, member.PlayerCoord + z).setType(Loop ? Material.AIR : Material.GOLD_PLATE);
                            }
                            player.closeInventory();
                        }
                        break;
                }
            } else if (holder == ItemStackList.confirm) {
                e.setCancelled(true);
                switch (RawSlot) {
                    case 11:
                        Member member = Member.getMember(player);
                        member.user.dbUpdate("fb_" + member.currentMode.sqlId + "_pb=0");
                        member.updateSidebar(player.getName(), member.currentMode, false);
                        SideBar.getSidebar(player).setLineByScore(StringList.SideBar[member.user.lang][4] + "§7-,---", 9);
                        player.closeInventory();
                        break;
                    case 15:
                        player.closeInventory();
                        break;
                }
            } else BlockEditor.select(holder, e, RawSlot, player);
        }
    }
}
