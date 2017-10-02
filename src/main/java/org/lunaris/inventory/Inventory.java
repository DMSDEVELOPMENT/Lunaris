package org.lunaris.inventory;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.event.inventory.InventoryCloseEvent;
import org.lunaris.event.inventory.InventoryOpenEvent;
import org.lunaris.event.inventory.InventorySlotChangeEvent;
import org.lunaris.item.ItemStack;
import org.lunaris.material.Material;
import org.lunaris.network.protocol.packet.Packet31InventoryContent;
import org.lunaris.network.protocol.packet.Packet32InventorySlot;

import java.util.*;

/**
 * Created by RINES on 16.09.17.
 */
public abstract class Inventory implements Iterable<ItemStack> {

    private final ItemStack[] items;
    private final InventoryType type;
    private final String title;
    private int maxStackSize = 64;

    private final Set<Player> viewers = new HashSet<>();

    Inventory(InventoryType type) {
        this(type, null);
    }

    Inventory(InventoryType type, String title) {
        this.items = new ItemStack[type.getSize()];
        this.type = type;
        this.title = title == null ? type.getTitle() : title;
    }

    abstract int getReservedInventoryId();

    public InventoryType getType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    public String getName() {
        return getTitle();
    }

    public int size() {
        return this.items.length;
    }

    public int getSize() {
        return size();
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    public int getMaxStackSize(ItemStack is) {
        if(is == null)
            return 0;
        return Math.min(getMaxStackSize(), is.getMaxStackSize());
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public ItemStack getItem(int index) {
        ItemStack is = this.items[index];
        return is == null ? ItemStack.AIR : is;
    }

    public ItemStack[] getContents() {
        return this.items;
    }

    public void setContents(ItemStack[] items) {
        if(items.length > this.items.length)
            throw new IllegalArgumentException("Inventory size < given items array size");
        for(int i = 0; i < items.length; ++i)
            setItem(i, items[i]);
        for(int i = items.length; i < this.items.length; ++i)
            setItem(i, null);
    }

    public void setItem(int index, ItemStack item) {
        setItemWithoutUpdate(index, item);
        sendSlot(this.viewers, index);
    }

    public void setItemWithoutUpdate(int index, ItemStack item) {
        ItemStack previous = getItem(index);
        InventorySlotChangeEvent event = new InventorySlotChangeEvent(this, index, previous, item);
        Lunaris.getInstance().getEventManager().call(event);
        if(event.isCancelled())
            return;
        item = event.getNewItem();
        this.items[index] = item;
    }

    public void sendContents(Player player) {
        int id = player.getInventoryManager().getInventoryId(this);
        if(id == -1) {
            close(player);
            return;
        }
        player.sendPacket(new Packet31InventoryContent(id, this.items));
    }

    public void sendSlot(Collection<Player> players, int index) {
        players.forEach(player -> {
            int id = player.getInventoryManager().getInventoryId(this);
            if(id == -1) {
                close(player);
                return;
            }
            player.sendPacket(new Packet32InventorySlot(id, index, getItem(index)));
        });
    }

    public boolean contains(int id) {
        for(ItemStack is : this.items)
            if(is == null && id == 0 || is != null && is.getType().getId() == id)
                return true;
        return false;
    }

    public boolean contains(Material type) {
        for(ItemStack is : this.items)
            if(type == Material.AIR && is == null || is != null && is.getType() == type)
                return true;
        return false;
    }

    public boolean contains(ItemStack item) {
        for(ItemStack is : this.items)
            if(is == null && (item == null || item.getType() == Material.AIR) || is != null && is.equals(item))
                return true;
        return false;
    }

    public boolean contains(int id, int amount) {
        if(amount <= 0)
            return true;
        int stored = 0;
        for(ItemStack is : this.items)
            if(is != null && is.getType().getId() == id)
                if((stored += is.getAmount()) >= amount)
                    return true;
        return false;
    }

    public boolean contains(Material type, int amount) {
        if(amount <= 0)
            return true;
        int stored = 0;
        for(ItemStack is : this.items)
            if(is != null && is.getType() == type)
                if((stored += is.getAmount()) >= amount)
                    return true;
        return false;
    }

    public boolean contains(ItemStack item, int amount) {
        if(amount <= 0)
            return true;
        int stored = 0;
        for(ItemStack is : this.items)
            if(item.equals(is))
                if((stored += is.getAmount()) >= amount)
                    return true;
        return false;
    }

    public int firstEmpty() {
        for(int i = 0; i < this.items.length; ++i)
            if(this.items[i] == null || this.items[i].getType() == Material.AIR)
                return i;
        return -1;
    }

    public int firstPartial(Material type) {
        if(type == Material.AIR)
            return -1;
        for(int i = 0; i < this.items.length; ++i) {
            ItemStack is = this.items[i];
            if(is != null && is.getType() == type && is.getAmount() < is.getMaxStackSize())
                return i;
        }
        return -1;
    }

    private int firstPartial(ItemStack item) {
        if(item == null || item.getType() == Material.AIR)
            return -1;
        for(int i = 0; i < this.items.length; ++i) {
            ItemStack is = this.items[i];
            if(is != null && is.getAmount() < is.getMaxStackSize() && is.isSimilar(item))
                return i;
        }
        return -1;
    }

    public int first(int id) {
        for(int i = 0; i < this.items.length; ++i) {
            ItemStack is = this.items[i];
            if(is == null && id == 0 || is != null && is.getType().getId() == id)
                return i;
        }
        return -1;
    }

    public int first(Material type) {
        for(int i = 0; i < this.items.length; ++i) {
            ItemStack is = this.items[i];
            if(is == null && type == Material.AIR || is != null && is.getType() == type)
                return i;
        }
        return -1;
    }

    public int first(ItemStack item) {
        return first(item, true);
    }

    public int first(ItemStack item, boolean withAmount) {
        for(int i = 0; i < this.items.length; ++i) {
            ItemStack is = this.items[i];
            if(is == null) {
                if(item == null || item.getType() == Material.AIR)
                    return i;
                continue;
            }
            if(withAmount ? is.equals(item) : is.isSimilar(item))
                return i;
        }
        return -1;
    }

    public Map<Integer, ItemStack> addItem(ItemStack... items) {
        for(ItemStack is : items)
            if(is == null || is.getType() == Material.AIR)
                throw new IllegalArgumentException("Can not add air to the inventory");
        Map<Integer, ItemStack> leftover = new HashMap<>();
        for(int i = 0; i < items.length; ++i) {
            ItemStack item = items[i];
            int max = getMaxStackSize(item);
            while(true) {
                int firstPartial = firstPartial(item);
                if(firstPartial == -1) {
                    int firstFree = firstEmpty();
                    if(firstFree == -1) {
                        leftover.put(i, item);
                        break;
                    }
                    if(item.getAmount() > max) {
                        ItemStack clone = item.clone();
                        clone.setAmount(max);
                        item.setAmount(item.getAmount() - max);
                        setItem(firstFree, clone);
                    }else {
                        setItem(firstFree, item);
                        break;
                    }
                }else {
                    ItemStack partial = getItem(firstPartial);
                    int pamount = partial.getAmount();
                    if(pamount + item.getAmount() <= max) {
                        partial.setAmount(pamount + item.getAmount());
                        setItem(firstPartial, partial);
                        break;
                    }
                    partial.setAmount(max);
                    setItem(firstPartial, partial);
                    item.setAmount(item.getAmount() + pamount - max);
                }
            }
        }
        return leftover;
    }

    public Map<Integer, ItemStack> removeItem(ItemStack... items) {
        for(ItemStack is : items)
            if(is == null || is.getType() == Material.AIR)
                throw new IllegalArgumentException("Can not remove air from the inventory");
        Map<Integer, ItemStack> leftover = new HashMap<>();
        for(int i = 0; i < items.length; ++i) {
            ItemStack item = items[i];
            int deletable = item.getAmount();
            while(true) {
                int first = first(item, false);
                if(first == -1) {
                    item.setAmount(deletable);
                    leftover.put(i, item);
                    break;
                }else {
                    ItemStack stack = getItem(first);
                    if(stack.getAmount() <= deletable) {
                        setItem(first, null);
                        deletable -= stack.getAmount();
                    }else {
                        stack.setAmount(stack.getAmount() - deletable);
                        setItem(first, stack);
                        deletable = 0;
                    }
                }
                if(deletable <= 0)
                    break;
            }
        }
        return leftover;
    }

    public void clear() {
        for(int i = 0; i < this.items.length; ++i)
            setItem(i, null);
    }

    public Collection<Player> getViewers() {
        return this.viewers;
    }

    boolean open(Player player) {
        InventoryOpenEvent event = new InventoryOpenEvent(this, player);
        Lunaris.getInstance().getEventManager().call(event);
        if(event.isCancelled())
            return false;
        this.viewers.add(player);
        return true;
    }

    void close(Player player) {
        InventoryCloseEvent event = new InventoryCloseEvent(this, player);
        Lunaris.getInstance().getEventManager().call(event);
        this.viewers.remove(player);
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return new Iterator<ItemStack>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < items.length;
            }

            @Override
            public ItemStack next() {
                return getItem(this.index++);
            }

        };
    }

}
