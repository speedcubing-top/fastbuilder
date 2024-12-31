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
import top.speedcubing.common.events.ConfigReloadEvent;
import top.speedcubing.fastbuilder.bukkitcmd.replay;
import top.speedcubing.fastbuilder.bukkitcmd.stats;
import top.speedcubing.fastbuilder.bukkitlistener.BlockBreak;
import top.speedcubing.fastbuilder.bukkitlistener.BlockPlace;
import top.speedcubing.fastbuilder.bukkitlistener.EntityChangeBlock;
import top.speedcubing.fastbuilder.bukkitlistener.EntityDamage;
import top.speedcubing.fastbuilder.bukkitlistener.FoodLevelChange;
import top.speedcubing.fastbuilder.bukkitlistener.InventoryClick;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerChangedWorld;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerDropItem;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerInteract;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerJoin;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerQuit;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerToggleSneak;
import top.speedcubing.fastbuilder.core.BlockEditor;
import top.speedcubing.fastbuilder.core.Custom;
import top.speedcubing.fastbuilder.core.timed.ExtraShort;
import top.speedcubing.fastbuilder.core.timed.Inclined;
import top.speedcubing.fastbuilder.core.timed.InclinedShort;
import top.speedcubing.fastbuilder.core.timed.Long;
import top.speedcubing.fastbuilder.core.timed.Normal;
import top.speedcubing.fastbuilder.core.timed.OneStack;
import top.speedcubing.fastbuilder.core.timed.Short;
import top.speedcubing.fastbuilder.core.timed.TimedMode;
import top.speedcubing.fastbuilder.list.ItemStackList;
import top.speedcubing.fastbuilder.replay.ReplayGUI;
import top.speedcubing.fastbuilder.replay.ReplayRecorder;
import top.speedcubing.lib.eventbus.CubingEventManager;
import top.speedcubing.lib.minecraft.MinecraftConsole;
import top.speedcubing.server.speedcubingServer;

public class Main extends JavaPlugin implements Listener {

    public static List<TimedMode> modes = new ArrayList<>();

    public void onEnable() {
        MinecraftServer.getServer().getPropertyManager().setProperty("server-name", "fastbuilder");
        CubingEventManager.registerListeners(new ServerEvent());
        new ConfigReloadEvent().call();
        registerCommands();
        registerListeners();

        getServer().createWorld(new WorldCreator("Custom"));
        Bukkit.getWorld("Custom").setDifficulty(Difficulty.PEACEFUL);
        Bukkit.getWorld("Custom").setTime(6000);
        Bukkit.getWorld("Custom").setAutoSave(false);

        new ReplayGUI().init();
        modes.add(new Normal());
        modes.add(new Short());
        modes.add(new Long());
        modes.add(new Inclined());
        modes.add(new OneStack());
        modes.add(new InclinedShort());
        modes.add(new ExtraShort());
        new ItemStackList().Load();
        new BlockEditor().Load();
        new Custom().Load();
    }


    private void registerCommands() {
        Bukkit.getPluginCommand("stats").setExecutor(new stats());
        Bukkit.getPluginCommand("stats").setTabCompleter(new stats());
        Bukkit.getPluginCommand("replay").setExecutor(new replay());
        Bukkit.getPluginCommand("replay").setTabCompleter(new replay());
    }

    private void registerListeners() {
        speedcubingServer.registerListeners(
                new BlockBreak(),
                new BlockPlace(),
                new FoodLevelChange(),
                new EntityDamage(),
                new InventoryClick(),
                new PlayerChangedWorld(),
                new PlayerDropItem(),
                new PlayerInteract(),
                new PlayerJoin(),
                new PlayerQuit(),
                new PlayerToggleSneak(),
                new EntityChangeBlock(),
                new ReplayRecorder(null, null),
                this);
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
                MinecraftConsole.printlnColor("§cError: " + id + " " + b.getWorld() + " " + b.getX() + " " + b.getY() + " " + b.getZ());
                return;
            }
        }
        b.setType(type);
    }

    public static void setDataCheck(Block b, byte data) {
        b.setData(data);
    }
}
