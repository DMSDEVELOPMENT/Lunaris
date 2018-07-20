package org.lunaris.command;

import org.lunaris.LunarisServer;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 15.09.17.
 */
public class ConsoleSender implements CommandSender {

    ConsoleSender() {
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(String message) {
        LunarisServer.getInstance().getLogger().info(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void sendMessage(String message, Object... args) {
        LunarisServer.getInstance().getLogger().info(ChatColor.translateAlternateColorCodes('&', String.format(message, args)));
    }

    @Override
    public boolean hasPermission(LPermission permission) {
        return true;
    }

}
