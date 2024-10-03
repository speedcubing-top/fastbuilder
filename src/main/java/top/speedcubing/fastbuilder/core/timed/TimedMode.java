package top.speedcubing.fastbuilder.core.timed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.core.Core;
import top.speedcubing.fastbuilder.list.ItemStackList;
import top.speedcubing.fastbuilder.list.StringList;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;
import top.speedcubing.server.utils.MapManager;

public abstract class TimedMode {
    public static final int FALL = 4;
    public final int size = 31;
    public final boolean[] rooms = new boolean[size];
    public final List<Member> session = new ArrayList<>();
    public final Map<Member, Integer> score = new HashMap<>();
    public final int sqlId;
    public final int limit;
    public final boolean inclined;
    public final int distance;
    private final int height;
    public World replayWorld;
    public World world;
    public int used = 0;
    public int replayRoom = 1;
    public final Map<Member, Replay> lastAttempt = new HashMap<>();

    public TimedMode(String world, String replayWorld, String worldName, int sqlId, int limit, boolean inclined, int distance, int height) {
        Arrays.fill(rooms, true);
        this.inclined = inclined;
        this.limit = limit;
        this.sqlId = sqlId;
        this.distance = distance;
        this.height = height;
        try {
            this.world = MapManager.install(worldName, world, Difficulty.PEACEFUL, 6000, false);
            this.replayWorld = MapManager.install(replayWorld, replayWorld, Difficulty.PEACEFUL, 6000, false);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getServer().shutdown();
        }
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
        used++;
        Member member = Member.getMember(player);
        member.currentMode = this;
        int coord = roomToCoord(room);
        player.setAllowFlight(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(new Location(world, coord + 0.5, 100, (inclined ? coord : -4) + 0.5, inclined ? 225 : 0, 0));
        member.PlayerCoord = coord;
        System.out.print("[Join] " + room + " " + member.user.bGetName() + " " + world.getName());
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
                    result = ses.user.getColorName(false) + "§8: §e" + Core.tickToTime(sc);
            }
            sideBar.changeableLine(result, 6 - j);
        }
        sideBar.staticLine(sidebar[7], 3)
                .staticLine(sidebar[8], 2)
                .changeableLine(sidebar[9] + "§7-,---", 1)
                .staticLine(sidebar[10], 0).create();
        PlayerInventory inventory = player.getInventory();
        ItemStack stack = ItemStackList.Blocks[member.blockskin];
        inventory.clear();
        inventory.setItem(0, stack);
        inventory.setItem(1, stack);
        member.user.setLangItem(2, ItemStackList.pickaxe);
        member.user.setLangItem(6, ItemStackList.Replay);
        member.user.setLangItem(7, ItemStackList.Settings);
        member.user.setLangItem(8, ItemStackList.navigator);
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


    public void Quit(Player player) {
        Member member = Member.getMember(player);
        member.replay = null;
        member.updateSidebar(player.getName(), this, true);
        used -= 1;
        rooms[coordToRoom(member.PlayerCoord)] = true;
        System.out.print("[Quit] " + coordToRoom(member.PlayerCoord) + " " + member.user.bGetName() + " " + world.getName());
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
