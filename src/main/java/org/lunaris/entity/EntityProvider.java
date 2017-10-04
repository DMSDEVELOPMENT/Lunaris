package org.lunaris.entity;

import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.packet.Packet01Login;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.world.Location;

/**
 * Created by RINES on 04.10.17.
 */
public class EntityProvider {

    private volatile int entityIDincrementor;

    public Player createPlayer(Packet01Login packet, RakNetClientSession session) {
        return new Player(nextEntityID(), session, packet);
    }

    public Item spawnItem(Location location, ItemStack itemStack) {
        return spawn(new Item(nextEntityID(), itemStack), location);
    }

    private <T extends Entity> T spawn(T entity, Location location) {
        entity.initWorld(location.getWorld());
        entity.setPositionAndRotation(location);
        entity.getWorld().addEntityToWorld(entity);
        return entity;
    }

    private int nextEntityID() {
        return ++this.entityIDincrementor;
    }

}
