package org.lunaris.entity;

import org.lunaris.api.item.ItemStack;
import org.lunaris.network.PlayerConnection;
import org.lunaris.network_old.protocol.packet.Packet01Login;
import org.lunaris.network_old.raknet.session.RakNetClientSession;
import org.lunaris.api.world.Location;

import java.util.function.Function;

/**
 * Created by RINES on 04.10.17.
 */
public class EntityProvider {

    private volatile int entityIDincrementor;

    public LPlayer createPlayer(Packet01Login packet, PlayerConnection connection) {
        return new LPlayer(nextEntityID(), connection, packet);
    }

    public Item spawnItem(Location location, ItemStack itemStack) {
        return spawnItem(location, itemStack, null);
    }

    public Item spawnItem(Location location, ItemStack itemStack, Function<Item, Boolean> spawnController) {
        return spawn(new Item(nextEntityID(), itemStack), location, spawnController);
    }

    private <T extends LEntity> T spawn(T entity, Location location) {
        return spawn(entity, location, null);
    }

    private <T extends LEntity> T spawn(T entity, Location location, Function<T, Boolean> spawnController) {
        entity.initWorld(location.getWorld());
        entity.setPositionAndRotation(location);
        float hwidth = entity.getWidth() / 2;
        entity.getBoundingBox().setBounds(
                entity.getX() - hwidth,
                entity.getY(),
                entity.getZ() - hwidth,
                entity.getX() + hwidth,
                entity.getY() + entity.getHeight(),
                entity.getZ() + hwidth
        );
        if(spawnController != null && !spawnController.apply(entity))
            return null;
        entity.getWorld().addEntityToWorld(entity);
        return entity;
    }

    private int nextEntityID() {
        return ++this.entityIDincrementor;
    }

}
