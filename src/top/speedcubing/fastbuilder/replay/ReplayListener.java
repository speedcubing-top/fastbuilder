package top.speedcubing.fastbuilder.replay;

import top.speedcubing.fastbuilder.Member;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class ReplayListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(PlayerToggleSneakEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.replayRecorder.PlayerToggleSneakEvent(e.isSneaking());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(PlayerItemHeldEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.replayRecorder.PlayerItemHeldEvent(e.getPlayer().getInventory().getItem(e.getNewSlot()));
    }
}
