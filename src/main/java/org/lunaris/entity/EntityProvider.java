package org.lunaris.entity;

import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.packet.Packet01Login;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.world.Location;

import java.util.function.Function;

/**
 * Created by RINES on 04.10.17.
 */
public class EntityProvider {

    private volatile int entityIDincrementor;

    public Player createPlayer(Packet01Login packet, RakNetClientSession session) {
        return new Player(nextEntityID(), session, packet);
    }

    public Item spawnItem(Location location, ItemStack itemStack) {
        return spawnItem(location, itemStack, null);
    }

    public Item spawnItem(Location location, ItemStack itemStack, Function<Item, Boolean> spawnController) {
        return spawn(new Item(nextEntityID(), itemStack), location, spawnController);
    }

    private <T extends Entity> T spawn(T entity, Location location) {
        return spawn(entity, location, null);
    }

    private <T extends Entity> T spawn(T entity, Location location, Function<T, Boolean> spawnController) {
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
