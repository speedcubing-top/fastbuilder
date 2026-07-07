package top.speedcubing.fastbuilder;

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
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;
import top.speedcubing.fastbuilder.core.BlockEditor;
import top.speedcubing.fastbuilder.core.Core;
import top.speedcubing.fastbuilder.core.Custom;
import top.speedcubing.fastbuilder.core.timed.TimedMode;
import top.speedcubing.fastbuilder.list.ItemStackList;
import top.speedcubing.fastbuilder.list.StringList;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.fastbuilder.replay.ReplayViewer;
import top.speedcubing.fastbuilder.replay.io.ReplayIO;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.TitleType;
import top.speedcubing.lib.bukkit.WorldUtils;
import top.speedcubing.server.lang.LangInv;
import top.speedcubing.server.lang.LangItem;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class Member {
    public static Map<Integer, Member> users = new HashMap<>();

    public static Member getMember(CommandSender sender) {
        User user = User.getUser(sender);
        return user == null ? null : getMember(user.id);
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
    public List<Block> placedBlockList = new ArrayList<>();
    public int PlayerCoord;
    public int ReplayCoord;
    public int replayRoomSlot = -1;

    public TimedMode currentMode;

    //Replay

    public Replay replay;
    public ReplayViewer replayViewer;
    public int lasttime;
    public TimedMode replayMode;

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
    public boolean animation;
    public int placedBlock;

    public Member(Player player) {
        this.user = User.getUser(player);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(0, 30, 5));
        this.asyncLoc = new Location(user.bGetWorld(), 0, 0, 0);
        users.put(user.id, this);
        blockskin = user.dbSelect("fastbuild_blockskin").getInt(0);
    }

    public void clear(World world, boolean animation) {
        if (animation && !currentMode.inclined && this.animation) {
            TreeMap<Integer, Set<Block>> blockToAnimate = new TreeMap<>(Comparator.comparingInt(o -> o));

            Iterator<Block> blockIterator = placedBlockList.iterator();
            while (blockIterator.hasNext()) {
                Block b = blockIterator.next();
                if (b.getZ() < 0) {
                    Main.setTypeCheck(world.getBlockAt(b.getX(), b.getY(), b.getZ()), Material.AIR, 23);
                    blockIterator.remove();
                } else {
                    if (!blockToAnimate.containsKey(b.getZ())) {
                        blockToAnimate.put(b.getZ(), new HashSet<>());
                    }
                    blockToAnimate.get(b.getZ()).add(b);
                }
            }
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (!blockToAnimate.isEmpty()) {
                        for (Block b : blockToAnimate.firstEntry().getValue()) {
                            toFallingBlock(b);
                        }
                        blockToAnimate.pollFirstEntry();
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(speedcubingServer.getInstance(), 0, 1);
        } else {
            for (Block b : placedBlockList) {
                Main.setTypeCheck(world.getBlockAt(b.getX(), b.getY(), b.getZ()), Material.AIR, 23);
            }
        }
        placedBlockList.clear();
    }

    public void toFallingBlock(Block b) {
        b.getWorld().spawnFallingBlock(b.getLocation().add(0.5, 0, 0.5), b.getType(), b.getData()).setDropItem(false);
        b.setType(Material.AIR);
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
        Replay.replayReaderPool.execute(() -> {
            try {
                joinReplay(ReplayIO.readAuto(replayID), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void joinReplay(Replay replay, boolean lastAttempt) {
        if (replayViewer != null)
            replayViewer.unwatch();
        if (replay == null) {
            user.sendMessage("%lang_general_notfound%");
            return;
        }

        TimedMode replayMode = Core.getInstanceByID(replay.replayModeID);
        this.replayMode = replayMode;

        int room = -1;
        for (int h = 0; h < replayMode.size; h++) {
            if (replayMode.replayRooms[h]) {
                room = h;
                break;
            }
        }
        if (room == -1) {
            user.sendMessage("%lang_fb_nomap%");
            return;
        }
        replayMode.replayRooms[room] = false;
        replayRoomSlot = room;
        ReplayCoord = replayMode.roomToCoord(room) / 8 * 500;
        if (lastAttempt)
            replay.tickSize = replay.ticks.size();

        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
            user.bGetInventory().clear();
            user.player.sendTitle(new Title("§aReplay Loading...", "", 0, 100, 0));
            user.bSetGameMode(GameMode.ADVENTURE);
            user.bSetAllowFlight(true);
            user.bSetFlying(true);
            setLoc(replayMode.replayWorld, ReplayCoord + 0.5, 100, replayMode.inclined ? ReplayCoord + 0.5 : -4.5, replayMode.inclined ? 225 : 0, 0);

            replayViewer = new ReplayViewer(this, replay, lastAttempt);
            if (replayMode.inclined) {
                WorldUtils.cloneArea(-3, 91, 3, 33, 106, -33, -3 + ReplayCoord, 91, 3 + ReplayCoord, replayMode.replayWorld);
            } else {
                WorldUtils.cloneArea(-4, 83, -9, 5, 132, 100, -4 + ReplayCoord, 83, -9, replayMode.replayWorld);
            }

            user.player.resetTitle();
            user.bGetInventory().clear();
            user.setLangItem(2, ItemStackList.START);
            user.setLangItem(4, ItemStackList.TICK);
            user.setLangItem(6, ItemStackList.QUITREPLAY);
            if (!lastAttempt)
                user.setLangItem(8, ItemStackList.COPYID);
        });
    }

    public void finish() {
        int time = replay.replayRecorder.NewTick - 1;
        double TASrate = (double) (MinecraftServer.currentTick - times) / time;
        boolean TAS = TASrate > 10 / 9D || TASrate < 9D / 10;
        boolean OVERLIMIT = time < currentMode.limit;
        boolean qualified = !TAS && !OVERLIMIT;
        if (OVERLIMIT)
            user.sendMessage("%lang_fb_score_out%");
        replay.replayRecorder.pause(true, TAS);
        replay = null;
        waiting = true;
        int msrecord = time * 5;
        String display = Core.tickToTime(time);
        int last = user.dbSelect("fb_" + currentMode.sqlId + "_pb").getInt(0);
        if ((last == 0 || last > msrecord) && qualified) {
            user.dbUpdate("fb_" + currentMode.sqlId + "_pb=" + msrecord);
            SideBar.getSidebar(user.player).setLineByScore(StringList.SideBar[user.lang][4] + display, 9);
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                fw(user.player);
                fw(user.player);
                fw(user.player);
            });
            if (last == 0)
                user.sendMessage("%lang_fb_firstscore%", Core.tickToTime(time));
            else
                user.sendMessage("%lang_fb_pb%", Core.tickToTime(last / 5), Core.tickToTime(time), Core.tickToTime(last / 5 - time));
        }
        if (qualified)
            currentMode.a(this, time);
        user.sound(Sound.LEVEL_UP);
        user.sendLangTitle(TitleType.TITLE, "%lang_fb_time_display%", display);
        finishedtask = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            if (PlayerCoord != -1) {
                clear(currentMode.world, true);
                setLoc(user.bGetWorld(), PlayerCoord + 0.5, 100, (currentMode.inclined ? PlayerCoord : -4) + 0.5, currentMode.inclined ? 225 : 0, 0);
                waiting = false;
                Inventory inventory = user.bGetInventory();
                ItemStack stack = ItemStackList.Blocks[blockskin];
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
            replay.replayRecorder.playerMoveEvent(asyncLoc);
        }
        replay.replayRecorder.blockPlaceEvent(x, y, z);
        int empty = 1 - user.bGetInventory().getHeldItemSlot();
        if (empty > 0) {
            Inventory inventory = user.bGetInventory();
            if (inventory.getItem(empty) == null)
                inventory.setItem(empty, ItemStackList.Blocks[blockskin]);
        }
    }

    public void Fell(boolean fell) {
        boolean reset = false;
        if (waiting) {
            reset = true;
            Bukkit.getScheduler().cancelTask(finishedtask);
            waiting = false;
            Inventory inventory = user.bGetInventory();
            ItemStack stack = ItemStackList.Blocks[blockskin];
            inventory.setItem(0, stack);
            inventory.setItem(1, stack);
        } else {
            if (replay != null) {
                reset = true;
                Inventory inventory = user.bGetInventory();
                ItemStack stack = ItemStackList.Blocks[blockskin];
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
                if (newroom == -1) user.sendMessage("%lang_fb_nomap%");
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
        if (reset) { //這是他媽的清除方塊
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> clear(user.bGetWorld(), true));
        }
    }

    public void updateSidebar(String name, TimedMode mode, boolean quit) {
        int i = mode.session.indexOf(this);
        if (quit) {
            clear(mode.world, false);
            placedBlockList.clear();
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
        LangInv inventory = new LangInv(27, "%lang_inv_settings%");

        inventory.setItem(ItemStackList.openkit, (r) -> {
            BlockEditor.createMainPage(r.getPlayer());
        }, 12);

        String animationStatus = !animation ? "§aOn" : "§cOff"; //if off show on, if on show off
        inventory.setItem(new LangItem(Material.FEATHER).name("§eBlock Animation")
                .lore2("§aClick here to turn " + animationStatus), (r) -> {
            Player player = r.getPlayer();
            Member.getMember(player).animation = !Member.getMember(player).animation;
            Member.getMember(player).openSettings();
        }, 13);

        if (!user.bGetWorld().getName().equals("Custom")) {
            inventory.setItem(ItemStackList.Reset, 14);
        } else if (user.bGetInventory().getItem(5) != null) {
            inventory.setItem(new LangItem(Material.MINECART).name("%fb_item_custom_loop%").lore2("%fb_lore_custom_loop%", (hits.isEmpty() ? "%lang_general_off%" : "%lang_general_on%")), 14);
        }
        inventory.setClickEvent((r) -> {
            Player player = r.getPlayer();
            Member member = Member.getMember(player);
            if (!player.getWorld().getName().equals("Custom"))
                member.user.openLangInventory(ItemStackList.ResetConfirm);
            else if (player.getInventory().getItem(5) != null) {
                int[] center = member.Center;
                boolean Loop = member.hits.isEmpty();
                if (Loop)
                    Custom.AddBB(center[0], center[1], member.PlayerCoord, member);
                else
                    member.hits.clear();
                for (int z = -1; z <= 1; z++) {
                    Main.setTypeCheck(player.getWorld().getBlockAt(center[0] - 1, center[1] + 100, member.PlayerCoord + z), Loop ? Material.AIR : Material.GOLD_PLATE, 22);
//                                player.getWorld().getBlockAt(center[0] - 1, center[1] + 100, member.PlayerCoord + z).setType(Loop ? Material.AIR : Material.GOLD_PLATE);
                }
                player.closeInventory();
            }
        }, 14);
        user.openLangInventory(inventory);
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
