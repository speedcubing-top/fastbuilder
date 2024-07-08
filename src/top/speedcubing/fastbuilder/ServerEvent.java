package top.speedcubing.fastbuilder;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInChat;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutSetSlot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.common.rank.RankFormat;
import top.speedcubing.fastbuilder.core.Core;
import top.speedcubing.fastbuilder.core.Custom;
import top.speedcubing.fastbuilder.core.timed.TimedMode;
import top.speedcubing.fastbuilder.list.StringList;
import top.speedcubing.fastbuilder.replay.Replay;
import top.speedcubing.lib.bukkit.SideBar;
import top.speedcubing.lib.bukkit.events.packet.PlayInEvent;
import top.speedcubing.lib.eventbus.CubingEventHandler;
import top.speedcubing.lib.minecraft.text.TextBuilder;
import top.speedcubing.lib.minecraft.text.TextClickEvent;
import top.speedcubing.lib.minecraft.text.TextHoverEvent;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.events.player.SkinEvent;
import top.speedcubing.server.events.player.ToggleFlyEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.lang.LangMessage;
import top.speedcubing.server.player.User;
import top.speedcubing.server.share.Chat;

public class ServerEvent {

    @CubingEventHandler
    public void playInEvent(PlayInEvent e) {
        Member member = Member.getMember(e.getPlayer());
        if (e.getPacket() instanceof PacketPlayInFlying) {
            PacketPlayInFlying packetplayinflying = (PacketPlayInFlying) e.getPacket();
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
            if (LocationUtils.sameCoord(loc, member.tped))
                member.locationLock = false;
            if (member.locationLock)
                return;

            if (member.replay != null && member.replay.replayRecorder.isRecording) {
                member.replay.replayRecorder.PlayerMoveEvent(loc);
                if (!member.finishNext)
                    SideBar.getSidebar(e.getPlayer()).setLineByScore(StringList.SideBar[member.user.lang][9] + Core.tickToTime(member.replay.replayRecorder.NewTick), 1);
                if (member.replay.replayRecorder.NewTick > 2400) {
                    member.Fell(false);
                    member.user.sendLangMessage(StringList.cancelled);
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

            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                if (member.currentMode == null && member.user.bGetWorld().getName().equals("Custom")) {
                    if (loc.getY() <= member.Center[1] + 99 - member.LengthSettings[1] - TimedMode.FALL || Math.abs(loc.getZ() - member.PlayerCoord - 0.5) > 149.5)
                        Custom.Fell(e.getPlayer());
                    else {
                        if (loc.getX() >= 4 && member.MainTimer == -1) {
                            e.getPlayer().getInventory().setItem(5, new ItemStack(Material.AIR));
                            e.getPlayer().getInventory().setItem(6, new ItemStack(Material.AIR));
                        }
                        if (!member.hits.isEmpty())
                            for (AxisAlignedBB b : member.hits) {
                                if (((CraftPlayer) e.getPlayer()).getHandle().getBoundingBox().b(b)) {
                                    Custom.Finish(e.getPlayer(), false);
                                    break;
                                }
                            }
                    }
                    //replay border
                } else if (member.isInReplay()) {
                    if (member.replayMode != null)
                        if (member.replayMode.inclined) {
                            if (Math.abs(loc.getX() + loc.getZ() - 2 * member.ReplayCoord - 1) > 75.5)
                                member.user.bTeleport(new Location(member.replayMode.replayWorld, member.ReplayCoord + 0.5, 100, member.ReplayCoord + 0.5, 0, 0));
                        } else {
                            if (Math.abs(loc.getX() - member.ReplayCoord - 0.5) > 75.5 || Math.abs(loc.getZ() - 0.5) > member.replayMode.distance + 10.5 || loc.getY() < 0)
                                member.user.bTeleport(new Location(member.replayMode.replayWorld, member.ReplayCoord + 0.5, 100, -4.5, 0, 0));
                        }
                } else {
                    //normal border
                    if (member.currentMode != null)
                        if (member.currentMode.inclined) {
                            boolean fell = loc.getY() <= 100 - TimedMode.FALL;
                            if (fell || Math.abs(loc.getX() + loc.getZ() - 2 * member.PlayerCoord - 1) > 7.5)
                                member.Fell(fell);
                        } else {
                            boolean fell = loc.getY() <= 100 - TimedMode.FALL;
                            if (fell || Math.abs(loc.getX() - member.PlayerCoord - 0.5) > 5.5)
                                member.Fell(fell);
                        }
                }
            }
        } else if (e.getPacket() instanceof PacketPlayInBlockPlace) {
            PacketPlayInBlockPlace p = (PacketPlayInBlockPlace) e.getPacket();
            if (p.getFace() != 255 && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                ItemStack stack = p.getItemStack() == null ? null : CraftItemStack.asBukkitCopy(p.getItemStack());
                if (stack == null)
                    return;
                if (stack.getType() == Material.REDSTONE_COMPARATOR)
                    return;
                if (stack.getType() == Material.BOOK)
                    return;
                if (stack.getType() == Material.COMPASS)
                    return;
                BlockPosition pos = p.a();
                BlockPosition shifted = p.a().shift(EnumDirection.fromType1(p.getFace()));
                Block block = e.getPlayer().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
                if (!(block.getType() == Material.SNOW && block.getData() == 0))
                    pos = shifted;

                if (member.waiting)
                    e.setCancelled(true);
                else {
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();
                    if (member.currentMode == null && member.user.bGetWorld().getName().equals("Custom")) {
                        if (Math.abs(z - member.PlayerCoord) > 6 || y < 92)
                            e.setCancelled(true);
                        else Custom.validPlaceBlock(e.getPlayer(), e.getPlayer().getWorld().getBlockAt(x, y, z));
                    } else if (member.currentMode.inclined) {
                        if ((Math.abs(x + z - 2 * member.PlayerCoord) > 10 || x - z < 1 || x - z > member.currentMode.distance * 2 || y < 92) || (member.replay == null && y > 100))
                            e.setCancelled(true);
                        else member.validPlaceBlock(x, y, z);
                    } else {
                        if ((z < -1 || z > member.currentMode.distance + 2 || Math.abs(x - member.PlayerCoord) > 5 || y < 92) || (member.replay == null && y > 100))
                            e.setCancelled(true);
                        else member.validPlaceBlock(x, y, z);
                    }
                }
                if (e.isCancelled()) {
                    EntityPlayer nmsPlayer = ((CraftPlayer) e.getPlayer()).getHandle();
                    member.user.sendPacket(new PacketPlayOutBlockChange(nmsPlayer.world, p.a()), new PacketPlayOutBlockChange(nmsPlayer.world, shifted), new PacketPlayOutSetSlot(nmsPlayer.activeContainer.windowId, nmsPlayer.activeContainer.getSlot(nmsPlayer.inventory, nmsPlayer.inventory.itemInHandIndex).rawSlotIndex, nmsPlayer.inventory.getItemInHand()));
                }
            }
        } else if (e.getPacket() instanceof PacketPlayInHeldItemSlot) {
            int i = ((PacketPlayInHeldItemSlot) e.getPacket()).a();
            member.asyncHeldItemSlot = i;
            Replay replay = Member.getMember(e.getPlayer()).replay;
            if (replay != null)
                replay.replayRecorder.PlayerItemHeldEvent(e.getPlayer().getInventory().getItem(i));
        } else if (e.getPacket() instanceof PacketPlayInChat) {
            String message = ((PacketPlayInChat) e.getPacket()).a();
            if (message.charAt(0) != '/') {
                e.setCancelled(true);
                Player player = e.getPlayer();
                User user = User.getUser(player);
                RankFormat rankFormat = user.getFormat(false);
                String playerName = player.getName();
                LangMessage format = GlobalString.MessageFormat;
                TextBuilder[] text = new TextBuilder[4];
                for (int i = 0; i < text.length; i++) {
                    text[i] = new TextBuilder()
                            .both(rankFormat.getPrefix() + playerName,
                                    TextClickEvent.runCommand("/stats " + playerName),
                                    TextHoverEvent.showText(StringList.hover.get(i).replace("%1%", user.getColorName(false))))
                            .str(format.get(i).replace("%1%", "").replace("%2%", rankFormat.getChatColor()));
                }
                Chat.globalChat(Bukkit.getOnlinePlayers(), player, text, message);
                //Console.printlnColor("§7[§aChatLog§7] [§b" + player.getWorld().getName() + "§7] " + user.getPrefixName(false) + format.get(1).replace("%text%", "").replace("%text2%", user.getColorName(false)));
            }
        }
    }

    @CubingEventHandler
    public void nickEvent(NickEvent e) {
        e.setCancelled(true);
        Member.getMember(e.getPlayer()).user.sendLangMessage(GlobalString.OnlyInHub);
    }

    @CubingEventHandler
    public void skinEvent(SkinEvent e) {
        e.setCancelled(true);
        Member.getMember(e.getPlayer()).user.sendLangMessage(GlobalString.OnlyInHub);
    }

    @CubingEventHandler
    public void toggleFlyEvent(ToggleFlyEvent e) {
        e.setCancelled(true);
        Member.getMember(e.getPlayer()).user.sendLangMessage(GlobalString.OnlyInHub);
    }
}