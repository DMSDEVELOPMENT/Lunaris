package org.lunaris.command.defaults;

import org.lunaris.LunarisServer;
import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.command.CommandParameterType;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 16.09.17.
 */
public class CommandSay extends Command {

    public CommandSay() {
        super("say", LPermission.ADMINISTRATIVE);
        addParametersVariant(new CommandParameter("message", CommandParameterType.RAW_TEXT));
        setDescription("Broadcast message");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0)
            return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; ++i)
            sb.append(args[i]).append(" ");
        LunarisServer.getInstance().broadcastMessage(ChatColor.colored("&d[Server]&r ") + sb.toString().trim());
    }

}
