package org.lunaris.api.inventory;

/**
 * Created by RINES on 12.10.17.
 */
public interface PlayerInventory extends Inventory, EquipmentInventory {

    /**
     * Check whether given slot index is in hotbat.
     * @param index slot index.
     * @return if given slot index is in hotbat.
     */
    boolean isHotbarSlot(int index);

    /**
     * Get slot index of the item in hand.
     * @return index of the item in hand.
     */
    int getItemInHandIndex();

}
