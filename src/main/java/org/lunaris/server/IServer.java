package org.lunaris.server;

import org.lunaris.api.server.Scheduler;
import org.lunaris.command.CommandManager;
import org.lunaris.entity.EntityProvider;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.EventManager;
import org.lunaris.api.plugin.PluginManager;
import org.lunaris.resourcepacks.ResourcePackManager;
import org.lunaris.util.logger.FormatLogger;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by RINES on 12.09.17.
 */
public interface IServer {

    String getServerVersion();

    String getSupportedClientVersion();

    FormatLogger getLogger();

    Scheduler getScheduler();

    EventManager getEventManager();

    EntityProvider getEntityProvider();

    ServerSettings getServerSettings();

    ResourcePackManager getResourcePackManager();

    WorldProvider getWorldProvider();

    CommandManager getCommandManager();

    PluginManager getPluginManager();

    Collection<LPlayer> getOnlinePlayers();

    LPlayer getPlayer(String name);

    LPlayer getPlayer(UUID uuid);

    void broadcastMessage(String message);

}
