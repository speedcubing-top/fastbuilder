package speedcubing.Core;

import cubing.spigot.lib.bukkit.ItemUtils;
import cubingserver.StringList.GlobalString;
import cubingserver.libs.User;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import speedcubing.List.ItemStackList;

import java.util.Collections;
import java.util.UUID;

public class BlockEditor {

    public static final InventoryHolder BlockHolder = () -> null;
    public static final InventoryHolder sandstoneholder = () -> null;
    public static final InventoryHolder woolholder = () -> null;
    public static final InventoryHolder oreblockeditor = () -> null;
    public static final InventoryHolder oreeditor = () -> null;
    public static final InventoryHolder quartzeditor = () -> null;
    public static final InventoryHolder woodeditor = () -> null;
    public static final InventoryHolder clayeditor = () -> null;


    static int length;

    public void Load() {
        length = ItemStackList.wooleditor.length + ItemStackList.sandstoneeditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length + ItemStackList.woodeditor.length + ItemStackList.clayeditor.length;
    }

    public static void createMainPage(Player player) {
        player.playSound(player.getLocation(), Sound.CLICK, 1F, 1F);
        UUID uuid = player.getUniqueId();
        int lang = User.getLang(uuid);
        Inventory inv = Bukkit.createInventory(BlockHolder, 27, ItemStackList.blockedit[lang]);
        for (int i = 0; i < ItemStackList.categoryblockeditor.length; i++) {
            inv.setItem(i + 1, ItemStackList.categoryblockeditor[i][lang]);
        }
        for (int i = 0; i < ItemStackList.blockeditor.length; i++) {
            inv.setItem(i + 12, ItemStackList.blockeditor[i][lang]);
        }
        int x = Core.integerCache.get(uuid)[0];
        int index = x - length;
        if (index >= 0)
            inv.setItem(index + 12, ItemUtils.glow(ItemUtils.setLore(ItemStackList.blockeditor[index][lang].clone(), Collections.singletonList(GlobalString.selected[lang]))));
        player.openInventory(inv);
    }

    public static void select(InventoryHolder holder, InventoryClickEvent e, int RawSlot, Player player) {
        if (holder == BlockHolder) {
            e.setCancelled(true);
            if (RawSlot >= 12 && RawSlot < 12 + ItemStackList.blockeditor.length) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                int x = RawSlot + length - 12;
                UUID uuid = player.getUniqueId();
                Core.integerCache.get(uuid)[0] = x;
                player.closeInventory();
                int lang = User.getLang(uuid);
                ItemStack stack = ItemStackList.Blocks[x][lang];
                player.getInventory().setItem(0, stack);
                player.getInventory().setItem(1, stack);
                player.sendMessage(GlobalString.SavedSettings[lang]);
            } else if (RawSlot >= 1 && RawSlot <= ItemStackList.categoryblockeditor.length) {
                e.setCancelled(true);
                switch (RawSlot) {
                    case 1: {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        UUID uuid = player.getUniqueId();
                        int lang = User.getLang(uuid);
                        Inventory inv = Bukkit.createInventory(sandstoneholder, 27, ItemStackList.sandstoneedit[lang]);
                        for (int i = 0; i < 3; i++) {
                            inv.setItem(i + 10, ItemStackList.sandstoneeditor[i][lang]);
                        }
                        for (int i = 3; i < 6; i++) {
                            inv.setItem(i + 11, ItemStackList.sandstoneeditor[i][lang]);
                        }
                        int set = Core.integerCache.get(uuid)[0];
                        if (set < 6)
                            inv.setItem((set < 3) ? set + 10 : set + 11, ItemUtils.glow(ItemUtils.setLore(ItemStackList.sandstoneeditor[set][lang].clone(), Collections.singletonList(GlobalString.selected[lang]))));
                        player.openInventory(inv);
                    }
                    break;
                    case 2: {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        UUID uuid = player.getUniqueId();
                        int lang = User.getLang(uuid);
                        Inventory inv = Bukkit.createInventory(woolholder, 27, ItemStackList.wooledit[lang]);
                        for (int i = 0; i < ItemStackList.wooleditor.length; i++) {
                            inv.setItem(i, ItemStackList.wooleditor[i][lang]);
                        }
                        int index = Core.integerCache.get(uuid)[0] - ItemStackList.sandstoneeditor.length;
                        if (index < ItemStackList.wooleditor.length && index >= 0)
                            inv.setItem(index, ItemUtils.glow(ItemUtils.setLore(ItemStackList.wooleditor[index][lang].clone(), Collections.singletonList(GlobalString.selected[lang]))));
                        player.openInventory(inv);
                    }
                    break;
                    case 3: {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        UUID uuid = player.getUniqueId();
                        int lang = User.getLang(uuid);
                        Inventory inv = Bukkit.createInventory(oreblockeditor, 27, ItemStackList.oreblockedit[lang]);
                        for (int i = 0; i < ItemStackList.oreblockeditor.length; i++) {
                            inv.setItem(i + 10, ItemStackList.oreblockeditor[i][lang]);
                        }
                        int index = Core.integerCache.get(uuid)[0] - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length);
                        if (index < ItemStackList.oreblockeditor.length && index >= 0)
                            inv.setItem(index + 10, ItemUtils.glow(ItemUtils.setLore(ItemStackList.oreblockeditor[index][lang].clone(), Collections.singletonList(GlobalString.selected[lang]))));
                        player.openInventory(inv);
                    }
                    break;
                    case 4: {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        UUID uuid = player.getUniqueId();
                        int lang = User.getLang(uuid);
                        Inventory inv = Bukkit.createInventory(oreeditor, 27, ItemStackList.oreedit[lang]);
                        for (int i = 0; i < ItemStackList.oreeditor.length; i++) {
                            inv.setItem(i + 10, ItemStackList.oreeditor[i][lang]);
                        }
                        int index = Core.integerCache.get(uuid)[0] - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length);
                        if (index < ItemStackList.oreeditor.length && index >= 0)
                            inv.setItem(index + 10, ItemUtils.glow(ItemUtils.setLore(ItemStackList.oreeditor[index][lang].clone(), Collections.singletonList(GlobalString.selected[lang]))));
                        player.openInventory(inv);
                    }
                    break;
                    case 5: {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        UUID uuid = player.getUniqueId();
                        int lang = User.getLang(uuid);
                        Inventory inv = Bukkit.createInventory(quartzeditor, 27, ItemStackList.quartzedit[lang]);
                        for (int i = 0; i < ItemStackList.quartzeditor.length; i++) {
                            inv.setItem(i + 12, ItemStackList.quartzeditor[i][lang]);
                        }
                        int index = Core.integerCache.get(uuid)[0] - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length);
                        if (index < ItemStackList.quartzeditor.length && index >= 0)
                            inv.setItem(index + 12, ItemUtils.glow(ItemUtils.setLore(ItemStackList.quartzeditor[index][lang].clone(), Collections.singletonList(GlobalString.selected[lang]))));
                        player.openInventory(inv);
                    }
                    break;
                    case 6: {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        UUID uuid = player.getUniqueId();
                        int lang = User.getLang(uuid);
                        Inventory inv = Bukkit.createInventory(woodeditor, 27, ItemStackList.woodedit[lang]);
                        for (int i = 0; i < 3; i++) {
                            inv.setItem(i + 1, ItemStackList.woodeditor[i][lang]);
                        }
                        for (int i = 3; i < 6; i++) {
                            inv.setItem(i + 2, ItemStackList.woodeditor[i][lang]);
                        }
                        for (int i = 6; i < 9; i++) {
                            inv.setItem(i + 4, ItemStackList.woodeditor[i][lang]);
                        }
                        for (int i = 9; i < 12; i++) {
                            inv.setItem(i + 5, ItemStackList.woodeditor[i][lang]);
                        }
                        int index = Core.integerCache.get(uuid)[0] - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length);
                        if (index < ItemStackList.woodeditor.length && index >= 0)
                            inv.setItem((index < 3 ? index + 1 : (index < 6 ? index + 2 : (index < 9 ? index + 4 : index + 5))), ItemUtils.glow(ItemUtils.setLore(ItemStackList.woodeditor[index][lang].clone(), Collections.singletonList(GlobalString.selected[lang]))));
                        player.openInventory(inv);
                    }
                    break;
                    case 7: {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        UUID uuid = player.getUniqueId();
                        int lang = User.getLang(uuid);
                        Inventory inv = Bukkit.createInventory(clayeditor, 27, ItemStackList.clayedit[lang]);
                        for (int i = 0; i < ItemStackList.clayeditor.length; i++) {
                            inv.setItem(i, ItemStackList.clayeditor[i][lang]);
                        }
                        int index = Core.integerCache.get(uuid)[0] - (ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length + ItemStackList.woodeditor.length);
                        if (index < ItemStackList.clayeditor.length && index >= 0)
                            inv.setItem(index, ItemUtils.glow(ItemUtils.setLore(ItemStackList.clayeditor[index][lang].clone(), Collections.singletonList(GlobalString.selected[lang]))));
                        player.openInventory(inv);
                    }
                    break;
                }
            }
        } else if (holder == sandstoneholder) {
            e.setCancelled(true);
            if (RawSlot > 9 && RawSlot < 17 && RawSlot != 13) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                int x = RawSlot > 12 ? RawSlot - 11 : RawSlot - 10;
                UUID uuid = player.getUniqueId();
                int lang = User.getLang(uuid);
                Core.integerCache.get(uuid)[0] = x;
                player.closeInventory();
                ItemStack stack = ItemStackList.Blocks[x][lang];
                player.getInventory().setItem(0, stack);
                player.getInventory().setItem(1, stack);
                player.sendMessage(GlobalString.SavedSettings[lang]);
            }
        } else if (holder == woolholder) {
            e.setCancelled(true);
            if (RawSlot < 16) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                int x = RawSlot + ItemStackList.sandstoneeditor.length;
                UUID uuid = player.getUniqueId();
                int lang = User.getLang(uuid);
                Core.integerCache.get(uuid)[0] = x;
                player.closeInventory();
                ItemStack stack = ItemStackList.Blocks[x][lang];
                player.getInventory().setItem(0, stack);
                player.getInventory().setItem(1, stack);
                player.sendMessage(GlobalString.SavedSettings[lang]);
            }
        } else if (holder == oreblockeditor) {
            e.setCancelled(true);
            if (RawSlot <= 16 && RawSlot >= 10) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                int x = RawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length - 10;
                UUID uuid = player.getUniqueId();
                int lang = User.getLang(uuid);
                Core.integerCache.get(uuid)[0] = x;
                player.closeInventory();
                ItemStack stack = ItemStackList.Blocks[x][lang];
                player.getInventory().setItem(0, stack);
                player.getInventory().setItem(1, stack);
                player.sendMessage(GlobalString.SavedSettings[lang]);
            }
        } else if (holder == oreeditor) {
            e.setCancelled(true);
            if (RawSlot <= 16 && RawSlot >= 10) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                int x = RawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length - 10;
                UUID uuid = player.getUniqueId();
                int lang = User.getLang(uuid);
                Core.integerCache.get(uuid)[0] = x;
                player.closeInventory();
                ItemStack stack = ItemStackList.Blocks[x][lang];
                player.getInventory().setItem(0, stack);
                player.getInventory().setItem(1, stack);
                player.sendMessage(GlobalString.SavedSettings[lang]);
            }
        } else if (holder == quartzeditor) {
            e.setCancelled(true);
            if (RawSlot <= 14 && RawSlot >= 12) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                int x = RawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length - 12;
                UUID uuid = player.getUniqueId();
                int lang = User.getLang(uuid);
                Core.integerCache.get(uuid)[0] = x;
                player.closeInventory();
                ItemStack stack = ItemStackList.Blocks[x][lang];
                player.getInventory().setItem(0, stack);
                player.getInventory().setItem(1, stack);
                player.sendMessage(GlobalString.SavedSettings[lang]);
            }
        } else if (holder == woodeditor) {
            e.setCancelled(true);
            if (RawSlot <= 16 && RawSlot >= 1 && RawSlot != 13 && RawSlot != 4 && RawSlot != 8 && RawSlot != 9) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                int x = (RawSlot < 4 ? RawSlot - 1 : (RawSlot < 8 ? RawSlot - 2 : (RawSlot < 13 ? RawSlot - 4 : RawSlot - 5))) + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length;
                UUID uuid = player.getUniqueId();
                int lang = User.getLang(uuid);
                Core.integerCache.get(uuid)[0] = x;
                player.closeInventory();
                ItemStack stack = ItemStackList.Blocks[x][lang];
                player.getInventory().setItem(0, stack);
                player.getInventory().setItem(1, stack);
                player.sendMessage(GlobalString.SavedSettings[lang]);
            }
        } else if (holder == clayeditor) {
            e.setCancelled(true);
            if (RawSlot < 17) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                int x = RawSlot + ItemStackList.sandstoneeditor.length + ItemStackList.wooleditor.length + ItemStackList.oreblockeditor.length + ItemStackList.oreeditor.length + ItemStackList.quartzeditor.length + ItemStackList.woodeditor.length;
                UUID uuid = player.getUniqueId();
                int lang = User.getLang(uuid);
                Core.integerCache.get(uuid)[0] = x;
                player.closeInventory();
                ItemStack stack = ItemStackList.Blocks[x][lang];
                player.getInventory().setItem(0, stack);
                player.getInventory().setItem(1, stack);
                player.sendMessage(GlobalString.SavedSettings[lang]);
            }
        }
    }
}
