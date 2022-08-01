package speedcubing.Core;


import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Main;
import speedcubing.Member;
import speedcubing.lib.bukkit.BungeePluginMessage;
import speedcubing.lib.bukkit.SideBar;
import speedcubing.lib.utils.SQL.SQLUtils;
import speedcubing.server.speedcubingServer;

import java.util.HashSet;
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
        int lang = member.lang;
        SideBar.getSidebar(player).setLine(StringList.CustomSideBar[lang][10] + Core.tickToTime(member.times), 10);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutTitle(0, 30, 5));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(StringList.timedisplay[lang].replace("%time%", Core.tickToTime(member.times)))));
        member.times = -1;
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
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
                        world.getBlockAt(5 + Settings[0] + center[0] + X, Settings[1] + center[1] + Y, member.PlayerCoord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                    }
                }
            }
            for (int X = -2; X <= 2; X++) {
                for (int Y = 100; Y >= 97; Y--) {
                    for (int Z = -2; Z <= 2; Z++) {
                        world.getBlockAt(-5 - Settings[0] + center[0] + X, -Settings[1] + center[1] + Y, member.PlayerCoord + Z).setType(Material.AIR);
                    }
                }
            }
            AddBB(5 + Settings[0] + center[0], Settings[1] + center[1], member.PlayerCoord, member);
            member.Center = new int[]{5 + Settings[0] + center[0], Settings[1] + center[1]};
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
        int coord = 250 * (room * (int) Math.pow(-1, room + 1) + room % 2);
        World world = Bukkit.getWorld("Custom");
        for (int x = -2; x <= 2; x++) {
            for (int y = 97; y <= 99; y++) {
                for (int z = -2; z <= 2; z++) {
                    world.getBlockAt(x, y, coord + z).setType(world.getBlockAt(-500 + x, y, z).getType());
                }
            }
        }
        int lang = member.lang;
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(0, ItemStackList.Blocks[member.integerCache[0]][lang]);
        inventory.setItem(1, ItemStackList.pickaxe[lang]);
        inventory.setItem(7, ItemStackList.Settings[lang]);
        inventory.setItem(8, ItemStackList.navigator[lang]);
        inventory.setItem(5, ItemStackList.Length[lang]);
        inventory.setItem(6, ItemStackList.Height[lang]);
        inventory.setHeldItemSlot(0);
        member.times = -1;
        member.Blockplaced = 0;
        member.hits = new HashSet<>();
        String[] sidebar = StringList.CustomSideBar[lang];
        new Thread(() -> {
            int[] Settings = ArrayUtils.toPrimitive(SQLUtils.getIntArray(speedcubingServer.connection.select("playersdata", "customfb_x,customfb_y", "id=" + member.id)));
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
                            world.getBlockAt(5 + Settings[0] + X, Settings[1] + Y, coord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
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
                    if (f)
                        world.getBlockAt(-5 - Settings[0] + center[0] + X, -Settings[1] + center[1] + Y, member.PlayerCoord + Z).setType(Material.AIR);
                    world.getBlockAt(center[0] + X, center[1] + Y, member.PlayerCoord + Z).setType(Material.AIR);
                }
            }
        }
        if (!f)
            for (int X = -2; X <= 2; X++) {
                for (int Y = 97; Y <= 99; Y++) {
                    for (int Z = -2; Z <= 2; Z++) {
                        world.getBlockAt(X, Y, member.PlayerCoord + Z).setType(Material.AIR);
                    }
                }
            }
        member.times = -1;
        member.waiting = false;
        new Thread(() -> {
            int[] i = member.LengthSettings;
            speedcubingServer.connection.update("playersdata", "customfb_x=" + i[0] + ",customfb_y=" + i[1], "id='" + member.id + "'");
        }).start();
        used -= 1;
        rooms[coordToRoom(member.PlayerCoord)] = true;
    }

    public static void validPlaceBlock(Player player, Block b) {
        Member member = Member.getMember(player);
        int lang = member.lang;
        Inventory inventory = player.getInventory();
        if (member.times == -1) {
            member.times = 0;
            member.MainTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                member.times += 1;
                SideBar.getSidebar(player).setLine(StringList.CustomSideBar[lang][10] + Core.tickToTime(member.times), 10);
            }, 0, 1);
            inventory.setItem(5, new ItemStack(Material.AIR));
            inventory.setItem(6, new ItemStack(Material.AIR));
        }
        member.Blocks.add(b);
        member.Blockplaced += 1;
        if (member.Blockplaced > 32) {
            member.Blockplaced = 0;
            inventory.setItem(0, ItemStackList.Blocks[member.integerCache[0]][lang]);
        }
    }

    public static void Fell(Player player) {
        Member member = Member.getMember(player);
        member.replay = null;
        World world = player.getWorld();
        member.clear(world);
        Inventory inventory = player.getInventory();
        inventory.setItem(5, ItemStackList.Length[member.lang]);
        inventory.setItem(6, ItemStackList.Height[member.lang]);
        if (member.waiting) {
            Bukkit.getScheduler().cancelTask(member.finishedtask);
            member.waiting = false;
        } else {
            Bukkit.getScheduler().cancelTask(member.MainTimer);
            member.times = -1;
            if (member.finishedOnce) {
                int[] center = member.Center;
                int[] Settings = member.LengthSettings;
                for (int X = -2; X <= 2; X++) {
                    for (int Y = 100; Y >= 97; Y--) {
                        for (int Z = -2; Z <= 2; Z++) {
                            if (Y != 100)
                                world.getBlockAt(X, Y, member.PlayerCoord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                            world.getBlockAt(center[0] + X - 5 - Settings[0], center[1] + Y - Settings[1], member.PlayerCoord + Z).setType(Material.AIR);
                            world.getBlockAt(center[0] + X, center[1] + Y, member.PlayerCoord + Z).setType(Material.AIR);
                        }
                    }
                }
                member.finishedOnce = false;
                if (!member.hits.isEmpty()) {
                    member.hits.clear();
                    for (int X = -2; X <= 2; X++) {
                        for (int Y = 97; Y <= 100; Y++) {
                            for (int Z = -2; Z <= 2; Z++) {
                                world.getBlockAt(5 + Settings[0] + X, Settings[1] + Y, member.PlayerCoord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                            }
                        }
                    }
                    AddBB(5 + Settings[0], Settings[1], member.PlayerCoord, member);
                    member.Center = new int[]{5 + Settings[0], Settings[1]};
                }

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