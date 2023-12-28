package top.speedcubing.fastbuilder.Core.timed;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import top.speedcubing.fastbuilder.Core.Core;
import top.speedcubing.fastbuilder.List.*;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.fastbuilder.replay.gui.FastBuilderUtils;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.pluginMessage.BungeePluginMessage;

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
        Arrays.fill(rooms, true);
        this.inclined = inclined;
        this.limit = limit;
        this.sqlId = sqlId;
        this.distance = distance;
        this.height = height;
        this.world = Bukkit.getServer().createWorld(new WorldCreator(world));
        this.world.setDifficulty(Difficulty.PEACEFUL);
        this.world.setTime(6000);
        this.world.setAutoSave(false);
        this.replayWorld = Bukkit.getServer().createWorld(new WorldCreator(replayWorld));
        this.replayWorld.setDifficulty(Difficulty.PEACEFUL);
        this.replayWorld.setTime(6000);
        this.replayWorld.setAutoSave(false);
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
                    result = ses.user.getFormatName(false) + "§8: §e" + FastBuilderUtils.tickToTime(sc);
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
