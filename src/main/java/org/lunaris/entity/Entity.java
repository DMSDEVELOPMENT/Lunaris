package org.lunaris.entity;

import org.lunaris.nbt.tag.CompoundTag;
import org.lunaris.world.Location;
import org.lunaris.world.World;

/**
 * Created by RINES on 13.09.17.
 */
public class Entity {

    private final int entityID;
    private Location location;
    private CompoundTag nbt;

    protected Entity(int entityID) {
        this.entityID = entityID;
    }

    public int getEntityID() {
        return entityID;
    }

    public Location getLocation() {
        return this.location;
    }

    public World getWorld() {
        return this.location.getWorld();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setNbt(CompoundTag nbt) {
        this.nbt = nbt;
    }

    protected CompoundTag getNbt() {
        return this.nbt;
    }

    @Override
    public int hashCode() {
        return this.entityID;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof Entity))
            return false;
        return this.entityID == ((Entity) o).entityID;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-ID" + this.entityID;
    }

}
