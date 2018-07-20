package org.lunaris.api;

import org.lunaris.LunarisServer;
import org.lunaris.api.entity.Player;
import org.lunaris.api.event.Event;
import org.lunaris.api.event.Listener;
import org.lunaris.api.server.Scheduler;
import org.lunaris.api.world.World;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by RINES on 12.10.17.
 */
public class Lunaris {

    /**
     * Get this server core version.
     *
     * @return this server core version.
     */
    public static String getServerVersion() {
        return getServer().getServerVersion();
    }

    /**
     * Get supported client version.
     *
     * @return supported client version.
     */
    public static String getSupportedClientVersion() {
        return getServer().getSupportedClientVersion();
    }

    /**
     * Get player with given name.
     *
     * @param name nickname of the player.
     * @return null, whether there is no player of given nickname; player instance otherwise.
     */
    public static Player getPlayerExact(String name) {
        return getServer().getPlayer(name);
    }

    /**
     * Get player, whose nickname starts with given prefix.
     * This method ignores nicknames cases.
     *
     * @param name prefix of player's nickname.
     * @return null, whether there are none players with nicknames, containing given prefix; random one satisfying player instance otherwise.
     */
    public static Player getPlayer(String name) {
        String prefix = name.toLowerCase();
        return getServer().getOnlinePlayers().stream().filter(p -> p.getName().toLowerCase().startsWith(prefix)).findAny().orElse(null);
    }

    /**
     * Get all online players.
     *
     * @return collection of all online players.
     */
    public static Collection<? extends Player> getOnlinePlayers() {
        return getServer().getOnlinePlayers();
    }

    /**
     * Get player with given UUID.
     *
     * @param uuid the uuid of a player.
     * @return null, whether there is no player with given uuid; player instance otherwise.
     */
    public static Player getPlayer(UUID uuid) {
        return getServer().getPlayer(uuid);
    }

    /**
     * Register new event listener.
     *
     * @param listener the listener to register.
     */
    public static void registerEventListener(Listener listener) {
        listener.register();
    }

    /**
     * Fire event (pass it through all the listeners).
     *
     * @param event the event to fire.
     */
    public static void callEvent(Event event) {
        event.call();
    }

    /**
     * Get all worlds of this server.
     *
     * @return list of all worlds of this server.
     */
    public static List<? extends World> getWorlds() {
        return getServer().getWorldProvider().getWorlds();
    }

    /**
     * Get world by it's ordinal number on this server.
     *
     * @param index ordinal number of the world on this server.
     * @return null, whether ordinal number is over maximal index of this server's world; world instance otherwise.
     */
    public static World getWorld(int index) {
        return getServer().getWorldProvider().getWorld(index);
    }

    /**
     * Get world by given world name.
     *
     * @param name the name of the world.
     * @return null, whether there is no world of given name; world instance otherwise.
     */
    public static World getWorld(String name) {
        return getServer().getWorldProvider().getWorld(name);
    }

    /**
     * Get scheduler.
     *
     * @return the scheduler.
     */
    public static Scheduler getScheduler() {
        return getServer().getScheduler();
    }

    /**
     * Broadcast given message to all the players currently online at the server.
     *
     * @param message the message to be broadcasted
     */
    public static void broadcastMessage(String message) {
        getServer().broadcastMessage(message);
    }

    private static LunarisServer getServer() {
        return LunarisServer.getInstance();
    }

}
