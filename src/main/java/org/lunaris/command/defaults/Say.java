package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.data.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 16.09.17.
 */
public class Say extends Command {

    public Say() {
        super("say", LPermission.ADMINISTRATIVE, new CommandParameter("message", false));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0)
            return;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < args.length; ++i)
            sb.append(args[i]).append(" ");
        Lunaris.getInstance().broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&d[Server]&r ") + sb.toString().trim());
    }

}
