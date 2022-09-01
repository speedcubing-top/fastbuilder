package top.speedcubing.fastbuilder.replay.gui;

import top.speedcubing.fastbuilder.List.ItemStackList;
import top.speedcubing.fastbuilder.Member;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.system.speedcubingSystem;

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

    public static void openFastBuilderReplay(Member user, int mode) {
        String[][] modeName = {
                {"Normal", "Normal", "Normal", "Normal"},
                {"Short", "Short", "Short", "Short"},
                {"Onestack", "Onestack", "Onestack", "Onestack"},
                {"Inclined", "Inclined", "Inclined", "Inclined"},
                {"InclinedShort", "InclinedShort", "InclinedShort", "InclinedShort"}
        };
        try {
            ResultSet resultSet = speedcubingSystem.connection.select("fbreplay", "score,time,fav,id", "owner='" + user.user.player.getUniqueId() + "' AND mode=" + mode + " ORDER BY fav DESC,time DESC");
            Inventory inventory = Bukkit.createInventory(fb.get(mode), 63, "§bReplay-FastBuilder-" + modeName[mode][user.user.lang]);
            inventory.setItem(0, ItemStackList.LASTATTEMPT[user.user.lang]);
            int i = 1;
            user.replays.clear();
            while (resultSet.next()) {
                if (i < 63) {
                    user.replays.add(resultSet.getString(4));
                    inventory.setItem(i, new ItemBuilder(resultSet.getBoolean(3) ? Material.EMPTY_MAP : Material.PAPER).name("§aFastBuilder").lore(
                            "§8" + DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").format(LocalDateTime.ofInstant(Instant.ofEpochSecond(resultSet.getInt(2)), ZoneId.systemDefault())),
                            "",
                            "§bTime: §e" + FastBuilderUtils.tickToTime(resultSet.getInt(1)),
                            "",
                            "§aLeft click to watch",
                            "§dRight click to add/remove from favorite"
                    ).build());
                }
                i++;
            }
            user.user.player.openInventory(inventory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
