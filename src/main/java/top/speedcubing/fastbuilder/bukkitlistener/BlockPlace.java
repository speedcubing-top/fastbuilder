package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import top.speedcubing.fastbuilder.Member;

public class BlockPlace implements Listener {
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
            Member.getMember(e.getPlayer()).placedBlockList.add(e.getBlock());
    }
}
