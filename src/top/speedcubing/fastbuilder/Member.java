package top.speedcubing.fastbuilder;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.World;
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
import top.speedcubing.fastbuilder.BukkitCommands.stats;
import top.speedcubing.fastbuilder.Core.Core;
import top.speedcubing.fastbuilder.Core.timed.TimedMode;
import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.List.StringList;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.fastbuilder.replay.ReplayCompressor;
import top.speedcubing.fastbuilder.replay.ReplayViewer;
import top.speedcubing.fastbuilder.replay.gui.FastBuilderUtils;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.WorldUtils;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

import java.util.*;

public class Member {
    public static Map<Integer, Member> users = new HashMap<>();

    public static Member getMember(CommandSender sender) {
        return getMember(User.getUser(sender).id);
    }

    public static Member getMember(int id) {
        return users.get(id);
    }

    public final User user;

    public int times;

    public boolean waiting;
    public int blockskin;
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
    public List<String> replays = new ArrayList<>();

    public Location didntevenfuckingstart;
    public final Location asyncLoc;
    public boolean finishNext;
    public long quitReplay;

    public Member(Player player) {
        this.user = User.getUser(player);
        this.asyncLoc = new Location(user.player.getWorld(), 0, 0, 0);
        users.put(user.id, this);
        blockskin = speedcubingServer.connection.select("fastbuild_blockskin").from("playersdata").where("id=" + user.id).getInt();
    }

    public void clear(World world) {
        for (Block b : Blocks) {
            Main.setTypeCheck(world.getBlockAt(b.getX(), b.getY(), b.getZ()), Material.AIR, 23);
//            world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
        }
        Blocks.clear();
    }

    public void joinReplay(int slot) {
        Replay replay = slot == 0 ? currentMode.lastAttempt.get(this) : ReplayCompressor.read(this, slot - 1);
        if (replay != null) {
            int room = 1;
            while (currentMode.replayRoom.contains(room)) {
                room++;
            }
            currentMode.replayRoom.add(room);
            ReplayCoord = currentMode.roomToCoord(room) / 8 * 500;
            user.player.setGameMode(GameMode.ADVENTURE);
            user.player.setAllowFlight(true);
            user.player.setFlying(true);
            user.player.teleport(new Location(currentMode.replayWorld, ReplayCoord + 0.5, 100, currentMode.inclined ? ReplayCoord + 0.5 : -4.5, currentMode.inclined ? 225 : 0, 0));
            replayPlayer = new ReplayViewer(this, replay, slot == 0);
            if (currentMode.inclined)
                WorldUtils.cloneArea(-3, 91, 3, 33, 106, -33, -3 + ReplayCoord, 91, 3 + ReplayCoord, currentMode.replayWorld);
            else
                WorldUtils.cloneArea(-4, 83, -9, 5, 132, 57, -4 + ReplayCoord, 83, -9, currentMode.replayWorld);
            user.player.getInventory().clear();
            user.player.getInventory().setItem(2, ItemStackList.START[user.lang]);
            user.player.getInventory().setItem(4, ItemStackList.TICK[user.lang]);
            user.player.getInventory().setItem(6, ItemStackList.QUITREPLAY[user.lang]);
        } else
            user.player.sendMessage("not found");
    }

    public void finish() {
        int time = replay.replayRecorder.NewTick - 1;
        double TASrate = (double) (MinecraftServer.currentTick - times) / time;
        boolean TAS = TASrate > 10 / 9D || TASrate < 9D / 10;
        boolean OVERLIMIT = time < currentMode.limit;
        boolean qualified = !TAS && !stats.SHIT && !OVERLIMIT;
        if (TAS)
            user.player.sendMessage("§cYou are using timer, right?");
        if (OVERLIMIT)
            user.player.sendMessage("§cThis score is out of range.");
        replay.replayRecorder.pause(true, TAS);
        replay = null;
        waiting = true;
        int msrecord = time * 5;
        String display = FastBuilderUtils.tickToTime(time);
        int last = speedcubingServer.connection.select("fb_" + currentMode.sqlId + "_pb").from("playersdata").where("id=" + user.id).getInt();
        if ((last == 0 || last > msrecord) && qualified) {
            speedcubingServer.connection.update("playersdata", "fb_" + currentMode.sqlId + "_pb=" + msrecord, "id=" + user.id);
            SideBar.getSidebar(user.player).setLineByScore(StringList.SideBar[user.lang][4] + display, 9);
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                fw(user.player);
                fw(user.player);
                fw(user.player);
            });
            if (last == 0) {
                user.player.sendMessage("§8---------------------------------------------\n" +
                        "                        §bCongratulations!\n" +
                        "             §aYou got your first score of §e" + FastBuilderUtils.tickToTime(time) + "§a!\n" +
                        "§8---------------------------------------------");
            } else {
                user.player.sendMessage("§8---------------------------------------------\n" +
                        "                        §bCongratulations!\n" +
                        "                        §e" + FastBuilderUtils.tickToTime(last / 5) + " §8-> §e" + FastBuilderUtils.tickToTime(time) + " \n" +
                        "                        §8(§bbeated by §a" + FastBuilderUtils.tickToTime(last / 5 - time) + "§8)\n" +
                        "§8---------------------------------------------");
            }
        }
        if (qualified)
            currentMode.a(this, time);
        user.player.playSound(user.player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        PlayerConnection connection = ((CraftPlayer) user.player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutTitle(5, 60, 5));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(StringList.timedisplay[user.lang].replace("%time%", display))));
        finishedtask = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (PlayerCoord != -1) {
                clear(currentMode.world);
                user.player.teleport(new Location(user.player.getWorld(), PlayerCoord + 0.5, 100, (currentMode.inclined ? PlayerCoord : -4) + 0.5, currentMode.inclined ? 225 : 0, 0));
                waiting = false;
                Inventory inventory = user.player.getInventory();
                ItemStack stack = ItemStackList.Blocks[blockskin][user.lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
            }
        }, 40);
    }

    public void validPlaceBlock(int x, int y, int z) {
        if (replay == null) {
            times = MinecraftServer.currentTick;
            replay = new Replay(this, didntevenfuckingstart.clone());
            replay.replayRecorder.PlayerMoveEvent(asyncLoc);
        }
        replay.replayRecorder.BlockPlaceEvent(user.player.getWorld().getBlockAt(x, y, z));
        Inventory inventory = user.player.getInventory();
        int empty = 1 - user.player.getInventory().getHeldItemSlot();
        if (inventory.getItem(empty) == null)
            inventory.setItem(empty, ItemStackList.Blocks[blockskin][user.lang]);
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
                        SideBar.getSidebar(p).setLineByScore(s.get(j - i), 6 - j);
                    }
                }
            }
        }
    }

    public void openSettings() {
        Inventory inventory = Bukkit.createInventory(ItemStackList.settings, 27, StringList.Settings[user.lang]);
        inventory.setItem(12, ItemStackList.openkit[user.lang]);
        if (!user.player.getWorld().getName().equals("Custom"))
            inventory.setItem(14, ItemStackList.Reset[user.lang]);
        else if (user.player.getInventory().getItem(5) != null) {
            String[] lore = StringList.LoopLore[user.lang];
            inventory.setItem(14, new ItemBuilder(Material.MINECART).name(lore[0]).lore(lore[1], hits.isEmpty() ? lore[3] : lore[2]).build());
        }
        user.player.openInventory(inventory);
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
