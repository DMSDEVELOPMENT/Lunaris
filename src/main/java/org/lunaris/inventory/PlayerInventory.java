package org.lunaris.inventory;

import org.lunaris.entity.Player;
import org.lunaris.inventory.transaction.InventorySection;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;
import org.lunaris.network.protocol.packet.Packet1FMobEquipment;
import org.lunaris.network.protocol.packet.Packet31InventoryContent;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerInventory extends Inventory {

    private final Player holder;
    private int itemInHandIndex;
    private int[] hotbar = new int[9];

    public PlayerInventory(Player player) {
        super(InventoryType.PLAYER);
        this.holder = player;
        for(int i = 0; i < this.hotbar.length; ++i)
            this.hotbar[i] = i;
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

    public void updateItemInHandFor(Player... players) {
        ItemStack item = getItemInHand();
        for(Player player : players) {
            if(player == this.holder) {
                sendSlot(Collections.singleton(player), this.itemInHandIndex);
            }else {
                player.sendPacket(new Packet1FMobEquipment(this.holder.getEntityID(), item, this.itemInHandIndex, this.itemInHandIndex, 0));
            }
        }
    }

    public void updateItemInHandFor(Collection<Player> players) {
        updateItemInHandFor(players.stream().toArray(Player[]::new));
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if(index >= getSize()) {
            setItemWithoutUpdate(index, item);
            sendArmorSlot(index, item);
        }else
            super.setItem(index, item);
    }

    @Override
    public void setItemWithoutUpdate(int index, ItemStack item) {
        if(index >= getSize()) {
            setItemWithoutUpdate(index, item);
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

    public void sendCreativeContents() {
//        Packet31InventoryContent packet = new Packet31InventoryContent(InventorySection.CREATIVE.getId(), new ItemStack[]{
//                new ItemStack(Material.WOOL, 1, 0),
//                new ItemStack(Material.WOOL, 1, 1),
//                new ItemStack(Material.WOOL, 1, 2),
//                new ItemStack(Material.WOOL, 1, 3),
//                new ItemStack(Material.WOOL, 1, 4),
//                new ItemStack(Material.STONE, 1)
//        });
//        this.holder.sendPacket(packet);
    }

    private void sendArmorSlot(int index, ItemStack item) {
        ItemStack[] armor = getArmorContents();
        //do something
    }

}
