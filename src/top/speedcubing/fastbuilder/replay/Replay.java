package top.speedcubing.fastbuilder.replay;

import com.mojang.authlib.properties.Property;
import top.speedcubing.fastbuilder.Member;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Replay {
    public final ReplayRecorder replayRecorder;
    public final List<ReplayTick> ticks = new ArrayList<>();

    public Location StartLocation;
    public boolean StartSneaking;
    public ItemStack StartItem;
    public String value;
    public String signature;
    public String name;

    public Replay(Member member, Location startLocation) {
        replayRecorder = new ReplayRecorder(this, member);
        this.StartSneaking = member.user.bIsSneaking();
        this.StartLocation = startLocation.subtract(member.PlayerCoord, 0, 0);
        this.StartItem = member.user.bGetItemInHand();
        this.name = member.user.bGetName();
        Property profile = ((CraftPlayer) member.user.player).getProfile().getProperties().get("textures").iterator().next();
        this.value = profile.getValue();
        this.signature = profile.getSignature();
    }

    public Replay() {
        this.replayRecorder = null;
    }
}
