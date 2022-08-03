package speedcubing.replay;

import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import speedcubing.Member;
import speedcubing.lib.bukkit.entity.NPC;

import java.util.ArrayList;
import java.util.List;

public class Replay implements Listener {
    public int NewTick = -1;

    public void PlayerItemHeldEvent(ItemStack stack) {
        if (isRecording) {
            HandItemTicks.add((!HandItemTicks.isEmpty() && HandItemTicks.get(HandItemTicks.size() - 1) == NewTick + 1) ? NewTick + 2 : NewTick + 1);
            HandItemActions.add(stack);
        }
    }

    public void PlayerToggleSneakEvent(boolean sneak) {
        if (isRecording) {
            SneakTicks.add((!SneakTicks.isEmpty() && SneakTicks.get(SneakTicks.size() - 1) == NewTick + 1) ? NewTick + 2 : NewTick + 1);
            SneakActions.add(sneak);
        }
    }

    public void BlockPlaceEvent(BlockPosition position, Material stack) {
        if (isRecording) {
            BlockTicks.add((!BlockTicks.isEmpty() && BlockTicks.get(BlockTicks.size() - 1) == NewTick) ? NewTick + 1 : NewTick);
            BlockActions.add(position);
            BlockTypes.add(stack);
        }
    }

    public void PlayerMoveEvent(Location loc) {
        if (member.replay.isRecording) {
            member.replay.NewTick += 1;
            member.replay.LocationTicks.add(member.replay.NewTick);
            member.replay.LocationActions.add(loc.clone());
        }
    }

    public void pause() {
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
    public List<BlockPosition> BlockActions = new ArrayList<>();
    public List<Material> BlockTypes = new ArrayList<>();

    public List<Integer> HandItemTicks = new ArrayList<>();
    public List<ItemStack> HandItemActions = new ArrayList<>();

    public NPC npc;
    public final Member member;
    public final String value;
    public final String signature;

    public Replay(Member member,Location startLocation) {
        this.StartSneaking = member.player.isSneaking();
        this.StartLocation = startLocation;
        this.StartItem = member.player.getItemInHand();
        this.member = member;
        Property profile = ((CraftPlayer) member.player).getProfile().getProperties().get("textures").iterator().next();
        this.value = profile.getValue();
        this.signature = profile.getSignature();
    }
}
