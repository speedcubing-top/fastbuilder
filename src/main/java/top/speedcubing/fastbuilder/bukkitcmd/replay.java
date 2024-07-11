package top.speedcubing.fastbuilder.bukkitcmd;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import top.speedcubing.fastbuilder.Member;

public class replay implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 1) {
            commandSender.sendMessage("Usage: /replay <replayID>");
        } else Member.getMember(commandSender).joinReplayByReplayID(strings[0]);
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return Collections.emptyList();
    }
}
