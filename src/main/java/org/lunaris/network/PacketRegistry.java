package org.lunaris.network;

import org.lunaris.network.packet.Packet01Login;
import org.lunaris.network.packet.Packet02PlayStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class PacketRegistry {

    private final Map<Byte, Supplier<Packet>> constructors = new HashMap<>();

    public PacketRegistry() {
        registerPackets(
                Packet01Login::new,
                Packet02PlayStatus::new
        );
    }

    public Packet constructPacket(byte id) {
        Supplier<Packet> constructor = this.constructors.get(id);
        return constructor == null ? null : constructor.get();
    }

    private void registerPackets(Supplier<Packet>... constructors) {
        for (Supplier<Packet> constructor : constructors) {
            Packet packet = constructor.get();
            this.constructors.put(packet.getID(), constructor);
        }
    }

}
