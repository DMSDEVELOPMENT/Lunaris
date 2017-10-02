package org.lunaris.inventory;

import org.lunaris.entity.Player;
import org.lunaris.inventory.transaction.InventorySection;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;
import org.lunaris.network.protocol.packet.Packet31InventoryContent;

/**
 * Created by RINES on 02.10.17.
 */
public class CreativeInventory extends Inventory {

    private final ItemStack[] items = new ItemStack[]{
            new ItemStack(Material.STONE, 1, 0),
            new ItemStack(Material.STONE, 1, 1),
            new ItemStack(Material.STONE, 1, 2),
            new ItemStack(Material.STONE, 1, 3),
            new ItemStack(Material.STONE, 1, 4),
            new ItemStack(Material.STONE, 1, 5),
            new ItemStack(Material.STONE, 1, 6),
            new ItemStack(Material.GRASS, 1),
            new ItemStack(Material.DIRT, 1),
            new ItemStack(Material.COBBLESTONE, 1),
            new ItemStack(Material.WOOL, 1, 0),
            new ItemStack(Material.WOOL, 1, 1),
            new ItemStack(Material.WOOL, 1, 2),
            new ItemStack(Material.WOOL, 1, 3),
            new ItemStack(Material.WOOL, 1, 4),
            new ItemStack(Material.WOOL, 1, 5),
            new ItemStack(Material.WOOL, 1, 6),
            new ItemStack(Material.WOOL, 1, 7),
            new ItemStack(Material.WOOL, 1, 8),
            new ItemStack(Material.WOOL, 1, 9),
            new ItemStack(Material.WOOL, 1, 10),
            new ItemStack(Material.WOOL, 1, 11),
            new ItemStack(Material.WOOL, 1, 12),
            new ItemStack(Material.WOOL, 1, 13),
            new ItemStack(Material.WOOL, 1, 14),
            new ItemStack(Material.WOOL, 1, 15),
    };

    public CreativeInventory() {
        super(InventoryType.CREATIVE);
    }

    @Override
    int getReservedInventoryId() {
        return InventorySection.CREATIVE.getId();
    }

    @Override
    public void sendContents(Player player) {
        player.sendPacket(new Packet31InventoryContent(getReservedInventoryId(), this.items));
    }

}
