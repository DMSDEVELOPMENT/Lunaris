package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.entity.data.LPermission;
import org.lunaris.entity.data.Skin;
import org.lunaris.event.player.PlayerKickEvent;
import org.lunaris.inventory.PlayerInventory;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.util.logger.ChatColor;
import org.lunaris.world.util.LongHash;

import java.util.HashSet;
import java.util.Iterator;
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
        Lunaris.getInstance().getScheduler().addSyncTask(() -> this.session.getServer().removeSession(this.session));
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

    public void tick() {
        for(Iterator<Long> iterator = this.chunksSent.iterator(); iterator.hasNext();) {
            long chunk = iterator.next();
            int x = LongHash.msw(chunk), z = LongHash.lsw(chunk);
            if(!getWorld().isInRangeOfView(this, x << 4, z << 4))
                iterator.remove();
        }
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
        return sprinting;
    }

    public boolean isSneaking() {
        return sneaking;
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

    public PlayerInventory getInventory() {
        return inventory;
    }

    public enum IngameState {
        LOGIN, ONLINE, DISCONNECTING
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + this.username + "-" + this.clientUUID.toString();
    }

}
