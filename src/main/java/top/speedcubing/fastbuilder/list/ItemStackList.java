package top.speedcubing.fastbuilder.list;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.ReplayGUI;
import top.speedcubing.lib.bukkit.inventory.ItemBuilder;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.lang.LangInventory;
import top.speedcubing.server.lang.LangItemStack;

public class ItemStackList {
    public static final InventoryHolder settings = () -> null;
    public static final InventoryHolder confirm = () -> null;
    public static ItemStack[] openkit = new ItemStack[4];
    public static ItemStack[] navigator = new ItemStack[4];
    public static ItemStack[] pickaxe = new ItemStack[4];
    public static ItemStack[] Settings = new ItemStack[4];
    public static ItemStack[] Reset = new ItemStack[4];
    public static ItemStack[] Length = new ItemStack[4];
    public static ItemStack[] Height = new ItemStack[4];
    public static ItemStack[] Replay = new ItemStack[4];
    public static ItemStack[] TICK = new ItemStack[4];
    public static ItemStack[] QUITREPLAY = new ItemStack[4];
    public static ItemStack[] START = new ItemStack[4];
    public static ItemStack[] PAUSE = new ItemStack[4];
    public static ItemStack[] RESTART = new ItemStack[4];
    public static ItemStack[] COPYID = new ItemStack[4];

    public static ItemStack[][] Blocks = new ItemStack[81][4]; // blockedit-here

    public static ItemStack[][] categoryblockeditor = new ItemStack[8][4];

    public static ItemStack[][] blockeditor = new ItemStack[4][4];

    public static ItemStack[][] wooleditor = new ItemStack[16][4];
    public static ItemStack[][] sandstoneeditor = new ItemStack[6][4];
    public static ItemStack[][] oreblockeditor = new ItemStack[7][4];
    public static ItemStack[][] oreeditor = new ItemStack[7][4];
    public static ItemStack[][] quartzeditor = new ItemStack[3][4];
    public static ItemStack[][] woodeditor = new ItemStack[12][4];
    public static ItemStack[][] clayeditor = new ItemStack[17][4];
    public static ItemStack[][] specialeditor = new ItemStack[9][4]; // blockedit-here
    public static LangInventory replayBook;
    public static final String[] blockedit = {"§bBlock Skin", "§bBlock Skin", "§bBlock Skin", "§bBlock Skin"};
    public static final String[] sandstoneedit = {"§bCategory: Sandstone", "§bCategory: Sandstone", "§bCategory: Sandstone", "§bCategory: Sandstone"};
    public static final String[] wooledit = {"§bCategory: Wool", "§bCategory: Wool", "§bCategory: Wool", "§bCategory: Wool"};
    public static final String[] oreblockedit = {"§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)"};
    public static final String[] oreedit = {"§bCategory: Ores", "§bCategory: Ores", "§bCategory: Ores", "§bCategory: Ores"};
    public static final String[] quartzedit = {"§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks"};
    public static final String[] woodedit = {"§bCategory: Woods", "§bCategory: Woods", "§bCategory: Woods", "§bCategory: Woods"};
    public static final String[] clayedit = {"§bCategory: Clay", "§bCategory: Clay", "§bCategory: Clay", "§bCategory: Clay"};
    public static final String[] specialedit = {"§bSpecial Blocks", "§bSpecial Blocks", "§bSpecial Blocks", "§bSpecial Blocks"};

    public static final Inventory[] ResetConfirm = new Inventory[4];

    public void Load() {
        replayBook = new LangInventory(27, "fb_replaybook_title")
                .setItem(new LangItemStack(Material.LEATHER, "fb_lastattempt_leather"), (e) -> {
                    Member.getMember(e.getPlayer()).joinReplayLastAttempt();
                    e.getPlayer().closeInventory();
                }, 11)
                .setItem(new LangItemStack(Material.PAPER, "fb_replay_fav"), (e) -> {
                    ReplayGUI.openReplayPage(e.getPlayer(), Member.getMember(e.getPlayer()).currentMode.sqlId, true,0);
                }, 13)
                .setItem(new LangItemStack(Material.PAPER, "fb_replay_non_fav"), (e) -> {
                    ReplayGUI.openReplayPage(e.getPlayer(), Member.getMember(e.getPlayer()).currentMode.sqlId, false,0);
                }, 15);

        String[] blockedit = {"§bBlock Skin", "§bBlock Skin", "§bBlock Skin", "§bBlock Skin"};
        String[][] block = {
                {"§bSandstone Blocks", "§bSandstone Blocks", "§bSandstone Blocks", "§bSandstone Blocks"},
                {"§bWool Blocks", "§bWool Blocks", "§bWool Blocks", "§bWool Blocks"},
                {"§bOre (Blocks)", "§bOre (Blocks)", "§bOre (Blocks)", "§bOre (Blocks)"},
                {"§bOres", "§bOres", "§bOres", "§bOres"},
                {"§bQuartz Blocks", "§bQuartz Blocks", "§bQuartz Blocks", "§bQuartz Blocks"},
                {"§bWoods", "§bWoods", "§bWoods", "§bWoods"},
                {"§bClay", "§bClay", "§bClay", "§bClay"},
                {"§bSpecial Blocks", "§bSpecial Blocks", "§bSpecial Blocks", "§bSpecial Blocks"}
        };
        String[] yes = {"§aYes", "§aYes", "§aYes", "§aYes"};
        String[] no = {"§cNo", "§cNo", "§cNo", "§cNo"};
        for (int i = 0; i < 4; i++) {
            Length[i] = new ItemBuilder(Material.IRON_INGOT).name(new String[]{"§bLength (§aLeft--§8/§cRight++§b)", "§bLength (§aLeft--§8/§cRight++§b)", "§bLength (§aLeft--§8/§cRight++§b)", "§bLength (§aLeft--§8/§cRight++§b)"}[i]).build();
            Height[i] = new ItemBuilder(Material.GOLD_INGOT).name(new String[]{"§bHeight (§aLeft--§8/§cRight++§b)", "§bHeight (§aLeft--§8/§cRight++§b)", "§bHeight (§aLeft--§8/§cRight++§b)", "§bHeight (§aLeft--§8/§cRight++§b)"}[i]).build();
            navigator[i] = new ItemBuilder(Material.COMPASS).name(new String[]{"§aSwitch Mode", "§aSwitch Mode", "§aSwitch Mode", "§aSwitch Mode"}[i]).build();
            pickaxe[i] = new ItemBuilder(Material.DIAMOND_PICKAXE).glow().name(new String[]{"§6Tool for Breaking Block", "§6Tool for Breaking Block", "§6Tool for Breaking Block", "§6Tool for Breaking Block"}[i]).hideAttr().build();
            Replay[i] = new ItemBuilder(Material.BOOK).name(new String[]{"§bReplay", "§bReplay", "§bReplay", "§bReplay"}[i]).build();
            Settings[i] = new ItemBuilder(Material.REDSTONE_COMPARATOR).name(new String[]{"§cSettings", "§cSettings", "§cSettings", "§cSettings"}[i]).build();
            Reset[i] = new ItemBuilder(Material.BARRIER).name(new String[]{"§cReset Personal Best in your current mode", "§cReset Personal Best in your current mode", "§cReset Personal Best in your current mode", "§cReset Personal Best in your current mode"}[i]).build();
            TICK[i] = new ItemBuilder(Material.BLAZE_ROD).name(new String[]{"§aLeft--§8/§cRight++", "§aLeft--§8/§cRight++", "§aLeft--§8/§cRight++", "§aLeft--§8/§cRight++"}[i]).build();
            QUITREPLAY[i] = new ItemBuilder(Material.BARRIER).name(new String[]{"§cExit Replay", "§cExit Replay", "§cExit Replay", "§cExit Replay"}[i]).build();
            START[i] = new ItemBuilder(Material.INK_SACK).durability(10).name(new String[]{"§aStart", "§aStart", "§aStart", "§aStart"}[i]).build();
            PAUSE[i] = new ItemBuilder(Material.INK_SACK).durability(1).name(new String[]{"§cPause", "§cPause", "§cPause", "§cPause"}[i]).build();
            RESTART[i] = new ItemBuilder(Material.INK_SACK).durability(14).name(new String[]{"§6Reset", "§6Reset", "§6Reset", "§6Reset"}[i]).build();
            openkit[i] = new ItemBuilder(Material.SANDSTONE).durability(2).name(blockedit[i]).build();
            COPYID[i] = new ItemBuilder(Material.NAME_TAG).name("Copy Replay ID").build();
            ResetConfirm[i] = Bukkit.createInventory(confirm, 27, new String[]{"§cAre you sure you want to reset?", "§cAre you sure you want to reset?", "§cAre you sure you want to reset?", "§cAre you sure you want to reset?"}[i]);
            ResetConfirm[i].setItem(11, new ItemBuilder(Material.STAINED_CLAY).durability(13).name(yes[i]).build());
            ResetConfirm[i].setItem(15, new ItemBuilder(Material.STAINED_CLAY).durability(14).name(no[i]).build());
            ItemStack[] categoryblockeditor = {
                    new ItemBuilder(Material.SANDSTONE).name(block[0][i]).lore(GlobalString.category.get(i)).build(),
                    new ItemBuilder(Material.WOOL).name(block[1][i]).lore(GlobalString.category.get(i)).build(),
                    new ItemBuilder(Material.DIAMOND_BLOCK).name(block[2][i]).lore(GlobalString.category.get(i)).build(),
                    new ItemBuilder(Material.DIAMOND_ORE).name(block[3][i]).lore(GlobalString.category.get(i)).build(),
                    new ItemBuilder(Material.QUARTZ_BLOCK).name(block[4][i]).lore(GlobalString.category.get(i)).build(),
                    new ItemBuilder(Material.WOOD).name(block[5][i]).lore(GlobalString.category.get(i)).build(),
                    new ItemBuilder(Material.STAINED_CLAY).name(block[6][i]).lore(GlobalString.category.get(i)).build(),
                    new ItemBuilder(Material.BARRIER).name(block[7][i]).lore(GlobalString.category.get(i)).build()
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
                    new ItemBuilder(Material.COBBLE_WALL).build(),
                    new ItemBuilder(Material.WOOL).durability(0).ench(Enchantment.DURABILITY, 1).hideEnch().name("§cR§6A§eI§aN§bB§9O§5W").build(),
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
                    new ItemBuilder(Material.WOOL).durability(0).ench(Enchantment.DURABILITY, 1).hideEnch().name("§cR§6A§eI§aN§bB§9O§5W").amount(64).build(),

                    new ItemBuilder(Material.MELON_BLOCK).durability(2).amount(64).build(),
                    new ItemBuilder(Material.SNOW_BLOCK).amount(64).build(),
                    new ItemBuilder(Material.OBSIDIAN).amount(64).build(),
                    new ItemBuilder(Material.ENDER_STONE).amount(64).build()
            };

            for (int j = 0; j < blocks.length; j++) {
                ItemStackList.Blocks[j][i] = blocks[j];
            }
            for (int j = 0; j < categoryblockeditor.length; j++) {
                ItemStackList.categoryblockeditor[j][i] = categoryblockeditor[j];
            }
            for (int j = 0; j < blockeditor.length; j++) {
                ItemStackList.blockeditor[j][i] = blockeditor[j];
            }
            for (int j = 0; j < sandstoneeditor.length; j++) {
                ItemStackList.sandstoneeditor[j][i] = sandstoneeditor[j];
            }
            for (int j = 0; j < wooleditor.length; j++) {
                ItemStackList.wooleditor[j][i] = wooleditor[j];
            }
            for (int j = 0; j < oreblockeditor.length; j++) {
                ItemStackList.oreblockeditor[j][i] = oreblockeditor[j];
            }
            for (int j = 0; j < oreeditor.length; j++) {
                ItemStackList.oreeditor[j][i] = oreeditor[j];
            }
            for (int j = 0; j < quartzeditor.length; j++) {
                ItemStackList.quartzeditor[j][i] = quartzeditor[j];
            }
            for (int j = 0; j < woodeditor.length; j++) {
                ItemStackList.woodeditor[j][i] = woodeditor[j];
            }
            for (int j = 0; j < clayeditor.length; j++) {
                ItemStackList.clayeditor[j][i] = clayeditor[j];
            }
            for (int j = 0; j < specialeditor.length; j++) {
                ItemStackList.specialeditor[j][i] = specialeditor[j];
            }
        }
    }
}
