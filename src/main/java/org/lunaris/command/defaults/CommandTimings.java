package org.lunaris.command.defaults;

import co.aikar.timings.TimingsExport;
import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 30.09.17.
 */
public class CommandTimings extends Command {
    private static final String[] SUB_COMMANDS = {
        "on", "off", "report", "paste"
    };

    public CommandTimings() {
        super("timings", LPermission.OPERATOR);
        addParametersVariant(new CommandParameter("sub", SUB_COMMANDS));
        setDescription("Control over timings");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("/timings on - enable timings");
            sender.sendMessage("/timings paste - upload timings");
            sender.sendMessage("/timings off - disable timings");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "on": {
                co.aikar.timings.Timings.setTimingsEnabled(true);
                co.aikar.timings.Timings.reset();
                sender.sendMessage(ChatColor.GREEN + "Enabled timings.");
                break;
            }
            case "off": {
                co.aikar.timings.Timings.setTimingsEnabled(false);
                sender.sendMessage(ChatColor.GREEN + "Disabled timings.");
                break;
            }
            case "report":
            case "paste": {
                if (!co.aikar.timings.Timings.isTimingsEnabled()) {
                    sender.sendMessage(ChatColor.RED + "This subcommand requires timings to be enabled.");
                    break;
                }
                sender.sendMessage(ChatColor.YELLOW + "Pasting timings..");
                TimingsExport.report(sender);
                break;
            }
            default: {
                sender.sendMessage("Unknown timings subcommand.");
            }
        }
    }
}
