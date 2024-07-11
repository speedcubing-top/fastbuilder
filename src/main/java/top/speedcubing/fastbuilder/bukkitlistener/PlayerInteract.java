package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import top.speedcubing.fastbuilder.core.Custom;
import top.speedcubing.fastbuilder.core.timed.ExtraShort;
import top.speedcubing.fastbuilder.core.timed.Inclined;
import top.speedcubing.fastbuilder.core.timed.InclinedShort;
import top.speedcubing.fastbuilder.core.timed.Long;
import top.speedcubing.fastbuilder.core.timed.Normal;
import top.speedcubing.fastbuilder.core.timed.OneStack;
import top.speedcubing.fastbuilder.core.timed.Short;
import top.speedcubing.fastbuilder.list.ItemStackList;
import top.speedcubing.fastbuilder.list.StringList;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.inventory.InventoryBuilder;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.lib.minecraft.text.TextBuilder;
import top.speedcubing.lib.minecraft.text.TextClickEvent;
import top.speedcubing.lib.minecraft.text.TextHoverEvent;

public class PlayerInteract implements Listener {
    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Member member = Member.getMember(player);
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
                        member.user.openLangInventory(ItemStackList.replayBook);
                        break;
                    case DIAMOND_PICKAXE:
                        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
                            Block b = e.getClickedBlock();
                            if (member.placedBlockList.contains(b)) {
                                member.replay.replayRecorder.BlockBreakEvent(b);
                                member.placedBlockList.remove(b);
                                Main.setTypeCheck(b, Material.AIR, 17);
//                                b.setType(Material.AIR);
                            }
                        }
                        break;
                    case REDSTONE_COMPARATOR:
                        e.setCancelled(true);
                        member.openSettings();
                        break;
                    case COMPASS: {

                        int lang = Member.getMember(player).user.lang;

                        ItemBuilder[] builders = {
                                new ItemBuilder(Material.SANDSTONE).amount(51).durability(1).name(StringList.typename[0].get(lang)), //normal 0
                                new ItemBuilder(Material.SANDSTONE).amount(28).durability(1).name(StringList.typename[1].get(lang)), //short 1
                                new ItemBuilder(Material.SANDSTONE).durability(1).name(StringList.typename[2].get(lang)), //long 2
                                new ItemBuilder(Material.STEP).amount(50).durability(1).name(StringList.typename[3].get(lang)), //inc 3
                                new ItemBuilder(Material.STEP).amount(30).durability(1).name(StringList.typename[4].get(lang)), //incs 4
                                new ItemBuilder(Material.SANDSTONE_STAIRS).amount(28).durability(1).name(StringList.typename[5].get(lang)), //onestack 5
                                new ItemBuilder(Material.SANDSTONE).amount(16).durability(1).name(StringList.typename[6].get(lang)), //extrash 6
                                new ItemBuilder(Material.SANDSTONE).durability(1).name(StringList.typename[7].get(lang)) //custom
                        };
                        if (!player.getWorld().getName().equals("Custom"))
                            builders[member.currentMode.sqlId].glow();
                        else builders[builders.length - 1].glow();

                        InventoryBuilder inventory = new InventoryBuilder(36, StringList.MenuTitle.get(lang));
                        inventory.setItem(builders[2].build(), (event) -> {
                            if (!player.getWorld().equals(Long.instance.world))
                                Long.instance.Join(player, false, false);
                        }, 10);
                        inventory.setItem(builders[0].build(), (event) -> {
                            if (!player.getWorld().equals(Normal.instance.world))
                                Normal.instance.Join(player, false, false);
                        }, 11);
                        inventory.setItem(builders[1].build(), (event) -> {
                            if (!player.getWorld().equals(Short.instance.world))
                                Short.instance.Join(player, false, false);
                        }, 12);
                        inventory.setItem(builders[6].build(), (event) -> {
                            if (!player.getWorld().equals(ExtraShort.instance.world))
                                ExtraShort.instance.Join(player, false, false);
                        }, 13);
                        //onestack
                        inventory.setItem(builders[5].build(), (event) -> {
                            if (!player.getWorld().equals(OneStack.instance.world))
                                OneStack.instance.Join(player, false, false);
                        }, 15);
                        //custom
                        inventory.setItem(builders[7].build(), (event) -> {
                            if (!player.getWorld().getName().equals("Custom"))
                                Custom.Join(player);
                        }, 16);

                        //inc
                        inventory.setItem(builders[3].build(), (event) -> {
                            if (!player.getWorld().equals(Inclined.instance.world))
                                Inclined.instance.Join(player, false, false);
                        }, 19);
                        inventory.setItem(builders[4].build(), (event) -> {
                            if (!player.getWorld().equals(InclinedShort.instance.world))
                                InclinedShort.instance.Join(player, false, false);
                        }, 20);

                        player.openInventory(inventory.getInventory());
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
        } else if (player.getGameMode() == GameMode.ADVENTURE) {
            if (e.getAction() == Action.PHYSICAL) {
                e.setCancelled(true);
            } else {
                //REPLAY
                switch (player.getItemInHand().getType()) {
                    case BLAZE_ROD:
                        switch (e.getAction()) {
                            case LEFT_CLICK_BLOCK:
                            case LEFT_CLICK_AIR:
                                member.replayViewer.minusTick();
                                break;
                            case RIGHT_CLICK_AIR:
                            case RIGHT_CLICK_BLOCK:
                                member.replayViewer.addoneTick();
                                break;
                        }
                        return;
                    case INK_SACK:
                        member.replayViewer.start();
                        break;
                    case BARRIER:
                        member.replayViewer.unwatch();
                        member.currentMode.Join(player, true, false);
                        break;
                    case NAME_TAG:
                        member.user.sendComponent(new TextBuilder()
                                .both("§6" + member.replayViewer.replay.replayID,
                                        TextClickEvent.suggestCommand(member.replayViewer.replay.replayID),
                                        TextHoverEvent.showText("§eClick to put it in chat so you can copy!"))
                                .toBungee());
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
                member.user.sound(Sound.NOTE_PLING);
                c = false;
            } else {
                for (int X = -2; X <= 3; X++) {
                    for (int Y = 97; Y <= 100; Y++) {
                        for (int Z = -2; Z <= 2; Z++) {
                            Main.setTypeCheck(world.getBlockAt(center[0] - X * i + i, center[1] + Y, coord + Z), X == -3 * i ? Material.AIR : world.getBlockAt(center[0] - X * i, center[1] + Y, coord + Z).getType(), 18);
//                            world.getBlockAt(center[0] - X * i + i, center[1] + Y, coord + Z).setType(X == -3 * i ? Material.AIR : world.getBlockAt(center[0] - X * i, center[1] + Y, coord + Z).getType());
                        }
                    }
                }
            }
        } else {
            if ((settings[1] == 0 && i == -1) || (settings[1] == 64 && i == 1)) {
                member.user.sound(Sound.NOTE_PLING);
                c = false;
            } else {
                if (UnLoop && i == -1)
                    for (int Z = -1; Z <= 1; Z++) {
                        Main.setTypeCheck(world.getBlockAt(center[0] - 1, center[1] + 100, coord + Z), Material.AIR, 19);
//                        world.getBlockAt(center[0] - 1, center[1] + 100, coord + Z).setType(Material.AIR);
                    }
                for (int X = -2; X <= 2; X++) {
                    for (int Y = -2; Y <= 1; Y++) {
                        for (int Z = -2; Z <= 2; Z++) {
                            Main.setTypeCheck(world.getBlockAt(center[0] + X, center[1] - Y * i + 98, coord + Z), Y == 1 ? Material.AIR : world.getBlockAt(center[0] + X, center[1] - i - Y * i + 98, coord + Z).getType(), 20);
//                            world.getBlockAt(center[0] + X, center[1] - Y * i + 98, coord + Z).setType(Y == 1 ? Material.AIR : world.getBlockAt(center[0] + X, center[1] - i - Y * i + 98, coord + Z).getType());
                        }
                    }
                }
                if (UnLoop)
                    for (int Z = -1; Z <= 1; Z++) {
                        Main.setTypeCheck(world.getBlockAt(center[0] - 1, center[1] + i + 100, coord + Z), Material.GOLD_PLATE, 21);
//                        world.getBlockAt(center[0] - 1, center[1] + i + 100, coord + Z).setType(Material.GOLD_PLATE);
                    }
            }
        }
        if (c) {
            player.updateInventory();
            player.getInventory().setItem(horizontal ? 5 : 6, horizontal ? ItemStackList.Length[member.user.lang] : ItemStackList.Height[member.user.lang]);
            SideBar.getSidebar(player).setLineByScore(StringList.CustomSideBar[member.user.lang][horizontal ? 4 : 7] + (settings[horizontal ? 0 : 1] + i), horizontal ? 7 : 4);
            member.LengthSettings[horizontal ? 0 : 1] += i;
            center[horizontal ? 0 : 1] += i;
            if (!UnLoop) Custom.AddBB(center[0], center[1], coord, member);
        }
    }
}