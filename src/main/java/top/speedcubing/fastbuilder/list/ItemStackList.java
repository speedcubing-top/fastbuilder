package top.speedcubing.fastbuilder.list;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.ReplayGUI;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.lang.LangInv;
import top.speedcubing.server.lang.LangItem;

public class ItemStackList {
    public static LangItem openkit;
    public static LangItem navigator;
    public static LangItem pickaxe;
    public static LangItem Settings;
    public static LangItem Reset;
    public static LangItem Length;
    public static LangItem Height;
    public static LangItem Replay;
    public static LangItem TICK;
    public static LangItem QUITREPLAY;
    public static LangItem START;
    public static LangItem PAUSE;
    public static LangItem RESTART;
    public static LangItem COPYID;
    public static ItemStack[] Blocks = new ItemStack[80]; // blockedit-here

    public static LangItem[] categoryblockeditor = new LangItem[8];

    public static ItemStack[] blockeditor = new ItemStack[4];

    public static ItemStack[] wooleditor = new ItemStack[16];
    public static ItemStack[] sandstoneeditor = new ItemStack[6];
    public static ItemStack[] oreblockeditor = new ItemStack[7];
    public static ItemStack[] oreeditor = new ItemStack[7];
    public static ItemStack[] quartzeditor = new ItemStack[3];
    public static ItemStack[] woodeditor = new ItemStack[12];
    public static ItemStack[] clayeditor = new ItemStack[17];
    public static ItemStack[] specialeditor = new ItemStack[8]; // blockedit-here
    public static LangInv replayBook;
    public static final String[] sandstoneedit = {"§bCategory: Sandstone", "§bCategory: Sandstone", "§bCategory: Sandstone", "§bCategory: Sandstone"};
    public static final String[] wooledit = {"§bCategory: Wool", "§bCategory: Wool", "§bCategory: Wool", "§bCategory: Wool"};
    public static final String[] oreblockedit = {"§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)"};
    public static final String[] oreedit = {"§bCategory: Ores", "§bCategory: Ores", "§bCategory: Ores", "§bCategory: Ores"};
    public static final String[] quartzedit = {"§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks"};
    public static final String[] woodedit = {"§bCategory: Woods", "§bCategory: Woods", "§bCategory: Woods", "§bCategory: Woods"};
    public static final String[] clayedit = {"§bCategory: Clay", "§bCategory: Clay", "§bCategory: Clay", "§bCategory: Clay"};
    public static final String[] specialedit = {"§bSpecial Blocks", "§bSpecial Blocks", "§bSpecial Blocks", "§bSpecial Blocks"};

    public static final LangInv ResetConfirm = new LangInv(27, "%lang_lobby_msg_sure_reset%")
            .setItem(new LangItem(Material.STAINED_CLAY).durability(13).name("%lang_general_yes%"), 11)
            .setItem(new LangItem(Material.STAINED_CLAY).durability(14).name("%lang_general_no"), 15);

    public void Load() {
        replayBook = new LangInv(27, "%lang_fb_replaybook_title%")
                .setItem(new LangItem(Material.LEATHER).name("%lang_fb_lastattempt_leather%"), (e) -> {
                    Member.getMember(e.getPlayer()).joinReplayLastAttempt();
                    e.getPlayer().closeInventory();
                }, 11)
                .setItem(new LangItem(Material.PAPER).name("%lang_fb_replay_fav%"), (e) -> {
                    ReplayGUI.openReplayPage(e.getPlayer(), Member.getMember(e.getPlayer()).currentMode.sqlId, true, 0);
                }, 13)
                .setItem(new LangItem(Material.PAPER).name("%lang_fb_replay_non_fav%"), (e) -> {
                    ReplayGUI.openReplayPage(e.getPlayer(), Member.getMember(e.getPlayer()).currentMode.sqlId, false, 0);
                }, 15);


        Length = new LangItem(Material.IRON_INGOT).name("%lang_fb_custom_length%");
        Height = new LangItem(Material.GOLD_INGOT).name("%lang_fb_custom_height%");
        navigator = new LangItem(Material.COMPASS).name("%lang_fb_item_switchmode%");
        pickaxe = new LangItem(Material.DIAMOND_PICKAXE).glow().name("%lang_fb_item_pickaxe%").hideAttr();
        Replay = new LangItem(Material.BOOK).name("%lang_replay_inv%");
        Settings = new LangItem(Material.REDSTONE_COMPARATOR).name("%lang_lobby_item_settings%");
        Reset = new LangItem(Material.BARRIER).name("%lang_fb_reset_current%");
        TICK = new LangItem(Material.BLAZE_ROD).name("%lang_replay_step%");
        QUITREPLAY = new LangItem(Material.BARRIER).name("%lang_fb_exit_replay%");
        START = new LangItem(Material.INK_SACK).durability(10).name("%lang_general_start%");
        PAUSE = new LangItem(Material.INK_SACK).durability(1).name("%lang_general_pause%");
        RESTART = new LangItem(Material.INK_SACK).durability(14).name("%lang_general_reset%");
        openkit = new LangItem(Material.SANDSTONE).durability(2).name("%lang_block_inv%");
        COPYID = new LangItem(Material.NAME_TAG).name("Copy Replay ID");

        LangItem[] categoryblockeditor = {
                new LangItem(Material.SANDSTONE).name("%lang_block_item_sandstone%").lore2("%lang_inventory_see_category%"),
                new LangItem(Material.WOOL).name("%lang_block_item_wool%").lore2("%lang_inventory_see_category%"),
                new LangItem(Material.DIAMOND_BLOCK).name("%lang_block_item_ore%").lore2("%lang_inventory_see_category%"),
                new LangItem(Material.DIAMOND_ORE).name("%lang_block_item_ores%").lore2("%lang_inventory_see_category%"),
                new LangItem(Material.QUARTZ_BLOCK).name("%lang_block_item_quartz%").lore2("%lang_inventory_see_category%"),
                new LangItem(Material.WOOD).name("%lang_block_item_wood%").lore2("%lang_inventory_see_category%"),
                new LangItem(Material.STAINED_CLAY).name("%lang_block_item_clay%").lore2("%lang_inventory_see_category%"),
                new LangItem(Material.BARRIER).name("%lang_block_item_special%").lore2("%lang_inventory_see_category%")
        };

        ItemStack[] blockeditor = {
                new ItemBuilder(Material.MELON_BLOCK).build(),
                new ItemBuilder(Material.SNOW_BLOCK).build(),
                new ItemBuilder(Material.OBSIDIAN).build(),
                new ItemBuilder(Material.ENDER_STONE).build()
        };

        ItemStack[] sandstoneeditor = {
                new ItemBuilder(Material.SANDSTONE).build(),
                new ItemBuilder(Material.SANDSTONE).durability(1).build(),
                new ItemBuilder(Material.SANDSTONE).durability(2).build(),
                new ItemBuilder(Material.RED_SANDSTONE).build(),
                new ItemBuilder(Material.RED_SANDSTONE).durability(1).build(),
                new ItemBuilder(Material.RED_SANDSTONE).durability(2).build(),
        };

        ItemStack[] wooleditor = {
                new ItemBuilder(Material.WOOL).build(),
                new ItemBuilder(Material.WOOL).durability(1).build(),
                new ItemBuilder(Material.WOOL).durability(2).build(),
                new ItemBuilder(Material.WOOL).durability(3).build(),
                new ItemBuilder(Material.WOOL).durability(4).build(),
                new ItemBuilder(Material.WOOL).durability(5).build(),
                new ItemBuilder(Material.WOOL).durability(6).build(),
                new ItemBuilder(Material.WOOL).durability(7).build(),
                new ItemBuilder(Material.WOOL).durability(8).build(),
                new ItemBuilder(Material.WOOL).durability(9).build(),
                new ItemBuilder(Material.WOOL).durability(10).build(),
                new ItemBuilder(Material.WOOL).durability(11).build(),
                new ItemBuilder(Material.WOOL).durability(12).build(),
                new ItemBuilder(Material.WOOL).durability(13).build(),
                new ItemBuilder(Material.WOOL).durability(14).build(),
                new ItemBuilder(Material.WOOL).durability(15).build()
        };
        ItemStack[] oreblockeditor = {
                new ItemBuilder(Material.DIAMOND_BLOCK).build(),
                new ItemBuilder(Material.IRON_BLOCK).build(),
                new ItemBuilder(Material.REDSTONE_BLOCK).build(),
                new ItemBuilder(Material.LAPIS_BLOCK).build(),
                new ItemBuilder(Material.EMERALD_BLOCK).build(),
                new ItemBuilder(Material.GOLD_BLOCK).build(),
                new ItemBuilder(Material.COAL_BLOCK).build(),
        };
        ItemStack[] oreeditor = {
                new ItemBuilder(Material.DIAMOND_ORE).build(),
                new ItemBuilder(Material.IRON_ORE).build(),
                new ItemBuilder(Material.REDSTONE_ORE).build(),
                new ItemBuilder(Material.LAPIS_ORE).build(),
                new ItemBuilder(Material.EMERALD_ORE).build(),
                new ItemBuilder(Material.GOLD_ORE).build(),
                new ItemBuilder(Material.COAL_ORE).build(),
        };
        ItemStack[] quartzeditor = {
                new ItemBuilder(Material.QUARTZ_BLOCK).build(),
                new ItemBuilder(Material.QUARTZ_BLOCK).durability(1).build(),
                new ItemBuilder(Material.QUARTZ_BLOCK).durability(2).build(),
        };
        ItemStack[] woodeditor = {
                new ItemBuilder(Material.WOOD).build(),
                new ItemBuilder(Material.WOOD).durability(1).build(),
                new ItemBuilder(Material.WOOD).durability(2).build(),
                new ItemBuilder(Material.WOOD).durability(3).build(),
                new ItemBuilder(Material.WOOD).durability(4).build(),
                new ItemBuilder(Material.WOOD).durability(5).build(),
                new ItemBuilder(Material.LOG).build(),
                new ItemBuilder(Material.LOG).durability(1).build(),
                new ItemBuilder(Material.LOG).durability(2).build(),
                new ItemBuilder(Material.LOG).durability(3).build(),
                new ItemBuilder(Material.LOG_2).build(),
                new ItemBuilder(Material.LOG_2).durability(1).build(),
        };


        ItemStack[] clayeditor = {
                new ItemBuilder(Material.STAINED_CLAY).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(1).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(2).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(3).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(4).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(5).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(6).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(7).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(8).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(9).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(10).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(11).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(12).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(13).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(14).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(15).build(),
                new ItemBuilder(Material.HARD_CLAY).build()
        };

        // blockedit-here
        ItemStack[] specialeditor = {
                new ItemBuilder(Material.BARRIER).build(),
                new ItemBuilder(Material.ENDER_PORTAL_FRAME).build(),
                new ItemBuilder(Material.COMMAND).build(),
                new ItemBuilder(Material.HOPPER).build(),
                new ItemBuilder(Material.BEDROCK).build(),
                new ItemBuilder(Material.IRON_FENCE).build(),
                new ItemBuilder(Material.BIRCH_FENCE).build(),
                new ItemBuilder(Material.COBBLE_WALL).build()
        };


        ItemStack[] blocks = {
                new ItemBuilder(Material.SANDSTONE).amount(64).build(),
                new ItemBuilder(Material.SANDSTONE).durability(1).amount(64).build(),
                new ItemBuilder(Material.SANDSTONE).durability(2).amount(64).build(),
                new ItemBuilder(Material.RED_SANDSTONE).amount(64).build(),
                new ItemBuilder(Material.RED_SANDSTONE).durability(1).amount(64).build(),
                new ItemBuilder(Material.RED_SANDSTONE).durability(2).amount(64).build(),
                new ItemBuilder(Material.WOOL).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(1).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(2).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(3).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(4).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(5).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(6).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(7).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(8).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(9).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(10).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(11).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(12).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(13).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(14).amount(64).build(),
                new ItemBuilder(Material.WOOL).durability(15).amount(64).build(),
                new ItemBuilder(Material.DIAMOND_BLOCK).amount(64).build(),
                new ItemBuilder(Material.IRON_BLOCK).amount(64).build(),
                new ItemBuilder(Material.REDSTONE_BLOCK).amount(64).build(),
                new ItemBuilder(Material.LAPIS_BLOCK).amount(64).build(),
                new ItemBuilder(Material.EMERALD_BLOCK).amount(64).build(),
                new ItemBuilder(Material.GOLD_BLOCK).amount(64).build(),
                new ItemBuilder(Material.COAL_BLOCK).amount(64).build(),
                new ItemBuilder(Material.DIAMOND_ORE).amount(64).build(),
                new ItemBuilder(Material.IRON_ORE).amount(64).build(),
                new ItemBuilder(Material.REDSTONE_ORE).amount(64).build(),
                new ItemBuilder(Material.LAPIS_ORE).amount(64).build(),
                new ItemBuilder(Material.EMERALD_ORE).amount(64).build(),
                new ItemBuilder(Material.GOLD_ORE).amount(64).build(),
                new ItemBuilder(Material.COAL_ORE).amount(64).build(),
                new ItemBuilder(Material.QUARTZ_BLOCK).amount(64).build(),
                new ItemBuilder(Material.QUARTZ_BLOCK).durability(1).amount(64).build(),
                new ItemBuilder(Material.QUARTZ_BLOCK).durability(2).amount(64).build(),
                new ItemBuilder(Material.WOOD).amount(64).build(),
                new ItemBuilder(Material.WOOD).durability(1).amount(64).build(),
                new ItemBuilder(Material.WOOD).durability(2).amount(64).build(),
                new ItemBuilder(Material.WOOD).durability(3).amount(64).build(),
                new ItemBuilder(Material.WOOD).durability(4).amount(64).build(),
                new ItemBuilder(Material.WOOD).durability(5).amount(64).build(),
                new ItemBuilder(Material.LOG).amount(64).build(),
                new ItemBuilder(Material.LOG).durability(1).amount(64).build(),
                new ItemBuilder(Material.LOG).durability(2).amount(64).build(),
                new ItemBuilder(Material.LOG).durability(3).amount(64).build(),
                new ItemBuilder(Material.LOG_2).amount(64).build(),
                new ItemBuilder(Material.LOG_2).durability(1).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(1).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(2).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(3).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(4).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(5).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(6).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(7).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(8).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(9).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(10).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(12).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(13).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build(),
                new ItemBuilder(Material.STAINED_CLAY).durability(15).amount(64).build(),
                new ItemBuilder(Material.HARD_CLAY).amount(64).build(),

                // blockedit-here
                new ItemBuilder(Material.BARRIER).amount(64).build(),
                new ItemBuilder(Material.ENDER_PORTAL_FRAME).amount(64).build(),
                new ItemBuilder(Material.COMMAND).amount(64).build(),
                new ItemBuilder(Material.HOPPER).amount(64).build(),
                new ItemBuilder(Material.BEDROCK).amount(64).build(),
                new ItemBuilder(Material.IRON_FENCE).amount(64).build(),
                new ItemBuilder(Material.BIRCH_FENCE).amount(64).build(),
                new ItemBuilder(Material.COBBLE_WALL).amount(64).build(),

                new ItemBuilder(Material.MELON_BLOCK).durability(2).amount(64).build(),
                new ItemBuilder(Material.SNOW_BLOCK).amount(64).build(),
                new ItemBuilder(Material.OBSIDIAN).amount(64).build(),
                new ItemBuilder(Material.ENDER_STONE).amount(64).build()
        };

        for (int j = 0; j < blocks.length; j++) {
            ItemStackList.Blocks[j] = blocks[j];
        }
        for (int j = 0; j < categoryblockeditor.length; j++) {
            ItemStackList.categoryblockeditor[j] = categoryblockeditor[j];
        }
        for (int j = 0; j < blockeditor.length; j++) {
            ItemStackList.blockeditor[j] = blockeditor[j];
        }
        for (int j = 0; j < sandstoneeditor.length; j++) {
            ItemStackList.sandstoneeditor[j] = sandstoneeditor[j];
        }
        for (int j = 0; j < wooleditor.length; j++) {
            ItemStackList.wooleditor[j] = wooleditor[j];
        }
        for (int j = 0; j < oreblockeditor.length; j++) {
            ItemStackList.oreblockeditor[j] = oreblockeditor[j];
        }
        for (int j = 0; j < oreeditor.length; j++) {
            ItemStackList.oreeditor[j] = oreeditor[j];
        }
        for (int j = 0; j < quartzeditor.length; j++) {
            ItemStackList.quartzeditor[j] = quartzeditor[j];
        }
        for (int j = 0; j < woodeditor.length; j++) {
            ItemStackList.woodeditor[j] = woodeditor[j];
        }
        for (int j = 0; j < clayeditor.length; j++) {
            ItemStackList.clayeditor[j] = clayeditor[j];
        }
        for (int j = 0; j < specialeditor.length; j++) {
            ItemStackList.specialeditor[j] = specialeditor[j];
        }
    }
}
