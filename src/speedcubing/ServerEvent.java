package speedcubing;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import speedcubing.List.StringList;
import speedcubing.lib.event.LibEventHandler;
import speedcubing.lib.utils.Console;
import speedcubing.server.events.packet.PlayInChatEvent;
import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

public class ServerEvent {

    @LibEventHandler
    public void PlayInChatEvent(PlayInChatEvent e) {
        String message = e.packet.a();
        if (message.charAt(0) != '/') {
            Player player = e.player;
            e.setCancelled(true);
            String[] formats = speedcubingServer.getFormat(User.getUser(player.getUniqueId()).rank);
            String playerName = player.getName();
            BaseComponent[] empty = TextComponent.fromLegacyText("");
            BaseComponent[] name = TextComponent.fromLegacyText(formats[0] + playerName);
            TextComponent[] first = {new TextComponent(empty), new TextComponent(empty), new TextComponent(empty), new TextComponent(empty)};
            TextComponent[] second = {new TextComponent(name), new TextComponent(name), new TextComponent(name), new TextComponent(name)};
            String[] format = GlobalString.MessageFormat[0];
            for (int i = 0; i < second.length; i++) {
                second[i].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(StringList.hover[i].replace("%name%", formats[1] + playerName)).create()));
                second[i].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats " + playerName));
                first[i].addExtra(second[i]);
                first[i].addExtra(new TextComponent(TextComponent.fromLegacyText(format[i].replace("%text%", "").replace("%text2%", formats[2] + message))));
            }
            Bukkit.getOnlinePlayers().forEach(a -> a.spigot().sendMessage(first[User.getUser(a.getUniqueId()).lang]));
            Console.sendColoredConsole("§7[§aChatLog§7] [§b" + player.getWorld().getName() + "§7] " + formats[0] + playerName + format[1].replace("%text%", "").replace("%text2%", formats[2] + message));
        }
    }
}
