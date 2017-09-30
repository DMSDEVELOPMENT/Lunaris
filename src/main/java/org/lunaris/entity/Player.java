package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.data.*;
import org.lunaris.event.entity.EntityDamageEvent;
import org.lunaris.event.player.PlayerKickEvent;
import org.lunaris.inventory.PlayerInventory;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.network.util.PacketsBush;
import org.lunaris.server.Scheduler;
import org.lunaris.util.logger.ChatColor;
import org.lunaris.world.Location;
import org.lunaris.world.Sound;
import org.lunaris.world.World;
import org.lunaris.world.util.LongHash;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by RINES on 13.09.17.
 */
public class Player extends LivingEntity implements CommandSender {

    private final String ip;
    private final RakNetClientSession session;

    private final String username;
    private final UUID clientUUID;
    private final long clientID;
    private final int protocolVersion;
    private final Skin skin;
    private final String skinGeometryName;
    private final byte[] skinGeometry;

    private IngameState ingameState = IngameState.LOGIN;

    private String disconnectingReason = "Just disconnected";

    private Gamemode gamemode = Lunaris.getInstance().getServerSettings().getDefaultGamemode();
    private final PlayerInventory inventory = new PlayerInventory();

    private int foodLevel = 20;
    private float foodSaturationLevel = 20F;
    private boolean onGround = true;
    private boolean invulnerable = false;
    private boolean sprinting;
    private boolean sneaking;

    private LPermission permission = LPermission.USER;

    private int chunksView = Lunaris.getInstance().getServerSettings().getChunksView();

    private final Set<Long> chunksSent = new HashSet<>();

    private Scheduler.Task breakingBlockTask;

    private final AdventureSettings adventureSettings;

    private final PacketsBush packetsBush = new PacketsBush();

    public Player(int entityID, RakNetClientSession session, Packet01Login packetLogin) {
        super(entityID);
        this.session = session;
        this.ip = session.getAddress().getAddress().getHostAddress();
        this.username = ChatColor.stripColor(packetLogin.getUsername());
        this.clientUUID = packetLogin.getClientUuid();
        this.clientID = packetLogin.getClientId();
        this.protocolVersion = packetLogin.getProtocolVersion();
        this.skin = packetLogin.getSkin();
        this.skinGeometryName = packetLogin.getSkinGeometryName();
        this.skinGeometry = packetLogin.getSkinGeometry();

        this.adventureSettings = new AdventureSettings(this);
    }

    @Override
    protected EntityMovement generateEntityMovement() {
        return new PlayerMovement(this);
    }

    /**
     * Установить скорость.
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
        return this.ingameState == IngameState.ONLINE;
    }

    public String getIp() {
        return this.ip;
    }

    public String getAddress() {
        return this.ip;
    }

    public IngameState getIngameState() {
        return ingameState;
    }

    public void setIngameState(IngameState ingameState) {
        this.ingameState = ingameState;
    }

    public String getName() {
        return getUsername();
    }

    @Override
    public void sendMessage(String message) {
        sendPacket(new Packet09Text(Packet09Text.MessageType.RAW, "", message, false));
    }

    @Override
    public void sendMessage(String message, Object... args) {
        sendMessage(String.format(message, args));
    }

    public void sendTip(String message) {
        sendPacket(new Packet09Text(Packet09Text.MessageType.TIP, "", message, false));
    }

    public void sendTip(String message, Object... args) {
        sendTip(String.format(message, args));
    }

    public void sendPopup(String message) {
        sendPopup(message, "");
    }

    public void sendPopup(String message, String subtitle) {
        sendPacket(new Packet09Text(Packet09Text.MessageType.POPUP, message, subtitle, false));
    }

    @Override
    public boolean hasPermission(LPermission permission) {
        return this.permission.ordinal() >= permission.ordinal();
    }

    public String getUsername() {
        return username;
    }

    public UUID getClientUUID() {
        return clientUUID;
    }

    public long getClientID() {
        return clientID;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public Skin getSkin() {
        return skin;
    }

    public String getSkinGeometryName() {
        return skinGeometryName;
    }

    public byte[] getSkinGeometry() {
        return skinGeometry;
    }

    public String getDisconnectingReason() {
        return disconnectingReason;
    }

    public boolean tryDisconnectReason(String reason) {
        if(reason == null || !this.disconnectingReason.equals("Just disconnected"))
            return false;
        this.disconnectingReason = reason;
        return true;
    }

    public void sendPacket(MinePacket packet) {
        Lunaris.getInstance().getNetworkManager().sendPacket(this, packet);
    }

    public void disconnect() {
        disconnect("You have been disconnected");
    }

    public void disconnect(String reason) {
        if(this.ingameState == IngameState.DISCONNECTING)
            throw new IllegalStateException("Disconnected player can't be disconnected");
        sendPacket(new Packet05Disconnect(reason));
        Lunaris.getInstance().getScheduler().run(() -> this.session.getServer().removeSession(this.session));
        if(reason == null)
            reason = "Unknown reason";
        this.disconnectingReason = reason.replace("\n", "\\n");
        this.ingameState = IngameState.DISCONNECTING;
    }

    public void kick() {
        kick(null);
    }

    public void kick(String reason) {
        PlayerKickEvent event = new PlayerKickEvent(this, reason);
        Lunaris.getInstance().getEventManager().call(event);
        if(event.isCancelled())
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
    public void tick() {
        super.tick();
        World world = getWorld();
        this.chunksSent.removeIf(chunk -> !world.isInRangeOfViewChunk(this, LongHash.msw(chunk), LongHash.lsw(chunk)));
    }

    public RakNetClientSession getSession() {
        return session;
    }

    public int getChunksView() {
        return chunksView;
    }

    public void setChunksView(int chunksView) {
        this.chunksView = chunksView;
    }

    public LPermission getPermission() {
        return permission;
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
        switch(action) {
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

    @Override
    public void damage(EntityDamageEvent.DamageCause cause, double damage) {
        if(!isInvulnerable())
            super.damage(cause, damage);
    }

    @Override
    public void damage(Entity damager, double damage) {
        if(!isInvulnerable())
            super.damage(damager, damage);
    }

    public void respawn(Location location) {
        this.sprinting = false;
        this.sneaking = false;
        setDataFlag(false, EntityDataFlag.SPRINTING, false, false);
        setDataFlag(false, EntityDataFlag.SNEAKING, false, false);
        Attribute health = getAttribute(Attribute.MAX_HEALTH);
        health.setMaxValue(health.getDefaultValue());
        health.setValue(health.getMaxValue());
        setDirtyMetadata(false);
        sendPacket(new Packet2DRespawn((float) location.getX(), (float) location.getY(), (float) location.getZ()));
        sendPacket(new Packet1DUpdateAttributes(
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

    public PlayerInventory getInventory() {
        return this.inventory;
    }

    public Gamemode getGamemode() {
        return this.gamemode;
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    public float getFoodSaturationLevel() {
        return this.foodSaturationLevel;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public Scheduler.Task getBreakingBlockTask() {
        return this.breakingBlockTask;
    }

    public void setBreakingBlockTask(Scheduler.Task breakingBlockTask) {
        this.breakingBlockTask = breakingBlockTask;
    }

    public PacketsBush getPacketsBush() {
        return this.packetsBush;
    }

    public AdventureSettings getAdventureSettings() {
        return this.adventureSettings;
    }

    public void playSound(Sound sound, Location loc) {
        sendPacket(new Packet18LevelSoundEvent(sound, loc, -1, 1, false, false));
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getLength() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public float getEyeHeight() {
        return 1.62f;
    }

    @Override
    public float getBaseOffset() {
        return this.getEyeHeight();
    }

    public enum IngameState {
        LOGIN, ONLINE, DISCONNECTING
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + this.username + "-" + this.clientUUID.toString();
    }

}
