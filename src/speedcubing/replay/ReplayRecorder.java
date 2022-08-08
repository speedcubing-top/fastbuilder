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
            replay.HandItemTicks.add((!replay.HandItemTicks.isEmpty() && replay.HandItemTicks.get(replay.HandItemTicks.size() - 1) == NewTick + 1) ? NewTick + 2 : NewTick + 1);
            stack = stack == null ? new ItemStack(Material.AIR) : stack;
            replay.HandItemActions.add(stack.getTypeId());
            replay.HandItemDatas.add(stack.getData().getData());
            replay.HandItemEnchants.add(stack.containsEnchantment(Glow.glow));
        }
    }

    public void PlayerToggleSneakEvent(boolean sneak) {
        if (isRecording) {
            replay.SneakTicks.add((!replay.SneakTicks.isEmpty() && replay.SneakTicks.get(replay.SneakTicks.size() - 1) == NewTick + 1) ? NewTick + 2 : NewTick + 1);
            replay.SneakActions.add(sneak);
        }
    }

    public void BlockBreakEvent(Block block) {
        if (isRecording) {
            replay.BreakTicks.add((!replay.BreakTicks.isEmpty() && replay.BreakTicks.get(replay.BreakTicks.size() - 1) == NewTick) ? NewTick + 1 : NewTick);
            replay.BreakActions.add(block.getWorld().getBlockAt(block.getX() - member.PlayerCoord, block.getY(), block.getZ()));
            replay.BreakTypes.add(block.getType().getId());
            replay.BreakDatas.add( block.getData());
        }
    }

    public void BlockPlaceEvent(Block block) {
        if (isRecording) {
            replay.BlockTicks.add((!replay.BlockTicks.isEmpty() && replay.BlockTicks.get(replay.BlockTicks.size() - 1) == NewTick) ? NewTick + 1 : NewTick);
            replay.BlockActions.add(block.getWorld().getBlockAt(block.getX() - member.PlayerCoord, block.getY(), block.getZ()));
        }
    }

    public void PlayerMoveEvent(Location loc) {
        if (isRecording) {
            NewTick += 1;
            replay.LocationTicks.add(NewTick);
            replay.LocationActions.add(loc.clone().subtract(member.PlayerCoord, 0, 0));
        }
    }

    public void pause() {
        isRecording = false;
        ReplayCompressor.write(replay, member.player.getUniqueId());
    }
}
