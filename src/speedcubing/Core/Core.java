package speedcubing.Core;

import com.mojang.authlib.properties.Property;
import speedcubing.lib.bukkit.PlayerUtils;
import speedcubing.lib.bukkit.SideBarAPI;
import speedcubing.server.PluginMessage;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.Main;
import speedcubing.replay.test;

import java.util.*;

public class Core {
    public static Set<UUID> waiting = new HashSet<>();
    public static Map<UUID, Integer[]> integerCache = new HashMap<>();

    public static Map<UUID, Integer> times = new HashMap<>();
    public static Map<UUID, Integer> MainTimer = new HashMap<>();
    public static Map<UUID, Integer> finishedtask = new HashMap<>();
    public static Map<UUID, Set<BlockPosition>> Blocks = new HashMap<>();
    public static final Map<UUID, Integer> PlayerCoord = new HashMap<>();

    public static String msToTime(int ms) {
        return String.format("%.3f", ms * 0.01);
    }

    public static String tickToTime(int tick) {
        return String.format("%.3f", tick * 0.05);
    }

    public static int cal(int oldroom, boolean left, boolean[] b) {
        int a = left ? 0 : 1;
        int l = b.length - (a * -1 + 2);
        while (oldroom != l) {
            oldroom += (oldroom == a ? 1 : (oldroom % 2) * 4 - 2) * (a * -2 + 1);
            if (b[oldroom])
                return oldroom;
        }
        return -1;
    }

    public static void clear(UUID uuid, World world) {
        for (BlockPosition b : Blocks.get(uuid)) {
            world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
        }
    }

    public static void set1(int i, World world, List<String> prefix, List<String> suffix) {
        for (int j = i; j < 3; j++) {
            for (Player p : world.getPlayers()) {
                Team t = p.getScoreboard().getTeam("§" + (j + 4) + "§f");
                t.setPrefix(prefix.get(j - i));
                t.setSuffix(suffix.get(j - i));
            }
        }
    }

    public static void finish(String w, int type, boolean inclined, Player player, List<UUID> session, Map<UUID, Integer> score, int limit) {
        World world = Bukkit.getWorld(w);
        UUID uuid = player.getUniqueId();
        int time = times.get(uuid);
        if (time < limit) {
            PluginMessage.switchServer(player, "Lobby");
            return;
        }
        waiting.add(uuid);
        Bukkit.getScheduler().cancelTask(MainTimer.get(uuid));
        int lang = User.getLang(uuid);
        int msrecord = time * 5;
        String display = tickToTime(time);
        long timeMillis = System.currentTimeMillis() / 1000L;
        new Thread(() -> {
            int last = speedcubingServer.connection.selectInt("playersdata", "fb_" + type + "_pb", "uuid='" + uuid + "'");
            if (last == 0 || last > msrecord) {
                speedcubingServer.connection.insert("fastbuilder", "uuid,at,score,mode", "'" + uuid + "'," + timeMillis + "," + msrecord + "," + type);
                speedcubingServer.connection.update("playersdata", "fb_" + type + "_pb=" + msrecord, "uuid='" + uuid + "'");
                player.getScoreboard().getTeam(StringList.SideBar[lang][9]).setSuffix(display);
            }
        }).start();
        times.put(uuid, -1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutTitle(0, 60, 5));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(StringList.timedisplay[lang].replace("%time%", display))));
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
                            String[] x = SideBarAPI.prefixSuffixSplit(User.getFormat(User.getRank(ses))[1] + Bukkit.getPlayer(ses).getName() + "§8: §e" + tickToTime(sc));
                            prefix.add(x[0]);
                            suffix.add(x[1]);
                        }
                    }
                }
                set1(i, world, prefix, suffix);
            }
        }
        finishedtask.put(uuid, Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (PlayerCoord.containsKey(uuid)) {
                clear(uuid, world);
                if (inclined) {
                    int fixed = PlayerCoord.get(uuid);
                    PlayerUtils.teleport(connection, fixed + 0.5, 100, fixed + 0.5, 225, 0);
                } else
                    PlayerUtils.teleport(connection, 0.5, 100, PlayerCoord.get(uuid) + 0.5, 270, 0);
                Blocks.get(uuid).clear();
                waiting.remove(uuid);
                Inventory inventory = player.getInventory();
                ItemStack stack = ItemStackList.Blocks[integerCache.get(uuid)[0]][lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
            }
        }, 40));
    }

    public static boolean validPlaceBlock(Player player, BlockPosition b) {
        UUID uuid = player.getUniqueId();
        int lang = User.getLang(uuid);
        if (times.get(uuid) == -1) {
            if (b.getY() > 100)
                return true;
            if (player.getName().equals("speedcubing") && player.getWorld().getName().equals("world")) {
                new test().reset();
                Property profile = ((CraftPlayer) player).getProfile().getProperties().get("textures").iterator().next();
                test.StartSkin = new String[]{profile.getValue()};
                test.StartLocation = player.getLocation();
                test.BlockTicks.add(0);
                test.BlockActions.add(b);
            }
            times.put(uuid, 0);
            MainTimer.put(uuid, Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                int up = times.get(uuid) + 1;
                times.put(uuid, up);
                player.getScoreboard().getTeam(StringList.SideBar[lang][1]).setSuffix(tickToTime(up));
                if (up >= 2400) {
                    switch (player.getWorld().getName()) {
                        case "world":
                            Long.Fell(player, false);
                            break;
                        case "Short":
                            Short.Fell(player, false);
                            break;
                        case "Inclined":
                            Inclined.Fell(player, false);
                            break;
                        case "Onestack":
                            OneStack.Fell(player, false);
                            break;
                        case "Inclinedshort":
                            InclinedShort.Fell(player, false);
                            break;
                    }
                    player.sendMessage(StringList.cancelled[lang]);
                }
            }, 0, 1));
        } else if (player.getWorld().getName().equals("world")) {
            test.BlockTicks.add(Core.times.get(uuid));
            test.BlockActions.add(b);
        }
        Blocks.get(uuid).add(b);
        Inventory inventory = player.getInventory();
        int empty = 1 - player.getInventory().getHeldItemSlot();
        if (inventory.getItem(empty) == null)
            inventory.setItem(empty, ItemStackList.Blocks[integerCache.get(uuid)[0]][lang]);
        return false;
    }

    public static void updateSidebar(String name, UUID uuid, List<UUID> session, Map<UUID, Integer> score, World world, boolean quit) {
//        if (player.getName().equals("speedcubing"))
//            new test().reset();
        int x = session.indexOf(uuid);
        if (quit) {
            if (MainTimer.containsKey(uuid))
                Bukkit.getScheduler().cancelTask(MainTimer.get(uuid));
            clear(uuid, world);
            Blocks.get(uuid).clear();
            times.put(uuid, -1);
            waiting.remove(uuid);
            session.remove(uuid);
            score.remove(uuid);
        } else {
            session.remove(uuid);
            session.add(uuid);
            score.put(uuid, 0);
        }
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
                        String[] xx = SideBarAPI.prefixSuffixSplit(User.getFormat(User.getRank(ses))[1] + Bukkit.getPlayer(ses).getName() + "§8: §e" + tickToTime(sc));
                        prefix.add(xx[0]);
                        suffix.add(xx[1]);
                    }
                }
            }
            for (Player p : world.getPlayers()) {
                if (!p.getName().equals(name)) {
                    Scoreboard scoreboard = p.getScoreboard();
                    for (int j = x; j < 3; j++) {
                        Team t = scoreboard.getTeam("§" + (j + 4) + "§f");
                        t.setPrefix(prefix.get(j - x));
                        t.setSuffix(suffix.get(j - x));
                    }
                }
            }
        }
    }
}
