package top.speedcubing.fastbuilder.replay.gui;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import top.speedcubing.common.database.Database;
import top.speedcubing.fastbuilder.Core.Core;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.lib.utils.UUIDUtils;
import top.speedcubing.lib.utils.bytes.DataConversion;
import top.speedcubing.server.lang.LangInventory;
import top.speedcubing.server.lang.LangItemStack;
import top.speedcubing.server.lang.LangMessage;

public class ReplayGUI {
    public static int pageSize = 54;
    public static int replaySize = 42;
    public static List<InventoryHolder> fb = new ArrayList<>();

    public void init() {
        for (int i = 0; i < 5; i++) {
            fb.add(() -> null);
        }
    }

    public static void openReplayPage(Player player, int mode, boolean fav, int page) {
        Member member = Member.getMember(player);
        if (!member.openingBook) {
            member.openingBook = true;
            member.isFavoritePage = fav;
            new Thread(() -> {
                String[] modeName = {"fb_long_name", "fb_short_name", "fb_onestack_name", "fb_inclined_name", "fb_inclinedshort_name"};
                try {
                    String uuidHex = UUIDUtils.undash(member.user.bGetUniqueId());
                    ResultSet r = Database.systemConnection.select("id,score,time").from("fbreplay").where("uuid=X'" + uuidHex + "' AND mode=" + mode + " AND fav=" + (fav ? 1 : 0)).orderBy("time DESC").limit(page * replaySize, replaySize + 1).executeQuery();

                    LangInventory inventory = new LangInventory(pageSize, LangMessage.load("replay-fastbuilder", modeName[mode]));
                    inventory.deleteOnClose(true);

                    if (page != 0)
                        inventory.setItem(new LangItemStack(Material.STONE_BUTTON, "replay_button_prevpage"), (e) -> {
                            ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page - 1);
                        }, 8);

                    for (int i = 0; i < pageSize; i++) {
                        if (i % 9 == 7) {
                            inventory.setItem(new LangItemStack(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name("§a")), i);
                        }
                    }

                    int i = 0;
                    member.replayPageList.clear();
                    int preserveSide = 2;
                    while (r.next()) {
                        //found next page
                        if (i == replaySize) {
                            break;
                        }
                        String replayID = DataConversion.byteArrayToHexString(r.getBytes(1));
                        int score = r.getInt(2);
                        int time = r.getInt(3);
                        int slot = i + ((i / (9 - preserveSide)) * preserveSide);
                        member.replayPageList.put(slot, replayID);
                        System.out.println(slot + " " + Core.tickToTime(score));
                        inventory.setItem(new LangItemStack(new ItemBuilder(Material.PAPER).name("§aFastBuilder").lore(
                                "§8" + DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").format(LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault())),
                                "",
                                "§bTime: §e" + Core.tickToTime(score),
                                "",
                                "§bID: §6" + replayID,
                                "",
                                "§aLeft click to watch",
                                "§dRight click to add/remove from favorite")
                        ), (e) -> {
                            switch (e.getEvent().getAction()) {
                                case PICKUP_ALL:
                                    System.out.println(member.replayPageList.get(e.getEvent().getRawSlot()));
                                    member.joinReplayByReplayID(member.replayPageList.get(e.getEvent().getRawSlot()));
                                    break;
                                case PICKUP_HALF:
                                    Database.systemConnection.update("fbreplay", "fav=" + (fav ? 0 : 1), "id=X'" + member.replayPageList.get(e.getEvent().getRawSlot()) + "'");
                                    ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page);
                                    break;
                            }
                        }, slot);
                        i++;
                    }
                    Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                        member.openingBook = false;
                        member.user.openLangInventory(inventory);
                    });

                    //found next page
                    if (i == replaySize) {
                        inventory.setItem(new LangItemStack(Material.STONE_BUTTON, "replay_button_nextpage"), (e) -> {
                            ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page + 1);
                        }, pageSize - 1);
                    }
                } catch (Exception e) {
                    member.openingBook = false;
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
