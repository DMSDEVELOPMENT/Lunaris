package org.lunaris.command;

import org.lunaris.entity.misc.LPermission;

/**
 * Created by RINES on 15.09.17.
 */
public interface CommandSender {

    String getName();

    void sendMessage(String message);

    void sendMessage(String message, Object... args);

    boolean hasPermission(LPermission permission);

}
