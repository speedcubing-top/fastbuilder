package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.Member;

public class BlockPlace implements Listener {
    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE)
            Member.getMember(e.getPlayer()).placedBlockList.add(e.getBlock());

        //rainbow wool
        if (e.getPlayer().getItemInHand() == null)
            return;

        if (player.getItemInHand().getItemMeta() == null)
            return;

        if (player.getItemInHand().getItemMeta().getDisplayName() == null)
            return;

        if (player.getItemInHand().getItemMeta().getDisplayName().isEmpty())
            return;

        if (!player.getItemInHand().getItemMeta().getDisplayName().contains("§"))
            return;

        if (!ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName()).contains("RAINBOW")) {
            return;
        }


        if (player.getItemInHand().getType() != Material.WOOL)
            return;
        ItemStack item = player.getItemInHand();
        item.setDurability((short) (Math.random() * 16));
    }
}
