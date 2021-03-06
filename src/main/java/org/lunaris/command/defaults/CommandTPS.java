package org.lunaris.command.defaults;

import org.lunaris.LunarisServer;
import org.lunaris.command.Command;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 04.10.17.
 */
public class CommandTPS extends Command {

    public CommandTPS() {
        super("tps", LPermission.ADMINISTRATIVE);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        float tick = LunarisServer.getInstance().getWorldProvider().getLastTickTime();
        sender.sendMessage(ChatColor.WHITE + "Last tick took %d extra milliseconds", (int) ((tick - .05F) * 1000));
        sender.sendMessage(ChatColor.WHITE + "That's %d%% more than single tick uses", (int) (100 * (tick / .05F - 1F)));
        sender.sendMessage(ChatColor.WHITE + "Instant TPS at the moment = %d", (int) (1F / tick));
    }

}
