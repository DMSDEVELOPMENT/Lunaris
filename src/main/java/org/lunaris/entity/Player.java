package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.entity.data.Skin;
import org.lunaris.event.player.PlayerKickEvent;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.Packet01Login;
import org.lunaris.network.protocol.packet.Packet05Disconnect;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.util.logger.ChatColor;

import java.util.UUID;

/**
 * Created by RINES on 13.09.17.
 */
public class Player extends Entity {

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

    public long getFirstPlayed() {
        return getNbt().getLong("firstPlayed") * 1000L;
    }

    public long getlastPlayed() {
        return getNbt().getLong("lastPlayed") * 1000L;
    }

    public Gamemode getGamemode() {
        return Gamemode.values()[getNbt().getInt("playerGameType")];
    }

    public float getFallDistance() {
        return getNbt().getFloat("FallDistance");
    }

    public int getFireTicks() {
        return getNbt().getShort("Fire");
    }

    public int getAirLeft() {
        return getNbt().getShort("Air");
    }

    public boolean isOnGround() {
        return getNbt().getBoolean("OnGround");
    }

    public boolean isInvulnerable() {
        return getNbt().getBoolean("Invulnerable");
    }

    public int getFoodLevel() {
        return getNbt().getInt("foodLevel");
    }

    public float getFoodSaturationLevel() {
        return getNbt().getFloat("FoodSaturationLevel");
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

    public RakNetClientSession getSession() {
        return session;
    }

    public enum IngameState {
        LOGIN, ONLINE, DISCONNECTING
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-" + this.username + "-" + this.clientUUID.toString();
    }

}
