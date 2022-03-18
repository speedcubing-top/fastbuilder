package speedcubing.Core;

import cubing.spigot.lib.bukkit.PlayerUtils;
import cubing.spigot.lib.bukkit.api.SideBarAPI;
import cubingserver.PluginMessage;
import cubingserver.libs.User;

import cubingserver.speedcubingServer;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Main;

import java.util.*;

public class InclinedShort {
    public static final int size = 31;
    public static int used = 0;
    public static boolean[] rooms = new boolean[size];
    public static List<UUID> session = new ArrayList<>();
    public static Map<UUID, Integer> score = new HashMap<>();

    public void Load() {
        for (int i = 0; i < size; i++) {
            rooms[i] = true;
        }
    }

    public static void Finish(Player player) {
        World world = Bukkit.getWorld("Inclinedshort");
        UUID uuid = player.getUniqueId();
        int time = Core.times.get(uuid);
        if (time < 40) {
            PluginMessage.switchServer(player, "Lobby");
            return;
        }
        Core.waiting.add(uuid);
        Bukkit.getScheduler().cancelTask(Core.MainTimer.get(uuid));
        int lang = User.getLang(uuid);
        int msrecord = time * 5;
        String display = Core.tickToTime(time);
        long timeMillis = System.currentTimeMillis() / 1000L;
        new Thread(() -> {
            int last = speedcubingServer.connection.selectInt("playersdata", "fb_4_pb", "uuid='" + uuid + "'");
            if (last == 0 || last > msrecord) {
                speedcubingServer.connection.insert("fastbuilder", "uuid,at,score,mode", "'" + uuid + "'," + timeMillis + "," + msrecord + ",4");
                speedcubingServer.connection.update("playersdata", "fb_4_pb=" + msrecord, "uuid='" + uuid + "'");
                player.getScoreboard().getTeam(StringList.SideBar[lang][9]).setSuffix(display);
            }
        }).start();
        Core.times.put(uuid, -1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutTitle(0, 60, 5));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + StringList.timedisplay[lang].replace("%time%", display) + "\"}")));
        if (score.get(uuid) > time || score.get(uuid) == 0) {
            int i;
            for (i = 0; i < session.size(); i++) {
                if (score.get(session.get(i)) > time || score.get(session.get(i)) == 0) {
                    session.remove(uuid);
                    session.add(i, uuid);
                    break;
                }
            }
            score.put(uuid, time);
            if (i < 4) {
                List<String> prefix = new ArrayList<>();
                List<String> suffix = new ArrayList<>();
                int size = session.size();
                for (int j = i; j < 3; j++) {
                    if (size <= j) {
                        prefix.add("§7-,---");
                        suffix.add("");
                    } else {
                        UUID ses = session.get(j);
                        int sc = score.get(ses);
                        if (sc == 0) {
                            prefix.add("§7-,---");
                            suffix.add("");
                        } else {
                            String[] xx = SideBarAPI.prefixSuffixSplit(User.getFormat(User.getRank(ses))[0] + Bukkit.getPlayer(ses).getName() + "§8: §e" + Core.tickToTime(sc));
                            prefix.add(xx[0]);
                            suffix.add(xx[1]);
                        }
                    }
                }
                for (int j = i; j < 3; j++) {
                    for (Player p : world.getPlayers()) {
                        Scoreboard scoreboard = p.getScoreboard();
                        Team t = scoreboard.getTeam("§" + (j + 4) + "§f");
                        t.setPrefix(prefix.get(j - i));
                        t.setSuffix(suffix.get(j - i));
                    }
                }
            }
        }
        Core.finishedtask.put(uuid, Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (Core.PlayerCoord.containsKey(uuid)) {
                int fixed = Core.PlayerCoord.get(uuid);
                for (BlockPosition b : Core.Blocks.get(uuid)) {
                    world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
                }
                PlayerUtils.teleport(connection, fixed + 0.5, 100, fixed + 0.5, 225, 0);
                Core.Blocks.get(uuid).clear();
                Core.waiting.remove(uuid);
                Inventory inventory = player.getInventory();
                ItemStack stack = ItemStackList.Blocks[Core.integerCache.get(uuid)[0]][User.getLang(uuid)];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
            }
        }, 40));
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
        int coord = roomToCoord(room);
        player.teleport(new Location(Bukkit.getWorld("Inclinedshort"), coord + 0.5, 100, coord + 0.5, 225, 0));
        Core.PlayerCoord.put(uuid, coord);
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        int lang = User.getLang(uuid);
        String[] sidebar = StringList.SideBar[lang];
        objective.setDisplayName(sidebar[13]);
        int a = speedcubingServer.connection.selectInt("playersdata", "fb_4_pb", "uuid='" + uuid + "'");
        SideBarAPI.sidebarTeams(scoreboard, new String[][]{
                {sidebar[12]},
                {sidebar[11]},
                {sidebar[10]},
                {"", sidebar[9], a == 0 ? "§7-,---" : Core.msToTime(a)},
                {sidebar[8]},
                {sidebar[7]},
                {"", sidebar[6], ""},
                {"", sidebar[5], ""},
                {"", sidebar[4], ""},
                {sidebar[3]},
                {sidebar[2]},
                {"", sidebar[1], "§7-,---"},
                {sidebar[0]},
        }, new int[]{12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0});
        int size = session.size();
        for (int j = 0; j < 3; j++) {
            String prefix, suffix;
            if (size <= j) {
                prefix = "§7-,---";
                suffix = "";
            } else {
                UUID ses = session.get(j);
                int sc = score.get(ses);
                if (sc == 0) {
                    prefix = "§7-,---";
                    suffix = "";
                } else {
                    String[] xx = SideBarAPI.prefixSuffixSplit(User.getFormat(User.getRank(ses))[0] + Bukkit.getPlayer(ses).getName() + "§8: §e" + Core.tickToTime(sc));
                    prefix = xx[0];
                    suffix = xx[1];
                }
            }
            Team t = scoreboard.getTeam("§" + (j + 4) + "§f");
            t.setPrefix(prefix);
            t.setSuffix(suffix);
        }
        player.setScoreboard(scoreboard);
        PlayerInventory inventory = player.getInventory();
        ItemStack stack = ItemStackList.Blocks[Core.integerCache.get(uuid)[0]][lang];
        inventory.clear();
        inventory.setItem(0, stack);
        inventory.setItem(1, stack);
        inventory.setItem(2, ItemStackList.pickaxe[lang]);
        inventory.setItem(7, ItemStackList.Settings[lang]);
        inventory.setItem(8, ItemStackList.navigator[lang]);
        inventory.setHeldItemSlot(0);
        session.add(uuid);
        Core.times.put(uuid, -1);
        score.put(uuid, 0);
    }

    public static void Quit(Player player) {
        UUID uuid = player.getUniqueId();
        if (Core.MainTimer.containsKey(uuid))
            Bukkit.getScheduler().cancelTask(Core.MainTimer.get(uuid));
        World world = Bukkit.getWorld("Inclinedshort");
        for (BlockPosition b : Core.Blocks.get(uuid)) {
            world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
        }
        Core.Blocks.get(uuid).clear();
        Core.times.put(uuid, -1);
        Core.waiting.remove(uuid);
        int x = session.indexOf(uuid);
        session.remove(uuid);
        score.remove(uuid);
        if (x != -1 && x < 3) {
            List<String> prefix = new ArrayList<>();
            List<String> suffix = new ArrayList<>();
            int size = session.size();
            for (int j = x; j < 3; j++) {
                if (size <= j) {
                    prefix.add("§7-,---");
                    suffix.add("");
                } else {
                    UUID ses = session.get(j);
                    int sc = score.get(ses);
                    if (sc == 0) {
                        prefix.add("§7-,---");
                        suffix.add("");
                    } else {
                        String[] xx = SideBarAPI.prefixSuffixSplit(User.getFormat(User.getRank(ses))[0] + Bukkit.getPlayer(ses).getName() + "§8: §e" + Core.tickToTime(sc));
                        prefix.add(xx[0]);
                        suffix.add(xx[1]);
                    }
                }
            }
            for (Player p : world.getPlayers()) {
                if (!p.getName().equals(player.getName())) {
                    Scoreboard scoreboard = p.getScoreboard();
                    for (int j = x; j < 3; j++) {
                        Team t = scoreboard.getTeam("§" + (j + 4) + "§f");
                        t.setPrefix(prefix.get(j - x));
                        t.setSuffix(suffix.get(j - x));
                    }
                }
            }
        }
        used -= 1;
        rooms[coordToRoom(Core.PlayerCoord.get(uuid))] = true;
    }

    public static void validPlaceBlock(Player player, BlockPosition b) {
        UUID uuid = player.getUniqueId();
        int lang = User.getLang(uuid);
        if (Core.times.get(uuid) == -1) {
            Core.times.put(uuid, 0);
            Core.MainTimer.put(uuid, Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                int up = Core.times.get(uuid) + 1;
                Core.times.put(uuid, up);
                player.getScoreboard().getTeam(StringList.SideBar[lang][1]).setSuffix(Core.tickToTime(up));
                if (up >= 2400) {
                    Fell(player, false);
                    player.sendMessage(StringList.cancelled[lang]);
                }
            }, 0, 1));
        }
        Core.Blocks.get(uuid).add(b);
        Inventory inventory = player.getInventory();
        int empty = 1 - player.getInventory().getHeldItemSlot();
        if (inventory.getItem(empty) == null)
            inventory.setItem(empty, ItemStackList.Blocks[Core.integerCache.get(uuid)[0]][lang]);
    }

    public static void Fell(Player player, boolean fell) {
        UUID uuid = player.getUniqueId();
        if (Core.waiting.contains(uuid)) {
            Bukkit.getScheduler().cancelTask(Core.finishedtask.get(uuid));
            World world = player.getWorld();
            for (BlockPosition b : Core.Blocks.get(uuid)) {
                world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
            }
            Core.Blocks.get(uuid).clear();
            Core.waiting.remove(uuid);
            Inventory inventory = player.getInventory();
            ItemStack stack = ItemStackList.Blocks[Core.integerCache.get(uuid)[0]][User.getLang(uuid)];
            inventory.setItem(0, stack);
            inventory.setItem(1, stack);
        } else {
            if (Core.times.get(uuid) != -1) {
                Bukkit.getScheduler().cancelTask(Core.MainTimer.get(uuid));
                org.bukkit.World world = player.getWorld();
                for (BlockPosition b : Core.Blocks.get(uuid)) {
                    world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
                }
                Core.Blocks.get(uuid).clear();
                Inventory inventory = player.getInventory();
                ItemStack stack = ItemStackList.Blocks[Core.integerCache.get(uuid)[0]][User.getLang(uuid)];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
                Core.times.put(uuid, -1);
            } else if (!fell) {
                Location location = player.getLocation();
                int coord = Core.PlayerCoord.get(uuid);
                int oldroom = coordToRoom(coord);
                int newroom = Core.cal(oldroom, location.getX() + location.getZ() > coord * 2, rooms);
                if (newroom == -1) player.sendMessage(StringList.nomapsavaliable[User.getLang(uuid)]);
                else {
                    Core.PlayerCoord.put(uuid, roomToCoord(newroom));
                    rooms[oldroom] = true;
                    rooms[newroom] = false;
                }
            }
            player.closeInventory();
        }
        int fixed = Core.PlayerCoord.get(uuid);
        PlayerUtils.teleport(((CraftPlayer) player).getHandle().playerConnection, fixed + 0.5, 100, fixed + 0.5, 225, 0);
    }

    public static int roomToCoord(int room) {
        return 4 * (room * (int) Math.pow(-1, room + 1) + room % 2);
    }

    public static int coordToRoom(int z) {
        return z == 0 ? 0 : Math.abs(z / 4) - (Math.abs(z / 8) + z / 8) / (z / 4);
    }
}
