package org.lunaris.inventory;

import org.lunaris.entity.LPlayer;
import org.lunaris.inventory.transaction.InventorySection;
import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.packet.Packet1FMobEquipment;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerInventory extends Inventory {

    private final LPlayer holder;
    private int itemInHandIndex;
    private int[] hotbar = new int[9];

    public PlayerInventory(LPlayer player) {
        super(InventoryType.PLAYER);
        this.holder = player;
        for(int i = 0; i < this.hotbar.length; ++i)
            this.hotbar[i] = i;
    }

    @Override
    int getReservedInventoryId() {
        return InventorySection.INVENTORY.getId();
    }

    @Override
    public int size() {
        return super.size() - 4; //because of armor inventory
    }

    public boolean isHotbarSlot(int index) {
        return index >= 0 && index < this.hotbar.length;
    }

    public int getItemInHandIndex() {
        return this.itemInHandIndex;
    }

    public ItemStack getItemInHand() {
        return getItem(this.itemInHandIndex);
    }

    public void setItemInHand(ItemStack hand) {
        setItem(this.itemInHandIndex, hand);
    }

    public ItemStack getHelmet() {
        return getItem(getSize());
    }

    public void setHelmet(ItemStack item) {
        setItem(getSize(), item);
    }

    public ItemStack getChestplate() {
        return getItem(getSize() + 1);
    }

    public void setChestplate(ItemStack item) {
        setItem(getSize() + 1, item);
    }

    public ItemStack getLeggings() {
        return getItem(getSize() + 2);
    }

    public void setLeggings(ItemStack item) {
        setItem(getSize() + 2, item);
    }

    public ItemStack getBoots() {
        return getItem(getSize() + 3);
    }

    public void setBoots(ItemStack item) {
        setItem(getSize() + 3, item);
    }

    public void updateItemInHandFor(LPlayer... players) {
        ItemStack item = getItemInHand();
        for(LPlayer player : players) {
            if(player == this.holder) {
                sendSlot(Collections.singleton(player), this.itemInHandIndex);
            }else {
                player.sendPacket(new Packet1FMobEquipment(this.holder.getEntityID(), item, this.itemInHandIndex, this.itemInHandIndex, 0));
            }
        }
    }

    public void updateItemInHandFor(Collection<LPlayer> players) {
        updateItemInHandFor(players.toArray(new LPlayer[players.size()]));
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if(index >= getSize()) {
            super.setItemWithoutUpdate(index, item);
            sendArmorSlot(index, item);
        }else
            super.setItem(index, item);
    }

    @Override
    public void setItemWithoutUpdate(int index, ItemStack item) {
        if(index >= getSize()) {
            super.setItemWithoutUpdate(index, item);
            sendArmorSlot(index, item);
        }else
            super.setItemWithoutUpdate(index, item);
    }

    public ItemStack[] getArmorContents() {
        ItemStack[] armor = new ItemStack[4];
        for(int i = 0; i < armor.length; ++i)
            armor[i] = getItem(getSize() + i);
        return armor;
    }

    public void decreaseHandDurability() {
        decreaseDurability(this.itemInHandIndex);
    }

    public void decreaseArmorDurability() {
        for(int i = getSize(); i < getSize() + 4; ++i)
            decreaseDurability(i);
    }

    public void equipItem0(int slot) {
        if(!isHotbarSlot(slot)) {
            sendContents(this.holder);
            return;
        }
        this.itemInHandIndex = slot;
    }

    private void sendArmorSlot(int index, ItemStack item) {
        ItemStack[] armor = getArmorContents();
        //do something
    }

}
