package org.lunaris.inventory;

import org.lunaris.Lunaris;
import org.lunaris.api.entity.Player;
import org.lunaris.api.world.Location;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.protocol.packet.Packet2EContainerOpen;
import org.lunaris.world.BlockVector;
import org.lunaris.world.tileentity.ContainerTileEntity;

/**
 * Created by RINES on 12.10.17.
 */
public class ContainerInventory extends Inventory {

    private final ContainerTileEntity holder;

    ContainerInventory(ContainerTileEntity holder, InventoryType type) {
        super(type);
        this.holder = holder;
    }

    @Override
    int getReservedInventoryId() {
        return -1;
    }

    public ContainerTileEntity getHolder() {
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
