package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.Replay;

public class PlayerToggleSneak implements Listener {

    @EventHandler
    public void playerToggleSneak(PlayerToggleSneakEvent e) {
        Replay replay = Member.getMember(e.getPlayer()).replay;
        if (replay != null)
            replay.replayRecorder.playerToggleSneakEvent(e.isSneaking());
    }
}
