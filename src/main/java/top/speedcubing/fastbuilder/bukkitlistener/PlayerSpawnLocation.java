package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import top.speedcubing.fastbuilder.core.timed.Normal;

public class PlayerSpawnLocation implements Listener {
    @EventHandler
    public void PlayerSpawnLocationEvent(PlayerSpawnLocationEvent e) {
        e.setSpawnLocation(new Location(Normal.instance.world, 0, 0, 0, 0, 0));
    }
}

