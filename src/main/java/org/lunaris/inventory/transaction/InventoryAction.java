package org.lunaris.inventory.transaction;

import org.lunaris.entity.LPlayer;
import org.lunaris.api.item.ItemStack;

/**
 * Created by RINES on 01.10.17.
 */
public abstract class InventoryAction {

    private final long creationTime;
    private ItemStack sourceItem;
    private ItemStack targetItem;

    protected InventoryAction(ItemStack sourceItem, ItemStack targetItem) {
        this.sourceItem = sourceItem;
        this.targetItem = targetItem;
        this.creationTime = System.currentTimeMillis();
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public ItemStack getSourceItem() {
        return this.sourceItem.clone();
    }

    public ItemStack getTargetItem() {
        return this.targetItem.clone();
    }

    /**
     * Проверка на то, может ли игрок совершить это действие.
     */
    abstract public boolean isValid(LPlayer source);

    /**
     * Вызывается, если транзакция, часть которой является, признана валидной. Выполняет все server-side действия,
     * реализующие действие. Возвращает true в случае успеха, false в случае отмены плагинами.
     */
    abstract public boolean execute(LPlayer source);

    /**
     * Метод, вызываемый после полного исполнения действия.
     */
    abstract public void onExecuteSuccess(LPlayer source);

    /**
     * Метод, вызываемый в случае, если действие не смогло быть окончено.
     */
    abstract public void onExecuteFail(LPlayer source);

}
