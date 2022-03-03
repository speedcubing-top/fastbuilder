package speedcubing.All;

import cubing.utils.Console;
import cubing.utils.Reflections;
import cubingserver.StringList.GlobalString;
import cubingserver.libs.PlayerData;
import cubingserver.libs.Rank;
import cubingserver.speedcubingServer;
import cubingserver.things.events.PlayerJoin;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_8_R3.PacketPlayInChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import speedcubing.List.StringList;

import java.util.List;
import java.util.UUID;

public class Chat {
    //text
    public void PacketInject(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        if (channel.pipeline().get("PacketPlayInChat") != null)
            return;
        channel.pipeline().addAfter("decoder", "PacketPlayInChat", new MessageToMessageDecoder<PacketPlayInChat>() {
            @Override
            protected void decode(ChannelHandlerContext channel, PacketPlayInChat packet, List<Object> arg) {
                String message = packet.a();
                long time = System.currentTimeMillis();
                UUID uuid = player.getUniqueId();
                boolean spam = PlayerJoin.Canspam.contains(uuid);
                if (!spam && time - speedcubingServer.spam.get(uuid) < 500)
                    player.sendMessage(GlobalString.spam[PlayerData.getLang(uuid)]);
                else if (message.charAt(0) != '/') {
                    if (!spam && speedcubingServer.lastmsg.get(uuid).equals(message))
                        player.sendMessage(GlobalString.samemsg[PlayerData.getLang(uuid)]);
                    else {
                        speedcubingServer.lastmsg.put(uuid, message);
                        String[] formats = Rank.format(player.getUniqueId());
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
                            p.spigot().sendMessage(first[PlayerData.getLang(p.getUniqueId())]);
                        }
                        Console.sendColoredConsole("§7[§aChatLog§7] [§b" + player.getWorld().getName() + "§7] " + formats[0] + formats[1] + playerName + format[1].replace("%text%", "").replace("%text2%", formats[2] + message));
                        speedcubingServer.lastmsg.put(uuid, message);
                    }
                } else arg.add(packet);
                speedcubingServer.spam.put(uuid, time);
            }
        });
    }
}
