package top.speedcubing.fastbuilder.bukkitlistener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import top.speedcubing.common.database.Database;
import top.speedcubing.fastbuilder.core.timed.Normal;
import top.speedcubing.fastbuilder.Member;

public class PlayerJoin implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Member member = new Member(player);
        member.animation = member.user.dbSelect("fastbuild_animation").getBoolean(0);
        player.setGameMode(GameMode.SURVIVAL);
        Normal.instance.Join(player, false, true);
    }
}
