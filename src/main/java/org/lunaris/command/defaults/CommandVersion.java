package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.data.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 15.09.17.
 */
public class CommandVersion extends Command {

    public CommandVersion() {
        super("version", LPermission.USER);
        setDescription("Display server version");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.colored("This server is running &aLunaris&r version &a" + Lunaris.getInstance().getServerVersion()));
    }

}
