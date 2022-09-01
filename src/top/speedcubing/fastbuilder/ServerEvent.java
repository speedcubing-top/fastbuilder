package top.speedcubing.fastbuilder;

import top.speedcubing.fastbuilder.Core.Custom;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.fastbuilder.Core.timed.TimedMode;
import top.speedcubing.fastbuilder.List.StringList;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.event.PlayInEvent;
import top.speedcubing.lib.eventbus.LibEventHandler;
import top.speedcubing.lib.utils.Console;
import top.speedcubing.fastbuilder.replay.gui.FastBuilderUtils;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.events.player.SkinEvent;
import top.speedcubing.server.events.player.ToggleFlyEvent;
import top.speedcubing.server.libs.GlobalString;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

public class ServerEvent {

    @LibEventHandler
    public void ddwdw(PlayInEvent e) {
        Member member = Member.getMember(e.player);
        if (e.packet instanceof PacketPlayInFlying) {
            PacketPlayInFlying packetplayinflying = (PacketPlayInFlying) e.packet;
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

            if (member.replay != null && member.replay.replayRecorder.isRecording) {
                member.replay.replayRecorder.PlayerMoveEvent(loc);
                if (!member.finishNext)
                    SideBar.getSidebar(e.player).setLine(StringList.SideBar[member.user.lang][9] + FastBuilderUtils.tickToTime(member.replay.replayRecorder.NewTick), 12);
                if (member.replay.replayRecorder.NewTick >= 2400) {
                    member.currentMode.Fell(member, false);
                    e.player.sendMessage(StringList.cancelled[member.user.lang]);
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

            if (e.player.getGameMode() != GameMode.CREATIVE) {
                switch (e.player.getWorld().getName()) {
                    case "replay_normal":
                    case "replay_onestack":
                    case "replay_short":
                        if (Math.abs(loc.getX() - member.ReplayCoord - 0.5) > 75.5 || Math.abs(loc.getZ() - 0.5) > 75.5 || loc.getY() < 0)
                            member.user.player.teleport(new Location(member.currentMode.replayWorld, member.ReplayCoord + 0.5, 100, -4.5, 0, 0));
                        break;
                    case "replay_inclined":
                    case "replay_incinedshort":
                        if (Math.abs(loc.getX() + loc.getZ() - 2 * member.ReplayCoord - 1) > 75.5)
                            member.user.player.teleport(new Location(member.currentMode.replayWorld, member.ReplayCoord + 0.5, 100, member.ReplayCoord + 0.5, 0, 0));
                        break;
                    case "world":
                    case "Short":
                    case "Onestack": {
                        boolean fell = loc.getY() <= 100 - TimedMode.FALL;
                        if (fell || Math.abs(loc.getX() - member.PlayerCoord - 0.5) > 5.5)
                            member.currentMode.Fell(member, fell);
                    }
                    break;
                    case "Inclinedshort":
                    case "Inclined": {
                        boolean fell = loc.getY() <= 100 - TimedMode.FALL;
                        if (fell || Math.abs(loc.getX() + loc.getZ() - 2 * member.PlayerCoord - 1) > 7.5)
                            member.currentMode.Fell(member, fell);
                    }
                    break;
                    case "Custom": {
                        if (loc.getY() <= member.Center[1] + 99 - member.LengthSettings[1] - TimedMode.FALL || Math.abs(loc.getZ() - member.PlayerCoord - 0.5) > 149.5)
                            Custom.Fell(e.player);
                        else {
                            if (loc.getX() >= 4 && member.MainTimer == -1) {
                                e.player.getInventory().setItem(5, new ItemStack(Material.AIR));
                                e.player.getInventory().setItem(6, new ItemStack(Material.AIR));
                            }
                            if (!member.hits.isEmpty())
                                for (AxisAlignedBB b : member.hits) {
                                    if (((CraftPlayer) e.player).getHandle().getBoundingBox().b(b)) {
                                        Custom.Finish(e.player, false);
                                        break;
                                    }
                                }
                        }
                    }
                    break;
                }
            }
        } else if (e.packet instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace p = (PacketPlayInBlockPlace) e.packet;
            if (p.getFace() != 255 && e.player.getGameMode() == GameMode.SURVIVAL) {
                ItemStack stack = p.getItemStack() == null ? null : CraftItemStack.asBukkitCopy(p.getItemStack());
                if (stack == null)
                    return;
                if (stack.getType() == Material.REDSTONE_COMPARATOR)
                    return;
                BlockPosition pos = p.a();
                BlockPosition shifted = p.a().shift(EnumDirection.fromType1(p.getFace()));
                Block block = e.player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
                if (!(block.getType() == Material.SNOW && block.getData() == 0))
                    pos = shifted;

                if (member.waiting)
                    e.isCancelled = true;
                else if (e.player.getInventory().getHeldItemSlot() < 2) {
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();
                    switch (e.player.getWorld().getName()) {
                        case "world":
                        case "Short":
                        case "Onestack":
                            if ((z < -1 || z > member.currentMode.distance + 2 || Math.abs(x - member.PlayerCoord) > 6 || y < 92) || (member.replay == null && y > 100)) {
                                e.isCancelled = true;
                            } else
                                member.validPlaceBlock(x, y, z);
                            break;
                        case "Inclined":
                        case "Inclinedshort":
                            if ((Math.abs(x + z - 2 * member.PlayerCoord) > 10 || x - z < 1 || x - z > member.currentMode.distance * 2 || y < 92) || (member.replay == null && y > 100)) {
                                e.isCancelled = true;
                            } else member.validPlaceBlock(x, y, z);
                            break;
                        case "Custom":
                            if (Math.abs(z - member.PlayerCoord) > 6 || y < 92) {
                                e.isCancelled = true;
                            } else Custom.validPlaceBlock(e.player, e.player.getWorld().getBlockAt(x, y, z));
                            break;
                    }
                }

                if (e.isCancelled) {
                    EntityPlayer nmsPlayer = ((CraftPlayer) e.player).getHandle();
                    nmsPlayer.playerConnection.sendPacket(new PacketPlayOutBlockChange(nmsPlayer.world, p.a()));
                    nmsPlayer.playerConnection.sendPacket(new PacketPlayOutBlockChange(nmsPlayer.world, shifted));
                    nmsPlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(nmsPlayer.activeContainer.windowId, nmsPlayer.activeContainer.getSlot(nmsPlayer.inventory, nmsPlayer.inventory.itemInHandIndex).rawSlotIndex, nmsPlayer.inventory.getItemInHand()));
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
                Bukkit.getOnlinePlayers().forEach(a -> a.spigot().sendMessage(first[Member.getMember(a).user.lang]));
                Console.sendColoredConsole("§7[§aChatLog§7] [§b" + player.getWorld().getName() + "§7] " + formats[0] + playerName + format[1].replace("%text%", "").replace("%text2%", formats[2] + message));
            }
        }
    }

    @LibEventHandler
    public void ToggleFlyEvent(ToggleFlyEvent e) {
        e.isCancelled = true;
        e.player.sendMessage(GlobalString.OnlyInHub[Member.getMember(e.player).user.lang]);
    }

    @LibEventHandler
    public void NickEvent(NickEvent e) {
        e.isCancelled = true;
        e.player.sendMessage(GlobalString.OnlyInHub[Member.getMember(e.player).user.lang]);
    }

    @LibEventHandler
    public void SkinEvent(SkinEvent e) {
        e.isCancelled = true;
        e.player.sendMessage(GlobalString.OnlyInHub[Member.getMember(e.player).user.lang]);
    }
}