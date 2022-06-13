package speedcubing;


import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import speedcubing.BukkitCommands.stats;
import speedcubing.BukkitEventListener.*;
import speedcubing.Core.Long;
import speedcubing.Core.*;
import speedcubing.List.ItemStackList;
import speedcubing.lib.event.LibEventManager;
import speedcubing.server.speedcubingServer;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends JavaPlugin implements Listener {

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new FoodLevelChange(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlace(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangedWorld(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMove(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItem(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getWorld("world").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("world").setTime(6000);
        getServer().createWorld(new WorldCreator("Inclined"));
        Bukkit.getWorld("Inclined").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("Inclined").setTime(6000);
        getServer().createWorld(new WorldCreator("Short"));
        Bukkit.getWorld("Short").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("Short").setTime(6000);
        getServer().createWorld(new WorldCreator("Onestack"));
        Bukkit.getWorld("Onestack").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("Onestack").setTime(6000);
        getServer().createWorld(new WorldCreator("Inclinedshort"));
        Bukkit.getWorld("Inclinedshort").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("Inclinedshort").setTime(6000);
        getServer().createWorld(new WorldCreator("Custom"));
        Bukkit.getWorld("Custom").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("Custom").setTime(6000);
        Bukkit.getWorld("world").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("world").setTime(6000);
        Bukkit.getPluginCommand("stats").setExecutor(new stats());
        Bukkit.getPluginCommand("stats").setTabCompleter(new stats());
        new Long().Load();
        new speedcubing.Core.Short().Load();
        new Inclined().Load();
        new OneStack().Load();
        new InclinedShort().Load();
        new ItemStackList().Load();
        new BlockEditor().Load();
        new Custom().Load();
        LibEventManager.registerListeners(new ServerEvent());
        Bukkit.getPluginManager().registerEvents(this,this);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                speedcubingServer.tcp.sendClean(25576 - Bukkit.getPort() % 2, "online|2|" + Bukkit.getOnlinePlayers().size());
            }
        }, 0, 1000);
    }


    @EventHandler
    public void dwwa(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) {
            if (e.getFrom().getWorld() != e.getTo().getWorld()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage("Cancelled.");
            }
        }
    }
}
