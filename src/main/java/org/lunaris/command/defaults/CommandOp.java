package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.command.CommandParameterType;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.Player;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * @author xtrafrancyz
 */
public class CommandOp extends Command {
    public CommandOp() {
        super("op", LPermission.OPERATOR);
        addParametersVariant(new CommandParameter("player", CommandParameterType.PLAYER));
        setDescription("Make the player an operator");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.colored("&cEnter the player name"));
            return;
        }
        Player player = Lunaris.getInstance().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player %s not found", args[0]);
            return;
        }
        player.setPermission(LPermission.OPERATOR);
        player.sendMessage(ChatColor.GREEN + "Congratulations, now you have operator permissions");
        player.sendAvailableCommands();
    }
}
