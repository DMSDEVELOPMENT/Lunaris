package org.lunaris.command.defaults;

import org.lunaris.LunarisServer;
import org.lunaris.command.Command;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.misc.LPermission;

/**
 * Created by RINES on 15.09.17.
 */
public class CommandStop extends Command {

    public CommandStop() {
        super("stop", LPermission.ADMINISTRATIVE);
        setDescription("Stop server");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        LunarisServer.getInstance().disable();
    }

}
