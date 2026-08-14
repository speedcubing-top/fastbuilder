package top.speedcubing.fastbuilder.replay;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import top.speedcubing.common.database.Database;
import top.speedcubing.fastbuilder.Main;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.core.Core;
import top.speedcubing.fastbuilder.list.ItemStackList;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.lib.utils.SQL.SQLResult;
import top.speedcubing.lib.utils.SQL.SQLRow;
import top.speedcubing.lib.utils.UUIDUtils;
import top.speedcubing.lib.utils.bytes.DataConversion;
import top.speedcubing.server.lang.LangInv;
import top.speedcubing.server.lang.LangItem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        Member member = Member.getMember(player);
        if (member.openingBook) {
            return;
        }

        member.openingBook = true;
        member.isFavoritePage = fav;
        String uuidHex = UUIDUtils.undash(member.user.bGetUniqueId());
        executor.execute(() -> {
            try (SQLConnection connection = Database.getCubing()) {
                SQLResult result = connection.select("id,score,time").from("fbreplay").where("uuid=X'" + uuidHex + "' AND mode=" + mode + " AND fav=" + (fav ? 1 : 0)).orderBy("time DESC").limit(page * replaySize, replaySize + 1).executeResult();

                String[] modeName = {"fb_normal_name", "fb_short_name", "fb_long_name", "fb_onestack_name", "fb_inclined_name", "fb_inclinedshort_name", "fb_extrashort_name"};
                LangInv inventory = new LangInv(pageSize, "%lang_replay_fastbuilder%%lang_" + modeName[mode] + "%");

                int i = 0;
                member.replayPageList.clear();
                int preserveSide = 2;
                for (SQLRow r : result) {
                    //found next page
                    String replayID = DataConversion.byteArrayToHexString(r.getBytes(0));
                    int score = r.getInt(1);
                    int time = r.getInt(2);
                    int slot = i + ((i / (9 - preserveSide)) * preserveSide);
                    member.replayPageList.put(slot, replayID);

                    LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault());
                    LangItem paper = new LangItem(Material.PAPER).name("§aFastBuilder");
                    if (fav) {
                        paper.lore2(
                                "%lang_fb_lore_replay_single_fav%",
                                DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").format(now),
                                Core.tickToTime(score),
                                replayID
                        );
                    } else {
                        paper.lore2(
                                "%lang_fb_lore_replay_single%",
                                DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").format(now),
                                Core.tickToTime(score),
                                replayID,
                                DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").format(now.plusDays(7))
                        );
                    }

                    inventory.setItem(paper, (e) -> {
                        switch (e.getEvent().getAction()) {
                            case PICKUP_ALL:
                                member.joinReplayByReplayID(player, member.replayPageList.get(e.getEvent().getRawSlot()));
                                break;
                            case PICKUP_HALF:
                                try (SQLConnection conn = Database.getCubing()) {
                                    conn.update("fbreplay", "fav=" + (fav ? 0 : 1), "id=X'" + member.replayPageList.get(e.getEvent().getRawSlot()) + "'");
                                }
                                ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page);
                                break;
                        }
                    }, slot);
                    i++;
                    if (i == replaySize) {
                        break;
                    }
                }
                boolean nextPage = result.size() > replaySize;

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
                    inventory.setItem(new LangItem(Material.STONE_BUTTON).name("%lang_replay_button_nextpage%"), (e) -> {
                        ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page + 1);
                    }, 17);
                }

                if (page != 0) {
                    inventory.setItem(new LangItem(Material.STONE_BUTTON).name("%lang_replay_button_prevpage%"), (e) -> {
                        ReplayGUI.openReplayPage(player, member.currentMode.sqlId, fav, page - 1);
                    }, 8);
                }

                //go back
                inventory.setItem(new LangItem(Material.ARROW).name("%lang_inv_last_page%"), (e) -> {
                    member.user.openLangInventory(ItemStackList.replayBook);
                }, pageSize - 1);

                for (int j = 0; j < pageSize; j++) {
                    if (j % 9 == 7) {
                        inventory.setItem(new LangItem(Material.STAINED_GLASS_PANE).durability(7).name("§a"), j);
                    }
                }
            } catch (Exception e) {
                member.openingBook = false;
                e.printStackTrace();
            }
        });
    }
}
