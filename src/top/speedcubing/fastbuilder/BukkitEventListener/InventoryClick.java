package top.speedcubing.fastbuilder.BukkitEventListener;

import top.speedcubing.fastbuilder.Core.BlockEditor;
import top.speedcubing.fastbuilder.Core.Custom;
import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.List.StringList;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.server.speedcubingServer;

public class InventoryClick implements Listener {
    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();
        int RawSlot = e.getRawSlot();
        InventoryHolder holder = inventory.getHolder();
        if (RawSlot != 999) {
            if (inventory.getName().equals("container.crafting")) {
                if (player.getGameMode() == GameMode.SURVIVAL)
                    e.setCancelled(true);
            } else if (holder == ItemStackList.menu) {
                e.setCancelled(true);
                switch (RawSlot) {
                    case 10:
                        if (!player.getWorld().getName().equals("world"))
                            Main.Long.Join(player, false,false);
                        break;
                    case 11:
                        if (!player.getWorld().getName().equals("Short"))
                            Main.Short.Join(player, false,false);
                        break;
                    case 12:
                        if (!player.getWorld().getName().equals("Onestack"))
                            Main.OneStack.Join(player, false,false);
                        break;
                    case 13:
                        if (!player.getWorld().getName().equals("Inclined"))
                            Main.Inclined.Join(player, false,false);
                        break;
                    case 14:
                        if (!player.getWorld().getName().equals("Inclinedshort"))
                            Main.InclinedShort.Join(player, false,false);
                        break;
                    case 16:
                        if (!player.getWorld().getName().equals("Custom"))
                            Custom.Join(player);
                        break;
                }
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
                                player.getWorld().getBlockAt(center[0] - 1, center[1] + 100, member.PlayerCoord + z).setType(Loop ? Material.AIR : Material.GOLD_PLATE);
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
                        new Thread(() -> speedcubingServer.connection.update("playersdata", "fb_" + member.currentMode.sqlId + "_pb=0", "id=" + member.user.id)).start();
                        member.updateSidebar(player.getName(), member.currentMode, false);
                        SideBar.getSidebar(player).setLine(StringList.SideBar[member.user.lang][4] + "§7-,---", 4);
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
