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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import speedcubing.Core.Core;
import speedcubing.Core.timed.TimedMode;
import speedcubing.List.ItemStackList;
import speedcubing.List.StringList;
import speedcubing.lib.bukkit.BungeePluginMessage;
import speedcubing.lib.bukkit.SideBar;
import speedcubing.lib.bukkit.WorldUtils;
import speedcubing.lib.bukkit.inventory.ItemBuilder;
import speedcubing.lib.utils.SQL.SQLUtils;
import speedcubing.replay.Replay;
import speedcubing.replay.ReplayViewer;
import speedcubing.replay.gui.FastBuilderUtils;
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
    public int ReplayCoord;
    public Replay replay;
    public ReplayViewer replayPlayer;
    public int lasttime;

    public TimedMode currentMode;

    public int[] LengthSettings;
    public int[] Center;
    public int Blockplaced;
    public boolean finishedOnce;
    public Set<AxisAlignedBB> hits = new HashSet<>();

    public Location didntevenfuckingstart = new Location(player.getWorld(), 0, 0, 0);
    public final Location asyncLoc = new Location(player.getWorld(), 0, 0, 0);
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

    public void joinReplay(int slot) {
        int room = 1;
        while (currentMode.replayRoom.contains(room)) {
            room++;
        }
        currentMode.replayRoom.add(room);
        ReplayCoord = currentMode.roomToCoord(room) / 8 * 500;
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.teleport(new Location(currentMode.replayWorld, ReplayCoord + 0.5, 100, currentMode.inclined ? ReplayCoord + 0.5 : -4.5, 0, 0));
        replayPlayer = new ReplayViewer(this, slot);
        if (currentMode.inclined)
            WorldUtils.cloneArea(-3, 91, 3, 33, 106, -33, -3 + ReplayCoord, 91, 3 + ReplayCoord, currentMode.replayWorld);
        else
            WorldUtils.cloneArea(-4, 83, -9, 5, 132, 57, -4 + ReplayCoord, 83, -9, currentMode.replayWorld);
        player.getInventory().clear();
        player.getInventory().setItem(2, ItemStackList.START[lang]);
        player.getInventory().setItem(4, ItemStackList.TICK[lang]);
        player.getInventory().setItem(6, ItemStackList.QUITREPLAY[lang]);
    }

    public void finish() {
        replay.replayRecorder.pause();
        int time = replay.replayRecorder.NewTick - 1;
        if (time < currentMode.limit) {
            BungeePluginMessage.switchServer(player, "Lobby");
            return;
        }
        waiting = true;
        int msrecord = time * 5;
        String display = FastBuilderUtils.tickToTime(time);
        new Thread(() -> {
            int last = SQLUtils.getInt(speedcubingServer.connection.select("playersdata", "fb_" + currentMode.sqlId + "_pb", "id=" + id));
            if (last == 0 || last > msrecord) {
                speedcubingServer.connection.update("playersdata", "fb_" + currentMode.sqlId + "_pb=" + msrecord, "id=" + id);
                SideBar.getSidebar(player).setLine(StringList.SideBar[lang][4] + display, 4);
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                    fw(player);
                    fw(player);
                    fw(player);
                });
                if (last == 0) {
                    player.sendMessage("§8---------------------------------------------\n" +
                            "                        §bCongratulations!\n" +
                            "             §aYou got your first score of §e" + FastBuilderUtils.tickToTime(time) + "§a!\n" +
                            "§8---------------------------------------------");
                } else {
                    player.sendMessage("§8---------------------------------------------\n" +
                            "                        §bCongratulations!\n" +
                            "                        §e" + FastBuilderUtils.tickToTime(last / 5) + " §8-> §e" + FastBuilderUtils.tickToTime(time) + " \n" +
                            "                        §8(§bbeated by §a" + FastBuilderUtils.tickToTime(last / 5 - time) + "§8)\n" +
                            "§8---------------------------------------------");
                }
            }
        }).start();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutTitle(5, 60, 5));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(StringList.timedisplay[lang].replace("%time%", display))));
        currentMode.a(this,time);
        finishedtask = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (PlayerCoord != -1) {
                replay = null;
                clear(currentMode.world);
                player.teleport(new Location(player.getWorld(), PlayerCoord + 0.5, 100, (currentMode.inclined ? PlayerCoord : -4) + 0.5, currentMode.inclined ? 225 : 0, 0));
                waiting = false;
                Inventory inventory = player.getInventory();
                ItemStack stack = ItemStackList.Blocks[integerCache[0]][lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
            }
        }, 40);
    }

    public void validPlaceBlock(int x, int y, int z) {
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


    static void fw(Player player) {
        final Random random = new Random();
        Location location = random.nextBoolean() ? player.getLocation().subtract(random.nextDouble(), random.nextDouble(), random.nextDouble()) : player.getLocation().add(random.nextDouble(), random.nextDouble(), random.nextDouble());
        final Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK);
        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        final FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(getColor(random.nextInt(17) + 1)).with(FireworkEffect.Type.BALL_LARGE).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }

    static Color getColor(final int i) {
        switch (i) {
            case 1:
                return Color.AQUA;
            case 2:
                return Color.BLACK;
            case 3:
                return Color.BLUE;
            case 4:
                return Color.FUCHSIA;
            case 5:
                return Color.GRAY;
            case 6:
                return Color.GREEN;
            case 7:
                return Color.LIME;
            case 8:
                return Color.MAROON;
            case 9:
                return Color.NAVY;
            case 10:
                return Color.OLIVE;
            case 11:
                return Color.ORANGE;
            case 12:
                return Color.PURPLE;
            case 13:
                return Color.RED;
            case 14:
                return Color.SILVER;
            case 15:
                return Color.TEAL;
            case 16:
                return Color.WHITE;
            case 17:
                return Color.YELLOW;
        }
        return null;
    }
}
