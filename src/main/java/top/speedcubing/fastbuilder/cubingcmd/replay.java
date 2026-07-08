package top.speedcubing.fastbuilder.cubingcmd;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.fastbuilder.Member;
import top.speedcubing.server.system.command.CubingCommand;

public class replay extends CubingCommand {
    public replay() {
        super("replay");
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("must be executed by a player.");
            return;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /replay <replayID>");
        } else Member.getMember(sender).joinReplayByReplayID((Player) sender, args[0]);
    }
}
