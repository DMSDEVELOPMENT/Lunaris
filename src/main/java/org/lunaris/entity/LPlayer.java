package org.lunaris.entity;

import org.lunaris.LunarisServer;
import org.lunaris.api.entity.EntityType;
import org.lunaris.api.entity.Gamemode;
import org.lunaris.api.entity.Player;
import org.lunaris.api.event.player.PlayerKickEvent;
import org.lunaris.api.event.player.PlayerPickupItemEvent;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.world.Location;
import org.lunaris.api.world.Sound;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.data.*;
import org.lunaris.entity.misc.AdventureSettings;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.entity.misc.Skin;
import org.lunaris.inventory.InventoryManager;
import org.lunaris.inventory.LInventory;
import org.lunaris.inventory.LPlayerInventory;
import org.lunaris.network.Packet;
import org.lunaris.network.PlayerConnection;
import org.lunaris.network.PlayerConnectionState;
import org.lunaris.network.packet.*;
import org.lunaris.util.logger.ChatColor;
import org.lunaris.world.LWorld;
import org.lunaris.world.util.LongHash;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by RINES on 13.09.17.
 */
public class LPlayer extends LLivingEntity implements CommandSender, Player {

    private final String ip;
    private final PlayerConnection connection;

    private final String username;
    private final UUID clientUUID;
    private final String xboxID;
    private final int protocolVersion;
    private final Skin skin;
    private final String skinGeometryName;
    private final byte[] skinGeometry;

    private String disconnectingReason = "Just disconnected";

    private Gamemode gamemode = LunarisServer.getInstance().getServerSettings().getDefaultGamemode();
    private final InventoryManager inventoryManager;

    private int foodLevel = 20;
    private float foodSaturationLevel = 20F;
    private boolean sprinting;
    private boolean sneaking;

    private LPermission permission = LPermission.USER;

    private int chunksView = LunarisServer.getInstance().getServerSettings().getChunksView();

    private final Set<Long> chunksSent = new HashSet<>();

    private final BlockBreakingData blockBreakingData = new BlockBreakingData();

    private final AdventureSettings adventureSettings;

    private long lastUseTime = -1;

    LPlayer(int entityID, PlayerConnection connection, Packet01Login packetLogin) {
        super(entityID, EntityType.PLAYER);
        this.connection = connection;
        this.ip = connection.getConnection().getAddress().getAddress().getHostAddress();
        this.username = ChatColor.stripColor(packetLogin.getUsername());
        this.clientUUID = packetLogin.getClientUuid();
        this.xboxID = packetLogin.getXboxID();
        this.protocolVersion = packetLogin.getProtocolVersion();
        this.skin = packetLogin.getSkin();
        this.skinGeometryName = packetLogin.getSkinGeometryName();
        this.skinGeometry = packetLogin.getSkinGeometry();

        this.adventureSettings = new AdventureSettings(this);
        this.inventoryManager = new InventoryManager(this);
    }

    /**
     * Установить скорость.
     *
     * @param speed: 1.0 - обычная скорость.
     */
    public void setSpeed(float speed) {
        setAttribute(Attribute.MOVEMENT_SPEED, speed / 10F);
    }

    @Override
    public void setAttribute(int id, float value) {
        super.setAttribute(id, value);
        sendPacket(new Packet1DUpdateAttributes(getEntityID(), getAttribute(id)));
    }

    public boolean isOnline() {
        return this.connection.getConnectionState() == PlayerConnectionState.PLAYING;
    }

    public String getIp() {
        return this.ip;
    }

    public String getAddress() {
        return this.ip;
    }

    public String getName() {
        return getUsername();
    }

    @Override
    public void sendMessage(String message) {
        sendPacket(new Packet09Text(Packet09Text.MessageType.CLIENT_MESSAGE, "", message));
    }

    @Override
    public void sendMessage(String message, Object... args) {
        sendMessage(String.format(message, args));
    }

    public void sendTip(String message) {
        sendPacket(new Packet09Text(Packet09Text.MessageType.TIP, "", message));
    }

    public void sendTip(String message, Object... args) {
        sendTip(String.format(message, args));
    }

    public void sendPopup(String message) {
        sendPopup(message, "");
    }

    public void sendPopup(String message, String subtitle) {
        sendPacket(new Packet09Text(Packet09Text.MessageType.POPUP, message, subtitle));
    }

    public void sendAvailableCommands() {
        sendPacket(new Packet4CAvailableCommands(LunarisServer.getInstance().getCommandManager().getAvailableCommands(this)));
    }

    @Override
    public boolean hasPermission(LPermission permission) {
        return this.permission.ordinal() >= permission.ordinal();
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUUID() {
        return this.clientUUID;
    }

    public String getXboxID() {
        return this.xboxID;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public String getSkinGeometryName() {
        return this.skinGeometryName;
    }

    public byte[] getSkinGeometry() {
        return this.skinGeometry;
    }

    public String getDisconnectingReason() {
        return this.disconnectingReason;
    }

    public boolean tryDisconnectReason(String reason) {
        if (reason == null || !this.disconnectingReason.equals("Just disconnected"))
            return false;
        this.disconnectingReason = reason;
        return true;
    }

    public void sendPacket(Packet packet) {
        LunarisServer.getInstance().getNetworkManager().sendPacket(this, packet);
    }

    public void sendPacketImmediately(Packet packet) {
        LunarisServer.getInstance().getNetworkManager().sendPacketImmediately(this, packet);
    }

    @Override
    public void sendPacketToWatchersAndMe(Packet packet) {
        Collection<LPlayer> watchers = getWatchers();
        watchers.add(this);
        LunarisServer.getInstance().getNetworkManager().sendPacket(watchers, packet);
    }

    public void disconnect() {
        disconnect("You have been disconnected");
    }

    public void disconnect(String reason) {
        if (reason != null && !reason.isEmpty()) {
            this.disconnectingReason = reason;
        }
        this.connection.disconnect(reason);
    }

    public void kick() {
        kick(null);
    }

    public void kick(String reason) {
        PlayerKickEvent event = new PlayerKickEvent(this, reason);
        LunarisServer.getInstance().getEventManager().call(event);
        if (event.isCancelled())
            return;
        disconnect(reason);
    }

    public boolean hasChunkSent(int x, int z) {
        return this.chunksSent.contains(LongHash.toLong(x, z));
    }

    public void addChunkSent(int x, int z) {
        this.chunksSent.add(LongHash.toLong(x, z));
    }

    @Override
    public void tick(long current, float dT) {
        super.tick(current, dT);
        LWorld world = getWorld();
        this.chunksSent.removeIf(chunk -> !world.isInRangeOfViewChunk(this, LongHash.msw(chunk), LongHash.lsw(chunk)));
        world.getNearbyEntitiesByClass(Item.class, getLocation().add(0, 1, 0), 1.5D, 1.5D).forEach(item -> {
            if (current < item.getPickupDelay())
                return;
            PlayerPickupItemEvent event = new PlayerPickupItemEvent(this, item);
            LunarisServer.getInstance().getEventManager().call(event);
            if (event.isCancelled())
                return;
            ItemStack is = item.getItemStack();
            LPlayerInventory pinv = getInventory();
            Collection<ItemStack> left = pinv.addItem(is).values();
            if (!left.isEmpty()) {
                ItemStack leftIS = left.iterator().next();
                if (leftIS.getAmount() == is.getAmount())
                    return;
                is.setAmount(is.getAmount() - leftIS.getAmount());
            } else
                is.setAmount(0);
            sendPacketToWatchersAndMe(new Packet11PickupItem(item.getEntityID(), getEntityID()));
            if (is.getAmount() == 0)
                item.remove();
        });
    }

    public PlayerConnection getConnection() {
        return this.connection;
    }

    public int getChunksView() {
        return this.chunksView;
    }

    public void setChunksView(int chunksView) {
        this.chunksView = chunksView;
    }

    public LPermission getPermission() {
        return this.permission;
    }

    public void setPermission(LPermission permission) {
        this.permission = permission;
    }

    public boolean isSprinting() {
        return this.sprinting;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    public void setState(Packet24PlayerAction packet) {
        Packet24PlayerAction.Action action = packet.getAction();
        switch (action) {
            case START_SPRINT:
                this.sprinting = true;
                break;
            case STOP_SPRINT:
                this.sprinting = false;
                break;
            case START_SNEAK:
                this.sneaking = true;
                break;
            case STOP_SNEAK:
                this.sneaking = false;
                break;
            default:
                break;
        }
    }

    public void respawn(Location location) {
        this.sprinting = false;
        this.sneaking = false;
        getMetadata().setDataFlag(false, EntityDataFlag.SPRINTING, false, false);
        getMetadata().setDataFlag(false, EntityDataFlag.SNEAKING, false, false);
        Attribute health = getAttribute(Attribute.MAX_HEALTH);
        health.setMaxValue(health.getDefaultValue());
        health.setValue(health.getMaxValue());
        getMetadata().setDirtyMetadata(false);
        sendPacketToWatchers(new Packet2DRespawn((float) location.getX(), (float) location.getY(), (float) location.getZ()));
        sendPacketToWatchersAndMe(new Packet1DUpdateAttributes(
                getEntityID(),
                getAttribute(Attribute.MAX_HEALTH),
                getAttribute(Attribute.MAX_HUNGER),
                getAttribute(Attribute.MOVEMENT_SPEED),
                getAttribute(Attribute.EXPERIENCE_LEVEL),
                getAttribute(Attribute.EXPERIENCE)
        ));
        teleport(location);
        sendPacket(new Packet28SetEntityMotion(getEntityID(), 0F, 0F, 0F));

        //remove all effects
        this.adventureSettings.update();
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public void openInventory(LInventory inventory) {
        this.inventoryManager.addInventory(inventory);
        this.inventoryManager.sendInventory(inventory);
    }

    @Override
    public LPlayerInventory getInventory() {
        return this.inventoryManager.getPlayerInventory();
    }

    @Override
    public void teleport(Location location) {
        if (getWorld() == null)
            initWorld(location.getWorld());
        if (getWorld() != location.getWorld()) {
            sendPacket(new Packet13MovePlayer(getEntityID(), getX() + 1000000, 4000, getZ() + 1000000, 0F, 0F, 0F).mode(Packet13MovePlayer.MODE_RESET));
            getWorld().removePlayerFromWorld(this);
            initWorld(location.getWorld());
            ((LWorld) location.getWorld()).addPlayerToWorld(this);
        }
        setPositionAndRotation(location);
        sendPacket(new Packet13MovePlayer(this).mode(Packet13MovePlayer.MODE_RESET));
    }

    public Gamemode getGamemode() {
        return this.gamemode;
    }

    public void setGamemode(Gamemode gamemode) {
        if (this.gamemode == gamemode)
            return;
        this.gamemode = gamemode;
        sendPacket(new Packet3ESetPlayerGameType(gamemode));
        this.adventureSettings.update(gamemode);
        if (gamemode == Gamemode.SPECTATOR) {
            //...
        } else {


        }
        LPlayerInventory inventory = getInventory();
        inventory.sendContents(this);
        if (gamemode == Gamemode.CREATIVE)
            this.inventoryManager.getCreativeInventory().sendContents(this);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public float getFoodSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public BlockBreakingData getBlockBreakingData() {
        return this.blockBreakingData;
    }

    public boolean isBreakingBlock() {
        return this.blockBreakingData.isBreakingBlock();
    }

    public long getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(long time) {
        this.lastUseTime = time;
    }

    public AdventureSettings getAdventureSettings() {
        return this.adventureSettings;
    }

    public void playSound(Sound sound, Location loc) {
        sendPacket(new Packet18LevelSoundEvent(sound, loc, -1, 1, false, false));
    }

    @Override
    public float getWidth() {
        return .8F;
    }

    @Override
    public float getStepHeight() {
        return .8F;
    }

    @Override
    public Packet createSpawnPacket() {
        return new Packet0CAddPlayer(this);
    }

    @Override
    public float getHeight() {
        return 1.8F;
    }

    @Override
    public float getEyeHeight() {
        return 1.62f;
    }

    @Override
    protected MovementData generateEntityMovement() {
        return new PlayerMovementData(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + this.username + "-" + this.clientUUID.toString();
    }

}
