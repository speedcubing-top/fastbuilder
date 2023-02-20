package top.speedcubing.fastbuilder.Core;


import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.List.StringList;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.gui.FastBuilderUtils;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.TitleType;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;

import java.util.Set;

public class Custom {
    public static final int size = 31;
    public static int used = 0;
    public static boolean[] rooms = new boolean[size];

    public void Load() {
        for (int i = 0; i < size; i++) {
            rooms[i] = true;
        }
    }

    public static void Finish(Player player, boolean pressure) {
        Member member = Member.getMember(player);
        if (member.times == -1) {
            BungeePluginMessage.switchServer(player, "Lobby");
            return;
        }
        Bukkit.getScheduler().cancelTask(member.MainTimer);
        World world = Bukkit.getWorld("Custom");
        int lang = member.user.lang;
        SideBar.getSidebar(player).setLineByScore(StringList.CustomSideBar[lang][10] + FastBuilderUtils.tickToTime(member.times), 1);
        PlayerUtils.sendTitle(player, TitleType.TITLE, StringList.timedisplay[lang].replace("%time%", FastBuilderUtils.tickToTime(member.times)));
        member.times = -1;
        member.user.sound(Sound.LEVEL_UP);
        if (pressure) {
            member.waiting = true;
            member.finishedtask = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                if (Member.getMember(player) != null) {
                    member.clear(world);
                    player.teleport(new Location(player.getWorld(), -0.5, 100, member.PlayerCoord + 0.5, 270, 0));
                    member.waiting = false;
                    Inventory inventory = player.getInventory();
                    inventory.setItem(5, ItemStackList.Length[lang]);
                    inventory.setItem(6, ItemStackList.Height[lang]);
                }
            }, 40);
        } else {
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                member.clear(world);
                member.finishedOnce = true;
                int[] center = member.Center;
                int[] Settings = member.LengthSettings;
                member.hits.clear();
                if (Settings[1] + center[1] + 99 > 255) {
                    player.sendMessage("Height Limit reached!");
                    return;
                }
                for (int X = -2; X <= 2; X++) {
                    for (int Y = 97; Y <= 99; Y++) {
                        for (int Z = -2; Z <= 2; Z++) {
                            Main.setTypeCheck(world.getBlockAt(5 + Settings[0] + center[0] + X, Settings[1] + center[1] + Y, member.PlayerCoord + Z), world.getBlockAt(-500 + X, Y, Z).getType(), 6);
//                            world.getBlockAt(5 + Settings[0] + center[0] + X, Settings[1] + center[1] + Y, member.PlayerCoord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                        }
                    }
                }
                for (int X = -2; X <= 2; X++) {
                    for (int Y = 100; Y >= 97; Y--) {
                        for (int Z = -2; Z <= 2; Z++) {
                            Main.setTypeCheck(world.getBlockAt(-5 - Settings[0] + center[0] + X, -Settings[1] + center[1] + Y, member.PlayerCoord + Z), Material.AIR, 7);
//                            world.getBlockAt(-5 - Settings[0] + center[0] + X, -Settings[1] + center[1] + Y, member.PlayerCoord + Z).setType(Material.AIR);
                        }
                    }
                }
                AddBB(5 + Settings[0] + center[0], Settings[1] + center[1], member.PlayerCoord, member);
                member.Center = new int[]{5 + Settings[0] + center[0], Settings[1] + center[1]};
            });
        }
    }

    public static void Join(Player player) {
        if (used == size) {
            player.sendMessage("There is no enough servers. please report this to speedcubing.");
            player.closeInventory();
            return;
        }
        int room = -1;
        for (int h = 0; h < size; h++) {
            if (rooms[h]) {
                room = h;
                break;
            }
        }
        rooms[room] = false;
        used += 1;
        Member member = Member.getMember(player);
        member.currentMode = null;
        int coord = 250 * (room * (int) Math.pow(-1, room + 1) + room % 2);
        World world = Bukkit.getWorld("Custom");
        for (int x = -2; x <= 2; x++) {
            for (int y = 97; y <= 99; y++) {
                for (int z = -2; z <= 2; z++) {
                    Main.setTypeCheck(world.getBlockAt(x, y, coord + z), world.getBlockAt(-500 + x, y, z).getType(), 8);
//                    world.getBlockAt(x, y, coord + z).setType(world.getBlockAt(-500 + x, y, z).getType());
                }
            }
        }
        int lang = member.user.lang;
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(0, ItemStackList.Blocks[member.blockskin][lang]);
        inventory.setItem(1, ItemStackList.pickaxe[lang]);
        inventory.setItem(7, ItemStackList.Settings[lang]);
        inventory.setItem(8, ItemStackList.navigator[lang]);
        inventory.setItem(5, ItemStackList.Length[lang]);
        inventory.setItem(6, ItemStackList.Height[lang]);
        inventory.setHeldItemSlot(0);
        member.times = -1;
        member.Blockplaced = 0;
        member.hits.clear();
        String[] sidebar = StringList.CustomSideBar[lang];
        new Thread(() -> {
            int[] Settings = ArrayUtils.toPrimitive(member.user.dbSelect("customfb_x,customfb_y").getIntArray());
            member.LengthSettings = Settings;
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                new SideBar(player, sidebar[0])
                        .staticLine(sidebar[1], 10)
                        .staticLine(sidebar[2], 9)
                        .staticLine(sidebar[3], 8)
                        .changeableLine(sidebar[4] + Settings[0], 7)
                        .staticLine(sidebar[5], 6)
                        .staticLine(sidebar[6], 5)
                        .changeableLine(sidebar[7] + Settings[1], 4)
                        .staticLine(sidebar[8], 3)
                        .staticLine(sidebar[9], 2)
                        .changeableLine(sidebar[10] + "§7-,---", 1)
                        .staticLine(sidebar[11], 0).create();
                member.Center = new int[]{5 + Settings[0], Settings[1]};
                for (int X = -2; X <= 2; X++) {
                    for (int Y = 97; Y <= 99; Y++) {
                        for (int Z = -2; Z <= 2; Z++) {
                            Main.setTypeCheck(world.getBlockAt(5 + Settings[0] + X, Settings[1] + Y, coord + Z), world.getBlockAt(-500 + X, Y, Z).getType(), 9);
//                            world.getBlockAt(5 + Settings[0] + X, Settings[1] + Y, coord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                        }
                    }
                }
                player.teleport(new Location(world, -0.5, 100, coord + 0.5, 270, 0));
                member.PlayerCoord = coord;
                AddBB(5 + Settings[0], Settings[1], coord, member);
            });
        }).start();
    }


    public static void Quit(Player player) {
        Member member = Member.getMember(player);
        if (member.MainTimer != -1)
            Bukkit.getScheduler().cancelTask(member.MainTimer);
        World world = Bukkit.getWorld("Custom");
        member.clear(world);
        int[] center = member.Center;
        int[] Settings = member.LengthSettings;
        boolean f = member.finishedOnce;
        for (int X = -2; X <= 2; X++) {
            for (int Y = 100; Y >= 97; Y--) {
                for (int Z = -2; Z <= 2; Z++) {
                    if (f) {
                        Main.setTypeCheck(world.getBlockAt(-5 - Settings[0] + center[0] + X, -Settings[1] + center[1] + Y, member.PlayerCoord + Z), Material.AIR, 10);
//                        world.getBlockAt(-5 - Settings[0] + center[0] + X, -Settings[1] + center[1] + Y, member.PlayerCoord + Z).setType(Material.AIR);
                    }
                    Main.setTypeCheck(world.getBlockAt(center[0] + X, center[1] + Y, member.PlayerCoord + Z), Material.AIR, 11);
//                    world.getBlockAt(center[0] + X, center[1] + Y, member.PlayerCoord + Z).setType(Material.AIR);
                }
            }
        }
        if (!f)
            for (int X = -2; X <= 2; X++) {
                for (int Y = 97; Y <= 99; Y++) {
                    for (int Z = -2; Z <= 2; Z++) {
                        Main.setTypeCheck(world.getBlockAt(X, Y, member.PlayerCoord + Z), Material.AIR, 12);
//                        world.getBlockAt(X, Y, member.PlayerCoord + Z).setType(Material.AIR);
                    }
                }
            }
        member.times = -1;
        member.waiting = false;
        new Thread(() -> {
            int[] i = member.LengthSettings;
            member.user.dbUpdate("customfb_x=" + i[0] + ",customfb_y=" + i[1]);
        }).start();
        used -= 1;
        rooms[coordToRoom(member.PlayerCoord)] = true;
    }

    public static void validPlaceBlock(Player player, Block b) {
        Member member = Member.getMember(player);
        int lang = member.user.lang;
        Inventory inventory = player.getInventory();
        if (member.times == -1) {
            member.times = 0;
            member.MainTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                member.times += 1;
                SideBar.getSidebar(player).setLineByScore(StringList.CustomSideBar[lang][10] + FastBuilderUtils.tickToTime(member.times), 1);
            }, 0, 1);
            inventory.setItem(5, new ItemStack(Material.AIR));
            inventory.setItem(6, new ItemStack(Material.AIR));
        }
        member.Blocks.add(b);
        member.Blockplaced += 1;
        if (member.Blockplaced > 32) {
            member.Blockplaced = 0;
            inventory.setItem(0, ItemStackList.Blocks[member.blockskin][lang]);
        }
    }

    public static void Fell(Player player) {
        Member member = Member.getMember(player);
        World world = player.getWorld();
        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> member.clear(world));
        Inventory inventory = player.getInventory();
        inventory.setItem(5, ItemStackList.Length[member.user.lang]);
        inventory.setItem(6, ItemStackList.Height[member.user.lang]);
        if (member.waiting) {
            Bukkit.getScheduler().cancelTask(member.finishedtask);
            member.waiting = false;
        } else {
            Bukkit.getScheduler().cancelTask(member.MainTimer);
            member.times = -1;
            if (member.finishedOnce) {
                int[] center = member.Center;
                int[] Settings = member.LengthSettings;
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                    for (int X = -2; X <= 2; X++) {
                        for (int Y = 100; Y >= 97; Y--) {
                            for (int Z = -2; Z <= 2; Z++) {
                                if (Y != 100) {
                                    Main.setTypeCheck(world.getBlockAt(X, Y, member.PlayerCoord + Z), world.getBlockAt(-500 + X, Y, Z).getType(), 13);
//                                    world.getBlockAt(X, Y, member.PlayerCoord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                                }
                                Main.setTypeCheck(world.getBlockAt(center[0] + X - 5 - Settings[0], center[1] + Y - Settings[1], member.PlayerCoord + Z), Material.AIR, 14);
                                Main.setTypeCheck(world.getBlockAt(center[0] + X, center[1] + Y, member.PlayerCoord + Z), Material.AIR, 15);
//                                world.getBlockAt(center[0] + X - 5 - Settings[0], center[1] + Y - Settings[1], member.PlayerCoord + Z).setType(Material.AIR);
//                                world.getBlockAt(center[0] + X, center[1] + Y, member.PlayerCoord + Z).setType(Material.AIR);
                            }
                        }
                    }
                    member.finishedOnce = false;
                    if (!member.hits.isEmpty()) {
                        member.hits.clear();
                        for (int X = -2; X <= 2; X++) {
                            for (int Y = 97; Y <= 100; Y++) {
                                for (int Z = -2; Z <= 2; Z++) {
                                    Main.setTypeCheck(world.getBlockAt(5 + Settings[0] + X, Settings[1] + Y, member.PlayerCoord + Z), world.getBlockAt(-500 + X, Y, Z).getType(), 16);
//                                    world.getBlockAt(5 + Settings[0] + X, Settings[1] + Y, member.PlayerCoord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                                }
                            }
                        }
                        AddBB(5 + Settings[0], Settings[1], member.PlayerCoord, member);
                        member.Center = new int[]{5 + Settings[0], Settings[1]};
                    }
                });
            }
        }
        player.closeInventory();
        player.getInventory().setHeldItemSlot(0);
        player.teleport(new Location(player.getWorld(), -0.5, 100, member.PlayerCoord + 0.5, 270, 0));
    }

    public static int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 250) - (Math.abs(z / 500) + z / 500) / (z / 250);
    }

    public static void AddBB(int cX, int cY, int cZ, Member member) {
        Set<AxisAlignedBB> a = member.hits;
        cY += 100;
        int[][] x = {
                {cX, cY, cZ},
                {cX + 1, cY, cZ},
                {cX - 1, cY, cZ},
                {cX, cY, cZ + 1},
                {cX, cY, cZ - 1},
                {cX + 1, cY, cZ + 1},
                {cX - 1, cY, cZ - 1},
                {cX + 1, cY, cZ - 1},
                {cX - 1, cY, cZ + 1},
                {cX + 2, cY, cZ},
                {cX - 2, cY, cZ},
                {cX, cY, cZ + 2},
                {cX, cY, cZ - 2},
                {cX + 2, cY, cZ + 1},
                {cX + 2, cY, cZ - 1},
                {cX - 2, cY, cZ + 1},
                {cX - 2, cY, cZ - 1},
                {cX + 1, cY, cZ + 2},
                {cX - 1, cY, cZ + 2},
                {cX + 1, cY, cZ - 2},
                {cX - 1, cY, cZ - 2},
        };
        for (int[] b : x) {
            a.add(new AxisAlignedBB((float) b[0] + 0.125F, b[1], (float) b[2] + 0.125F, (float) (b[0] + 1) - 0.125F, (double) b[1] + 0.25D, (float) (b[2] + 1) - 0.125F));
        }
    }
}