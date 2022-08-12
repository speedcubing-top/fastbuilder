package speedcubing.BukkitEventListener;

import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import speedcubing.Core.Core;
import speedcubing.Core.Custom;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Member;
import speedcubing.lib.bukkit.SideBar;
import speedcubing.lib.bukkit.inventory.ItemBuilder;
import speedcubing.replay.gui.ReplayGUI;

public class PlayerInteract implements Listener {
    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Member member = Member.getMember(player);
        switch (player.getItemInHand().getType()) {
            case BLAZE_ROD:
                switch (e.getAction()) {
                    case LEFT_CLICK_BLOCK:
                    case LEFT_CLICK_AIR:
                        member.replayPlayer.minusTick();
                        break;
                    case RIGHT_CLICK_AIR:
                    case RIGHT_CLICK_BLOCK:
                        member.replayPlayer.addoneTick();
                        break;
                }
                return;
            case INK_SACK:
                member.replayPlayer.start();
                break;
            case BARRIER:
                member.replayPlayer.unwatch();
                member.currentMode.Join(player, true,false);
                break;
        }
        if (member.waiting) {
            e.setCancelled(true);
            player.updateInventory();
            return;
        }
        if (player.getGameMode() == GameMode.SURVIVAL) {
            if (e.getAction() == Action.PHYSICAL) {
                if (e.getClickedBlock().getType() == Material.GOLD_PLATE) {
                    if (player.getWorld().getName().equals("Custom"))
                        Custom.Finish(player, true);
                }
            } else {
                Action action = e.getAction();
                switch (player.getItemInHand().getType()) {
                    case BOOK:
//                        player.sendMessage("Release in this weekend!");
                        ReplayGUI.openFastBuilderReplay(member, member.currentMode.sqlId);
                        break;
                    case DIAMOND_PICKAXE:
                        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                            Block b = e.getClickedBlock();
                            if (member.Blocks.contains(b)) {
                                member.replay.replayRecorder.BlockBreakEvent(b);
                                member.Blocks.remove(b);
                                b.setType(Material.AIR);
                            }
                        }
                        break;
                    case REDSTONE_COMPARATOR:
                        e.setCancelled(true);
                        member.openSettings();
                        break;
                    case COMPASS: {
                        int lang = Member.getMember(player).lang;
                        Inventory inventory = Bukkit.createInventory(ItemStackList.menu, 27, StringList.MenuTitle[lang]);
                        ItemBuilder[] builders = {
                                new ItemBuilder(Material.SANDSTONE).amount(51).durability(2).name(StringList.typename[0][lang]),
                                new ItemBuilder(Material.SANDSTONE).amount(28).durability(1).name(StringList.typename[1][lang]),
                                new ItemBuilder(Material.SANDSTONE_STAIRS).amount(28).durability(1).name(StringList.typename[3][lang]),
                                new ItemBuilder(Material.STEP).amount(50).durability(1).name(StringList.typename[2][lang]),
                                new ItemBuilder(Material.STEP).amount(30).durability(1).name(StringList.typename[4][lang]),
                                new ItemBuilder(Material.SANDSTONE).durability(1).name(StringList.typename[5][lang])
                        };
                        if (!player.getWorld().getName().equals("Custom"))
                            builders[member.currentMode.sqlId].glow();
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
                        EditLength(true, (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) ? -1 : 1, player);
                        break;
                    case GOLD_INGOT:
                        e.setCancelled(true);
                        EditLength(false, (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) ? -1 : 1, player);
                        break;
                }
            }
        }
    }

    public void EditLength(boolean horizontal, int i, Player player) {
        Member member = Member.getMember(player);
        int[] center = member.Center;
        int[] settings = member.LengthSettings;
        int coord = Member.getMember(player).PlayerCoord;
        World world = Bukkit.getWorld("Custom");
        boolean c = true;
        boolean UnLoop = member.hits.isEmpty();
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
            player.updateInventory();
            player.getInventory().setItem(horizontal ? 5 : 6, horizontal ? ItemStackList.Length[member.lang] : ItemStackList.Height[member.lang]);
            SideBar.getSidebar(player).setLine(StringList.CustomSideBar[member.lang][horizontal ? 4 : 7] + (settings[horizontal ? 0 : 1] + i), horizontal ? 4 : 7);
            member.LengthSettings[horizontal ? 0 : 1] += i;
            center[horizontal ? 0 : 1] += i;
            if (!UnLoop) Custom.AddBB(center[0], center[1], coord, member);
        }
    }
}