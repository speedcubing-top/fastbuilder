package speedcubing.replay;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import speedcubing.List.ItemStackList;
import speedcubing.Main;
import speedcubing.Member;
import speedcubing.lib.bukkit.PlayerUtils;
import speedcubing.lib.bukkit.entity.NPC;
import speedcubing.lib.bukkit.inventory.ItemUtils;
import speedcubing.replay.gui.FastBuilderUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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

    public ReplayViewer(Member member, Replay replay, boolean lastAttempt) {
        this.lastAttempt = lastAttempt;
        player = member.user.player;
        this.replay = replay;
        this.member = member;
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        currentItem = replay.StartItem;
        serial(member.ReplayCoord);
        npc = new NPC(replay.name, UUID.randomUUID(), true).setSkin(replay.value, replay.signature).world(player.getWorld().getName())
                .setClickEvent(((player1, enumEntityUseAction) -> {
                    if (enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                        PlayerConnection connection = ((CraftPlayer) player1).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutGameStateChange(3, 3));
                        connection.sendPacket(new PacketPlayOutCamera(npc.entityPlayer));
                        PacketPlayOutAbilities abilities = new PacketPlayOutAbilities();
                        abilities.b(true);
                        abilities.c(true);
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(abilities);
                        ((CraftPlayer) player).getHandle().playerConnection.a(member.ReplayCoord, 90, 0, 0, 0);
                        isSpec = true;
                    }
                }));
        resetNPC();
        npc.listener.add(((CraftPlayer) player).getHandle().playerConnection);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                actionbar();
            }
        }, 0, 1000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc.entityPlayer));
            }
        }, 4000);
        npc.spawn().updateNpcLocation();
    }

    void resetNPC() {
        npc.setSneaking(replay.StartSneaking).setItemInHand(replay.StartItem).setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
    }

    void actionbar() {
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
                    if (replayTick.blockLocation != null)
                        player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ()).setType(Material.AIR);
                }
                resetNPC();
                player.getInventory().setItem(2, ItemStackList.START[member.user.lang]);
                npc.updateNpcLocation();
            }
        else {
            player.getInventory().setItem(2, ItemStackList.START[member.user.lang]);
            Bukkit.getScheduler().cancelTask(task);
        }
        playing = !playing;
    }

    public void minusTick() {
        if (tick != 0) {
            if (!playing && player.getInventory().getItem(2).getData().getData() == 14)
                player.getInventory().setItem(2, ItemStackList.START[member.user.lang]);
            tick--;
            ReplayTick replayTick = replay.ticks.get(tick);
            ReplayTick last = tick == 0 ? null : replay.ticks.get(tick - 1);
            if (tick == 0)
                npc.setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
            else
                npc.setLocation(last.location.getX(), last.location.getY(), last.location.getZ(), last.location.getYaw(), last.location.getPitch());
            npc.updateNpcLocation();
            if (replayTick.blockLocation != null)
                player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ()).setType(Material.AIR);
            if (replayTick.breakLocation != null) {
                Block newP = player.getWorld().getBlockAt(replayTick.breakLocation.getX(), replayTick.breakLocation.getY(), replayTick.breakLocation.getZ());
                newP.setType(Material.getMaterial(replayTick.breakMaterial));
                newP.setData(replayTick.breakDatas);
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
            npc.setLocation(replayTick.location.getX(), replayTick.location.getY(), replayTick.location.getZ(), replayTick.location.getYaw(), replayTick.location.getPitch());
            npc.updateNpcLocation();
            if (replayTick.blockLocation != null) {
                Block newP = player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ());
                newP.setType(Material.getMaterial(currentItem.getTypeId()));
                newP.setData(currentItem.getData().getData());
                npc.animation(0);
            }
            if (replayTick.breakLocation != null) {
                player.getWorld().getBlockAt(replayTick.breakLocation.getX(), replayTick.breakLocation.getY(), replayTick.breakLocation.getZ()).setType(Material.AIR);
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
