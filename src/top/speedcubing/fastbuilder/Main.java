package top.speedcubing.fastbuilder;


import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import top.speedcubing.fastbuilder.BukkitCommands.stats;
import top.speedcubing.fastbuilder.BukkitEventListener.*;
import top.speedcubing.fastbuilder.Core.BlockEditor;
import top.speedcubing.fastbuilder.Core.Custom;
import top.speedcubing.fastbuilder.Core.timed.Long;
import top.speedcubing.fastbuilder.Core.timed.Short;
import top.speedcubing.fastbuilder.Core.timed.*;
import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.replay.ReplayRecorder;
import top.speedcubing.fastbuilder.replay.gui.ReplayGUI;
import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.utils.Console;

public class Main extends JavaPlugin implements Listener {

    public static TimedMode Long;
    public static TimedMode Short;
    public static TimedMode Inclined;
    public static TimedMode OneStack;
    public static TimedMode InclinedShort;

    public void onEnable() {
        MinecraftServer.getServer().getPropertyManager().setProperty("server-name", "fastbuilder");
        Bukkit.getPluginManager().registerEvents(new FoodLevelChange(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChangedWorld(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlace(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerToggleSneak(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItem(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new ReplayRecorder(null,null), this);
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
        getServer().createWorld(new WorldCreator("replay_short"));
        Bukkit.getWorld("replay_short").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("replay_short").setTime(6000);
        getServer().createWorld(new WorldCreator("replay_normal"));
        Bukkit.getWorld("replay_normal").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("replay_normal").setTime(6000);
        getServer().createWorld(new WorldCreator("replay_onestack"));
        Bukkit.getWorld("replay_onestack").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("replay_onestack").setTime(6000);
        getServer().createWorld(new WorldCreator("replay_inclined"));
        Bukkit.getWorld("replay_inclined").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("replay_inclined").setTime(6000);
        getServer().createWorld(new WorldCreator("replay_inclinedshort"));
        Bukkit.getWorld("replay_inclinedshort").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("replay_inclinedshort").setTime(6000);
        Bukkit.getPluginCommand("stats").setExecutor(new stats());
        Bukkit.getPluginCommand("stats").setTabCompleter(new stats());
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new top.speedcubing.fastbuilder.replay.gui.InventoryClick(), this);
        new ReplayGUI().init();
        Long = new Long();
        Short = new Short();
        Inclined = new Inclined();
        OneStack = new OneStack();
        InclinedShort = new InclinedShort();
        new ItemStackList().Load();
        new BlockEditor().Load();
        new Custom().Load();
        LibEventManager.registerListeners(new ServerEvent());
        Bukkit.getPluginManager().registerEvents(this, this);
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

    public static void setTypeCheck(Block b, Material type, int id) {
        if (b.getWorld().getName().startsWith("replay_")) {
            if (Math.abs(b.getX()) < 250) {
                Console.printlnColor("§cError: " + id + " " + b.getWorld() + " " + b.getX() + " " + b.getY() + " " + b.getZ());
                return;
            }
        }
        b.setType(type);
    }


    public static void setDataCheck(Block b, byte data) {
        b.setData(data);
    }
}
