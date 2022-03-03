package speedcubing.BukkitEventListener;

import cubing.bukkit.ItemUtils;
import cubingserver.libs.PlayerData;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import speedcubing.Core.Long;
import speedcubing.Core.Short;
import speedcubing.Core.*;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class PlayerInteract implements Listener {
    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (Core.waiting.contains(uuid))
            return;
        if (player.getGameMode() == GameMode.SURVIVAL) {
            if (e.getAction() == Action.PHYSICAL) {
                if (e.getClickedBlock().getType() == Material.GOLD_PLATE) {
                    switch (player.getWorld().getName()) {
                        case "world":
                            Long.Finish(player);
                            break;
                        case "Short":
                            Short.Finish(player);
                            break;
                        case "Inclined":
                            Inclined.Finish(player);
                            break;
                        case "Onestack":
                            OneStack.Finish(player);
                            break;
                        case "Inclinedshort":
                            InclinedShort.Finish(player);
                            break;
                        case "Custom":
                            Custom.Finish(player, true);
                            break;
                    }
                }
            } else {
                Action action = e.getAction();
                switch (player.getItemInHand().getType()) {
                    case DIAMOND_PICKAXE:
                        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                            Block b = e.getClickedBlock();
                            BlockPosition position = new BlockPosition(b.getX(), b.getY(), b.getZ());
                            if (Core.Blocks.get(uuid).contains(position)) {
                                b.setType(Material.AIR);
                                Core.Blocks.get(uuid).remove(position);
                            }
                        }
                        break;
                    case REDSTONE_COMPARATOR: {
                        int lang = PlayerData.getLang(player.getUniqueId());
                        Inventory inventory = Bukkit.createInventory(ItemStackList.settings, 27, StringList.Settings[lang]);
                        String[] hit = StringList.FallDelayLore[lang];
                        inventory.setItem(11, ItemUtils.item(Material.RAW_FISH, hit[0], 1, 3, false, false, null, null, null, Arrays.asList(hit[1], hit[2].replace("%int%", Integer.toString(Core.integerCache.get(player.getUniqueId())[1])), hit[3], hit[4], hit[5])));
                        inventory.setItem(13, ItemStackList.openkit[lang]);
                        if (!player.getWorld().getName().equals("Custom"))
                            inventory.setItem(15, ItemStackList.Reset[lang]);
                        else if (player.getInventory().getItem(5) != null) {
                            String[] lore = StringList.LoopLore[lang];
                            inventory.setItem(15, ItemUtils.item(Material.MINECART, lore[0], 1, -1, false, false, null, null, null, Arrays.asList(lore[1], Custom.hits.get(uuid).isEmpty() ? lore[3] : lore[2])));
                        }
                        player.openInventory(inventory);
                    }
                    break;
                    case COMPASS: {
                        int lang = PlayerData.getLang(player.getUniqueId());
                        Inventory inventory = Bukkit.createInventory(ItemStackList.menu, 27, StringList.MenuTitle[lang]);
                        inventory.setItem(10, ItemUtils.item(Material.SANDSTONE, StringList.typename[0][lang], 51, 2, player.getWorld().getName().equals("world"), false, null, null, null, null));
                        inventory.setItem(11, ItemUtils.item(Material.SANDSTONE, StringList.typename[1][lang], 28, 1, player.getWorld().getName().equals("Short"), false, null, null, null, null));
                        inventory.setItem(12, ItemUtils.item(Material.SANDSTONE_STAIRS, StringList.typename[3][lang], 28, 1, player.getWorld().getName().equals("Onestack"), false, null, null, null, null));
                        inventory.setItem(13, ItemUtils.item(Material.STEP, StringList.typename[2][lang], 50, 1, player.getWorld().getName().equals("Inclined"), false, null, null, null, null));
                        inventory.setItem(14, ItemUtils.item(Material.STEP, StringList.typename[4][lang], 30, 1, player.getWorld().getName().equals("Inclinedshort"), false, null, null, null, null));
                        inventory.setItem(16, ItemUtils.item(Material.SANDSTONE, StringList.typename[5][lang], 1, 1, player.getWorld().getName().equals("Custom"), false, null, null, null, null));
                        player.openInventory(inventory);
                    }
                    break;
                    case IRON_INGOT:
                        e.setCancelled(true);
                        EditLength(true, (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) ? -1 : 1, player, uuid);
                        break;
                    case GOLD_INGOT:
                        e.setCancelled(true);
                        EditLength(false, (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) ? -1 : 1, player, uuid);
                        break;
                }
            }
        }
    }

    public void EditLength(boolean horizontal, int i, Player player, UUID uuid) {
        Integer[] center = Custom.Center.get(uuid);
        Integer[] settings = Custom.LengthSettings.get(uuid);
        int coord = Core.PlayerCoord.get(uuid);
        World world = Bukkit.getWorld("Custom");
        boolean c = true;
        boolean UnLoop = Custom.hits.get(uuid).isEmpty();
        if (horizontal) {
            if ((settings[0] == 5 && i == -1) || (settings[0] == 64 && i == 1)) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                c = false;
            } else {
                for (int X = -2; X <= 3; X++) {
                    for (int Y = 97; Y <= 100; Y++) {
                        for (int Z = -2; Z <= 2; Z++) {
                            world.getBlockAt(center[0] - X * i + i, center[1] + Y, coord + Z).setType(X == -3 * i ? Material.AIR : world.getBlockAt(center[0] - X * i, center[1] + Y, coord + Z).getType());
                        }
                    }
                }
            }
        } else {
            if ((settings[1] == 0 && i == -1) || (settings[1] == 64 && i == 1)) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                c = false;
            } else {
                if (UnLoop && i == -1)
                    for (int Z = -1; Z <= 1; Z++) {
                        world.getBlockAt(center[0] - 1, center[1] + 100, coord + Z).setType(Material.AIR);
                    }
                for (int X = -2; X <= 2; X++) {
                    for (int Y = -2; Y <= 1; Y++) {
                        for (int Z = -2; Z <= 2; Z++) {
                            world.getBlockAt(center[0] + X, center[1] - Y * i + 98, coord + Z).setType(Y == 1 ? Material.AIR : world.getBlockAt(center[0] + X, center[1] -i - Y * i + 98, coord + Z).getType());
                        }
                    }
                }
                if (UnLoop)
                    for (int Z = -1; Z <= 1; Z++) {
                        world.getBlockAt(center[0] - 1, center[1] + i + 100, coord + Z).setType(Material.GOLD_PLATE);
                    }
            }
        }
        if (c) {
            int lang = PlayerData.getLang(uuid);
            player.updateInventory();
            player.getInventory().setItem(horizontal ? 5 : 6, horizontal ? ItemStackList.Length[lang] : ItemStackList.Height[lang]);
            player.getScoreboard().getTeam(StringList.CustomSideBar[lang][horizontal ? 7 : 4]).setSuffix(Integer.toString(settings[horizontal ? 0 : 1] + i));
            Custom.LengthSettings.get(uuid)[horizontal ? 0 : 1] += i;
            center[horizontal ? 0 : 1] += i;
            if (!UnLoop) Custom.AddBB(center[0], center[1], coord, uuid);
        }
    }
}