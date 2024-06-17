package top.speedcubing.fastbuilder.BukkitCommands;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.fastbuilder.replay.gui.ReplayGUI;

public class replaypage implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        if (strings.length != 1) {
//            commandSender.sendMessage("Usage: /replay <replayID>");
//            return true;
//        }
//
//        int page;
//        try {
//            page = Integer.parseInt(strings[0]);
//        } catch (Exception e) {
//            commandSender.sendMessage("Usage: /replay <replayID>");
//            return true;
//        }
//        Member member = Member.getMember(commandSender);
//        ReplayGUI.openReplayPage(member, member.currentMode.sqlId, page);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
