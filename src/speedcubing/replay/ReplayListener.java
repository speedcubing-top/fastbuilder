package speedcubing.replay;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import speedcubing.Member;

public class ReplayListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(PlayerMoveEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.PlayerMoveEvent(((int) (System.currentTimeMillis() / 50L)) - replay.StartTick, e.getTo());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(BlockPlaceEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.BlockPlaceEvent(((int) (System.currentTimeMillis() / 50L)) - replay.StartTick, e.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(PlayerToggleSneakEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.PlayerToggleSneakEvent(((int) (System.currentTimeMillis() / 50L)) - replay.StartTick, e.isSneaking());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(PlayerItemHeldEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.PlayerItemHeldEvent(((int) (System.currentTimeMillis() / 50L)) - replay.StartTick, e.getPlayer().getInventory().getItem(e.getNewSlot()));
    }
}
