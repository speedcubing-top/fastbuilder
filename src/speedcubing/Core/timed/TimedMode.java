package speedcubing.Core.timed;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import speedcubing.Core.Core;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Member;
import speedcubing.lib.bukkit.BungeePluginMessage;
import speedcubing.lib.bukkit.PlayerUtils;
import speedcubing.lib.bukkit.SideBarAPI;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class TimedMode {
    public final int size = 31;
    public int used = 0;
    public boolean[] rooms = new boolean[size];
    public List<UUID> session = new ArrayList<>();
    public Map<UUID, Integer> score = new HashMap<>();
    public World world;
    public int sqlId;
    public int limit;
    public boolean inclined;

    public TimedMode(String world, int sqlId, int limit, boolean inclined) {
        for (int i = 0; i < size; i++) {
            rooms[i] = true;
        }
        this.inclined = inclined;
        this.limit = limit;
        this.sqlId = sqlId;
        this.world = Bukkit.getWorld(world);
    }

    public void Join(Player player, boolean kick) {
        if (used == size) {
            player.sendMessage("There is no enough servers. please report this to speedcubing.");
            if (kick) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        BungeePluginMessage.switchServer(player, "lobby");
                    }
                }, 500);
            } else
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
        Member member = Member.getMember(uuid);
        int coord = roomToCoord(room);
        player.teleport(new Location(world, (inclined ? coord : 0) + 0.5, 100, coord + 0.5, inclined ? 225 : 270, 0));
        member.PlayerCoord = coord;
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("sidebar", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        int lang = member.user.lang;
        String[] sidebar = StringList.SideBar[lang];
        objective.setDisplayName(sidebar[13]);
        int a = speedcubingServer.connection.selectInt("playersdata", "fb_" + sqlId + "_pb", "uuid='" + uuid + "'");
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
                    String[] xx = SideBarAPI.prefixSuffixSplit(speedcubingServer.getFormat(Member.getMember(ses).user.rank)[1] + Bukkit.getPlayer(ses).getName() + "§8: §e" + Core.tickToTime(sc));
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
        ItemStack stack = ItemStackList.Blocks[member.integerCache[0]][lang];
        inventory.clear();
        inventory.setItem(0, stack);
        inventory.setItem(1, stack);
        inventory.setItem(2, ItemStackList.pickaxe[lang]);
        inventory.setItem(7, ItemStackList.Settings[lang]);
        inventory.setItem(8, ItemStackList.navigator[lang]);
        inventory.setHeldItemSlot(0);
        session.add(uuid);
        member.times = -1;
        score.put(uuid, 0);
    }

    public void Fell(Player player, boolean fell) {
        Member member = Member.getMember(player.getUniqueId());
        if (member.waiting) {
            Bukkit.getScheduler().cancelTask(member.finishedtask);
            member.clear(player.getWorld());
            member.waiting = false;
            Inventory inventory = player.getInventory();
            ItemStack stack = ItemStackList.Blocks[member.integerCache[0]][member.user.lang];
            inventory.setItem(0, stack);
            inventory.setItem(1, stack);
        } else {
            if (member.times != -1) {
                Bukkit.getScheduler().cancelTask(member.MainTimer);
                member.clear(player.getWorld());
                Inventory inventory = player.getInventory();
                ItemStack stack = ItemStackList.Blocks[member.integerCache[0]][member.user.lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
                member.times = -1;
            } else if (!fell) {
                Location location = player.getLocation();
                int coord = member.PlayerCoord;
                int oldroom = coordToRoom(coord);
                int newroom = Core.cal(oldroom, location.getX() + location.getZ() > coord * 2, rooms);
                if (newroom == -1) player.sendMessage(StringList.nomapsavaliable[member.user.lang]);
                else {
                    member.PlayerCoord = roomToCoord(newroom);
                    rooms[oldroom] = true;
                    rooms[newroom] = false;
                }
            }
            player.closeInventory();
        }
        PlayerUtils.teleport(((CraftPlayer) player).getHandle().playerConnection, (inclined ? member.PlayerCoord : 0) + 0.5, 100, member.PlayerCoord + 0.5, inclined ? 225 : 270, 0);
    }


    public void Quit(Player player) {
        Member member = Member.getMember(player.getUniqueId());
        member.updateSidebar(player.getName(), this, true);
        used -= 1;
        rooms[coordToRoom(member.PlayerCoord)] = true;
    }

    public int coordToRoom(int i) {
        return -1;
    }

    public int roomToCoord(int i) {
        return -1;
    }
}
