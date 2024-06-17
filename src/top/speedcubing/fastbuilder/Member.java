package top.speedcubing.fastbuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import top.speedcubing.fastbuilder.Core.Core;
import top.speedcubing.fastbuilder.Core.timed.TimedMode;
import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.List.StringList;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.fastbuilder.replay.ReplayCompressor;
import top.speedcubing.fastbuilder.replay.ReplayViewer;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.TitleType;
import top.speedcubing.lib.bukkit.WorldUtils;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.player.User;

public class Member {
    public static Map<Integer, Member> users = new HashMap<>();

    public static Member getMember(CommandSender sender) {
        return getMember(User.getUser(sender).id);
    }

    public static Member getMember(int id) {
        return users.get(id);
    }

    public final User user;

    //replay page
    public Map<Integer, String> replayPageList = new HashMap<>();
    public boolean isFavoritePage;

    public int times;
    public boolean waiting;
    public int blockskin;
    public int MainTimer;
    public int finishedtask;
    public Set<Block> Blocks = new HashSet<>();
    public int PlayerCoord;
    public int ReplayCoord;

    //Replay

    public Replay replay;
    public ReplayViewer replayViewer;
    public int lasttime;

    public TimedMode currentMode;

    //async things

    public Location didntevenfuckingstart;
    public final Location asyncLoc;
    public int asyncHeldItemSlot;
    //plate
    public Set<AxisAlignedBB> hits = new HashSet<>();

    //other
    public int[] LengthSettings;
    public int[] Center;
    public int Blockplaced;
    public boolean finishedOnce;
    public boolean finishNext;
    public Location tped = new Location(Bukkit.getWorlds().get(0), 0, 0, 0, 0, 0);
    public boolean locationLock;
    public boolean openingBook;
    public int placedBlock;

    public Member(Player player) {
        this.user = User.getUser(player);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(0, 30, 5));
        this.asyncLoc = new Location(user.bGetWorld(), 0, 0, 0);
        users.put(user.id, this);
        blockskin = user.dbSelect("fastbuild_blockskin").getInt();
    }

    public void clear(World world) {
        for (Block b : Blocks) {
            Main.setTypeCheck(world.getBlockAt(b.getX(), b.getY(), b.getZ()), Material.AIR, 23);
//            world.getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
        }
        Blocks.clear();
    }

    public boolean isInReplay() {
        return user.bGetWorld().getName().startsWith("map_fb_replay");
    }

    public void setLoc(World world, double x, double y, double z, float yaw, float pitch) {
        tped = new Location(world, x, y, z, yaw, pitch);
        user.bTeleport(tped);
        locationLock = true;
    }

    public void joinReplayLastAttempt() {
        joinReplay(currentMode.lastAttempt.get(this), true);
    }

    public void joinReplayByReplayID(String replayID) {
        joinReplay(ReplayCompressor.read(replayID), false);
    }

    private void joinReplay(Replay replay, boolean lastAttempt) {
        if (replayViewer != null)
            replayViewer.unwatch();
        if (replay != null) {
            int room = 1;
            TimedMode replayMode = Main.modes.get(replay.replayModeID);
            while (replayMode.replayRoom.contains(room))
                room++;
            replayMode.replayRoom.add(room);
            ReplayCoord = replayMode.roomToCoord(room) / 8 * 500;
            user.bSetGameMode(GameMode.ADVENTURE);
            user.bSetAllowFlight(true);
            user.bSetFlying(true);
            setLoc(replayMode.replayWorld, ReplayCoord + 0.5, 100, replayMode.inclined ? ReplayCoord + 0.5 : -4.5, replayMode.inclined ? 225 : 0, 0);
            if (lastAttempt)
                replay.tickSize = replay.ticks.size();
            replayViewer = new ReplayViewer(this, replay, lastAttempt);
            if (replayMode.inclined)
                WorldUtils.cloneArea(-3, 91, 3, 33, 106, -33, -3 + ReplayCoord, 91, 3 + ReplayCoord, replayMode.replayWorld);
            else
                WorldUtils.cloneArea(-4, 83, -9, 5, 132, 100, -4 + ReplayCoord, 83, -9, replayMode.replayWorld);

            user.bGetInventory().clear();
            user.bGetInventory().setItem(2, ItemStackList.START[user.lang]);
            user.bGetInventory().setItem(4, ItemStackList.TICK[user.lang]);
            user.bGetInventory().setItem(6, ItemStackList.QUITREPLAY[user.lang]);
            if (!lastAttempt)
                user.bGetInventory().setItem(8, ItemStackList.COPYID[user.lang]);
        } else
            user.sendLangMessage(StringList.replayNotFound);
    }

    public void finish() {
        int time = replay.replayRecorder.NewTick - 1;
        double TASrate = (double) (MinecraftServer.currentTick - times) / time;
        boolean TAS = TASrate > 10 / 9D || TASrate < 9D / 10;
        boolean OVERLIMIT = time < currentMode.limit;
        boolean qualified = !TAS && !OVERLIMIT;
        if (OVERLIMIT)
            user.sendLangMessage(StringList.tooFast);
        replay.replayRecorder.pause(true, TAS);
        replay = null;
        waiting = true;
        int msrecord = time * 5;
        String display = Core.tickToTime(time);
        int last = user.dbSelect("fb_" + currentMode.sqlId + "_pb").getInt();
        if ((last == 0 || last > msrecord) && qualified) {
            user.dbUpdate("fb_" + currentMode.sqlId + "_pb=" + msrecord);
            SideBar.getSidebar(user.player).setLineByScore(StringList.SideBar[user.lang][4] + display, 9);
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                fw(user.player);
                fw(user.player);
                fw(user.player);
            });
            if (last == 0)
                user.sendLangMessage(StringList.firstScore, Core.tickToTime(time));
            else
                user.sendLangMessage(StringList.pb, Core.tickToTime(last / 5), Core.tickToTime(time), Core.tickToTime(last / 5 - time));
        }
        if (qualified)
            currentMode.a(this, time);
        user.sound(Sound.LEVEL_UP);
        PlayerUtils.sendTitle(user.player, TitleType.TITLE, StringList.timedisplay.get(user.lang).replace("%1%", display));
        finishedtask = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (PlayerCoord != -1) {
                clear(currentMode.world);
                setLoc(user.bGetWorld(), PlayerCoord + 0.5, 100, (currentMode.inclined ? PlayerCoord : -4) + 0.5, currentMode.inclined ? 225 : 0, 0);
                waiting = false;
                Inventory inventory = user.bGetInventory();
                ItemStack stack = ItemStackList.Blocks[blockskin][user.lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
            }
        }, 40);
    }

    public void validPlaceBlock(int x, int y, int z) {
        placedBlock++;
        if (replay == null) {
            times = MinecraftServer.currentTick;
            replay = new Replay(this, didntevenfuckingstart.clone());
            replay.replayRecorder.PlayerMoveEvent(asyncLoc);
        }
        replay.replayRecorder.BlockPlaceEvent(x, y, z);
        int empty = 1 - user.bGetInventory().getHeldItemSlot();
        if (empty > 0) {
            Inventory inventory = user.bGetInventory();
            if (inventory.getItem(empty) == null)
                inventory.setItem(empty, ItemStackList.Blocks[blockskin][user.lang]);
        }
    }

    public void Fell(boolean fell) {
        boolean reset = false;
        if (waiting) {
            reset = true;
            Bukkit.getScheduler().cancelTask(finishedtask);
            waiting = false;
            Inventory inventory = user.bGetInventory();
            ItemStack stack = ItemStackList.Blocks[blockskin][user.lang];
            inventory.setItem(0, stack);
            inventory.setItem(1, stack);
        } else {
            if (replay != null) {
                reset = true;
                Inventory inventory = user.bGetInventory();
                ItemStack stack = ItemStackList.Blocks[blockskin][user.lang];
                inventory.setItem(0, stack);
                inventory.setItem(1, stack);
                SideBar.getSidebar(user.player).setLineByScore(StringList.SideBar[user.lang][9] + Core.tickToTime(replay.replayRecorder.NewTick - 1), 1);
                replay.replayRecorder.pause(false, false);
                currentMode.lastAttempt.put(this, replay);
                replay = null;
            } else if (!fell) {
                Location location = user.bGetLocation();
                int coord = PlayerCoord;
                int oldroom = currentMode.coordToRoom(coord);
                int newroom = Core.cal(oldroom, currentMode.inclined ? location.getX() + location.getZ() > coord * 2 : location.getX() > coord, currentMode.rooms);
                if (newroom == -1) user.sendLangMessage(StringList.nomapsavaliable);
                else {
                    PlayerCoord = currentMode.roomToCoord(newroom);
                    currentMode.setPressurePlate(this);
                    currentMode.rooms[oldroom] = true;
                    currentMode.rooms[newroom] = false;
                    System.out.print("[Swap] " + oldroom + " " + newroom + " " + user.bGetName() + " " + currentMode.world.getName());
                }
            }
            user.bCloseInventory();
        }
        setLoc(user.bGetWorld(), PlayerCoord + 0.5, 100, (currentMode.inclined ? PlayerCoord : -4) + 0.5, currentMode.inclined ? 225 : 0, 0);
        if (reset)
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> clear(user.bGetWorld()));
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
        Inventory inventory = Bukkit.createInventory(ItemStackList.settings, 27, StringList.Settings.get(user.lang));
        inventory.setItem(12, ItemStackList.openkit[user.lang]);
        if (!user.bGetWorld().getName().equals("Custom"))
            inventory.setItem(14, ItemStackList.Reset[user.lang]);
        else if (user.bGetInventory().getItem(5) != null) {
            String[] lore = StringList.LoopLore[user.lang];
            inventory.setItem(14, new ItemBuilder(Material.MINECART).name(lore[0]).lore(lore[1], hits.isEmpty() ? lore[3] : lore[2]).build());
        }
        user.bOpenInventory(inventory);
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
