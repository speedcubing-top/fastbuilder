package speedcubing.BukkitCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import speedcubing.Member;
import speedcubing.replay.ReplayViewer;

import java.util.ArrayList;
import java.util.List;

public class stats implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("currently unavailable to use.");
        if (strings.length == 0) {
            ReplayViewer replayPlayer = new ReplayViewer(Member.getMember(commandSender).replays.get(Member.getMember(commandSender).replays.size()-1), (Player) commandSender);
            Member.getMember(commandSender).replayPlayer = replayPlayer;
            replayPlayer.spawnReplay();
        }
        if (strings.length == 1)
            Member.getMember(commandSender).replayPlayer.start();
        if (strings.length == 2)
            Member.getMember(commandSender).replayPlayer.togglePlaying();
        if (strings.length == 3) {
        }
//        if (User.getRank(((Player) commandSender).getUniqueId()).equals("owner")) {
//            String player;
//            if (strings.length == 0)
//                player = commandSender.getName();
//            else if (strings.length == 1)
//                player = strings[0];
//            else {
//                commandSender.sendMessage("/stats <player>\n/stats #<index>\n/stats");
//                return true;
//            }
//            UUID uuid = ((Player) commandSender).getUniqueId();
//
//            new Thread(() -> {
//                if (player.startsWith("#")) {
//                    int target;
//                    try {
//                        target = Integer.parseInt(player.substring(1)) - 1;
//                    } catch (Exception e) {
//                        commandSender.sendMessage("Invalid format! /stats #<index>");
//                        return;
//                    }
//                    if (target > 24)
//                        commandSender.sendMessage("the maximum index is 25 !");
//                    else if (target < 0)
//                        commandSender.sendMessage("the minimum index is 1 !");
//                    else {
//                        String[] out = StringList.ranks[User.getUser(uuid).lang];
//                        int[] scores = {
//                                speedcubingServer.connection.selectInt("playersdata", "fastbuild_long_pb", "fastbuild_long_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist) ORDER BY fastbuild_long_pb ASC LIMIT " + target + ",1"),
//                                speedcubingServer.connection.selectInt("playersdata", "fastbuild_short_pb", "fastbuild_short_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist) ORDER BY fastbuild_short_pb ASC LIMIT " + target + ",1"),
//                                speedcubingServer.connection.selectInt("playersdata", "fastbuild_inclined_pb", "fastbuild_inclined_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist) ORDER BY fastbuild_inclined_pb ASC LIMIT " + target + ",1"),
//                                speedcubingServer.connection.selectInt("playersdata", "fastbuild_onestack_pb", "fastbuild_onestack_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist) ORDER BY fastbuild_onestack_pb ASC LIMIT " + target + ",1"),
//                                speedcubingServer.connection.selectInt("playersdata", "fastbuild_inclinedshort_pb", "fastbuild_inclinedshort_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist) ORDER BY fastbuild_inclinedshort_pb ASC LIMIT " + target + ",1")
//                        };
//                        String str = out[0];
//                        try {
//                            ResultSet players0 = speedcubingServer.connection.connection.createStatement().executeQuery("SELECT uuid FROM playersdata WHERE fastbuild_long_pb=" + scores[0] + " AND NOT uuid IN (SELECT uuid FROM banlist)");
//                            ResultSet players1 = speedcubingServer.connection.connection.createStatement().executeQuery("SELECT uuid FROM playersdata WHERE fastbuild_short_pb=" + scores[1] + " AND NOT uuid IN (SELECT uuid FROM banlist)");
//                            ResultSet players2 = speedcubingServer.connection.connection.createStatement().executeQuery("SELECT uuid FROM playersdata WHERE fastbuild_inclined_pb=" + scores[2] + " AND NOT uuid IN (SELECT uuid FROM banlist)");
//                            ResultSet players3 = speedcubingServer.connection.connection.createStatement().executeQuery("SELECT uuid FROM playersdata WHERE fastbuild_onestack_pb=" + scores[3] + " AND NOT uuid IN (SELECT uuid FROM banlist)");
//                            ResultSet players4 = speedcubingServer.connection.connection.createStatement().executeQuery("SELECT uuid FROM playersdata WHERE fastbuild_inclinedshort_pb=" + scores[4] + " AND NOT uuid IN (SELECT uuid FROM banlist)");
//                            str += out[1]
//                                    .replace("%d%", Core.msToTime(scores[0]))
//                                    .replace("%a%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_long_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")))
//                                    .replace("%r%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_long_pb < " + scores[0] + " AND fastbuild_long_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1));
//                            while (players0.next()) {
//                                String set = players0.getString(1);
//                                str += out[7].replace("%p%", Rank.format(set)[0] + speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + set + "'"));
////                            str += out[7].replace("%p%", Rank.format(set)[0] + MojangAPI.getLatestName(set));
//                            }
//                            str += out[2]
//                                    .replace("%d%", Core.msToTime(scores[1]))
//                                    .replace("%a%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_short_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")))
//                                    .replace("%r%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_short_pb < " + scores[1] + " AND fastbuild_short_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1));
//                            while (players1.next()) {
//                                String set = players1.getString(1);
//                                str += out[7].replace("%p%", Rank.format(set)[0] + speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + set + "'"));
////                            str += out[7].replace("%p%", Rank.format(set)[0] + MojangAPI.getLatestName(set));
//                            }
//                            str += out[3]
//                                    .replace("%d%", Core.msToTime(scores[2]))
//                                    .replace("%a%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_inclined_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")))
//                                    .replace("%r%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_inclined_pb < " + scores[2] + " AND fastbuild_inclined_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1));
//                            while (players2.next()) {
//                                String set = players2.getString(1);
//                                str += out[7].replace("%p%", Rank.format(set)[0] + speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + set + "'"));
////                            str += out[7].replace("%p%", Rank.format(set)[0] + MojangAPI.getLatestName(set));
//                            }
//                            str += out[4]
//                                    .replace("%d%", Core.msToTime(scores[3]))
//                                    .replace("%a%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_onestack_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")))
//                                    .replace("%r%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_onestack_pb < " + scores[3] + " AND fastbuild_onestack_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1));
//                            while (players3.next()) {
//                                String set = players3.getString(1);
//                                str += out[7].replace("%p%", Rank.format(set)[0] + speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + set + "'"));
////                            str += out[7].replace("%p%", Rank.format(set)[0] + MojangAPI.getLatestName(set));
//                            }
//                            str += out[5]
//                                    .replace("%d%", Core.msToTime(scores[4]))
//                                    .replace("%a%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_inclinedshort_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")))
//                                    .replace("%r%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_inclinedshort_pb < " + scores[4] + " AND fastbuild_inclinedshort_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1));
//                            while (players4.next()) {
//                                String set = players4.getString(1);
//                                str += out[7].replace("%p%", Rank.format(set)[0] + speedcubingServer.connection.selectString("playersdata", "name", "uuid='" + set + "'"));
////                            str += out[7].replace("%p%", Rank.format(set)[0] + MojangAPI.getLatestName(set));
//                            }
//                            str += out[6];
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        commandSender.sendMessage(str);
//                    }
//                } else {
//                    String[] result;
//                    try {
//                        result = MojangAPI.getUUIDAndName(player);
//                    } catch (APIErrorException e) {
//                        result = new String[]{"invalidName", ""};
//                    }
//                    String id = result[0];
//                    if (id.equals("invalidName")) {
//                        commandSender.sendMessage(GlobalString.invalidName[User.getUser(uuid).lang]);
//                    } else {
//                        id = UUIDUtils.addDash(id);
//                        if (!speedcubingServer.connection.isStringExist("playersdata", "uuid='" + id + "'"))
//                            commandSender.sendMessage("Error: No results");
//                        else {
//                            String[] format = Rank.format(id);
//                            String out = StringList.Stats[User.getUser(uuid).lang].replace("%player%", format[0] + format[1] + result[1]);
//                            int[] ints = speedcubingServer.connection.selectInts("playersdata", "fastbuild_long_pb,fastbuild_short_pb,fastbuild_inclined_pb,fastbuild_onestack_pb,fastbuild_inclinedshort_pb", "uuid='" + id + "'");
//                            if (ints[0] == 0)
//                                out = out.replace("%db% §8[§b#%pos%§8/§b#%all%§8]", "null");
//                            else {
//                                out = out
//                                        .replace("%db%", Core.msToTime(ints[0]))
//                                        .replace("%pos%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_long_pb < (SELECT fastbuild_long_pb FROM playersdata WHERE uuid='" + id + "' ) AND fastbuild_long_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1))
//                                        .replace("%all%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_long_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")));
//                            }
//                            if (ints[1] == 0)
//                                out = out.replace("%db1% §8[§b#%pos1%§8/§b#%all1%§8]", "null");
//                            else {
//                                out = out
//                                        .replace("%db1%", Core.msToTime(ints[1]))
//                                        .replace("%pos1%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_short_pb < (SELECT fastbuild_short_pb FROM playersdata WHERE uuid='" + id + "' ) AND fastbuild_short_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1))
//                                        .replace("%all1%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_short_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")));
//                            }
//                            if (ints[2] == 0)
//                                out = out.replace("%db2% §8[§b#%pos2%§8/§b#%all2%§8]", "null");
//                            else {
//                                out = out
//                                        .replace("%db2%", Core.msToTime(ints[2]))
//                                        .replace("%pos2%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_inclined_pb < (SELECT fastbuild_inclined_pb FROM playersdata WHERE uuid='" + id + "' ) AND fastbuild_inclined_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1))
//                                        .replace("%all2%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_inclined_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")));
//                            }
//                            if (ints[3] == 0)
//                                out = out.replace("%db3% §8[§b#%pos3%§8/§b#%all3%§8]", "null");
//                            else {
//                                out = out
//                                        .replace("%db3%", Core.msToTime(ints[3]))
//                                        .replace("%pos3%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_onestack_pb < (SELECT fastbuild_onestack_pb FROM playersdata WHERE uuid='" + id + "' ) AND fastbuild_onestack_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1))
//                                        .replace("%all3%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_onestack_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")));
//                            }
//                            if (ints[4] == 0)
//                                out = out.replace("%db4% §8[§b#%pos4%§8/§b#%all4%§8]", "null");
//                            else {
//                                out = out
//                                        .replace("%db4%", Core.msToTime(ints[4]))
//                                        .replace("%pos4%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_inclinedshort_pb < (SELECT fastbuild_inclinedshort_pb FROM playersdata WHERE uuid='" + id + "' ) AND fastbuild_inclinedshort_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)") + 1))
//                                        .replace("%all4%", Integer.toString(speedcubingServer.connection.count("playersdata", "*", "fastbuild_inclinedshort_pb > 0 AND NOT uuid IN (SELECT uuid FROM banlist)")));
//                            }
//                            commandSender.sendMessage(out);
//                        }
//                    }
//                }
//            }).start();
//        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}

