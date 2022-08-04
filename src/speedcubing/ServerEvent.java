package speedcubing;

import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import speedcubing.Core.Core;
import speedcubing.Core.Custom;
import speedcubing.List.StringList;
import speedcubing.lib.bukkit.SideBar;
import speedcubing.lib.bukkit.event.PlayInEvent;
import speedcubing.lib.eventbus.LibEventHandler;
import speedcubing.lib.utils.Console;
import speedcubing.replay.Replay;
import speedcubing.server.events.player.NickEvent;
import speedcubing.server.events.player.SkinEvent;
import speedcubing.server.events.player.ToggleFlyEvent;
import speedcubing.server.libs.GlobalString;
import speedcubing.server.libs.User;
import speedcubing.server.speedcubingServer;

import java.util.HashSet;
import java.util.Set;

public class ServerEvent {

    @LibEventHandler
    public void ddwdw(PlayInEvent e) {
        Member member = Member.getMember(e.player);
        if (e.packet instanceof PacketPlayInFlying packetplayinflying) {
            member.didntevenfuckingstart = member.asyncLoc.clone();
            Location loc = member.asyncLoc;
            if (packetplayinflying.g() && !(packetplayinflying.g() && packetplayinflying.b() == -999.0D)) {
                loc.setX(packetplayinflying.a());
                loc.setY(packetplayinflying.b());
                loc.setZ(packetplayinflying.c());
            }
            if (packetplayinflying.h()) {
                loc.setYaw(packetplayinflying.d());
                loc.setPitch(packetplayinflying.e());
            }
            if (member.replay != null && member.replay.isRecording) {
                member.replay.PlayerMoveEvent(loc);
                if (!member.finishNext)
                    SideBar.getSidebar(e.player).setLine(StringList.SideBar[member.lang][9] + Core.tickToTime(member.replay.NewTick), 12);
                if (member.replay.NewTick >= 2400) {
                    Core.getInstance(e.player.getWorld().getName()).Fell(e.player, false);
                    e.player.sendMessage(StringList.cancelled[member.lang]);
                } else {
                    if (member.finishNext) {
                        member.finish();
                        member.finishNext = false;
                    } else {
                        AxisAlignedBB bb = new AxisAlignedBB(loc.getX() + 0.3, loc.getY(), loc.getZ() + 0.3, loc.getX() - 0.3, loc.getY() + 1.8, loc.getZ() - 0.3);
                        for (AxisAlignedBB bbs : member.hits) {
                            if (bb.b(bbs)) {
                                member.finishNext = true;
                                break;
                            }
                        }
                    }
                }
            }
        } else if (e.packet instanceof PacketPlayInBlockPlace p && p.getFace() != 255) {
            if (e.player.getGameMode() == GameMode.SURVIVAL) {
                ItemStack stack = CraftItemStack.asBukkitCopy(p.getItemStack());
                if (member.waiting || stack.getType().equals(Material.REDSTONE_COMPARATOR_OFF)) {
                    e.isCancelled = true;
                    return;
                }
                BlockPosition pos = p.a().shift(EnumDirection.fromType1(p.getFace()));
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                switch (e.player.getWorld().getName()) {
                    case "world":
                        if (z < -1 || z > 53 || Math.abs(x - member.PlayerCoord) > 6 || y < 92) {
                            e.isCancelled = true;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> e.player.sendBlockChange(new Location(e.player.getWorld(), x, y, z), Material.AIR, (byte) 0), 10);
                        } else e.isCancelled = (member.validPlaceBlock(e.player, pos));
                        break;
                    case "Short":
                        if (z < -1|| z > 30 || Math.abs(x - member.PlayerCoord) > 6 || y < 92) {
                            e.isCancelled = true;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> e.player.sendBlockChange(new Location(e.player.getWorld(), x, y, z), Material.AIR, (byte) 0), 10);
                        } else e.isCancelled = (member.validPlaceBlock(e.player, pos));
                        break;
                    case "Onestack":
                        if (z < -1 || z > 29 || Math.abs(x - member.PlayerCoord) > 6 || y < 92) {
                            e.isCancelled = true;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> e.player.sendBlockChange(new Location(e.player.getWorld(), x, y, z), Material.AIR, (byte) 0), 10);
                        } else e.isCancelled = (member.validPlaceBlock(e.player, pos));
                        break;
                    case "Inclined":
                        if (Math.abs(x + z - 2 * member.PlayerCoord) > 10 || x - z < 1 || x - z > 58 || y < 92) {
                            e.isCancelled = true;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> e.player.sendBlockChange(new Location(e.player.getWorld(), x, y, z), Material.AIR, (byte) 0), 10);
                        } else e.isCancelled = (member.validPlaceBlock(e.player, pos));
                        break;
                    case "Inclinedshort":
                        if (Math.abs(x + z - 2 * member.PlayerCoord) > 10 || x - z < 1 || x - z > 38 || y < 92) {
                            e.isCancelled = true;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> e.player.sendBlockChange(new Location(e.player.getWorld(), x, y, z), Material.AIR, (byte) 0), 10);
                        } else e.isCancelled = (member.validPlaceBlock(e.player, pos));
                        break;
                    case "Custom":
                        if (Math.abs(z - member.PlayerCoord) > 6 || y < 92) {
                            e.isCancelled = true;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> e.player.sendBlockChange(new Location(e.player.getWorld(), x, y, z), Material.AIR, (byte) 0), 10);
                        } else Custom.validPlaceBlock(e.player, pos);
                        break;
                }
                if (e.isCancelled) {
                    ((CraftPlayer) e.player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockChange(((CraftWorld) e.player.getWorld()).getHandle(), p.a()));
                    ((CraftPlayer) e.player).getHandle().playerConnection.sendPacket(new PacketPlayOutBlockChange(((CraftWorld) e.player.getWorld()).getHandle(), pos));
                } else {
                    Replay replay = member.replay;
                    if (replay != null)
                        replay.BlockPlaceEvent(pos, stack.getType());
                }
            }
        }
    }

    @LibEventHandler
    public void PlayInEvent(PlayInEvent e) {
        if (e.packet instanceof PacketPlayInChat) {
            String message = ((PacketPlayInChat) e.packet).a();
            if (message.charAt(0) != '/') {
                Player player = e.player;
                e.isCancelled = true;
                String[] formats = speedcubingServer.getFormat(User.getUser(player).rank);
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
                Bukkit.getOnlinePlayers().forEach(a -> a.spigot().sendMessage(first[Member.getMember(a).lang]));
                Console.sendColoredConsole("§7[§aChatLog§7] [§b" + player.getWorld().getName() + "§7] " + formats[0] + playerName + format[1].replace("%text%", "").replace("%text2%", formats[2] + message));
            }
        }
    }

    @LibEventHandler
    public void ToggleFlyEvent(ToggleFlyEvent e) {
        e.isCancelled = true;
        e.player.sendMessage(GlobalString.OnlyInHub[Member.getMember(e.player).lang]);
    }

    @LibEventHandler
    public void NickEvent(NickEvent e) {
        e.isCancelled = true;
        e.player.sendMessage(GlobalString.OnlyInHub[Member.getMember(e.player).lang]);
    }

    @LibEventHandler
    public void SkinEvent(SkinEvent e) {
        e.isCancelled = true;
        e.player.sendMessage(GlobalString.OnlyInHub[Member.getMember(e.player).lang]);
    }
}