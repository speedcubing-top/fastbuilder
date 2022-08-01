package speedcubing.replay;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import speedcubing.Main;
import speedcubing.Member;
import speedcubing.lib.bukkit.entity.NPC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

public class ReplayViewer {
    public static Map<Player,Integer> lastXX = new HashMap<>();
    public int Locationindex;
    public int nextLocation;

    public int Blockindex;
    public int nextBlock;

    public int SneakIndex;
    public int nextSneak;

    public int Handindex;
    public int nextHand;
    public int task;

    public int tick;
    public final Replay replay;
    public final Player player;
    public final int locationlength;
    public final int blocklength;
    public final int sneaklength;
    public final int handlength;
    public boolean playing = false;

    public ReplayViewer(Replay replay, Player player) {
        this.replay = replay;
        this.player = player;
        locationlength = replay.LocationTicks.size();
        blocklength = replay.BlockTicks.size();
        sneaklength = replay.SneakTicks.size();
        handlength = replay.HandItemTicks.size();
    }

    public void togglePlaying() {
        playing = !playing;
    }

    public void spawnReplay() {
        nextLocation = replay.LocationTicks.get(0);
        Set<PlayerConnection> all = new HashSet<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            all.add(((CraftPlayer) p).getHandle().playerConnection);
        }
        replay.npc = new NPC(replay.member.player.getName(), UUID.randomUUID(), true)
                .setSkin(replay.value, replay.signature)
                .setSneaking(replay.StartSneaking)
                .setItemInHand(replay.StartItem)
                .setClickEvent(((player1, enumEntityUseAction) -> {
                    if (enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                        player1.setGameMode(GameMode.SPECTATOR);
                        player1.setSpectatorTarget(Member.getMember(player1).replay.npc.entityPlayer.getBukkitEntity());
                    }
                }))
                .setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
        replay.npc.listener.addAll(all);
        replay.npc.listener.add(((CraftPlayer) player).getHandle().playerConnection);
        replay.npc.spawn();
    }


    public void start() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (playing) {
                addoneTick();
                if (tick == replay.length)
                    Bukkit.getScheduler().cancelTask(task);
            }
        }, 0, 1);
    }

    public void addoneTick() {
        if (tick != replay.length) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(player.getName() + " " + (tick + 1) + " ticks");
            }
            if (locationlength != 0)
                while (tick == nextLocation) {
                    Location loc = replay.LocationActions.get(Locationindex);
                    replay.npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                    replay.npc.updateNpcLocation();
                    Locationindex++;
                    nextLocation = Locationindex == locationlength ? -1 : replay.LocationTicks.get(Locationindex);
                }
            if (sneaklength != 0)
                while (tick == nextSneak) {
                    replay.npc.setSneaking(replay.SneakActions.get(SneakIndex));
                    SneakIndex++;
                    nextSneak = SneakIndex == sneaklength ? -1 : replay.SneakTicks.get(SneakIndex);
                }
            if (handlength != 0)
                while (tick == nextHand) {
                    replay.npc.setItemInHand(replay.HandItemActions.get(Handindex));
                    Handindex++;
                    nextHand = Handindex == handlength ? -1 : replay.HandItemTicks.get(Handindex);
                }
            if (blocklength != 0)
                while (tick == nextBlock) {
                    Block p = replay.BlockActions.get(Blockindex);
                    p.getWorld().getBlockAt(p.getX(), p.getY(), p.getZ()).setType(replay.BlockTypes.get(Blockindex));
                    replay.npc.animation(0);
                    Blockindex++;
                    nextBlock = Blockindex == blocklength ? -1 : replay.BlockTicks.get(Blockindex);
                }
            tick++;
        }
    }
}
