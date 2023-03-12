package top.speedcubing.fastbuilder.replay;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.gui.FastBuilderUtils;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.entity.NPC;
import top.speedcubing.lib.bukkit.inventory.ItemUtils;

import java.util.Timer;
import java.util.TimerTask;

public class ReplayViewer {
    int task;

    ItemStack currentItem;

    int tick;
    public final Replay replay;
    public final Player player;
    public final Member member;
    boolean playing;
    public boolean isSpec;
    public NPC npc;
    final Timer timer;
    public boolean lastAttempt;

    //    public EntityPlayer test;
    public ReplayViewer(Member member, Replay replay, boolean lastAttempt) {
        WorldServer world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
//        test = new EntityPlayer(((CraftServer)Bukkit.getServer()).getServer(),world,new GameProfile(UUID.randomUUID(),"test"), new PlayerInteractManager(world));
        this.lastAttempt = lastAttempt;
        player = member.user.player;
        this.replay = replay;
        this.member = member;
//        String[] sidebar = StringList.replaySidebar[member.user.lang];
//        new SideBar(player, sidebar[0])
//                .staticLine(sidebar[1], 7)
//                .staticLine(sidebar[2], 6)
//                .changeableLine(sidebar[3] + replay.StartLocation.getX(), 5)
//                .changeableLine(sidebar[4] + replay.StartLocation.getY(), 4)
//                .changeableLine(sidebar[5] + replay.StartLocation.getZ(), 3)
//                .changeableLine(sidebar[6] + replay.StartLocation.getYaw(), 2)
//                .changeableLine(sidebar[7] + replay.StartLocation.getPitch(), 1)
//                .staticLine(sidebar[8], 0).create();
        currentItem = replay.StartItem;
        serial(member.ReplayCoord);
        npc = new NPC(replay.name, null, true, false, false).setSkin(replay.value, replay.signature).world(player.getWorld().getName())
                .setClickEvent(((p, enumEntityUseAction) -> {
                    if (enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutGameStateChange(3, 3));
                        connection.sendPacket(new PacketPlayOutCamera(npc.entityPlayer));
                        PacketPlayOutAbilities abilities = new PacketPlayOutAbilities();
                        abilities.b(true);
                        abilities.c(true);
                        connection.sendPacket(abilities);
//                        test.setSpectatorTarget(npc.entityPlayer);
                        p.teleport(new Location(p.getWorld(), member.ReplayCoord, 90, 0, 0, 0));
                        isSpec = true;
                    }
                }));
        resetNPC(false);
        npc.addListener(player);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                actionbar();
            }
        }, 0, 1000);
        npc.spawn().hideFromTab(4000).updateNpcLocation();
    }

    private void resetNPC(boolean resetSidebar) {
        npc.setSneaking(replay.StartSneaking).setItemInHand(replay.StartItem);
        if (resetSidebar)
            setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
        else
            npc.setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
    }

    private void actionbar() {
        PlayerUtils.sendActionBar(player, "§e" + (tick < 1 ? "DNS" : FastBuilderUtils.tickToTime(tick - 1)) + " §8/§e " + FastBuilderUtils.tickToTime(replay.ticks.size() - 2));
    }

    public void start() {
        if (!playing)
            if (tick != replay.ticks.size() - 1) {
                player.getInventory().setItem(2, ItemStackList.PAUSE[member.user.lang]);
                task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                    addoneTick();
                    if (tick == replay.ticks.size() - 1) {
                        Bukkit.getScheduler().cancelTask(task);
                        playing = false;
                        player.getInventory().setItem(2, ItemStackList.RESTART[member.user.lang]);
                    }
                }, 0, 1);
            } else {
                tick = 0;
                playing = true;
                for (ReplayTick replayTick : replay.ticks) {
                    if (replayTick.blockLocation != null) {
                        Main.setTypeCheck(player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ()), Material.AIR, 2);
//                        player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ()).setType(Material.AIR);
                    }
                }
                resetNPC(true);
                player.getInventory().setItem(2, ItemStackList.START[member.user.lang]);
                npc.updateNpcLocation();
            }
        else {
            player.getInventory().setItem(2, ItemStackList.START[member.user.lang]);
            Bukkit.getScheduler().cancelTask(task);
        }
        playing = !playing;
    }

    private void setLocation(double x, double y, double z, float headRotation, float pitch) {
        npc.setLocation(x, y, z, headRotation, pitch);
//        System.out.println(test.locX);
//        player.sendMessage(x + " " + npc.entityPlayer.locX + " " + npc.entityPlayer.lastX + " " + Reflections.getField(((CraftPlayer) player).getHandle(), "bU") + " " + player.getLocation().getX());
//        String[] sidebar = StringList.replaySidebar[member.user.lang];
//        SideBar.getSidebar(player)
//                .setLineByScore(sidebar[3] + x, 5)
//                .setLineByScore(sidebar[4] + y, 4)
//                .setLineByScore(sidebar[5] + z, 3)
//                .setLineByScore(sidebar[6] + headRotation, 2)
//                .setLineByScore(sidebar[7] + pitch, 1);
    }

    public void minusTick() {
        if (tick != 0) {
            if (!playing && player.getInventory().getItem(2).getData().getData() == 14)
                player.getInventory().setItem(2, ItemStackList.START[member.user.lang]);
            tick--;
            ReplayTick replayTick = replay.ticks.get(tick);
            ReplayTick last = tick == 0 ? null : replay.ticks.get(tick - 1);
            if (tick == 0)
                setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
            else
                setLocation(last.location.getX(), last.location.getY(), last.location.getZ(), last.location.getYaw(), last.location.getPitch());
            npc.updateNpcLocation();
            if (replayTick.blockLocation != null) {
                Main.setTypeCheck(player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ()), Material.AIR, 1);
//                player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ()).setType(Material.AIR);
            }
            if (replayTick.breakLocation != null) {
                Block newP = player.getWorld().getBlockAt(replayTick.breakLocation.getX(), replayTick.breakLocation.getY(), replayTick.breakLocation.getZ());
                Main.setTypeCheck(newP, Material.getMaterial(replayTick.breakMaterial), 3);
//                newP.setType(Material.getMaterial(replayTick.breakMaterial));
                Main.setDataCheck(newP, replayTick.breakDatas);
//                newP.setData(replayTick.breakDatas);
            }
            if (replayTick.sneak != null)
                npc.setSneaking(!replayTick.sneak);
            if (replayTick.handMaterial != null) {
                if (tick == 0)
                    currentItem = replay.StartItem;
                else {
                    currentItem = new ItemStack(last.handMaterial, 1, last.handDatas);
                    if (last.handEchants)
                        ItemUtils.glow(currentItem);
                }
                npc.setItemInHand(currentItem);
            }
            actionbar();
        }
    }

    public void addoneTick() {
        if (tick != replay.ticks.size() - 1) {
            ReplayTick replayTick = replay.ticks.get(tick);
            setLocation(replayTick.location.getX(), replayTick.location.getY(), replayTick.location.getZ(), replayTick.location.getYaw(), replayTick.location.getPitch());
            npc.updateNpcLocation();
            if (replayTick.blockLocation != null) {
                Block newP = player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ());
                Main.setTypeCheck(newP, Material.getMaterial(currentItem.getTypeId()), 4);
//                newP.setType(Material.getMaterial(currentItem.getTypeId()));
                Main.setDataCheck(newP, currentItem.getData().getData());
//                newP.setData(currentItem.getData().getData());
                npc.animation(0);
            }
            if (replayTick.breakLocation != null) {
                Main.setTypeCheck(player.getWorld().getBlockAt(replayTick.breakLocation.getX(), replayTick.breakLocation.getY(), replayTick.breakLocation.getZ()), Material.AIR, 5);
//                player.getWorld().getBlockAt(replayTick.breakLocation.getX(), replayTick.breakLocation.getY(), replayTick.breakLocation.getZ()).setType(Material.AIR);
                npc.animation(0);
            }
            if (replayTick.sneak != null)
                npc.setSneaking(replayTick.sneak);
            if (replayTick.handMaterial != null) {
                currentItem = new ItemStack(replayTick.handMaterial, 1, replayTick.handDatas);
                if (replayTick.handEchants)
                    ItemUtils.glow(currentItem);
                npc.setItemInHand(currentItem);
            }
            tick++;
            actionbar();
        }
    }

    public void unwatch() {
        timer.cancel();
        Bukkit.getScheduler().cancelTask(task);
        member.replayPlayer = null;
        member.quitReplay = System.currentTimeMillis();
        if (lastAttempt)
            serial(-member.PlayerCoord);
    }

    void serial(int coord) {
        boolean inclined = member.currentMode.inclined;
        World world = replay.StartLocation.getWorld();
        replay.StartLocation.setX(replay.StartLocation.getX() + coord);
        if (inclined)
            replay.StartLocation.setZ(replay.StartLocation.getZ() + coord);
        for (ReplayTick replayTick : replay.ticks) {
            replayTick.location.setX(replayTick.location.getX() + coord);
            if (inclined)
                replayTick.location.setZ(replayTick.location.getZ() + coord);
            if (replayTick.blockLocation != null)
                replayTick.blockLocation = world.getBlockAt(replayTick.blockLocation.getX() + coord, replayTick.blockLocation.getY(), replayTick.blockLocation.getZ() + (inclined ? coord : 0));
            if (replayTick.breakLocation != null)
                replayTick.breakLocation = world.getBlockAt(replayTick.breakLocation.getX() + coord, replayTick.breakLocation.getY(), replayTick.breakLocation.getZ() + (inclined ? coord : 0));
        }
    }
}
