package speedcubing;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import speedcubing.Core.Core;
import speedcubing.Core.timed.TimedMode;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.lib.bukkit.BungeePluginMessage;
import speedcubing.lib.bukkit.SideBar;
import speedcubing.lib.bukkit.inventory.ItemBuilder;
import speedcubing.lib.utils.SQL.SQLUtils;
import speedcubing.replay.Replay;
import speedcubing.replay.ReplayViewer;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class Member extends User {
    public static Map<Integer, Member> users = new HashMap<>();

    public static Member getMember(CommandSender sender) {
        return getMember((Player) sender);
    }

    public static Member getMember(Player player) {
        return getMember(User.getUser(player).id);
    }

    public static Member getMember(int id) {
        return users.get(id);
    }

    public int times;

    public boolean waiting;
    public int[] integerCache;
    public int MainTimer;
    public int finishedtask;
    public Set<Block> Blocks = new HashSet<>();
    public int PlayerCoord;
    public Replay replay;
    public ReplayViewer replayPlayer;

    public int[] LengthSettings;
    public int[] Center;
    public int Blockplaced;
    public boolean finishedOnce;
    public Set<AxisAlignedBB> hits = new HashSet<>();

    public Location didntevenfuckingstart = new Location(player.getWorld(), 0, 0, 0);
    public final Location asyncLoc = new Location(player.getWorld(), 0, 0, 0);
    public final List<Replay> replays = new ArrayList<>();

    public boolean finishNext;

    public Member(Player player) {
        super(player.getUniqueId());
        users.put(id, this);
        integerCache = ArrayUtils.toPrimitive(SQLUtils.getIntArray(speedcubingServer.connection.select("playersdata", "fastbuild_blockskin,fastbuild_falldelay", "id=" + id)));
    }

    public void clear(World world) {
        for (Block b : Blocks) {
            world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
        }
        Blocks.clear();
    }

    public void finish() {
        TimedMode mode = Core.getInstance(player.getWorld().getName());
        replay.replayRecorder.pause();
        replays.add(replay);
        int time = replay.replayRecorder.NewTick - 1;
        if (time < mode.limit) {
            BungeePluginMessage.switchServer(player, "Lobby");
            return;
        }
        waiting = true;
        int msrecord = time * 5;
        String display = Core.tickToTime(time);
        long timeMillis = System.currentTimeMillis() / 1000L;
        new Thread(() -> {
            int last = SQLUtils.getInt(speedcubingServer.connection.select("playersdata", "fb_" + mode.sqlId + "_pb", "id=" + id));
            if (last == 0 || last > msrecord) {
                speedcubingServer.connection.insert("fastbuilder", "id,at,score,mode", id + "," + timeMillis + "," + msrecord + "," + mode.sqlId);
                speedcubingServer.connection.update("playersdata", "fb_" + mode.sqlId + "_pb=" + msrecord, "id=" + id);
                SideBar.getSidebar(player).setLine(StringList.SideBar[lang][4] + display, 4);
            }
        }).start();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutTitle(0, 60, 5));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(StringList.timedisplay[lang].replace("%time%", display))));
        if (mode.score.get(this) > time || mode.score.get(this) == 0) {
            int i;
            for (i = 0; i < mode.session.size(); i++) {
                if (mode.score.get(mode.session.get(i)) > time || mode.score.get(mode.session.get(i)) == 0) {
                    mode.session.remove(this);
                    mode.session.add(i, this);
                    break;
                }
            }
            mode.score.put(this, time);
            if (i < 4) {
                List<String> s = Core.getSideBar(i, mode.session, mode.score);
                for (int j = i; j < 3; j++) {
                    for (Player p : mode.world.getPlayers()) {
                        SideBar.getSidebar(p).setLine(s.get(j - i), j + 7);
                    }
                }
            }
        }
        finishedtask = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (PlayerCoord != -1) {
                clear(mode.world);
                player.teleport(new Location(player.getWorld(), PlayerCoord + 0.5, 100, (mode.inclined ? PlayerCoord : -4) + 0.5, mode.inclined ? 225 : 0, 0));
                waiting = false;
                Inventory inventory = player.getInventory();
                ItemStack stack = ItemStackList.Blocks[integerCache[0]][lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
            }
        }, 40);
    }

    public boolean validPlaceBlock(int x, int y, int z) {
        if (replay == null) {
            replay = new Replay(this, didntevenfuckingstart.clone());
            replay.replayRecorder.PlayerMoveEvent(asyncLoc);
        }
        Block b = player.getWorld().getBlockAt(x, y, z);
        replay.replayRecorder.BlockPlaceEvent(b);
        Blocks.add(b);
        Inventory inventory = player.getInventory();
        int empty = 1 - player.getInventory().getHeldItemSlot();
        if (inventory.getItem(empty) == null)
            inventory.setItem(empty, ItemStackList.Blocks[integerCache[0]][lang]);
        return false;
    }

    public void updateSidebar(String name, TimedMode mode, boolean quit) {
        int i = mode.session.indexOf(this);
        if (quit) {
            clear(mode.world);
            Blocks.clear();
            waiting = false;
            mode.session.remove(this);
            mode.score.remove(this);
        } else {
            mode.session.remove(this);
            mode.session.add(this);
            mode.score.put(this, 0);
        }
        if (i != -1 && i < 3) {
            List<String> s = Core.getSideBar(i, mode.session, mode.score);
            for (Player p : mode.world.getPlayers()) {
                if (!p.getName().equals(name)) {
                    for (int j = i; j < 3; j++) {
                        SideBar.getSidebar(p).setLine(s.get(j - i), j + 7);
                    }
                }
            }
        }
    }

    public void openSettings() {
        Inventory inventory = Bukkit.createInventory(ItemStackList.settings, 27, StringList.Settings[lang]);
        String[] hit = StringList.FallDelayLore[lang];
        inventory.setItem(11, new ItemBuilder(Material.RAW_FISH).durability(3).name(hit[0]).lore(hit[1], hit[2].replace("%int%", Integer.toString(integerCache[1])), hit[3], hit[4], hit[5]).build());
        inventory.setItem(13, ItemStackList.openkit[lang]);
        if (!player.getWorld().getName().equals("Custom"))
            inventory.setItem(15, ItemStackList.Reset[lang]);
        else if (player.getInventory().getItem(5) != null) {
            String[] lore = StringList.LoopLore[lang];
            inventory.setItem(15, new ItemBuilder(Material.MINECART).name(lore[0]).lore(lore[1], hits.isEmpty() ? lore[3] : lore[2]).build());
        }
        player.openInventory(inventory);
    }

}
