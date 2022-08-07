package speedcubing.replay;

import com.mojang.authlib.properties.Property;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import speedcubing.Member;

import java.util.ArrayList;
import java.util.List;

public class Replay implements Listener {

    public final ReplayRecorder replayRecorder;

    public final List<Integer> SneakTicks = new ArrayList<>();
    public final List<Integer> LocationTicks = new ArrayList<>();
    public final List<Integer> BlockTicks = new ArrayList<>();
    public final List<Integer> BreakTicks = new ArrayList<>();
    public final List<Integer> HandItemTicks = new ArrayList<>();


    public final List<Location> LocationActions = new ArrayList<>();

    public final List<Block> BlockActions = new ArrayList<>();

    public final List<Block> BreakActions = new ArrayList<>();
    public final List<Integer> BreakTypes = new ArrayList<>();
    public final List<Byte> BreakDatas = new ArrayList<>();

    public final List<Boolean> SneakActions = new ArrayList<>();

    public final List<Integer> HandItemActions = new ArrayList<>();
    public final List<Byte> HandItemDatas = new ArrayList<>();
    public final List<Boolean> HandItemEnchants = new ArrayList<>();

    public Location StartLocation;
    public boolean StartSneaking;
    public ItemStack StartItem;
    public String value;
    public String signature;

    public Replay(Member member, Location startLocation) {
        replayRecorder = new ReplayRecorder(this, member);
        this.StartSneaking = member.player.isSneaking();
        this.StartLocation = startLocation;
        this.StartItem = member.player.getItemInHand();
        Property profile = ((CraftPlayer) member.player).getProfile().getProperties().get("textures").iterator().next();
        this.value = profile.getValue();
        this.signature = profile.getSignature();
    }

    public Replay() {
        this.replayRecorder = null;
    }
}
