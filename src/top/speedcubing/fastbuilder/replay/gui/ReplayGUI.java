package top.speedcubing.fastbuilder.replay.gui;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import top.speedcubing.common.database.Database;
import top.speedcubing.fastbuilder.Core.Core;
import top.speedcubing.fastbuilder.List.ItemStackList;
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

    static Executor executor = Executors.newFixedThreadPool(5);

    public static void openReplayPage(Player player, int mode, boolean fav, int page) {
        System.out.println("open " + page);
        Member member = Member.getMember(player);
        if (!member.openingBook) {
            member.openingBook = true;
            member.isFavoritePage = fav;
            executor.execute(() -> {
                String[] modeName = {"fb_normal_name", "fb_short_name", "fb_long_name", "fb_onestack_name", "fb_inclined_name", "fb_inclinedshort_name"};
                try {
                    String uuidHex = UUIDUtils.undash(member.user.bGetUniqueId());
                    ResultSet r = Database.systemConnection.select("id,score,time").from("fbreplay").where("uuid=X'" + uuidHex + "' AND mode=" + mode + " AND fav=" + (fav ? 1 : 0)).orderBy("time DESC").limit(page * replaySize, replaySize + 1).executeQuery();

                    LangInventory inventory = new LangInventory(pageSize, LangMessage.load("replay-fastbuilder", modeName[mode]));
                    inventory.deleteOnClose(true);

                    int i = 0;
                    member.replayPageList.clear();
                    int preserveSide = 2;
                    while (r.next()) {
                        //found next page
                        String replayID = DataConversion.byteArrayToHexString(r.getBytes(1));
                        int score = r.getInt(2);
                        int time = r.getInt(3);
                        int slot = i + ((i / (9 - preserveSide)) * preserveSide);
                        member.replayPageList.put(slot, replayID);
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
                                    member.joinReplayByReplayID(member.replayPageList.get(e.getEvent().getRawSlot()));
                                    break;
                                case PICKUP_HALF:
                                    Database.systemConnection.update("fbreplay", "fav=" + (fav ? 0 : 1), "id=X'" + member.replayPageList.get(e.getEvent().getRawSlot()) + "'");
                                    ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page);
                                    break;
                            }
                        }, slot);
                        i++;
                        if (i == replaySize) {
                            break;
                        }
                    }
                    boolean nextPage = r.next();
                    r.close();

                    if (i == 0 && page != 0) {
                        member.openingBook = false;
                        ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page - 1);
                        return;
                    }

                    Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                        member.openingBook = false;
                        member.user.openLangInventory(inventory);
                    });

                    //found next page
                    if (nextPage) {
                        inventory.setItem(new LangItemStack(Material.STONE_BUTTON, "replay_button_nextpage"), (e) -> {
                            ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page + 1);
                        }, 17);
                    }

                    if (page != 0) {
                        inventory.setItem(new LangItemStack(Material.STONE_BUTTON, "replay_button_prevpage"), (e) -> {
                            ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page - 1);
                        }, 8);
                    }

                    //go back
                    inventory.setItem(new LangItemStack(Material.ARROW, "inv_last_page"), (e) -> {
                        member.user.openLangInventory(ItemStackList.replayBook);
                    }, pageSize - 1);

                    for (int j = 0; j < pageSize; j++) {
                        if (j % 9 == 7) {
                            inventory.setItem(new LangItemStack(new ItemBuilder(Material.STAINED_GLASS_PANE).durability(7).name("§a")), j);
                        }
                    }
                } catch (Exception e) {
                    member.openingBook = false;
                    e.printStackTrace();
                }
            });
        }
    }
}
