package org.lunaris.inventory;

import org.lunaris.api.entity.Player;
import org.lunaris.api.inventory.ContainerInventory;
import org.lunaris.api.inventory.InventoryType;
import org.lunaris.api.world.Location;
import org.lunaris.entity.LPlayer;
import org.lunaris.network_old.protocol.packet.Packet2EContainerOpen;
import org.lunaris.world.BlockVector;
import org.lunaris.world.tileentity.LContainerTileEntity;

/**
 * Created by RINES on 12.10.17.
 */
public class LContainerInventory extends LInventory implements ContainerInventory {

    private final LContainerTileEntity holder;

    LContainerInventory(LContainerTileEntity holder, InventoryType type) {
        super(type);
        this.holder = holder;
    }

    @Override
    int getReservedInventoryId() {
        return -1;
    }

    public LContainerTileEntity getHolder() {
        return this.holder;
    }

    @Override
    boolean open(Player player) {
        if(!super.open(player))
            return false;
        LPlayer lp = (LPlayer) player;
        Location loc = this.holder.getLocation();
        lp.sendPacket(new Packet2EContainerOpen(
                (byte) lp.getInventoryManager().getInventoryId(this),
                (byte) getType().getNetworkTypeId(),
                new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()),
                -1L
        ));
        this.holder.onInventoryOpened(player);
        return true;
    }

    @Override
    void close(Player player) {
        this.holder.onInventoryClosed(player);
        super.close(player);
    }

}
