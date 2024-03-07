package top.speedcubing.fastbuilder;


import java.util.ArrayList;
import java.util.List;
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
import top.speedcubing.fastbuilder.BukkitEventListener.BlockBreak;
import top.speedcubing.fastbuilder.BukkitEventListener.BlockPlace;
import top.speedcubing.fastbuilder.BukkitEventListener.EntityDamage;
import top.speedcubing.fastbuilder.BukkitEventListener.FoodLevelChange;
import top.speedcubing.fastbuilder.BukkitEventListener.InventoryClick;
import top.speedcubing.fastbuilder.BukkitEventListener.PlayerChangedWorld;
import top.speedcubing.fastbuilder.BukkitEventListener.PlayerDropItem;
import top.speedcubing.fastbuilder.BukkitEventListener.PlayerInteract;
import top.speedcubing.fastbuilder.BukkitEventListener.PlayerJoin;
import top.speedcubing.fastbuilder.BukkitEventListener.PlayerQuit;
import top.speedcubing.fastbuilder.BukkitEventListener.PlayerToggleSneak;
import top.speedcubing.fastbuilder.Core.BlockEditor;
import top.speedcubing.fastbuilder.Core.Custom;
import top.speedcubing.fastbuilder.Core.timed.Inclined;
import top.speedcubing.fastbuilder.Core.timed.InclinedShort;
import top.speedcubing.fastbuilder.Core.timed.Long;
import top.speedcubing.fastbuilder.Core.timed.Normal;
import top.speedcubing.fastbuilder.Core.timed.OneStack;
import top.speedcubing.fastbuilder.Core.timed.Short;
import top.speedcubing.fastbuilder.Core.timed.TimedMode;
import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.replay.ReplayRecorder;
import top.speedcubing.fastbuilder.replay.gui.ReplayGUI;
import top.speedcubing.lib.eventbus.CubingEventManager;
import top.speedcubing.lib.utils.Console;

public class Main extends JavaPlugin implements Listener {

    public static List<TimedMode> modes = new ArrayList<>();

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
        Bukkit.getPluginManager().registerEvents(new ReplayRecorder(null, null), this);

        getServer().createWorld(new WorldCreator("Custom"));
        Bukkit.getWorld("Custom").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("Custom").setTime(6000);
        Bukkit.getWorld("Custom").setAutoSave(false);

        Bukkit.getPluginCommand("stats").setExecutor(new stats());
        Bukkit.getPluginCommand("stats").setTabCompleter(new stats());
        Bukkit.getPluginManager().registerEvents(new InventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new top.speedcubing.fastbuilder.replay.gui.InventoryClick(), this);
        new ReplayGUI().init();
        modes.add(new Normal());
        modes.add(new Short());
        modes.add(new Long());
        modes.add(new Inclined());
        modes.add(new OneStack());
        modes.add(new InclinedShort());
        new ItemStackList().Load();
        new BlockEditor().Load();
        new Custom().Load();
        CubingEventManager.registerListeners(new ServerEvent());
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
        if (b.getWorld().getName().startsWith("map_fb_replay")) {
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
