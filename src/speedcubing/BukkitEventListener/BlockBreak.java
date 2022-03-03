package speedcubing.BukkitEventListener;

import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import speedcubing.Core.Core;
import speedcubing.Core.Inclined;
import speedcubing.Core.Long;
import speedcubing.Core.Short;

import java.util.UUID;

public class BlockBreak implements Listener {
    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SURVIVAL)
            e.setCancelled(true);
    }
}
