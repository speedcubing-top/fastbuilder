package top.speedcubing.fastbuilder.core;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.list.ItemStackList;
import top.speedcubing.lib.bukkit.inventory.ItemUtils;
import top.speedcubing.server.lang.Lang;
import top.speedcubing.server.lang.LangInv;

public class BlockEditor {

    static int length;

    public void Load() {
        length = ItemStackList.wooleditor.length + ItemStackList.sandstoneeditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length + ItemStackList.woodeditor.length +
                ItemStackList.clayeditor.length + ItemStackList.specialeditor.length;
    }

    public static void createMainPage(Player player) {
        Member member = Member.getMember(player);
        member.user.sound(Sound.CLICK);
        LangInv inv = new LangInv(27, "%lang_block_inv%");
        for (int i = 0; i < ItemStackList.categoryblockeditor.length; i++) {
            inv.setItem(ItemStackList.categoryblockeditor[i], i + 1);
        }
        for (int i = 0; i < ItemStackList.blockeditor.length; i++) {
            inv.setItem(ItemStackList.blockeditor[i], i + 12);
        }
        int index = member.blockskin - length;
        if (index >= 0)
            inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.blockeditor[index].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), index + 12);
        inv.setAllClickEvent((r) -> BlockEditor.select(r.getEvent(), r.getEvent().getRawSlot(), player));
        member.user.openLangInventory(inv);
    }

    public static void select(InventoryClickEvent e, int RawSlot, Player player) {
        e.setCancelled(true);
        if (RawSlot >= 12 && RawSlot < 12 + ItemStackList.blockeditor.length) {
            int x = RawSlot + length - 12;
            Member member = Member.getMember(player);
            member.user.sound(Sound.NOTE_PLING);
            member.blockskin = x;
            player.closeInventory();
            ItemStack stack = ItemStackList.Blocks[x];
            player.getInventory().setItem(0, stack);
            player.getInventory().setItem(1, stack);
            member.user.sendMessage("%lang_general_saved_setting%");
        } else if (RawSlot >= 1 && RawSlot <= ItemStackList.categoryblockeditor.length) {
            e.setCancelled(true);
            switch (RawSlot) {
                case 1: {
                    Member member = Member.getMember(player);
                    member.user.sound(Sound.NOTE_PLING);
                    LangInv inv = new LangInv(27, "%lang_block_category%%lang_block_item_sandstone%");
                    for (int i = 0; i < 3; i++) {
                        inv.setItem(ItemStackList.sandstoneeditor[i], i + 10);
                    }
                    for (int i = 3; i < 6; i++) {
                        inv.setItem(ItemStackList.sandstoneeditor[i], i + 11);
                    }
                    int set = member.blockskin;
                    if (set < 6)
                        inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.sandstoneeditor[set].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), (set < 3) ? set + 10 : set + 11);

                    inv.setAllClickEvent((r) -> {
                        e.setCancelled(true);
                        int rawSlot = r.getEvent().getRawSlot();

                        if (rawSlot > 9 && rawSlot < 17 && rawSlot != 13) {
                            int x = rawSlot > 12 ? rawSlot - 11 : rawSlot - 10;
                            member.user.sound(Sound.NOTE_PLING);
                            member.blockskin = x;
                            player.closeInventory();
                            ItemStack stack = ItemStackList.Blocks[x];
                            player.getInventory().setItem(0, stack);
                            player.getInventory().setItem(1, stack);
                            member.user.sendMessage("%lang_general_saved_setting%");
                        }
                    });
                    member.user.openLangInventory(inv);
                }
                break;
                case 2: {
                    Member member = Member.getMember(player);
                    member.user.sound(Sound.NOTE_PLING);
                    LangInv inv = new LangInv(27, "%lang_block_category%%lang_block_item_wool%");
                    for (int i = 0; i < ItemStackList.wooleditor.length; i++) {
                        inv.setItem(ItemStackList.wooleditor[i], i);
                    }
                    int index = member.blockskin - ItemStackList.sandstoneeditor.length;
                    if (index < ItemStackList.wooleditor.length && index >= 0)
                        inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.wooleditor[index].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), index);

                    inv.setAllClickEvent((r) -> {
                        e.setCancelled(true);
                        int rawSlot = r.getEvent().getRawSlot();
                        if (rawSlot < 16) {
                            int x = rawSlot + ItemStackList.sandstoneeditor.length;
                            member.user.sound(Sound.NOTE_PLING);
                            member.blockskin = x;
                            player.closeInventory();
                            ItemStack stack = ItemStackList.Blocks[x];
                            player.getInventory().setItem(0, stack);
                            player.getInventory().setItem(1, stack);
                            member.user.sendMessage("%lang_general_saved_setting%");
                        }
                    });
                    member.user.openLangInventory(inv);
                }
                break;
                case 3: {
                    Member member = Member.getMember(player);
                    member.user.sound(Sound.NOTE_PLING);
                    LangInv inv = new LangInv(27, "%lang_block_category%%lang_block_item_ore%");
                    for (int i = 0; i < ItemStackList.oreblockeditor.length; i++) {
                        inv.setItem(ItemStackList.oreblockeditor[i], i + 10);
                    }
                    int index = member.blockskin - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length);
                    if (index < ItemStackList.oreblockeditor.length && index >= 0)
                        inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.oreblockeditor[index].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), index + 10);

                    inv.setAllClickEvent((r) -> {
                        e.setCancelled(true);
                        int rawSlot = r.getEvent().getRawSlot();
                        if (rawSlot <= 16 && rawSlot >= 10) {
                            int x = rawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length - 10;
                            member.user.sound(Sound.NOTE_PLING);
                            member.blockskin = x;
                            player.closeInventory();
                            ItemStack stack = ItemStackList.Blocks[x];
                            player.getInventory().setItem(0, stack);
                            player.getInventory().setItem(1, stack);
                            member.user.sendMessage("%lang_general_saved_setting%");
                        }
                    });
                    member.user.openLangInventory(inv);
                }
                break;
                case 4: {
                    Member member = Member.getMember(player);
                    member.user.sound(Sound.NOTE_PLING);
                    LangInv inv = new LangInv(27, "%lang_block_category%%lang_block_item_ores%");
                    for (int i = 0; i < ItemStackList.oreeditor.length; i++) {
                        inv.setItem(ItemStackList.oreeditor[i], i + 10);
                    }
                    int index = member.blockskin - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length);
                    if (index < ItemStackList.oreeditor.length && index >= 0)
                        inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.oreeditor[index].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), index + 10);

                    inv.setAllClickEvent((r) -> {
                        e.setCancelled(true);
                        int rawSlot = r.getEvent().getRawSlot();
                        if (rawSlot <= 16 && rawSlot >= 10) {
                            int x = rawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length - 10;
                            member.user.sound(Sound.NOTE_PLING);
                            member.blockskin = x;
                            player.closeInventory();
                            ItemStack stack = ItemStackList.Blocks[x];
                            player.getInventory().setItem(0, stack);
                            player.getInventory().setItem(1, stack);
                            member.user.sendMessage("%lang_general_saved_setting%");
                        }
                    });
                    member.user.openLangInventory(inv);
                }
                break;
                case 5: {
                    Member member = Member.getMember(player);
                    member.user.sound(Sound.NOTE_PLING);
                    LangInv inv = new LangInv(27, "%lang_block_category%%lang_block_item_quartz%");
                    for (int i = 0; i < ItemStackList.quartzeditor.length; i++) {
                        inv.setItem(ItemStackList.quartzeditor[i], i + 12);
                    }
                    int index = member.blockskin - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length);
                    if (index < ItemStackList.quartzeditor.length && index >= 0)
                        inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.quartzeditor[index].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), index + 12);

                    inv.setAllClickEvent((r) -> {
                        e.setCancelled(true);
                        int rawSlot = r.getEvent().getRawSlot();

                        if (rawSlot <= 14 && rawSlot >= 12) {
                            int x = rawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length - 12;
                            member.user.sound(Sound.NOTE_PLING);
                            member.blockskin = x;
                            player.closeInventory();
                            ItemStack stack = ItemStackList.Blocks[x];
                            player.getInventory().setItem(0, stack);
                            player.getInventory().setItem(1, stack);
                            member.user.sendMessage("%lang_general_saved_setting%");
                        }
                    });
                    member.user.openLangInventory(inv);
                }
                break;
                case 6: {
                    Member member = Member.getMember(player);
                    member.user.sound(Sound.NOTE_PLING);
                    LangInv inv = new LangInv(27, "%lang_block_category%%lang_block_item_wood%");
                    for (int i = 0; i < 3; i++) {
                        inv.setItem(ItemStackList.woodeditor[i], i + 1);
                    }
                    for (int i = 3; i < 6; i++) {
                        inv.setItem(ItemStackList.woodeditor[i], i + 2);
                    }
                    for (int i = 6; i < 9; i++) {
                        inv.setItem(ItemStackList.woodeditor[i], i + 4);
                    }
                    for (int i = 9; i < 12; i++) {
                        inv.setItem(ItemStackList.woodeditor[i], i + 5);
                    }
                    int index = member.blockskin - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length);
                    if (index < ItemStackList.woodeditor.length && index >= 0)
                        inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.woodeditor[index].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), (index < 3 ? index + 1 : (index < 6 ? index + 2 : (index < 9 ? index + 4 : index + 5))));

                    inv.setAllClickEvent((r) -> {
                        e.setCancelled(true);
                        int rawSlot = r.getEvent().getRawSlot();

                        if (rawSlot <= 16 && rawSlot >= 1 && rawSlot != 13 && rawSlot != 4 && rawSlot != 8 && rawSlot != 9) {
                            int x = (rawSlot < 4 ? rawSlot - 1 : (rawSlot < 8 ? rawSlot - 2 : (rawSlot < 13 ? rawSlot - 4 : rawSlot - 5))) + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length;
                            member.user.sound(Sound.NOTE_PLING);
                            member.blockskin = x;
                            player.closeInventory();
                            ItemStack stack = ItemStackList.Blocks[x];
                            player.getInventory().setItem(0, stack);
                            player.getInventory().setItem(1, stack);
                            member.user.sendMessage("%lang_general_saved_setting%");
                        }
                    });
                    member.user.openLangInventory(inv);
                }
                break;
                case 7: {
                    Member member = Member.getMember(player);
                    member.user.sound(Sound.NOTE_PLING);
                    LangInv inv = new LangInv(27, "%lang_block_category%%lang_block_item_clay%");
                    for (int i = 0; i < ItemStackList.clayeditor.length; i++) {
                        inv.setItem(ItemStackList.clayeditor[i], i);
                    }
                    int index = member.blockskin - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length + ItemStackList.woodeditor.length);
                    if (index < ItemStackList.clayeditor.length && index >= 0)
                        inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.clayeditor[index].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), index);

                    inv.setAllClickEvent((r) -> {
                        e.setCancelled(true);
                        int rawSlot = r.getEvent().getRawSlot();
                        if (rawSlot < 17) {
                            int x = rawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length + ItemStackList.woodeditor.length;
                            member.user.sound(Sound.NOTE_PLING);
                            member.blockskin = x;
                            player.closeInventory();
                            ItemStack stack = ItemStackList.Blocks[x];
                            player.getInventory().setItem(0, stack);
                            player.getInventory().setItem(1, stack);
                            member.user.sendMessage("%lang_general_saved_setting%");
                        }
                    });
                    member.user.openLangInventory(inv);
                }
                break;
                case 8: {
                    Member member = Member.getMember(player);
                    member.user.sound(Sound.NOTE_PLING);
                    LangInv inv = new LangInv(27, "%lang_block_category%%lang_block_item_special%");
                    for (int i = 0; i < ItemStackList.specialeditor.length; i++) {
                        inv.setItem(ItemStackList.specialeditor[i], i);
                    }
                    int index = member.blockskin - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length + ItemStackList.woodeditor.length + ItemStackList.clayeditor.length);
                    if (index < ItemStackList.specialeditor.length && index >= 0)
                        inv.setItem(ItemUtils.glow(ItemUtils.setLore(ItemStackList.specialeditor[index].clone(), Lang.of("%lang_general_selected%").getString(member.user.lang))), index);

                    inv.setAllClickEvent((r) -> {
                        e.setCancelled(true);
                        int rawSlot = r.getEvent().getRawSlot();
                        if (rawSlot < 9) { // blockedit-here 幾個方塊就小於幾
                            int x = rawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length + ItemStackList.woodeditor.length + ItemStackList.clayeditor.length;
                            member.user.sound(Sound.NOTE_PLING);
                            member.blockskin = x;
                            player.closeInventory();
                            ItemStack stack = ItemStackList.Blocks[x];
                            player.getInventory().setItem(0, stack);
                            player.getInventory().setItem(1, stack);
                            member.user.sendMessage("%lang_general_saved_setting%");
                        }
                    });
                    member.user.openLangInventory(inv);
                }
                break;
            }
        }
    }
}
