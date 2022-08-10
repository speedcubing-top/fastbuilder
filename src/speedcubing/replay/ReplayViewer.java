package speedcubing.replay;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import speedcubing.Main;
import speedcubing.lib.bukkit.entity.NPC;
import speedcubing.lib.bukkit.inventory.ItemUtils;

import java.util.UUID;

public class ReplayViewer {
    public int Locationindex;
    public int nextLocation;

    public int Breakindex;
    public int nextBreak;

    public int Blockindex;
    public int nextBlock;

    public int SneakIndex;
    public int nextSneak;

    public int Handindex;
    public int nextHand;
    public int task;

    public ItemStack currentItem;

    public int tick;
    public final Replay replay;
    public final Player player;
    public final int locationlength;
    public final int breaklength;
    public final int blocklength;
    public final int sneaklength;
    public final int handlength;
    public boolean playing;

    public NPC npc;

    public ReplayViewer(Replay replay, Player player) {
        this.replay = replay;
        this.player = player;
        locationlength = replay.LocationTicks.size();
        breaklength = replay.BreakTicks.size();
        blocklength = replay.BlockTicks.size();
        sneaklength = replay.SneakTicks.size();
        handlength = replay.HandItemTicks.size();
        nextLocation = replay.LocationTicks.size() == 0 ? -1 : replay.LocationTicks.get(0);
        nextBreak = replay.BreakTicks.size() == 0 ? -1 : replay.BreakTicks.get(0);
        nextSneak = replay.SneakTicks.size() == 0 ? -1 : replay.SneakTicks.get(0);
        nextBlock = replay.BlockTicks.size() == 0 ? -1 : replay.BlockTicks.get(0);
        nextHand = replay.HandItemTicks.size() == 0 ? -1 : replay.HandItemTicks.get(0);
        currentItem = replay.StartItem;
        npc = new NPC(replay.name, UUID.randomUUID(), true)
                .setSkin(replay.value, replay.signature)
                .setSneaking(replay.StartSneaking)
                .setItemInHand(replay.StartItem)
                .setClickEvent(((player1, enumEntityUseAction) -> {
                    if (enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                            player1.setGameMode(GameMode.SPECTATOR);
                            player1.setSpectatorTarget(npc.entityPlayer.getBukkitEntity());
                        });
                    }
                }))
                .world(player.getWorld().getName())
                .setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
        npc.listener.add(((CraftPlayer) player).getHandle().playerConnection);
        npc.spawn().updateNpcLocation();
    }

    public void start() {
        if (!playing)
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                addoneTick();
                if (tick == replay.LocationActions.size() - 1) {
                    Bukkit.getScheduler().cancelTask(task);
                    playing = false;
                }
            }, 0, 1);
        else Bukkit.getScheduler().cancelTask(task);
        playing = !playing;
    }

    public void minusTick() {
        if (tick != 0) {
            tick--;
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(player.getName() + " " + (tick - 1) + " ticks");
            }
            if (locationlength != 0 && Locationindex != 0) {
                if (tick == replay.LocationTicks.get(Locationindex - 1)) {
                    Locationindex--;
                    Location loc = Locationindex == 0 ? replay.StartLocation : replay.LocationActions.get(Locationindex - 1);
                    npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                    npc.updateNpcLocation();
                    nextLocation = replay.LocationTicks.get(Locationindex);
                }
            }
            if (blocklength != 0 && Blockindex != 0) {
                if (tick == replay.BlockTicks.get(Blockindex - 1)) {
                    Blockindex--;
                    Block p = replay.BlockActions.get(Blockindex);
                    player.getWorld().getBlockAt(p.getX(), p.getY(), p.getZ()).setType(Material.AIR);
                    nextBlock = replay.BlockTicks.get(Blockindex);
                }
            }
            if (breaklength != 0 && Breakindex != 0) {
                if (tick == replay.BreakTicks.get(Breakindex - 1)) {
                    Breakindex--;
                    Block p = replay.BreakActions.get(Breakindex);
                    Block newP = player.getWorld().getBlockAt(p.getX(), p.getY(), p.getZ());
                    newP.setType(Material.getMaterial(replay.BreakTypes.get(Breakindex)));
                    newP.setData(replay.BreakDatas.get(Breakindex));
                    nextBreak = replay.BreakTicks.get(Breakindex);
                }
            }
            if (sneaklength != 0 && SneakIndex != 0) {
                if (tick == replay.SneakTicks.get(SneakIndex - 1)) {
                    SneakIndex--;
                    npc.setSneaking(!replay.SneakActions.get(SneakIndex));
                    nextSneak = replay.SneakTicks.get(SneakIndex);
                }
            }
            if (handlength != 0 && Handindex != 0) {
                if (tick == replay.HandItemTicks.get(Handindex - 1)) {
                    Handindex--;
                    if (Handindex != 0) {
                        currentItem = new ItemStack(replay.HandItemActions.get(Handindex - 1), 1, (short) replay.HandItemDatas.get(Handindex - 1));
                        if (replay.HandItemEnchants.get(Handindex - 1))
                            ItemUtils.glow(currentItem);
                    } else currentItem = replay.StartItem;
                    npc.setItemInHand(currentItem);
                    nextHand = replay.HandItemTicks.get(Handindex);
                }
            }
        }
    }

    public void addoneTick() {
        if (tick != replay.LocationActions.size() - 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(player.getName() + " " + (tick + 1) + " ticks");
            }
            if (locationlength != 0) {
                if (tick == nextLocation) {
                    Location loc = replay.LocationActions.get(Locationindex);
                    npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                    npc.updateNpcLocation();
                    Locationindex++;
                    nextLocation = Locationindex == locationlength ? Locationindex : replay.LocationTicks.get(Locationindex);
                }
            }
            if (blocklength != 0) {
                if (tick == nextBlock) {
                    Block p = replay.BlockActions.get(Blockindex);
                    Block newP = player.getWorld().getBlockAt(p.getX(), p.getY(), p.getZ());
                    newP.setType(Material.getMaterial(currentItem.getTypeId()));
                    newP.setData(currentItem.getData().getData());
                    npc.animation(0);
                    Blockindex++;
                    nextBlock = Blockindex == blocklength ? Blockindex : replay.BlockTicks.get(Blockindex);
                }
            }
            if (breaklength != 0)
                if (tick == nextBreak) {
                    Block p = replay.BreakActions.get(Breakindex);
                    player.getWorld().getBlockAt(p.getX(), p.getY(), p.getZ()).setType(Material.AIR);
                    npc.animation(0);
                    Breakindex++;
                    nextBreak = Breakindex == breaklength ? Breakindex : replay.BreakTicks.get(Breakindex);
                }
            if (sneaklength != 0)
                if (tick == nextSneak) {
                    npc.setSneaking(replay.SneakActions.get(SneakIndex));
                    SneakIndex++;
                    nextSneak = SneakIndex == sneaklength ? SneakIndex : replay.SneakTicks.get(SneakIndex);
                }
            if (handlength != 0)
                if (tick == nextHand) {
                    currentItem = new ItemStack(replay.HandItemActions.get(Handindex), 1, (short) replay.HandItemDatas.get(Handindex));
                    if (replay.HandItemEnchants.get(Handindex))
                        ItemUtils.glow(currentItem);
                    npc.setItemInHand(currentItem);
                    Handindex++;
                    nextHand = Handindex == handlength ? Handindex : replay.HandItemTicks.get(Handindex);
                }
            tick++;
        }
    }
}
