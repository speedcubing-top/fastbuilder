package speedcubing.BukkitEventListener;

import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import speedcubing.Core.Core;
import speedcubing.Core.Custom;
import speedcubing.Main;

public class BlockPlace implements Listener {
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.SURVIVAL) {
            if (Core.waiting.contains(player.getUniqueId()) || block.getType().equals(Material.REDSTONE_COMPARATOR_OFF)) {
                e.setCancelled(true);
                return;
            }
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            switch (player.getWorld().getName()) {
                case "world":
                    if (x < 3 || x > 57 || Math.abs(z - Core.PlayerCoord.get(player.getUniqueId())) > 6 || y < 92) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0), 10);
                    } else e.setCancelled(Core.validPlaceBlock(player, new BlockPosition(x, y, z)));
                    break;
                case "Short":
                    if (x < 3 || x > 34 || Math.abs(z - Core.PlayerCoord.get(player.getUniqueId())) > 6 || y < 92) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0), 10);
                    } else e.setCancelled(Core.validPlaceBlock(player, new BlockPosition(x, y, z)));
                    break;
                case "Inclined":
                    if (Math.abs(x + z - 2 * Core.PlayerCoord.get(player.getUniqueId())) > 10 || x - z < 1 || x - z > 58 || y < 92) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0), 10);
                    } else e.setCancelled(Core.validPlaceBlock(player, new BlockPosition(x, y, z)));
                    break;
                case "Onestack":
                    if (x < 3 || x > 33 || Math.abs(z - Core.PlayerCoord.get(player.getUniqueId())) > 6 || y < 92) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0), 10);
                    } else e.setCancelled(Core.validPlaceBlock(player, new BlockPosition(x, y, z)));
                    break;
                case "Inclinedshort":
                    if (Math.abs(x + z - 2 * Core.PlayerCoord.get(player.getUniqueId())) > 10 || x - z < 1 || x - z > 38 || y < 92) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0), 10);
                    } else e.setCancelled(Core.validPlaceBlock(player, new BlockPosition(x, y, z)));
                    break;
                case "Custom":
                    if (Math.abs(z - Core.PlayerCoord.get(player.getUniqueId())) > 6 || y < 92) {
                        e.setCancelled(true);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> player.sendBlockChange(block.getLocation(), Material.AIR, (byte) 0), 10);
                    } else Custom.validPlaceBlock(player, new BlockPosition(x, y, z));
                    break;
            }
        }
    }
}
