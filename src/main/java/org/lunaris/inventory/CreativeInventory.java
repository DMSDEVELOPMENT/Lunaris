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
            new ItemStack(Material.GRASS),
            new ItemStack(Material.DIRT),
            new ItemStack(Material.COBBLESTONE, 1),
            new ItemStack(Material.PLANKS, 1, 0),
            new ItemStack(Material.PLANKS, 1, 1),
            new ItemStack(Material.PLANKS, 1, 2),
            new ItemStack(Material.PLANKS, 1, 3),
            new ItemStack(Material.PLANKS, 1, 4),
            new ItemStack(Material.PLANKS, 1, 5),
            new ItemStack(Material.BEDROCK),
            new ItemStack(Material.GOLD_ORE),
            new ItemStack(Material.IRON_ORE),
            new ItemStack(Material.GLASS),
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
            new ItemStack(Material.COBWEB),
            new ItemStack(Material.OBSIDIAN),

            new ItemStack(Material.WOODEN_SHOVEL),
            new ItemStack(Material.GOLDEN_SHOVEL),
            new ItemStack(Material.STONE_SHOVEL),
            new ItemStack(Material.IRON_SHOVEL),
            new ItemStack(Material.DIAMOND_SHOVEL),
            new ItemStack(Material.WOODEN_PICKAXE),
            new ItemStack(Material.GOLDEN_PICKAXE),
            new ItemStack(Material.STONE_PICKAXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.DIAMOND_PICKAXE),
            new ItemStack(Material.WOODEN_AXE),
            new ItemStack(Material.GOLDEN_AXE),
            new ItemStack(Material.STONE_AXE),
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.DIAMOND_AXE),
            new ItemStack(Material.WOODEN_SWORD),
            new ItemStack(Material.GOLDEN_SWORD),
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.IRON_SWORD),
            new ItemStack(Material.DIAMOND_SWORD),

            new ItemStack(Material.BUCKET, 1, 0),
            new ItemStack(Material.BUCKET, 1, 1),
            new ItemStack(Material.BUCKET, 1, 8),
            new ItemStack(Material.BUCKET, 1, 10),

            new ItemStack(Material.IRON_INGOT),
            new ItemStack(Material.GOLD_INGOT),
            new ItemStack(Material.DIAMOND),
            new ItemStack(Material.EMERALD),
            new ItemStack(Material.STICK),
            new ItemStack(Material.BOWL),
            new ItemStack(Material.FEATHER),
            new ItemStack(Material.GUNPOWDER),
            new ItemStack(Material.WHEAT),
            new ItemStack(Material.FLINT),
            new ItemStack(Material.LEATHER),
            new ItemStack(Material.BRICK),
            new ItemStack(Material.CLAY_BALL),
            new ItemStack(Material.PAPER),
            new ItemStack(Material.BOOK),
            new ItemStack(Material.SLIME_BALL),
            new ItemStack(Material.COMPASS),
            new ItemStack(Material.CLOCK),
            new ItemStack(Material.GLOWSTONE_DUST),
            new ItemStack(Material.BONE),
            new ItemStack(Material.SUGAR),
            new ItemStack(Material.BLAZE_ROD),
            new ItemStack(Material.GHAST_TEAR),
            new ItemStack(Material.GOLD_NUGGET),
            new ItemStack(Material.IRON_NUGGET),
            new ItemStack(Material.SPIDER_EYE),
            new ItemStack(Material.FERMENTED_SPIDER_EYE),
            new ItemStack(Material.BLAZE_POWDER),
            new ItemStack(Material.MAGMA_CREAM),
            new ItemStack(Material.SPECKLED_MELON),
            new ItemStack(Material.NETHER_STAR),
            new ItemStack(Material.NETHER_BRICK),
            new ItemStack(Material.QUARTZ),
            new ItemStack(Material.PRISMARINE_SHARD),
            new ItemStack(Material.PRISMARINE_CRYSTALS),
            new ItemStack(Material.RABBIT_FOOT),
            new ItemStack(Material.RABBIT_HOE)
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
