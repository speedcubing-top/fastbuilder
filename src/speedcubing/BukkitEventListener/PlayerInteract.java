package speedcubing.BukkitEventListener;

import cubing.lib.bukkit.ItemBuilder;
import cubingserver.libs.User;
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
                    String name = player.getWorld().getName();
                    switch (name) {
                        case "world":
                            Core.finish(name, 0, false, player, Long.session, Long.score, 146);
                            break;
                        case "Short":
                            Core.finish(name, 1, false, player, Short.session, Short.score, 76);
                            break;
                        case "Onestack":
                            Core.finish(name, 2, false, player, OneStack.session, OneStack.score, 220);
                            break;
                        case "Inclined":
                            Core.finish(name, 3, true, player, Inclined.session, Inclined.score, 110);
                            break;
                        case "Inclinedshort":
                            Core.finish(name, 4, true, player, InclinedShort.session, InclinedShort.score, 40);
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
                        int lang = User.getLang(player.getUniqueId());
                        Inventory inventory = Bukkit.createInventory(ItemStackList.settings, 27, StringList.Settings[lang]);
                        String[] hit = StringList.FallDelayLore[lang];
                        inventory.setItem(11, new ItemBuilder(Material.RAW_FISH).durability(3).name(hit[0]).lore(hit[1], hit[2].replace("%int%", Integer.toString(Core.integerCache.get(player.getUniqueId())[1])), hit[3], hit[4], hit[5]).build());
                        inventory.setItem(13, ItemStackList.openkit[lang]);
                        if (!player.getWorld().getName().equals("Custom"))
                            inventory.setItem(15, ItemStackList.Reset[lang]);
                        else if (player.getInventory().getItem(5) != null) {
                            String[] lore = StringList.LoopLore[lang];
                            inventory.setItem(15, new ItemBuilder(Material.MINECART).name(lore[0]).lore(lore[1], Custom.hits.get(uuid).isEmpty() ? lore[3] : lore[2]).build());
                        }
                        player.openInventory(inventory);
                    }
                    break;
                    case COMPASS: {
                        int lang = User.getLang(player.getUniqueId());
                        Inventory inventory = Bukkit.createInventory(ItemStackList.menu, 27, StringList.MenuTitle[lang]);
                        ItemBuilder[] builders = {
                                new ItemBuilder(Material.SANDSTONE).amount(51).durability(2).name(StringList.typename[0][lang]),
                                new ItemBuilder(Material.SANDSTONE).amount(28).durability(1).name(StringList.typename[1][lang]),
                                new ItemBuilder(Material.SANDSTONE_STAIRS).amount(28).durability(1).name(StringList.typename[3][lang]),
                                new ItemBuilder(Material.STEP).amount(50).durability(1).name(StringList.typename[2][lang]),
                                new ItemBuilder(Material.STEP).amount(30).durability(1).name(StringList.typename[4][lang]),
                                new ItemBuilder(Material.SANDSTONE).durability(1).name(StringList.typename[5][lang])
                        };
                        switch (player.getWorld().getName()) {
                            case "world":
                                builders[0].glow();
                                break;
                            case "Short":
                                builders[1].glow();
                                break;
                            case "Onestack":
                                builders[2].glow();
                                break;
                            case "Inclined":
                                builders[3].glow();
                                break;
                            case "Inclinedshort":
                                builders[4].glow();
                                break;
                        }
                        inventory.setItem(10, builders[0].build());
                        inventory.setItem(11, builders[1].build());
                        inventory.setItem(12, builders[2].build());
                        inventory.setItem(13, builders[3].build());
                        inventory.setItem(14, builders[4].build());
                        inventory.setItem(16, builders[5].build());
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
                            world.getBlockAt(center[0] + X, center[1] - Y * i + 98, coord + Z).setType(Y == 1 ? Material.AIR : world.getBlockAt(center[0] + X, center[1] - i - Y * i + 98, coord + Z).getType());
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
            int lang = User.getLang(uuid);
            player.updateInventory();
            player.getInventory().setItem(horizontal ? 5 : 6, horizontal ? ItemStackList.Length[lang] : ItemStackList.Height[lang]);
            player.getScoreboard().getTeam(StringList.CustomSideBar[lang][horizontal ? 7 : 4]).setSuffix(Integer.toString(settings[horizontal ? 0 : 1] + i));
            Custom.LengthSettings.get(uuid)[horizontal ? 0 : 1] += i;
            center[horizontal ? 0 : 1] += i;
            if (!UnLoop) Custom.AddBB(center[0], center[1], coord, uuid);
        }
    }
}