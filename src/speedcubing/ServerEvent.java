package speedcubing;

import cubing.bukkit.Event.ServerEventHandler;
import cubing.bukkit.Event.events.PlayInChatEvent;
import cubing.bukkit.PlayerUtils;
import cubing.utils.Console;
import cubingserver.StringList.GlobalString;
import cubingserver.customEvents.UDPEvent;
import cubingserver.libs.User;
import cubingserver.speedcubingServer;
import cubingserver.things.events.PlayerJoin;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import speedcubing.List.StringList;

import java.util.UUID;

public class ServerEvent {

    @ServerEventHandler
    public void UDPEvent(UDPEvent e) {
        String[] rs = e.rs;
        if (rs[0].equals("o") && rs[1].equals("1")) {
            speedcubingServer.AllPlayers = Integer.parseInt(rs[2]);
            for (Player p : Bukkit.getOnlinePlayers()) {
                int lang = User.getLang(p.getUniqueId());
                PlayerUtils.sendTabHeaderFooter(((CraftPlayer) p).getHandle().playerConnection, GlobalString.LobbyTabList[0][lang], GlobalString.LobbyTabList[1][lang].replace("%int%", rs[2]));
            }
        }
    }

    @ServerEventHandler
    public void PlayInChatEvent(PlayInChatEvent e) {
        e.setCancelled(true);
        Player player = e.player;
        String message = e.packet.a();
        long time = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();
        boolean spam = PlayerJoin.Canspam.contains(uuid);
        if (!spam && time - speedcubingServer.spam.get(uuid) < 500)
            player.sendMessage(GlobalString.spam[User.getLang(uuid)]);
        else if (message.charAt(0) != '/') {
            if (!spam && speedcubingServer.lastmsg.get(uuid).equals(message))
                player.sendMessage(GlobalString.samemsg[User.getLang(uuid)]);
            else {
                speedcubingServer.lastmsg.put(uuid, message);
                String[] formats = User.getFormat(User.getRank(player.getUniqueId()));
                String playerName = player.getName();
                BaseComponent[] empty = TextComponent.fromLegacyText("");
                BaseComponent[] name = TextComponent.fromLegacyText(formats[0] + formats[1] + playerName);
                TextComponent[] first = {new TextComponent(empty), new TextComponent(empty), new TextComponent(empty), new TextComponent(empty)};
                TextComponent[] second = {new TextComponent(name), new TextComponent(name), new TextComponent(name), new TextComponent(name)};
                String[] format = GlobalString.MessageFormat[0];
                for (int i = 0; i < second.length; i++) {
                    second[i].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(StringList.hover[i].replace("%name%", formats[0] + playerName)).create()));
                    second[i].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats " + playerName));
                    first[i].addExtra(second[i]);
                    first[i].addExtra(new TextComponent(TextComponent.fromLegacyText(format[i].replace("%text%", "").replace("%text2%", formats[2] + message))));
                }
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.spigot().sendMessage(first[User.getLang(p.getUniqueId())]);
                }
                Console.sendColoredConsole("§7[§aChatLog§7] [§b" + player.getWorld().getName() + "§7] " + formats[0] + formats[1] + playerName + format[1].replace("%text%", "").replace("%text2%", formats[2] + message));
                speedcubingServer.lastmsg.put(uuid, message);
            }
        } else e.setCancelled(false);
        speedcubingServer.spam.put(uuid, time);
    }
}
