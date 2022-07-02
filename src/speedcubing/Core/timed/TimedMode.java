package speedcubing.Core.timed;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import speedcubing.Core.Core;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Member;
import speedcubing.lib.bukkit.BungeePluginMessage;
import speedcubing.lib.bukkit.SideBar;
import speedcubing.replay.Replay;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class TimedMode {
    public final int size = 31;
    public int used = 0;
    public boolean[] rooms = new boolean[size];
    public List<Member> session = new ArrayList<>();
    public Map<Member, Integer> score = new HashMap<>();
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
        Member member = Member.getMember(player);
        member.replay = new Replay(world, player);
        int coord = roomToCoord(room);
        player.teleport(new Location(world, (inclined ? coord : 0) + 0.5, 100, coord + 0.5, inclined ? 225 : 270, 0));
        member.PlayerCoord = coord;
        String[] sidebar = StringList.SideBar[member.lang];
        int a = speedcubingServer.connection.selectInt("playersdata", "fb_" + sqlId + "_pb", "id=" + member.id);
        SideBar sideBar = new SideBar(player, sidebar[0])
                .staticLine(sidebar[1], 12)
                .staticLine(sidebar[2], 11)
                .staticLine(sidebar[3], 10)
                .changeableLine(sidebar[4] + (a == 0 ? "§7-,---" : Core.msToTime(a)), 9)
                .staticLine(sidebar[5], 8)
                .staticLine(sidebar[6], 7);
        int size = session.size();
        for (int j = 0; j < 3; j++) {
            String result;
            if (size <= j)
                result = "§7-,---";
            else {
                Member ses = session.get(j);
                int sc = score.get(ses);
                if (sc == 0)
                    result = "§7-,---";
                else
                    result = speedcubingServer.getFormat(ses.rank)[1] + ses.player.getName() + "§8: §e" + Core.tickToTime(sc);
            }
            sideBar.changeableLine(result, 6 - j);
        }
        sideBar.staticLine(sidebar[7], 3)
                .staticLine(sidebar[8], 2)
                .changeableLine(sidebar[9] + "§7-,---", 1)
                .staticLine(sidebar[10], 0).create();
        PlayerInventory inventory = player.getInventory();
        ItemStack stack = ItemStackList.Blocks[member.integerCache[0]][member.lang];
        inventory.clear();
        inventory.setItem(0, stack);
        inventory.setItem(1, stack);
        inventory.setItem(2, ItemStackList.pickaxe[member.lang]);
        inventory.setItem(7, ItemStackList.Settings[member.lang]);
        inventory.setItem(8, ItemStackList.navigator[member.lang]);
        inventory.setHeldItemSlot(0);
        session.add(member);
        member.times = -1;
        score.put(member, 0);
    }

    public void Fell(Player player, boolean fell) {
        Member member = Member.getMember(player);
        if (member.waiting) {
            Bukkit.getScheduler().cancelTask(member.finishedtask);
            member.clear(player.getWorld());
            member.waiting = false;
            Inventory inventory = player.getInventory();
            ItemStack stack = ItemStackList.Blocks[member.integerCache[0]][member.lang];
            inventory.setItem(0, stack);
            inventory.setItem(1, stack);
        } else {
            if (member.times != -1) {
                Bukkit.getScheduler().cancelTask(member.MainTimer);
                member.clear(player.getWorld());
                Inventory inventory = player.getInventory();
                ItemStack stack = ItemStackList.Blocks[member.integerCache[0]][member.lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
                member.times = -1;
            } else if (!fell) {
                Location location = player.getLocation();
                int coord = member.PlayerCoord;
                int oldroom = coordToRoom(coord);
                int newroom = Core.cal(oldroom, location.getX() + location.getZ() > coord * 2, rooms);
                if (newroom == -1) player.sendMessage(StringList.nomapsavaliable[member.lang]);
                else {
                    member.PlayerCoord = roomToCoord(newroom);
                    rooms[oldroom] = true;
                    rooms[newroom] = false;
                }
            }
            player.closeInventory();
        }
        player.teleport(new Location(player.getWorld(), (inclined ? member.PlayerCoord : 0) + 0.5, 100, member.PlayerCoord + 0.5, inclined ? 225 : 270, 0));
    }


    public void Quit(Player player) {
        Member member = Member.getMember(player);
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
