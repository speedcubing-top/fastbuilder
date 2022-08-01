package speedcubing.replay;

import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import speedcubing.Member;
import speedcubing.lib.bukkit.entity.NPC;

import java.util.ArrayList;
import java.util.List;

public class Replay implements Listener {


    public void PlayerItemHeldEvent(int tick, ItemStack stack) {
        if (isRecording) {
            HandItemTicks.add(tick);
            HandItemActions.add(stack);
        }
    }

    public void PlayerToggleSneakEvent(int tick, boolean sneak) {
        if (isRecording) {
            SneakTicks.add(tick);
            SneakActions.add(sneak);
        }
    }

    public void BlockPlaceEvent(int tick, Block block) {
        if (isRecording) {
            BlockTicks.add(tick);
            BlockActions.add(block);
            BlockTypes.add(block.getType());
        }
    }

    public void PlayerMoveEvent(int tick, Location location) {
        if (isRecording) {
            System.out.println(tick + " MOVE " + location + " " + (tick));
            if (ReplayViewer.lastXX.get(member.player) != null && ReplayViewer.lastXX.get(member.player) == tick) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("REPEATED TICK! " + tick);
                }
            }
            ReplayViewer.lastXX.put(member.player, tick);
            LocationTicks.add(tick);
            LocationActions.add(location);
        }
    }

    public void pause() {
        this.length = (int) (System.currentTimeMillis() / 50) - StartTick;
        isRecording = false;
    }

    public void start() {
        isRecording = true;
    }

    public boolean isRecording;

    public Location StartLocation;
    public boolean StartSneaking;
    public ItemStack StartItem;

    public List<Integer> SneakTicks = new ArrayList<>();

    public List<Boolean> SneakActions = new ArrayList<>();

    public List<Integer> LocationTicks = new ArrayList<>();
    public List<Location> LocationActions = new ArrayList<>();

    public List<Integer> BlockTicks = new ArrayList<>();
    public List<Block> BlockActions = new ArrayList<>();
    public List<Material> BlockTypes = new ArrayList<>();

    public List<Integer> HandItemTicks = new ArrayList<>();
    public List<ItemStack> HandItemActions = new ArrayList<>();

    public final int StartTick = (int) (System.currentTimeMillis() / 50);

    public NPC npc;
    public final Member member;
    public final String value;
    public final String signature;
    public int length;

    public Replay(Member member) {
        this.StartSneaking = member.player.isSneaking();
        this.StartLocation = member.player.getLocation();
        this.StartItem = member.player.getItemInHand();
        this.member = member;
        Property profile = ((CraftPlayer) member.player).getProfile().getProperties().get("textures").iterator().next();
        this.value = profile.getValue();
        this.signature = profile.getSignature();
        new ReplayPlayer(member, this);
    }
}
