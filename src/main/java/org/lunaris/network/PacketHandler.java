package org.lunaris.network;

import org.lunaris.LunarisServer;
import org.lunaris.network.handler.EncryptionHandler;
import org.lunaris.network.handler.HandshakeHandler;
import org.lunaris.network.handler.ResourcesHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public abstract class PacketHandler {

    public final static PacketHandler HANDSHAKE_HANDLER = new HandshakeHandler();
    public final static PacketHandler ENCRYPTION_HANDLER = new EncryptionHandler();
    public final static PacketHandler RESOURCES_HANDLER = new ResourcesHandler();

    private final Map<Class<? extends Packet>, List<Handler<Packet>>> handlers = new HashMap<>();

    public PacketHandler() {
        registerPacketHandlers();
    }

    protected abstract void registerPacketHandlers();

    @SuppressWarnings("unchecked")
    public <T extends Packet> void addHandler(Class<T> packetClass, Handler<T> handler) {
        this.handlers.computeIfAbsent(packetClass, pc -> new ArrayList<>()).add((Handler<Packet>) handler);
    }

    public void handle(Packet packet, long currentMillis) {
        List<Handler<Packet>> handlers = this.handlers.get(packet.getClass());
        if (handlers == null) {
            return;
        }
        handlers.forEach(handler -> handler.accept(packet, currentMillis));
    }

    protected void sync(Runnable run) {
        LunarisServer.getInstance().getScheduler().run(run);
    }

    protected LunarisServer getServer() {
        return LunarisServer.getInstance();
    }

    public interface Handler<T extends Packet> {

        void accept(T packet, long currentMillis);

    }

}
