package speedcubing.Core;

import cubing.lib.bukkit.PlayerUtils;
import cubing.lib.bukkit.SideBarAPI;
import cubingserver.libs.User;
import cubingserver.speedcubingServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
                    String[] xx = SideBarAPI.prefixSuffixSplit(User.getFormat(User.getRank(ses))[1] + Bukkit.getPlayer(ses).getName() + "§8: §e" + Core.tickToTime(sc));
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
        Core.updateSidebar(player.getName(), uuid, session, score, Bukkit.getWorld("Inclinedshort"),true);
        used -= 1;
        rooms[coordToRoom(Core.PlayerCoord.get(uuid))] = true;
    }

    public static void Fell(Player player, boolean fell) {
        UUID uuid = player.getUniqueId();
        if (Core.waiting.contains(uuid)) {
            Bukkit.getScheduler().cancelTask(Core.finishedtask.get(uuid));
            Core.clear(uuid, player.getWorld());
            Core.Blocks.get(uuid).clear();
            Core.waiting.remove(uuid);
            Inventory inventory = player.getInventory();
            ItemStack stack = ItemStackList.Blocks[Core.integerCache.get(uuid)[0]][User.getLang(uuid)];
            inventory.setItem(0, stack);
            inventory.setItem(1, stack);
        } else {
            if (Core.times.get(uuid) != -1) {
                Bukkit.getScheduler().cancelTask(Core.MainTimer.get(uuid));
                Core.clear(uuid, player.getWorld());
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
