package org.lunaris.api.entity;

import org.lunaris.api.inventory.PlayerInventory;
import org.lunaris.api.world.Location;
import org.lunaris.api.world.Sound;

import java.util.UUID;

/**
 * Created by RINES on 12.10.17.
 */
public interface Player extends LivingEntity {

    /**
     * Get chunks view range of this player.
     *
     * @return MIN(server ' s chunks view range limitation ; player ' s selected chunks view range).
     */
    int getChunksView();

    /**
     * Setup player's movement speed.
     * 1F - is the normal speed.
     *
     * @param speed new player's movement speed.
     */
    void setSpeed(float speed);

    /**
     * Get player's ip address.
     *
     * @return player's ip address.
     */
    String getIp();

    /**
     * @return player's ip address.
     * @see Player#getIp()
     */
    String getAddress();

    /**
     * Get player's nickname.
     *
     * @return player's nickname.
     */
    String getName();

    /**
     * Send uncolored message to this player.
     *
     * @param message text message.
     */
    void sendMessage(String message);

    /**
     * Send uncolored formatted message to this player.
     *
     * @param message text message.
     * @param args    format arguments.
     * @see String#format(String, Object...)
     */
    void sendMessage(String message, Object... args);

    /**
     * Send to this player all available commands.
     */
    void sendAvailableCommands();

    /**
     * Get this player's UUID.
     *
     * @return this player's UUID.
     */
    UUID getUUID();

    /**
     * Get this player's Xbox ID.
     *
     * @return this player's Xbox ID.
     */
    String getXboxID();

    /**
     * Disconnect this player with no reason.
     */
    void disconnect();

    /**
     * Disconnect this player with given reason.
     *
     * @param reason
     */
    void disconnect(String reason);

    /**
     * Kick this player.
     *
     * @see Player#disconnect()
     * Differs from disconnect, because kick can be cancelled via PlayerKickEvent.
     */
    void kick();

    /**
     * Check whether this player is sprinting now.
     *
     * @return if this player is sprinting now.
     */
    boolean isSprinting();

    /**
     * Check whether this player is sneaking now.
     *
     * @return if this player is sneaking now.
     */
    boolean isSneaking();

    /**
     * Get this player's gamemode.
     *
     * @return this player's gamemode.
     */
    Gamemode getGamemode();

    /**
     * Change this player's gamemode.
     *
     * @param gamemode gamemode to change to.
     */
    void setGamemode(Gamemode gamemode);

    /**
     * Check whether this player is breaking block right now.
     *
     * @return if this player is breaking block right now.
     */
    boolean isBreakingBlock();

    /**
     * Play sound for this player at given location.
     *
     * @param sound    the sound itself.
     * @param location the location to play sound at.
     */
    void playSound(Sound sound, Location location);

    /**
     * Get this player's inventory.
     *
     * @return this player's inventory.
     */
    PlayerInventory getInventory();

}
