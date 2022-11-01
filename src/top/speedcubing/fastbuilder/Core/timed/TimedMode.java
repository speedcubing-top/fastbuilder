package top.speedcubing.fastbuilder.Core.timed;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import top.speedcubing.fastbuilder.Core.Core;
import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.List.StringList;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.fastbuilder.replay.gui.FastBuilderUtils;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;
import top.speedcubing.server.speedcubingServer;

import java.util.*;

public class TimedMode {
    public static final int FALL = 4;
    public final int size = 31;
    public final boolean[] rooms = new boolean[size];
    public final List<Member> session = new ArrayList<>();
    public final Map<Member, Integer> score = new HashMap<>();
    public World world;
    public final int sqlId;
    public final int limit;
    public final boolean inclined;
    public final int distance;
    private final int height;
    public final World replayWorld;
    public int used = 0;
    public final Set<Integer> replayRoom = new HashSet<>();
    public final Map<Member, Replay> lastAttempt = new HashMap<>();

    public TimedMode(String world, int sqlId, int limit, boolean inclined, int distance, int height, String replayWorld) {
        for (int i = 0; i < size; i++) {
            rooms[i] = true;
        }
        this.inclined = inclined;
        this.limit = limit;
        this.sqlId = sqlId;
        this.world = Bukkit.getWorld(world);
        this.distance = distance;
        this.height = height;
        this.replayWorld = Bukkit.getWorld(replayWorld);
    }

    public void Join(Player player, boolean fromReplay, boolean kick) {
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
        member.currentMode = this;
        int coord = roomToCoord(room);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(new Location(world, coord + 0.5, 100, (inclined ? coord : -4) + 0.5, inclined ? 225 : 0, 0));
        member.PlayerCoord = coord;
        System.out.print("[Join] " + room + " " + member.user.player.getName() + " " + world.getName());
        setPressurePlate(member);
        String[] sidebar = StringList.SideBar[member.user.lang];
        int a = member.user.dbSelect("fb_" + sqlId + "_pb").getInt();
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
                    result = speedcubingServer.getFormat(ses.user.rank)[1] + ses.user.player.getName() + "§8: §e" + FastBuilderUtils.tickToTime(sc);
            }
            sideBar.changeableLine(result, 6 - j);
        }
        sideBar.staticLine(sidebar[7], 3)
                .staticLine(sidebar[8], 2)
                .changeableLine(sidebar[9] + "§7-,---", 1)
                .staticLine(sidebar[10], 0).create();
        PlayerInventory inventory = player.getInventory();
        ItemStack stack = ItemStackList.Blocks[member.blockskin][member.user.lang];
        inventory.clear();
        inventory.setItem(0, stack);
        inventory.setItem(1, stack);
        inventory.setItem(2, ItemStackList.pickaxe[member.user.lang]);
        inventory.setItem(6, ItemStackList.Replay[member.user.lang]);
        inventory.setItem(7, ItemStackList.Settings[member.user.lang]);
        inventory.setItem(8, ItemStackList.navigator[member.user.lang]);
        inventory.setHeldItemSlot(0);
        session.add(member);
        score.put(member, 0);
        if (fromReplay)
            a(member, member.lasttime);
        else
            member.lasttime = 0;
    }

    public void a(Member member, int time) {
        if (time != 0 && (score.get(member) > time || score.get(member) == 0)) {
            member.lasttime = time;
            int i;
            for (i = 0; i < session.size(); i++) {
                if (score.get(session.get(i)) > time || score.get(session.get(i)) == 0) {
                    session.remove(member);
                    session.add(i, member);
                    break;
                }
            }
            score.put(member, time);
            if (i < 4) {
                List<String> s = Core.getSideBar(i, session, score);
                for (int j = i; j < 3; j++) {
                    for (Player p : world.getPlayers()) {
                        SideBar.getSidebar(p).setLineByScore(s.get(j - i), 6 - j);
                    }
                }
            }
        }
    }

    public void Fell(Member member, boolean fell,boolean timeOut) {
        boolean reset = false;
        if (member.waiting) {
            reset = true;
            Bukkit.getScheduler().cancelTask(member.finishedtask);
            member.waiting = false;
            Inventory inventory = member.user.player.getInventory();
            ItemStack stack = ItemStackList.Blocks[member.blockskin][member.user.lang];
            inventory.setItem(0, stack);
            inventory.setItem(1, stack);
        } else {
            if (member.replay != null) {
                reset = true;
                Inventory inventory = member.user.player.getInventory();
                ItemStack stack = ItemStackList.Blocks[member.blockskin][member.user.lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
                SideBar.getSidebar(member.user.player).setLineByScore(StringList.SideBar[member.user.lang][9] + FastBuilderUtils.tickToTime(member.replay.replayRecorder.NewTick - 1), 1);
                member.replay.replayRecorder.pause(false, false);
                lastAttempt.put(member, member.replay);
                member.replay = null;
            } else if (!fell && !timeOut) {
                Location location = member.user.player.getLocation();
                int coord = member.PlayerCoord;
                int oldroom = coordToRoom(coord);
                int newroom = Core.cal(oldroom, inclined ? location.getX() + location.getZ() > coord * 2 : location.getX() > coord, rooms);
                if (newroom == -1) member.user.sendLangMessage(StringList.nomapsavaliable);
                else {
                    member.PlayerCoord = roomToCoord(newroom);
                    setPressurePlate(member);
                    rooms[oldroom] = true;
                    rooms[newroom] = false;
                    System.out.print("[Swap] " + oldroom + " " + newroom + " " + member.user.player.getName() + " " + world.getName());
                }
            }
            member.user.player.closeInventory();
        }
        member.user.player.teleport(new Location(member.user.player.getWorld(), member.PlayerCoord + 0.5, 100, (inclined ? member.PlayerCoord : -4) + 0.5, inclined ? 225 : 0, 0));
        if (reset)
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> member.clear(member.user.player.getWorld()));
    }


    public void Quit(Player player) {
        Member member = Member.getMember(player);
        member.replay = null;
        member.updateSidebar(player.getName(), this, true);
        used -= 1;
        rooms[coordToRoom(member.PlayerCoord)] = true;
        System.out.print("[Quit] " + coordToRoom(member.PlayerCoord) + " " + member.user.player.getName() + " " + world.getName());

    }

    public int coordToRoom(int i) {
        return -1;
    }

    public int roomToCoord(int i) {
        return -1;
    }

    public void setPressurePlate(Member member) {
        member.hits.clear();
        for (int[] b : (inclined) ? new int[][]{
                {member.PlayerCoord + distance + 1, height, member.PlayerCoord - distance + 1},
                {member.PlayerCoord + distance, height, member.PlayerCoord - distance},
                {member.PlayerCoord + distance - 1, height, member.PlayerCoord - distance - 1},
        } : new int[][]{
                {member.PlayerCoord + 1, height, distance},
                {member.PlayerCoord + 2, height, distance},
                {member.PlayerCoord, height, distance},
                {member.PlayerCoord - 1, height, distance},
                {member.PlayerCoord - 2, height, distance},
        }) {
            member.hits.add(new AxisAlignedBB((float) b[0] + 0.125F, b[1], (float) b[2] + 0.125F, (float) (b[0] + 1) - 0.125F, (double) b[1] + 0.25D, (float) (b[2] + 1) - 0.125F));
        }
    }
}
