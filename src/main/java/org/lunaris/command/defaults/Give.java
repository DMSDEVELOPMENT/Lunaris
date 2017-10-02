package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.LPermission;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 02.10.17.
 */
public class Give extends Command {

    public Give() {
        super("give", LPermission.ADMINISTRATIVE, new CommandParameter("player", false), new CommandParameter("item info", false), new CommandParameter("amount", true));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2)
            return;
        Player target = Lunaris.getInstance().getPlayer(args[0]);
        if(target == null) {
            sender.sendMessage("Target player is not online.");
            return;
        }
        int id = 0, data = 0;
        String sid = args[1], sdata = null;
        if(args[1].contains(":")) {
            String[] split = args[1].split(":");
            sid = split[0];
            sdata = split[1];
        }
        try {
            id = Integer.parseInt(sid);
        }catch(NumberFormatException ex) {
            try {
                id = Material.valueOf(sid.toUpperCase()).getId();
            }catch(Exception ex2) {
                sender.sendMessage(ChatColor.RED + "Unknown item material.");
                return;
            }
        }
        try {
            if(sdata != null && !sdata.isEmpty())
                data = Integer.parseInt(sdata);
        }catch(NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Illegal item data.");
            return;
        }
        int amount = 1;
        if(args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
            }catch(NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Illegal item amount.");
                return;
            }
        }
        target.getInventory().addItem(new ItemStack(id, amount, data));
        sender.sendMessage(ChatColor.GREEN + "Item has been given to " + target.getName() + ".");
    }

}
