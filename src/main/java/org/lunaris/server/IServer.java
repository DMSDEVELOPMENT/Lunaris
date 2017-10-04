package org.lunaris.server;

import org.lunaris.command.CommandManager;
import org.lunaris.entity.EntityProvider;
import org.lunaris.entity.Player;
import org.lunaris.event.EventManager;
import org.lunaris.plugin.PluginManager;
import org.lunaris.resourcepacks.ResourcePackManager;
import org.lunaris.util.configuration.ConfigurationManager;
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

    Collection<Player> getOnlinePlayers();

    Player getPlayer(String name);

    Player getPlayer(UUID uuid);

    void broadcastMessage(String message);

}
