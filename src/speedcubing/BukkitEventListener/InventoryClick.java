package speedcubing.BukkitEventListener;

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
import speedcubing.Core.BlockEditor;
import speedcubing.Core.Core;
import speedcubing.Core.Custom;
import speedcubing.Core.timed.TimedMode;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Main;
import speedcubing.Member;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

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
                            Main.Long.Join(player, false);
                        break;
                    case 11:
                        if (!player.getWorld().getName().equals("Short"))
                            Main.Short.Join(player, false);
                        break;
                    case 12:
                        if (!player.getWorld().getName().equals("Onestack"))
                            Main.OneStack.Join(player, false);
                        break;
                    case 13:
                        if (!player.getWorld().getName().equals("Inclined"))
                            Main.Inclined.Join(player, false);
                        break;
                    case 14:
                        if (!player.getWorld().getName().equals("Inclinedshort"))
                            Main.InclinedShort.Join(player, false);
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
                                player.playSound(player.getLocation(), Sound.CLICK, 1F, 1F);
                                Member member = Member.getMember(player.getUniqueId());
                                int x = member.integerCache[1];
                                if (x != 1) {
                                    member.integerCache[1] = x - 1;
                                    ItemStack stack = inventory.getItem(RawSlot);
                                    ItemMeta meta = stack.getItemMeta();
                                    String[] str = StringList.FallDelayLore[member.user.lang];
                                    meta.setLore(Arrays.asList(str[1], str[2].replace("%int%", Integer.toString(x - 1)), str[3], str[4], str[5]));
                                    stack.setItemMeta(meta);
                                } else {
                                    player.sendMessage(StringList.min[member.user.lang]);
                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                                }
                            }
                            break;
                            case PICKUP_HALF: {
                                player.playSound(player.getLocation(), Sound.CLICK, 1F, 1F);
                                Member member = Member.getMember(player.getUniqueId());
                                int x = member.integerCache[1];
                                if (x != 20) {
                                    member.integerCache[1] = x + 1;
                                    ItemStack stack = inventory.getItem(RawSlot);
                                    ItemMeta meta = stack.getItemMeta();
                                    String[] str = StringList.FallDelayLore[member.user.lang];
                                    meta.setLore(Arrays.asList(str[1], str[2].replace("%int%", Integer.toString(x + 1)), str[3], str[4], str[5]));
                                    stack.setItemMeta(meta);
                                } else {
                                    player.sendMessage(StringList.max[member.user.lang]);
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
                            player.openInventory(ItemStackList.ResetConfirm[User.getUser(player.getUniqueId()).lang]);
                        else if (player.getInventory().getItem(5) != null) {
                            UUID uuid = player.getUniqueId();
                            Integer[] center = Custom.Center.get(uuid);
                            boolean Loop = Custom.hits.get(uuid).isEmpty();
                            if (Loop)
                                Custom.AddBB(center[0], center[1], Member.getMember(uuid).PlayerCoord, uuid);
                            else
                                Custom.hits.get(uuid).clear();
                            for (int z = -1; z <= 1; z++) {
                                player.getWorld().getBlockAt(center[0] - 1, center[1] + 100, Member.getMember(uuid).PlayerCoord + z).setType(Loop ? Material.AIR : Material.GOLD_PLATE);
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
                        Member member = Member.getMember(uuid);
                        TimedMode mode = Core.getInstance(player.getWorld().getName());
                        new Thread(() -> speedcubingServer.connection.update("playersdata", "fb_" + mode.sqlId + "_pb=0", "uuid='" + uuid + "'")).start();
                        member.updateSidebar(player.getName(), mode, false);
                        player.getScoreboard().getTeam(StringList.SideBar[member.user.lang][9]).setSuffix("§7-,---");
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
