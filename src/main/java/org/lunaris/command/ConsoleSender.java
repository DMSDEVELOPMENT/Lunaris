package org.lunaris.command;

import org.lunaris.Lunaris;
import org.lunaris.entity.data.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 15.09.17.
 */
public class ConsoleSender implements CommandSender {

    ConsoleSender() {}

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String message) {
        Lunaris.getInstance().getLogger().info(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void sendMessage(String message, Object... args) {
        Lunaris.getInstance().getLogger().info(ChatColor.translateAlternateColorCodes('&', String.format(message, args)));
    }

    @Override
    public boolean hasPermission(LPermission permission) {
        return true;
    }

}
