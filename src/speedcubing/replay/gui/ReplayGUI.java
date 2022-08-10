package speedcubing.replay.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import speedcubing.lib.bukkit.inventory.ItemBuilder;
import speedcubing.server.libs.User;
import speedcubing.system.speedcubingSystem;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReplayGUI {
    public static List<InventoryHolder> fb = new ArrayList<>();

    public void init() {
        for (int i = 0; i < 5; i++) {
            fb.add(() -> null);
        }
    }

    public static void openFastBuilderReplay(User user, int mode) {
        String[][] modeName = {
                {"Normal", "Normal", "Normal", "Normal"},
                {"Short", "Short", "Short", "Short"},
                {"Onestack", "Onestack", "Onestack", "Onestack"},
                {"Inclined", "Inclined", "Inclined", "Inclined"},
                {"InclinedShort", "InclinedShort", "InclinedShort", "InclinedShort"}
        };
        try {
            ResultSet resultSet = speedcubingSystem.connection.select("fbreplay", "score,time", "owner='" + user.player.getUniqueId() + "' AND mode=" + mode, "time", "DESC");
            Inventory inventory = Bukkit.createInventory(fb.get(mode), 54, "§bReplay-FastBuilder-" + modeName[mode][user.lang]);
            int i = 0;
            while (resultSet.next()) {
                if (i < 63) {
                    inventory.setItem(i, new ItemBuilder(Material.PAPER).name("§aFastBuilder").lore(
                            "§8" + DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").format(LocalDateTime.ofInstant(Instant.ofEpochSecond(resultSet.getInt(2)), ZoneId.systemDefault())),
                            "",
                            "§bTime: §e" + FastBuilderUtils.tickToTime(resultSet.getInt(1))
                    ).build());
                }
                i++;
            }
            user.player.openInventory(inventory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
