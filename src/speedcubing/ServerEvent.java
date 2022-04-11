package speedcubing;

import cubing.lib.bukkit.Event.ServerEventHandler;
import cubing.lib.bukkit.Event.events.PlayInChatEvent;
import cubing.lib.bukkit.packetwrapper.OutPlayerListHeaderFooter;
import cubing.lib.utils.Console;
import cubingserver.StringList.GlobalString;
import cubingserver.customEvents.UDPEvent;
import cubingserver.libs.User;
import cubingserver.speedcubingServer;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import speedcubing.List.StringList;

public class ServerEvent {

    @ServerEventHandler
    public void UDPEvent(UDPEvent e) {
        String[] rs = e.rs;
        if (rs[0].equals("o") && rs[1].equals("1")) {
            speedcubingServer.AllPlayers = Integer.parseInt(rs[2]);
            for (Player p : Bukkit.getOnlinePlayers()) {
                int lang = User.getLang(p.getUniqueId());
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(OutPlayerListHeaderFooter.a(GlobalString.LobbyTabList[0][lang], GlobalString.LobbyTabList[1][lang].replace("%int%", rs[2])));
            }
        }
    }

    @ServerEventHandler
    public void PlayInChatEvent(PlayInChatEvent e) {
        String message = e.packet.a();
        if (message.charAt(0) != '/') {
            Player player = e.player;
            e.setCancelled(true);
            String[] formats = User.getFormat(User.getRank(player.getUniqueId()));
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
            Bukkit.getOnlinePlayers().forEach(a -> a.spigot().sendMessage(first[User.getLang(a.getUniqueId())]));
            Console.sendColoredConsole("§7[§aChatLog§7] [§b" + player.getWorld().getName() + "§7] " + formats[0] + playerName + format[1].replace("%text%", "").replace("%text2%", formats[2] + message));
        }
    }
}
