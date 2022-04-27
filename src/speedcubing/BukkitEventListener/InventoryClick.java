package speedcubing.BukkitEventListener;

import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import speedcubing.Core.Long;
import speedcubing.Core.Short;
import speedcubing.Core.*;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;

import java.util.Arrays;
import java.util.UUID;

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
                            Long.Join(player, false);
                        break;
                    case 11:
                        if (!player.getWorld().getName().equals("Short"))
                            Short.Join(player);
                        break;
                    case 12:
                        if (!player.getWorld().getName().equals("Onestack"))
                            OneStack.Join(player);
                        break;
                    case 13:
                        if (!player.getWorld().getName().equals("Inclined"))
                            Inclined.Join(player);
                        break;
                    case 14:
                        if (!player.getWorld().getName().equals("Inclinedshort"))
                            InclinedShort.Join(player);
                        break;
                    case 16:
                        if (!player.getWorld().getName().equals("Custom"))
                            Custom.Join(player);
                        break;
                }
            } else if (holder == ItemStackList.settings) {
                e.setCancelled(true);
                switch (RawSlot) {
                    case 11:
                        switch (e.getAction()) {
                            case PICKUP_ALL: {
                                UUID uuid = player.getUniqueId();
                                int lang = User.getLang(uuid);
                                player.playSound(player.getLocation(), Sound.CLICK, 1F, 1F);
                                int x = Core.integerCache.get(uuid)[1];
                                if (x != 1) {
                                    Core.integerCache.get(uuid)[1] = x - 1;
                                    ItemStack stack = inventory.getItem(RawSlot);
                                    ItemMeta meta = stack.getItemMeta();
                                    String[] str = StringList.FallDelayLore[lang];
                                    meta.setLore(Arrays.asList(str[1], str[2].replace("%int%", Integer.toString(x - 1)), str[3], str[4], str[5]));
                                    stack.setItemMeta(meta);
                                } else {
                                    player.sendMessage(StringList.min[lang]);
                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                                }
                            }
                            break;
                            case PICKUP_HALF: {
                                UUID uuid = player.getUniqueId();
                                int lang = User.getLang(uuid);
                                player.playSound(player.getLocation(), Sound.CLICK, 1F, 1F);
                                int x = Core.integerCache.get(uuid)[1];
                                if (x != 20) {
                                    Core.integerCache.get(uuid)[1] = x + 1;
                                    ItemStack stack = inventory.getItem(RawSlot);
                                    ItemMeta meta = stack.getItemMeta();
                                    String[] str = StringList.FallDelayLore[lang];
                                    meta.setLore(Arrays.asList(str[1], str[2].replace("%int%", Integer.toString(x + 1)), str[3], str[4], str[5]));
                                    stack.setItemMeta(meta);
                                } else {
                                    player.sendMessage(StringList.max[lang]);
                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                                }
                            }
                            break;
                        }
                        break;
                    case 13:
                        BlockEditor.createMainPage(player);
                        break;
                    case 15:
                        if (!player.getWorld().getName().equals("Custom"))
                            player.openInventory(ItemStackList.ResetConfirm[User.getLang(player.getUniqueId())]);
                        else if (player.getInventory().getItem(5) != null) {
                            UUID uuid = player.getUniqueId();
                            Integer[] center = Custom.Center.get(uuid);
                            int coord = Core.PlayerCoord.get(uuid);
                            boolean Loop = Custom.hits.get(uuid).isEmpty();
                            if (Loop)
                                Custom.AddBB(center[0], center[1], coord, uuid);
                            else
                                Custom.hits.get(uuid).clear();
                            for (int z = -1; z <= 1; z++) {
                                player.getWorld().getBlockAt(center[0] - 1, center[1] + 100, coord + z).setType(Loop ? Material.AIR : Material.GOLD_PLATE);
                            }
                            player.closeInventory();
                        }
                        break;
                }
            } else if (holder == ItemStackList.confirm) {
                e.setCancelled(true);
                switch (RawSlot) {
                    case 11:
                        UUID uuid = player.getUniqueId();
                        switch (player.getWorld().getName()) {
                            case "world":
                                new Thread(() -> speedcubingServer.connection.update("playersdata", "fb_0_pb=0", "uuid='" + uuid + "'")).start();
                                Core.updateSidebar(player.getName(), uuid, Long.session, Long.score, player.getWorld(),false);
                                break;
                            case "Short":
                                new Thread(() -> speedcubingServer.connection.update("playersdata", "fb_1_pb=0", "uuid='" + uuid + "'")).start();
                                Core.updateSidebar(player.getName(), uuid, Short.session, Short.score, player.getWorld(),false);
                                break;
                            case "Inclined":
                                new Thread(() -> speedcubingServer.connection.update("playersdata", "fb_2_pb=0", "uuid='" + uuid + "'")).start();
                                Core.updateSidebar(player.getName(), uuid, Inclined.session, Inclined.score, player.getWorld(),false);
                                break;
                            case "Onestack":
                                new Thread(() -> speedcubingServer.connection.update("playersdata", "fb_3_pb=0", "uuid='" + uuid + "'")).start();
                                Core.updateSidebar(player.getName(), uuid, OneStack.session, OneStack.score, player.getWorld(),false);
                                break;
                            case "Inclinedshort":
                                new Thread(() -> speedcubingServer.connection.update("playersdata", "fb_4_pb=0", "uuid='" + uuid + "'")).start();
                                Core.updateSidebar(player.getName(), uuid, InclinedShort.session, InclinedShort.score, player.getWorld(),false);
                                break;
                        }
                        player.getScoreboard().getTeam(StringList.SideBar[User.getLang(uuid)][9]).setSuffix("§7-,---");
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
