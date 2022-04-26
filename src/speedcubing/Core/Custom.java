package speedcubing.Core;


import cubing.lib.bukkit.PlayerUtils;
import cubing.lib.bukkit.SideBarAPI;
import cubingserver.PluginMessage;
import cubingserver.libs.User;
import cubingserver.speedcubingServer;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Main;

import java.util.*;

public class Custom {
    public static final int size = 31;
    public static int used = 0;
    public static boolean[] rooms = new boolean[size];

    public static Map<UUID, Integer[]> LengthSettings = new HashMap<>();
    public static Map<UUID, Integer[]> Center = new HashMap<>();
    public static Map<UUID, Integer> Blockplaced = new HashMap<>();
    public static Set<UUID> finishedOnce = new HashSet<>();
    public static Map<UUID, Set<AxisAlignedBB>> hits = new HashMap<>();

    public void Load() {
        for (int i = 0; i < size; i++) {
            rooms[i] = true;
        }
    }

    public static void Finish(Player player, boolean pressure) {
        UUID uuid = player.getUniqueId();
        if (Core.times.get(uuid) == -1) {
            PluginMessage.switchServer(player, "Lobby");
            return;
        }
        Bukkit.getScheduler().cancelTask(Core.MainTimer.get(uuid));
        World world = Bukkit.getWorld("Custom");
        int lang = User.getLang(uuid);
        player.getScoreboard().getTeam(StringList.CustomSideBar[lang][1]).setSuffix(Core.tickToTime(Core.times.get(uuid)));

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutTitle(0, 30, 5));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(StringList.timedisplay[lang].replace("%time%", Core.tickToTime(Core.times.get(uuid))))));

        Core.times.put(uuid, -1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        if (pressure) {
            Core.waiting.add(uuid);
            Core.finishedtask.put(uuid, Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                if (Core.PlayerCoord.containsKey(uuid)) {
                    Core.clear(uuid,world);
                    Core.Blocks.get(uuid).clear();
                    PlayerUtils.teleport(connection, -0.5, 100, Core.PlayerCoord.get(uuid) + 0.5, 270, 0);
                    Core.waiting.remove(uuid);
                    Inventory inventory = player.getInventory();
                    inventory.setItem(5, ItemStackList.Length[lang]);
                    inventory.setItem(6, ItemStackList.Height[lang]);
                }
            }, 40));
        } else {
            Core.clear(uuid,world);
            Core.Blocks.get(uuid).clear();
            finishedOnce.add(uuid);
            Integer[] center = Center.get(uuid);
            Integer[] Settings = LengthSettings.get(uuid);
            int coord = Core.PlayerCoord.get(uuid);

            hits.get(uuid).clear();
            if (Settings[1] + center[1] + 99 > 255) {
                Bukkit.getPlayer(uuid).sendMessage("Height Limit reached!");
                return;
            }
            for (int X = -2; X <= 2; X++) {
                for (int Y = 97; Y <= 99; Y++) {
                    for (int Z = -2; Z <= 2; Z++) {
                        world.getBlockAt(5 + Settings[0] + center[0] + X, Settings[1] + center[1] + Y, coord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                    }
                }
            }
            for (int X = -2; X <= 2; X++) {
                for (int Y = 100; Y >= 97; Y--) {
                    for (int Z = -2; Z <= 2; Z++) {
                        world.getBlockAt(-5 - Settings[0] + center[0] + X, -Settings[1] + center[1] + Y, coord + Z).setType(Material.AIR);
                    }
                }
            }
            AddBB(5 + Settings[0] + center[0], Settings[1] + center[1], coord, uuid);
            Center.put(uuid, new Integer[]{5 + Settings[0] + center[0], Settings[1] + center[1]});
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
        UUID uuid = player.getUniqueId();
        int coord = 250 * (room * (int) Math.pow(-1, room + 1) + room % 2);
        World world = Bukkit.getWorld("Custom");
        for (int x = -2; x <= 2; x++) {
            for (int y = 97; y <= 99; y++) {
                for (int z = -2; z <= 2; z++) {
                    world.getBlockAt(x, y, coord + z).setType(world.getBlockAt(-500 + x, y, z).getType());
                }
            }
        }
        int lang = User.getLang(uuid);
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setItem(0, ItemStackList.Blocks[Core.integerCache.get(uuid)[0]][lang]);
        inventory.setItem(1, ItemStackList.pickaxe[lang]);
        inventory.setItem(7, ItemStackList.Settings[lang]);
        inventory.setItem(8, ItemStackList.navigator[lang]);
        inventory.setItem(5, ItemStackList.Length[lang]);
        inventory.setItem(6, ItemStackList.Height[lang]);
        inventory.setHeldItemSlot(0);
        Core.times.put(uuid, -1);
        Blockplaced.put(uuid, 0);
        hits.put(uuid, new HashSet<>());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        String[] sidebar = StringList.CustomSideBar[lang];
        objective.setDisplayName(sidebar[11]);
        new Thread(() -> {
            Integer[] Settings = ArrayUtils.toObject(speedcubingServer.connection.selectInts("playersdata", "customfb_x,customfb_y", "uuid='" + uuid + "'"));
            LengthSettings.put(uuid, Settings);
            SideBarAPI.sidebarTeams(scoreboard, new String[][]{
                    {sidebar[10]},
                    {sidebar[9]},
                    {sidebar[8]},
                    {"", sidebar[7], Integer.toString(Settings[0])},
                    {sidebar[6]},
                    {sidebar[5]},
                    {"", sidebar[4], Integer.toString(Settings[1])},
                    {sidebar[3]},
                    {sidebar[2]},
                    {"", sidebar[1], "§7-,---"},
                    {sidebar[0]},
            }, new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0});
            player.setScoreboard(scoreboard);
            Center.put(uuid, new Integer[]{5 + Settings[0], Settings[1]});
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                for (int X = -2; X <= 2; X++) {
                    for (int Y = 97; Y <= 99; Y++) {
                        for (int Z = -2; Z <= 2; Z++) {
                            world.getBlockAt(5 + Settings[0] + X, Settings[1] + Y, coord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                        }
                    }
                }
                player.teleport(new Location(world, -0.5, 100, coord + 0.5, 270, 0));
                Core.PlayerCoord.put(uuid, coord);
                AddBB(5 + Settings[0], Settings[1], coord, uuid);
            });
        }).start();
    }


    public static void Quit(Player player) {
        UUID uuid = player.getUniqueId();
        if (Core.MainTimer.containsKey(uuid))
            Bukkit.getScheduler().cancelTask(Core.MainTimer.get(uuid));
        World world = Bukkit.getWorld("Custom");
        Core.clear(uuid,world);
        int coord = Core.PlayerCoord.get(uuid);
        Integer[] center = Center.get(uuid);
        Integer[] Settings = LengthSettings.get(uuid);
        boolean f = finishedOnce.contains(uuid);
        for (int X = -2; X <= 2; X++) {
            for (int Y = 100; Y >= 97; Y--) {
                for (int Z = -2; Z <= 2; Z++) {
                    if (f)
                        world.getBlockAt(-5 - Settings[0] + center[0] + X, -Settings[1] + center[1] + Y, coord + Z).setType(Material.AIR);
                    world.getBlockAt(center[0] + X, center[1] + Y, coord + Z).setType(Material.AIR);
                }
            }
        }
        if (!f)
            for (int X = -2; X <= 2; X++) {
                for (int Y = 97; Y <= 99; Y++) {
                    for (int Z = -2; Z <= 2; Z++) {
                        world.getBlockAt(X, Y, coord + Z).setType(Material.AIR);
                    }
                }
            }
        Core.Blocks.get(uuid).clear();
        Core.times.put(uuid, -1);
        Core.waiting.remove(uuid);
        new Thread(() -> {
            Integer[] i = LengthSettings.get(uuid);
            speedcubingServer.connection.update("playersdata", "customfb_x=" + i[0] + ",customfb_y=" + i[1], "uuid='" + uuid + "'");
            LengthSettings.remove(uuid);
        }).start();
        Center.remove(uuid);
        Blockplaced.remove(uuid);
        finishedOnce.remove(uuid);
        hits.remove(uuid);
        used -= 1;
        rooms[coordToRoom(Core.PlayerCoord.get(uuid))] = true;
    }

    public static void validPlaceBlock(Player player, BlockPosition b) {
        UUID uuid = player.getUniqueId();
        int lang = User.getLang(uuid);
        Inventory inventory = player.getInventory();
        if (Core.times.get(uuid) == -1) {
            Core.times.put(uuid, 0);
            Core.MainTimer.put(uuid, Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                int up = Core.times.get(uuid) + 1;
                Core.times.put(uuid, up);
                player.getScoreboard().getTeam(StringList.CustomSideBar[lang][1]).setSuffix(Core.tickToTime(up));
            }, 0, 1));
            inventory.setItem(5, new ItemStack(Material.AIR));
            inventory.setItem(6, new ItemStack(Material.AIR));
        }
        Core.Blocks.get(uuid).add(b);
        Blockplaced.put(uuid, Blockplaced.get(uuid) + 1);
        if (Blockplaced.get(uuid) > 32) {
            Blockplaced.put(uuid, 0);
            inventory.setItem(0, ItemStackList.Blocks[Core.integerCache.get(uuid)[0]][lang]);
        }
    }

    public static void Fell(Player player) {
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();
        Core.clear(uuid,world);
        Core.Blocks.get(uuid).clear();
        int coord = Core.PlayerCoord.get(uuid);
        Inventory inventory = player.getInventory();
        int lang = User.getLang(uuid);
        inventory.setItem(5, ItemStackList.Length[lang]);
        inventory.setItem(6, ItemStackList.Height[lang]);
        if (Core.waiting.contains(uuid)) {
            Bukkit.getScheduler().cancelTask(Core.finishedtask.get(uuid));
            Core.waiting.remove(uuid);
        } else {
            if (Core.MainTimer.containsKey(uuid))
                Bukkit.getScheduler().cancelTask(Core.MainTimer.get(uuid));
            Core.times.put(uuid, -1);
            if (finishedOnce.contains(uuid)) {

                Integer[] center = Center.get(uuid);
                Integer[] Settings = LengthSettings.get(uuid);
                for (int X = -2; X <= 2; X++) {
                    for (int Y = 100; Y >= 97; Y--) {
                        for (int Z = -2; Z <= 2; Z++) {
                            if (Y != 100)
                                world.getBlockAt(X, Y, coord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                            world.getBlockAt(center[0] + X - 5 - Settings[0], center[1] + Y - Settings[1], coord + Z).setType(Material.AIR);
                            world.getBlockAt(center[0] + X, center[1] + Y, coord + Z).setType(Material.AIR);
                        }
                    }
                }
                finishedOnce.remove(uuid);
                if (!hits.get(uuid).isEmpty()) {
                    hits.get(uuid).clear();
                    for (int X = -2; X <= 2; X++) {
                        for (int Y = 97; Y <= 100; Y++) {
                            for (int Z = -2; Z <= 2; Z++) {
                                world.getBlockAt(5 + Settings[0] + X, Settings[1] + Y, coord + Z).setType(world.getBlockAt(-500 + X, Y, Z).getType());
                            }
                        }
                    }
                    AddBB(5 + Settings[0], Settings[1], coord, uuid);
                    Center.put(uuid, new Integer[]{5 + Settings[0], Settings[1]});
                }

            }
        }
        player.closeInventory();
        player.getInventory().setHeldItemSlot(0);
        PlayerUtils.teleport(((CraftPlayer) player).getHandle().playerConnection, -0.5, 100, coord + 0.5, 270, 0);
    }

    public static int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 250) - (Math.abs(z / 500) + z / 500) / (z / 250);
    }

    public static void AddBB(int cX, int cY, int cZ, UUID uuid) {
        Set<AxisAlignedBB> a = hits.get(uuid);
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