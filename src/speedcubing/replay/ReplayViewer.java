package speedcubing.replay;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
    public final Member member;
    public final int locationlength;
    public final int breaklength;
    public final int blocklength;
    public final int sneaklength;
    public final int handlength;
    public boolean playing;
    public NPC npc;
    public final Timer timer;

    public ReplayViewer(Member member, int slot) {
        replay = ReplayCompressor.read(member, slot);
        player = member.player;
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        this.member = member;
        locationlength = replay.LocationTicks.size();
        breaklength = replay.BreakTicks.size();
        blocklength = replay.BlockTicks.size();
        sneaklength = replay.SneakTicks.size();
        handlength = replay.HandItemTicks.size();
        currentItem = replay.StartItem;
        npc = new NPC(replay.name, UUID.randomUUID(), true).setSkin(replay.value, replay.signature).world(player.getWorld().getName())
                .setClickEvent(((player1, enumEntityUseAction) -> {
                    if (enumEntityUseAction == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                            ((CraftPlayer) player1).getHandle().playerConnection.sendPacket(new PacketPlayOutCamera(npc.entityPlayer));
                            player1.setGameMode(GameMode.SPECTATOR);
                            player1.setSpectatorTarget(npc.entityPlayer.getBukkitEntity());
                        });
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
        nextLocation = locationlength == 0 ? -1 : replay.LocationTicks.get(0);
        nextBreak = breaklength == 0 ? -1 : replay.BreakTicks.get(0);
        nextSneak = sneaklength == 0 ? -1 : replay.SneakTicks.get(0);
        nextBlock = blocklength == 0 ? -1 : replay.BlockTicks.get(0);
        nextHand = handlength == 0 ? -1 : replay.HandItemTicks.get(0);
        npc.setSneaking(replay.StartSneaking).setItemInHand(replay.StartItem).setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
    }

    void actionbar() {
        PlayerUtils.sendActionBar(player, "§e" + (tick < 1 ? "DNS" : FastBuilderUtils.tickToTime(tick - 1)) + " §8/§e " + FastBuilderUtils.tickToTime(locationlength - 2));
    }

    public void start() {
        if (!playing)
            if (tick != locationlength - 1) {
                player.getInventory().setItem(2, ItemStackList.PAUSE[member.lang]);
                task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                    addoneTick();
                    if (tick == locationlength - 1) {
                        Bukkit.getScheduler().cancelTask(task);
                        playing = false;
                        player.getInventory().setItem(2, ItemStackList.RESTART[member.lang]);
                    }
                }, 0, 1);
            } else {
                tick = 0;
                playing = true;
                for (Block b : replay.BlockActions) {
                    player.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ()).setType(Material.AIR);
                }
                Locationindex = 0;
                Breakindex = 0;
                Blockindex = 0;
                SneakIndex = 0;
                Handindex = 0;
                resetNPC();
                player.getInventory().setItem(2, ItemStackList.START[member.lang]);
                npc.updateNpcLocation();
            }
        else {
            player.getInventory().setItem(2, ItemStackList.START[member.lang]);
            Bukkit.getScheduler().cancelTask(task);
        }
        playing = !playing;
    }

    public void minusTick() {
        if (tick != 0) {
            tick--;
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
            actionbar();
        }
    }

    public void addoneTick() {
        if (tick != locationlength - 1) {
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
            actionbar();
        }
    }

    public void unwatch() {
        timer.cancel();
        Bukkit.getScheduler().cancelTask(task);
        member.replayPlayer = null;
    }
}
