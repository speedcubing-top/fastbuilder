package speedcubing.replay;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import speedcubing.Member;
import speedcubing.lib.bukkit.inventory.Glow;

public class ReplayRecorder {

    public int NewTick = -1;

    public final Member member;
    public boolean isRecording = true;

    public final Replay replay;

    public ReplayRecorder(Replay replay, Member member) {
        this.member = member;
        this.replay = replay;
    }

    public void PlayerItemHeldEvent(ItemStack stack) {
        if (isRecording) {
            ReplayTick replayTick = getTick();
            stack = stack == null ? new ItemStack(Material.AIR) : stack;
            replayTick.handMaterial = stack.getTypeId();
            replayTick.handDatas = stack.getData().getData();
            replayTick.handEchants = stack.containsEnchantment(Glow.glow);
        }
    }

    public void PlayerToggleSneakEvent(boolean sneak) {
        if (isRecording) getTick2().sneak = sneak;
    }

    public void BlockBreakEvent(Block block) {
        if (isRecording) {
            ReplayTick replayTick = getTick2();
            replayTick.breakLocation = block.getWorld().getBlockAt(block.getX() - member.PlayerCoord, block.getY(), block.getZ() - (member.currentMode.inclined ? member.PlayerCoord : 0));
            replayTick.breakMaterial = block.getType().getId();
            replayTick.breakDatas = block.getData();
        }
    }

    public void BlockPlaceEvent(Block block) {
        if (isRecording) {
            getTick2().blockLocation = block.getWorld().getBlockAt(block.getX() - member.PlayerCoord, block.getY(), block.getZ() - (member.currentMode.inclined ? member.PlayerCoord : 0));
        }
    }

    public void PlayerMoveEvent(Location loc) {
        if (isRecording) {
            NewTick += 1;
            getTick().location = loc.clone().subtract(member.PlayerCoord, 0, member.currentMode.inclined ? member.PlayerCoord : 0);
        }
    }

    ReplayTick getTick() {
        int size = replay.ticks.size();
        if (size == 0 || size == NewTick) {
            ReplayTick replayTick = new ReplayTick();
            replay.ticks.add(replayTick);
            return replayTick;
        } else return replay.ticks.get(NewTick);
    }

    ReplayTick getTick2() {
        int size = replay.ticks.size();
        if (size == 0 || size > NewTick) {
            replay.ticks.add(new ReplayTick());
            return replay.ticks.get(size - 1);
        } else return replay.ticks.get(NewTick - 1);
    }

    public void pause(boolean write, boolean TAS) {
        isRecording = false;
        if (write)
            ReplayCompressor.write(replay, member.player.getUniqueId(), TAS);
    }
}
