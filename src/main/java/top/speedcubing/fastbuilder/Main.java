package top.speedcubing.fastbuilder;


import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import top.speedcubing.common.database.Database;
import top.speedcubing.fastbuilder.bukkitcmd.replay;
import top.speedcubing.fastbuilder.bukkitcmd.stats;
import top.speedcubing.fastbuilder.bukkitlistener.BlockBreak;
import top.speedcubing.fastbuilder.bukkitlistener.BlockPlace;
import top.speedcubing.fastbuilder.bukkitlistener.EntityChangeBlock;
import top.speedcubing.fastbuilder.bukkitlistener.EntityDamage;
import top.speedcubing.fastbuilder.bukkitlistener.InventoryClick;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerChangedWorld;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerDropItem;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerInteract;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerJoin;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerQuit;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerSpawnLocation;
import top.speedcubing.fastbuilder.bukkitlistener.PlayerTeleport;
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
import top.speedcubing.fastbuilder.replay.io.ReplayIO;
import top.speedcubing.lib.eventbus.CubingEventManager;
import top.speedcubing.lib.minecraft.MinecraftConsole;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.MapManager;

public class Main extends JavaPlugin {

    public static List<TimedMode> modes = new ArrayList<>();

    public void onEnable() {
        MinecraftServer.getServer().getPropertyManager().setProperty("server-name", "fastbuilder");
        CubingEventManager.registerListeners(new ServerEvent());
        registerCommands();
        registerListeners();

        try {
            MapManager.install("Custom", "Custom", Difficulty.PEACEFUL, 6000, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        new ReplayGUI().init();
        ReplayIO.init();
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
        startAutoCleanupTask();
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
                new EntityDamage(),
                new InventoryClick(),
                new PlayerChangedWorld(),
                new PlayerDropItem(),
                new PlayerInteract(),
                new PlayerJoin(),
                new PlayerQuit(),
                new PlayerSpawnLocation(),
                new PlayerToggleSneak(),
                new EntityChangeBlock(),
                new PlayerTeleport()
        );
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

    public void startAutoCleanupTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            long now = System.currentTimeMillis() / 1000;
            long sevenDaysAgo = now - (7L * 24 * 60 * 60);

            try (SQLConnection conn = Database.getCubing()) {

                int deleted0 = conn.delete("fbreplay", "fav = 0 AND time < " + sevenDaysAgo);

                if (deleted0 > 0) {
                    Bukkit.getLogger().info("[Replay清理] 已清理過期紀錄：普通(fav=0) " + deleted0 + " 筆。");

                }
            } catch (Exception e) {
                Bukkit.getLogger().warning("[Replay清理] 清理過程發生錯誤！");
                e.printStackTrace();
            }
        }, 100L, 20L * 60 * 60 * 24);
    }
}
