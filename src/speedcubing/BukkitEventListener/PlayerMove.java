package speedcubing.BukkitEventListener;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import speedcubing.Core.*;
import speedcubing.Core.Long;
import speedcubing.Core.Short;
import speedcubing.replay.test;

import java.util.UUID;

public class PlayerMove implements Listener {

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();
        if (player.getGameMode() == GameMode.SURVIVAL)
            switch (player.getWorld().getName()) {
                case "world": {
                    UUID uuid = player.getUniqueId();
                    boolean fell = to.getY() <= 100 - Core.integerCache.get(uuid)[1];
                    if (fell || Math.abs(to.getZ() - Core.PlayerCoord.get(uuid) - 0.5) > 5.5)
                        Long.Fell(player, fell);
                    else if (Core.times.get(uuid) != -1) {
                        if (player.getName().equals("speedcubing")) {
                            test.LocationActions.add(e.getTo());
                            test.LocationTicks.add(Core.times.get(uuid));
                        }
                    }
                }
                break;
                case "Short": {
                    UUID uuid = player.getUniqueId();
                    boolean fell = to.getY() <= 100 - Core.integerCache.get(uuid)[1];
                    if (fell || Math.abs(to.getZ() - Core.PlayerCoord.get(uuid) - 0.5) > 5.5)
                        Short.Fell(player, fell);
                }
                break;
                case "Inclined": {
                    UUID uuid = player.getUniqueId();
                    boolean fell = to.getY() <= 100 - Core.integerCache.get(uuid)[1];
                    if (fell || Math.abs(to.getX() + to.getZ() - 2 * Core.PlayerCoord.get(uuid) - 1) > 7.5)
                        Inclined.Fell(player, fell);
                }
                break;
                case "Onestack": {
                    UUID uuid = player.getUniqueId();
                    boolean fell = to.getY() <= 100 - Core.integerCache.get(uuid)[1];
                    if (fell || Math.abs(to.getZ() - Core.PlayerCoord.get(uuid) - 0.5) > 5.5)
                        OneStack.Fell(player, fell);
                }
                break;
                case "Inclinedshort": {
                    UUID uuid = player.getUniqueId();
                    boolean fell = to.getY() <= 100 - Core.integerCache.get(uuid)[1];
                    if (fell || Math.abs(to.getX() + to.getZ() - 2 * Core.PlayerCoord.get(uuid) - 1) > 7.5)
                        InclinedShort.Fell(player, fell);
                }
                break;
                case "Custom": {
                    UUID uuid = player.getUniqueId();
                    if (to.getY() <= Custom.Center.get(uuid)[1] + 99 - Custom.LengthSettings.get(uuid)[1] - Core.integerCache.get(uuid)[1] || Math.abs(to.getZ() - Core.PlayerCoord.get(uuid) - 0.5) > 149.5)
                        Custom.Fell(player);
                    else {
                        if (e.getTo().getX() >= 4 && !Core.MainTimer.containsKey(uuid)) {
                            player.getInventory().setItem(5, new ItemStack(Material.AIR));
                            player.getInventory().setItem(6, new ItemStack(Material.AIR));
                        }
                        World world = ((CraftWorld) player.getWorld()).getHandle();
                        if (!Custom.hits.get(uuid).isEmpty())
                            for (AxisAlignedBB b : Custom.hits.get(uuid)) {
                                if (!world.a(Entity.class, b).isEmpty()) {
                                    Custom.Finish(player,false);
                                    break;
                                }
                            }
                    }
                }
                break;
            }
    }
}
