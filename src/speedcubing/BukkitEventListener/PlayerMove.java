package speedcubing.BukkitEventListener;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import speedcubing.Core.Custom;
import speedcubing.Main;
import speedcubing.Member;

public class PlayerMove implements Listener {

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();
        Member member = Member.getMember(player);
        if (player.getGameMode() != GameMode.CREATIVE) {
            switch (player.getWorld().getName()) {
                case "replay_normal":
                case "replay_onestack":
                case "replay_short":
                    if (Math.abs(to.getX() - member.ReplayCoord - 0.5) > 75.5 || Math.abs(to.getZ() - 0.5) > 75.5 || to.getY() < 0)
                        member.player.teleport(new Location(member.currentMode.replayWorld, member.ReplayCoord + 0.5, 100, -4.5, 0, 0));
                    break;
                case "replay_inclined":
                case "replay_incinedshort":
                    if (Math.abs(to.getX() + to.getZ() - 2 * member.ReplayCoord - 1) > 75.5)
                        member.player.teleport(new Location(member.currentMode.replayWorld, member.ReplayCoord + 0.5, 100, member.ReplayCoord + 0.5, 0, 0));
                    break;
                case "world": {
                    boolean fell = to.getY() <= 100 - member.integerCache[1];
                    if (fell || Math.abs(to.getX() - member.PlayerCoord - 0.5) > 5.5)
                        Main.Long.Fell(player, fell);
                }
                break;
                case "Short": {
                    boolean fell = to.getY() <= 100 - member.integerCache[1];
                    if (fell || Math.abs(to.getX() - member.PlayerCoord - 0.5) > 5.5)
                        Main.Short.Fell(player, fell);
                }
                break;
                case "Onestack": {
                    boolean fell = to.getY() <= 100 - member.integerCache[1];
                    if (fell || Math.abs(to.getX() - member.PlayerCoord - 0.5) > 5.5)
                        Main.OneStack.Fell(player, fell);
                }
                break;
                case "Inclined": {
                    boolean fell = to.getY() <= 100 - member.integerCache[1];
                    if (fell || Math.abs(to.getX() + to.getZ() - 2 * member.PlayerCoord - 1) > 7.5)
                        Main.Inclined.Fell(player, fell);
                }
                break;
                case "Inclinedshort": {
                    boolean fell = to.getY() <= 100 - member.integerCache[1];
                    if (fell || Math.abs(to.getX() + to.getZ() - 2 * member.PlayerCoord - 1) > 7.5)
                        Main.InclinedShort.Fell(player, fell);
                }
                break;
                case "Custom": {
                    if (to.getY() <= member.Center[1] + 99 - member.LengthSettings[1] - member.integerCache[1] || Math.abs(to.getZ() - member.PlayerCoord - 0.5) > 149.5)
                        Custom.Fell(player);
                    else {
                        if (e.getTo().getX() >= 4 && member.MainTimer == -1) {
                            player.getInventory().setItem(5, new ItemStack(Material.AIR));
                            player.getInventory().setItem(6, new ItemStack(Material.AIR));
                        }
                        if (!member.hits.isEmpty())
                            for (AxisAlignedBB b : member.hits) {
                                if (((CraftPlayer) player).getHandle().getBoundingBox().b(b)) {
                                    Custom.Finish(player, false);
                                    break;
                                }
                            }
                    }
                }
                break;
            }
        }
    }
}
