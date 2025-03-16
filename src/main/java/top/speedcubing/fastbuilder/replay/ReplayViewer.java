package top.speedcubing.fastbuilder.replay;

import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutAbilities;
import net.minecraft.server.v1_8_R3.PacketPlayOutCamera;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.core.Core;
import top.speedcubing.fastbuilder.list.ItemStackList;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.entity.NPC;
import top.speedcubing.lib.bukkit.inventory.ItemUtils;

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
        this.lastAttempt = lastAttempt;
        player = member.user.player;
        this.member = member;
        currentItem = replay.StartItem;
        npc = new NPC(replay.name, null, true, false, false).setSkin(replay.value, replay.signature).world(player.getWorld().getName())
                .setClickEvent(((r) -> {
                    if (r.getAction() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                        PlayerConnection connection = ((CraftPlayer) r.getPlayer()).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutGameStateChange(3, 3));
                        connection.sendPacket(new PacketPlayOutCamera(npc.entityPlayer));
                        PacketPlayOutAbilities abilities = new PacketPlayOutAbilities();
                        abilities.b(true);
                        abilities.c(true);
                        connection.sendPacket(abilities);
                        r.getPlayer().teleport(new Location(r.getPlayer().getWorld(), member.ReplayCoord, 90, 0, 0, 0));
                        isSpec = true;
                    }
                }));
        //Validate Replay
        ReplayTick tick = null;
        ReplayTick lastTick = null;
        for (int i = 0; i < replay.tickSize; i++) {
            if (i != 0)
                lastTick = tick;
            tick = replay.ticks.get(i);
            if (tick.handMaterial == null && lastTick != null) {
                tick.handMaterial = lastTick.handMaterial;
                tick.handEchants = lastTick.handEchants;
                tick.handDatas = lastTick.handDatas;
            }
        }
        this.replay = replay;
        serial(member.ReplayCoord);
        //NPC
        resetNPC(false);
        npc.addListener(player);
        npc.spawn().hideFromTab(4000).updateNpcLocation();
        //ActionBar
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                actionbar();
            }
        }, 0, 1000);
    }

    private void resetNPC(boolean resetSidebar) {
        npc.setSneaking(replay.StartSneaking).setItemInHand(replay.StartItem);
        if (resetSidebar)
            setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
        else
            npc.setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
    }

    private void actionbar() {
        PlayerUtils.sendActionBar(player, "§e" + (tick < 1 ? "DNS" : Core.tickToTime(tick - 1)) + " §8/§e " + Core.tickToTime(replay.tickSize - 2));
    }

    public void start() {
        if (!playing) {
            if (tick != replay.tickSize - 1) {
                member.user.setLangItem(2, ItemStackList.PAUSE);
                task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
                    addoneTick();
                    if (tick == replay.tickSize - 1) {
                        Bukkit.getScheduler().cancelTask(task);
                        playing = false;
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
                member.user.setLangItem(2, ItemStackList.START);
                npc.updateNpcLocation();
            }
        } else {
            member.user.setLangItem(2, ItemStackList.START);
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
        if (tick == 0) {
            return;
        }

        if (!playing && player.getInventory().getItem(2).getData().getData() == 14)
            member.user.setLangItem(2, ItemStackList.START);
        ReplayTick replayTick = replay.ticks.get(tick - 1);
        ReplayTick last = tick == 1 ? null : replay.ticks.get(tick - 2);

        //NPC Location
        if (tick == 1)
            setLocation(replay.StartLocation.getX(), replay.StartLocation.getY(), replay.StartLocation.getZ(), replay.StartLocation.getYaw(), replay.StartLocation.getPitch());
        else
            setLocation(last.location.getX(), last.location.getY(), last.location.getZ(), last.location.getYaw(), last.location.getPitch());
        npc.updateNpcLocation();

        //Undo Place
        if (replayTick.blockLocation != null) {
            Main.setTypeCheck(player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ()), Material.AIR, 1);
//                player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ()).setType(Material.AIR);
        }

        //Undo Break
        if (replayTick.breakLocation != null) {
            Block newP = player.getWorld().getBlockAt(replayTick.breakLocation.getX(), replayTick.breakLocation.getY(), replayTick.breakLocation.getZ());
            Main.setTypeCheck(newP, Material.getMaterial(replayTick.breakMaterial), 3);
//                newP.setType(Material.getMaterial(replayTick.breakMaterial));
            Main.setDataCheck(newP, replayTick.breakDatas);
//                newP.setData(replayTick.breakDatas);
        }

        //Sneak
        if (replayTick.sneak != null)
            npc.setSneaking(!replayTick.sneak);

        //Hand
        if (replayTick.handMaterial != null && last.handMaterial != null && !replayTick.handMaterial.equals(last.handMaterial)) {
            if (tick == 1)
                currentItem = replay.StartItem;
            else {
                currentItem = new ItemStack(last.handMaterial, 1, last.handDatas);
                if (last.handEchants)
                    ItemUtils.glow(currentItem);
            }
            npc.setItemInHand(currentItem);
        }

        tick--;
        actionbar();
    }

    public void addoneTick() {
        if (tick == replay.tickSize - 1) {
            return;
        }
        if (tick == replay.tickSize - 2) {
            member.user.setLangItem(2, ItemStackList.RESTART);
        }

        ReplayTick replayTick = replay.ticks.get(tick);

        //NPC Location
        setLocation(replayTick.location.getX(), replayTick.location.getY(), replayTick.location.getZ(), replayTick.location.getYaw(), replayTick.location.getPitch());
        npc.updateNpcLocation();

        //Place
        if (replayTick.blockLocation != null) {
            Block newP = player.getWorld().getBlockAt(replayTick.blockLocation.getX(), replayTick.blockLocation.getY(), replayTick.blockLocation.getZ());
            ReplayTick next = replay.ticks.get(tick + 1);
            ItemStack b = currentItem.getType().isBlock() ? currentItem : new ItemStack(next.handMaterial, 1, next.handDatas);
            Main.setTypeCheck(newP, Material.getMaterial(b.getTypeId()), 4);
//                newP.setType(Material.getMaterial(currentItem.getTypeId()));
            Main.setDataCheck(newP, b.getData().getData());
//                newP.setData(currentItem.getData().getData());
            npc.animation(0);
        }

        //Break
        if (replayTick.breakLocation != null) {
            Main.setTypeCheck(player.getWorld().getBlockAt(replayTick.breakLocation.getX(), replayTick.breakLocation.getY(), replayTick.breakLocation.getZ()), Material.AIR, 5);
//                player.getWorld().getBlockAt(replayTick.breakLocation.getX(), replayTick.breakLocation.getY(), replayTick.breakLocation.getZ()).setType(Material.AIR);
            npc.animation(0);
        }

        //Sneak
        if (replayTick.sneak != null) {
            npc.setSneaking(replayTick.sneak);
        }

        //Hand
        if (replayTick.handMaterial != null) {
            ReplayTick last = tick == 0 ? null : replay.ticks.get(tick - 1);
            if (last != null && last.handMaterial != null && !replayTick.handMaterial.equals(last.handMaterial)) {
                currentItem = new ItemStack(replayTick.handMaterial, 1, replayTick.handDatas);
                if (replayTick.handEchants)
                    ItemUtils.glow(currentItem);
                npc.setItemInHand(currentItem);
            }
        }
        tick++;
        actionbar();
    }

    public void unwatch() {
        timer.cancel();
        Bukkit.getScheduler().cancelTask(task);
        member.replayViewer = null;
        if (lastAttempt)
            serial(-member.PlayerCoord);
    }

    void serial(int coord) {
        boolean inclined = member.replayMode.inclined;
        replay.StartLocation.setX(replay.StartLocation.getX() + coord);
        if (inclined)
            replay.StartLocation.setZ(replay.StartLocation.getZ() + coord);
        for (ReplayTick replayTick : replay.ticks) {
            replayTick.location.setX(replayTick.location.getX() + coord);
            if (inclined)
                replayTick.location.setZ(replayTick.location.getZ() + coord);
            if (replayTick.blockLocation != null)
                replayTick.blockLocation = new BlockPosition(replayTick.blockLocation.getX() + coord, replayTick.blockLocation.getY(), replayTick.blockLocation.getZ() + (inclined ? coord : 0));
            if (replayTick.breakLocation != null)
                replayTick.breakLocation = new BlockPosition(replayTick.breakLocation.getX() + coord, replayTick.breakLocation.getY(), replayTick.breakLocation.getZ() + (inclined ? coord : 0));
        }
    }
}
