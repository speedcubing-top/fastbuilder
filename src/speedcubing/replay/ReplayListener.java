package speedcubing.replay;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import speedcubing.Member;

public class ReplayListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(PlayerToggleSneakEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.PlayerToggleSneakEvent(e.isSneaking());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void a(PlayerItemHeldEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.PlayerItemHeldEvent(e.getPlayer().getInventory().getItem(e.getNewSlot()));
    }
}
