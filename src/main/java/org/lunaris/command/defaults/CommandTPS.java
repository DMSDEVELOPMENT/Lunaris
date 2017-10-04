package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.misc.LPermission;

/**
 * Created by RINES on 04.10.17.
 */
public class CommandTPS extends Command {

    public CommandTPS() {
        super("tps", LPermission.ADMINISTRATIVE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        float tick = Lunaris.getInstance().getWorldProvider().getLastTickTime();
        sender.sendMessage("&fLast tick took %d extra milliseconds", (int) ((tick - .05F) * 1000));
        sender.sendMessage("&fThat's %d%% more than single tick uses", (int) (100 * (tick / .05F - 1F)));
        sender.sendMessage("&fInstant TPS at the moment = %d", (int) (1F / tick));
    }

}
