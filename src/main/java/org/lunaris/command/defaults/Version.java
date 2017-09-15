package org.lunaris.command.defaults;

import com.google.common.collect.Lists;
import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.data.LPermission;

/**
 * Created by RINES on 15.09.17.
 */
public class Version extends Command {

    public Version() {
        super("version", Lists.newArrayList("ver"), LPermission.USER);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("This server is running Lunaris version %s", Lunaris.getInstance().getServerVersion());
    }

}
