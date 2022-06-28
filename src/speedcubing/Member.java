package speedcubing;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import speedcubing.Core.Core;
import speedcubing.Core.timed.TimedMode;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.lib.bukkit.BungeePluginMessage;
import speedcubing.lib.bukkit.PlayerUtils;
import speedcubing.lib.bukkit.SideBar;
import speedcubing.replay.Replay;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.*;

public class Member {
    public static final Map<UUID, Member> users = new HashMap<>();

    public static Member getMember(UUID uuid) {
        return users.get(uuid);
    }

    public User user;

    public boolean waiting;
    public int[] integerCache;

    public int times;
    public int MainTimer;
    public int finishedtask;
    public Set<BlockPosition> Blocks = new HashSet<>();
    public int PlayerCoord;
    public UUID uuid;
    public Replay replay;

    public Member(UUID uuid) {
        this.user = User.getUser(uuid);
        this.uuid = uuid;
        users.put(uuid, this);
        integerCache = speedcubingServer.connection.selectInts("playersdata", "fastbuild_blockskin,fastbuild_falldelay", "uuid='" + uuid + "'");
    }

    public void clear(World world) {
        for (BlockPosition b : Blocks) {
            world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
        }
        Blocks.clear();
    }

    public void finish(Player player) {
        TimedMode mode = Core.getInstance(player.getWorld().getName());
        int time = times;
        if (time < mode.limit) {
            BungeePluginMessage.switchServer(player, "Lobby");
            return;
        }
        waiting = true;
        Bukkit.getScheduler().cancelTask(MainTimer);
        int lang = User.getUser(uuid).lang;
        int msrecord = time * 5;
        String display = Core.tickToTime(time);
        long timeMillis = System.currentTimeMillis() / 1000L;
        new Thread(() -> {
            int last = speedcubingServer.connection.selectInt("playersdata", "fb_" + mode.sqlId + "_pb", "uuid='" + uuid + "'");
            if (last == 0 || last > msrecord) {
                speedcubingServer.connection.insert("fastbuilder", "uuid,at,score,mode", "'" + uuid + "'," + timeMillis + "," + msrecord + "," + mode.sqlId);
                speedcubingServer.connection.update("playersdata", "fb_" + mode.sqlId + "_pb=" + msrecord, "uuid='" + uuid + "'");
                SideBar.getSidebar(player).setLine(StringList.SideBar[lang][4] + display, 4);
            }
        }).start();
        times = -1;
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutTitle(0, 60, 5));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(StringList.timedisplay[lang].replace("%time%", display))));
        if (mode.score.get(uuid) > time || mode.score.get(uuid) == 0) {
            int i;
            for (i = 0; i < mode.session.size(); i++) {
                if (mode.score.get(mode.session.get(i)) > time || mode.score.get(mode.session.get(i)) == 0) {
                    mode.session.remove(uuid);
                    mode.session.add(i, uuid);
                    break;
                }
            }
            mode.score.put(uuid, time);
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
                if (mode.inclined)
                    player.teleport(new Location(player.getWorld(), PlayerCoord + 0.5, 100, PlayerCoord + 0.5, 225, 0));
                else
                    player.teleport(new Location(player.getWorld(), 0.5, 100, PlayerCoord + 0.5, 270, 0));
                waiting = false;
                Inventory inventory = player.getInventory();
                ItemStack stack = ItemStackList.Blocks[integerCache[0]][lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
            }
        }, 40);
    }

    public boolean validPlaceBlock(Player player, BlockPosition b) {
        int lang = User.getUser(uuid).lang;
        if (times == -1) {
            if (b.getY() > 100)
                return true;
            replay.reset();
            replay.StartLocation = player.getLocation();
            replay.BlockTicks.add(0);
            replay.BlockActions.add(b);
            times = 0;
            MainTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                times += 1;
                SideBar.getSidebar(player).setLine(StringList.SideBar[lang][9] + Core.tickToTime(times), 12);
                if (times >= 2400) {
                    Core.getInstance(player.getWorld().getName()).Fell(player, false);
                    player.sendMessage(StringList.cancelled[lang]);
                }
            }, 0, 1);
        } else {
            replay.BlockTicks.add(times);
            replay.BlockActions.add(b);
        }
        Blocks.add(b);
        Inventory inventory = player.getInventory();
        int empty = 1 - player.getInventory().getHeldItemSlot();
        if (inventory.getItem(empty) == null)
            inventory.setItem(empty, ItemStackList.Blocks[integerCache[0]][lang]);
        return false;
    }

    public void updateSidebar(String name, TimedMode mode, boolean quit) {
        int i = mode.session.indexOf(uuid);
        if (quit) {
            if (MainTimer != -1)
                Bukkit.getScheduler().cancelTask(MainTimer);
            clear(mode.world);
            Blocks.clear();
            times = -1;
            waiting = false;
            mode.session.remove(uuid);
            mode.score.remove(uuid);
        } else {
            mode.session.remove(uuid);
            mode.session.add(uuid);
            mode.score.put(uuid, 0);
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
}
