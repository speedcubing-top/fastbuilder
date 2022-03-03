package speedcubing.List;

import cubing.bukkit.ItemUtils;
import cubingserver.StringList.GlobalString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class ItemStackList {
    public static final InventoryHolder menu = () -> null;
    public static final InventoryHolder settings = () -> null;
    public static final InventoryHolder confirm = () -> null;
    public static ItemStack[] openkit = new ItemStack[4];
    public static ItemStack[] navigator = new ItemStack[4];
    public static ItemStack[] pickaxe = new ItemStack[4];
    public static ItemStack[] Settings = new ItemStack[4];
    public static ItemStack[] Reset = new ItemStack[4];
    public static ItemStack[] Length = new ItemStack[4];
    public static ItemStack[] Height = new ItemStack[4];

    public static ItemStack[][] Blocks = new ItemStack[72][4];

    public static ItemStack[][] categoryblockeditor = new ItemStack[7][4];

    public static ItemStack[][] blockeditor = new ItemStack[4][4];
    public static ItemStack[][] wooleditor = new ItemStack[16][4];
    public static ItemStack[][] sandstoneeditor = new ItemStack[6][4];
    public static ItemStack[][] oreblockeditor = new ItemStack[7][4];
    public static ItemStack[][] oreeditor = new ItemStack[7][4];
    public static ItemStack[][] quartzeditor = new ItemStack[3][4];
    public static ItemStack[][] woodeditor = new ItemStack[12][4];
    public static ItemStack[][] clayeditor = new ItemStack[17][4];


    public static final String[] blockedit = {"§bBlock Skin", "§bBlock Skin", "§bBlock Skin", "§bBlock Skin"};
    public static final String[] sandstoneedit = {"§bCategory: Sandstone", "§bCategory: Sandstone", "§bCategory: Sandstone", "§bCategory: Sandstone"};
    public static final String[] wooledit = {"§bCategory: Wool", "§bCategory: Wool", "§bCategory: Wool", "§bCategory: Wool"};
    public static final String[] oreblockedit = {"§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)", "§bCategory: Ore (Blocks)"};
    public static final String[] oreedit = {"§bCategory: Ores", "§bCategory: Ores", "§bCategory: Ores", "§bCategory: Ores"};
    public static final String[] quartzedit = {"§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks", "§bCategory: Quartz Blocks"};
    public static final String[] woodedit = {"§bCategory: Woods", "§bCategory: Woods", "§bCategory: Woods", "§bCategory: Woods"};
    public static final String[] clayedit = {"§bCategory: Clay", "§bCategory: Clay", "§bCategory: Clay", "§bCategory: Clay"};

    public static final Inventory[] ResetConfirm = new Inventory[4];

    public void Load() {
        String[] blockedit = {"§bBlock Skin", "§bBlock Skin", "§bBlock Skin", "§bBlock Skin"};
        String[][] block = {
                {"§bSandstone Blocks", "§bSandstone Blocks", "§bSandstone Blocks", "§bSandstone Blocks"},
                {"§bWool Blocks", "§bWool Blocks", "§bWool Blocks", "§bWool Blocks"},
                {"§bOre (Blocks)", "§bOre (Blocks)", "§bOre (Blocks)", "§bOre (Blocks)"},
                {"§bOres", "§bOres", "§bOres", "§bOres"},
                {"§bQuartz Blocks", "§bQuartz Blocks", "§bQuartz Blocks", "§bQuartz Blocks"},
                {"§bWoods", "§bWoods", "§bWoods", "§bWoods"},
                {"§bClay", "§bClay", "§bClay", "§bClay"}
        };
        String[] yes = {"§aYes", "§aYes", "§aYes", "§aYes"};
        String[] no = {"§cNo", "§cNo", "§cNo", "§cNo"};
        for (int i = 0; i < 4; i++) {
            Length[i] = ItemUtils.item(Material.IRON_INGOT, new String[]{"§bLength (§aLeft--§8/§cRight++§b)", "§bLength (§aLeft--§8/§cRight++§b)", "§bLength (§aLeft--§8/§cRight++§b)", "§bLength (§aLeft--§8/§cRight++§b)"}[i], 1, -1, false, false, null, null, null, null);
            Height[i] = ItemUtils.item(Material.GOLD_INGOT, new String[]{"§bHeight (§aLeft--§8/§cRight++§b)", "§bHeight (§aLeft--§8/§cRight++§b)", "§bHeight (§aLeft--§8/§cRight++§b)", "§bHeight (§aLeft--§8/§cRight++§b)"}[i], 1, -1, false, false, null, null, null, null);
            navigator[i] = ItemUtils.item(Material.COMPASS, new String[]{"§aSwitch Mode", "§aSwitch Mode", "§aSwitch Mode", "§aSwitch Mode"}[i], 1, -1, false, false, null, null, null, null);
            pickaxe[i] = ItemUtils.item(Material.DIAMOND_PICKAXE, new String[]{"§6Tool for Breaking Block", "§6Tool for Breaking Block", "§6Tool for Breaking Block", "§6Tool for Breaking Block"}[i], 1, -1, true, true, new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES}, null, null, null);
            Settings[i] = ItemUtils.item(Material.REDSTONE_COMPARATOR, new String[]{"§cSettings", "§cSettings", "§cSettings", "§cSettings"}[i], 1, -1, false, false, null, null, null, null);
            Reset[i] = ItemUtils.item(Material.BARRIER, new String[]{"§cReset Personal Best in your current mode", "§cReset Personal Best in your current mode", "§cReset Personal Best in your current mode", "§cReset Personal Best in your current mode"}[i], 1, -1, false, false, null, null, null, null);
            openkit[i] = ItemUtils.item(Material.SANDSTONE, blockedit[i], 1, 2, false, false, null, null, null, null);
            ResetConfirm[i] = Bukkit.createInventory(confirm, 27, new String[]{"§cAre you sure you want to reset?", "§cAre you sure you want to reset?", "§cAre you sure you want to reset?", "§cAre you sure you want to reset?"}[i]);
            ResetConfirm[i].setItem(11, ItemUtils.item(Material.STAINED_CLAY, yes[i], 1, 13, false, false, null, null, null, null));
            ResetConfirm[i].setItem(15, ItemUtils.item(Material.STAINED_CLAY, no[i], 1, 14, false, false, null, null, null, null));
            ItemStack[] categoryblockeditor = {
                    ItemUtils.item(Material.SANDSTONE, block[0][i], 1, -1, false, false, null, null, null, Collections.singletonList(GlobalString.category[i])),
                    ItemUtils.item(Material.WOOL, block[1][i], 1, -1, false, false, null, null, null, Collections.singletonList(GlobalString.category[i])),
                    ItemUtils.item(Material.DIAMOND_BLOCK, block[2][i], 1, -1, false, false, null, null, null, Collections.singletonList(GlobalString.category[i])),
                    ItemUtils.item(Material.DIAMOND_ORE, block[3][i], 1, -1, false, false, null, null, null, Collections.singletonList(GlobalString.category[i])),
                    ItemUtils.item(Material.QUARTZ_BLOCK, block[4][i], 1, -1, false, false, null, null, null, Collections.singletonList(GlobalString.category[i])),
                    ItemUtils.item(Material.WOOD, block[5][i], 1, -1, false, false, null, null, null, Collections.singletonList(GlobalString.category[i])),
                    ItemUtils.item(Material.STAINED_CLAY, block[6][i], 1, -1, false, false, null, null, null, Collections.singletonList(GlobalString.category[i]))
            };

            ItemStack[] blockeditor = {
                    ItemUtils.item(Material.MELON_BLOCK, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.SNOW_BLOCK, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.OBSIDIAN, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.ENDER_STONE, null, 1, -1, false, false, null, null, null, null)
            };

            ItemStack[] sandstoneeditor = {
                    ItemUtils.item(Material.SANDSTONE, null, 1, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.SANDSTONE, null, 1, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.SANDSTONE, null, 1, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.RED_SANDSTONE, null, 1, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.RED_SANDSTONE, null, 1, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.RED_SANDSTONE, null, 1, 2, false, false, null, null, null, null),
            };

            ItemStack[] wooleditor = {
                    ItemUtils.item(Material.WOOL, null, 1, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 3, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 4, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 5, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 6, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 7, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 8, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 9, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 10, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 11, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 12, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 13, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 14, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 1, 15, false, false, null, null, null, null)
            };
            ItemStack[] oreblockeditor = {
                    ItemUtils.item(Material.DIAMOND_BLOCK, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.IRON_BLOCK, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.REDSTONE_BLOCK, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.LAPIS_BLOCK, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.EMERALD_BLOCK, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.GOLD_BLOCK, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.COAL_BLOCK, null, 1, -1, false, false, null, null, null, null),
            };
            ItemStack[] oreeditor = {
                    ItemUtils.item(Material.DIAMOND_ORE, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.IRON_ORE, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.REDSTONE_ORE, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.LAPIS_ORE, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.EMERALD_ORE, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.GOLD_ORE, null, 1, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.COAL_ORE, null, 1, -1, false, false, null, null, null, null),
            };
            ItemStack[] quartzeditor = {
                    ItemUtils.item(Material.QUARTZ_BLOCK, null, 1, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.QUARTZ_BLOCK, null, 1, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.QUARTZ_BLOCK, null, 1, 2, false, false, null, null, null, null),
            };
            ItemStack[] woodeditor = {
                    ItemUtils.item(Material.WOOD, null, 1, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 1, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 1, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 1, 3, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 1, 4, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 1, 5, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG, null, 1, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG, null, 1, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG, null, 1, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG, null, 1, 3, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG_2, null, 1, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG_2, null, 1, 1, false, false, null, null, null, null),
            };


            ItemStack[] clayeditor = {
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 3, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 4, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 5, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 6, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 7, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 8, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 9, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 10, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 11, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 12, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 13, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 14, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 1, 15, false, false, null, null, null, null),
                    ItemUtils.item(Material.HARD_CLAY, null, 1, -1, false, false, null, null, null, null)
            };
            ItemStack[] blocks = {
                    ItemUtils.item(Material.SANDSTONE, null, 64, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.SANDSTONE, null, 64, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.SANDSTONE, null, 64, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.RED_SANDSTONE, null, 64, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.RED_SANDSTONE, null, 64, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.RED_SANDSTONE, null, 64, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 3, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 4, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 5, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 6, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 7, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 8, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 9, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 10, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 11, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 12, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 13, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 14, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOL, null, 64, 15, false, false, null, null, null, null),
                    ItemUtils.item(Material.DIAMOND_BLOCK, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.IRON_BLOCK, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.REDSTONE_BLOCK, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.LAPIS_BLOCK, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.EMERALD_BLOCK, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.GOLD_BLOCK, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.COAL_BLOCK, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.DIAMOND_ORE, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.IRON_ORE, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.REDSTONE_ORE, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.LAPIS_ORE, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.EMERALD_ORE, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.GOLD_ORE, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.COAL_ORE, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.QUARTZ_BLOCK, null, 64, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.QUARTZ_BLOCK, null, 64, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.QUARTZ_BLOCK, null, 64, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 64, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 64, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 64, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 64, 3, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 64, 4, false, false, null, null, null, null),
                    ItemUtils.item(Material.WOOD, null, 64, 5, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG, null, 64, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG, null, 64, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG, null, 64, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG, null, 64, 3, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG_2, null, 64, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.LOG_2, null, 64, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 0, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 1, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 3, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 4, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 5, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 6, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 7, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 8, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 9, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 10, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 11, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 12, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 13, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 14, false, false, null, null, null, null),
                    ItemUtils.item(Material.STAINED_CLAY, null, 64, 15, false, false, null, null, null, null),
                    ItemUtils.item(Material.HARD_CLAY, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.MELON_BLOCK, null, 64, 2, false, false, null, null, null, null),
                    ItemUtils.item(Material.SNOW_BLOCK, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.OBSIDIAN, null, 64, -1, false, false, null, null, null, null),
                    ItemUtils.item(Material.ENDER_STONE, null, 64, -1, false, false, null, null, null, null)
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
        }
    }
}
