package top.speedcubing.fastbuilder.replay;

import com.mojang.authlib.properties.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.io.ReplayIO;
import top.speedcubing.fastbuilder.replay.io.ReplayIOv2;

public class Replay {
    public static ScheduledExecutorService replayReaderPool = Executors.newScheduledThreadPool(5);
    public final ReplayRecorder replayRecorder;
    public final int replayModeID;
    public final List<ReplayTick> ticks = new ArrayList<>();
    public String replayID;
    public Location StartLocation;
    public boolean StartSneaking;
    public ItemStack StartItem;
    public String value;
    public String signature;
    public String name;
    public int tickSize;

    public Replay(Member member, Location startLocation) {
        this.replayRecorder = new ReplayRecorder(this, member, ReplayIO.getLatest());
        this.StartSneaking = member.user.bIsSneaking();
        this.StartLocation = startLocation.subtract(member.PlayerCoord, 0, 0);
        this.StartItem = member.user.bGetInventory().getItem(member.asyncHeldItemSlot);
        this.name = member.user.bGetName();
        Property profile = ((CraftPlayer) member.user.player).getProfile().getProperties().get("textures").iterator().next();
        this.value = profile.getValue();
        this.signature = profile.getSignature();
        this.replayModeID = member.currentMode.sqlId;
    }

    public Replay(String replayID, int replayModeID) {
        this.replayRecorder = null;
        this.replayModeID = replayModeID;
        this.replayID = replayID;
    }
}
