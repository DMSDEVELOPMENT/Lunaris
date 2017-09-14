package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.entity.data.Skin;
import org.lunaris.event.player.PlayerKickEvent;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.Packet01Login;
import org.lunaris.network.protocol.packet.Packet05Disconnect;
import org.lunaris.network.protocol.packet.Packet1DUpdateAttributes;
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
public class Player extends LivingEntity {

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
    private int foodLevel = 20;
    private float foodSaturationLevel = 20F;
    private boolean onGround = true;
    private boolean invulnerable = false;

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

    public enum IngameState {
        LOGIN, ONLINE, DISCONNECTING
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + this.username + "-" + this.clientUUID.toString();
    }

}
