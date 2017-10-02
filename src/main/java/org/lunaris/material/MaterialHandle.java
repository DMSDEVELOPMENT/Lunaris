package org.lunaris.material;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class MaterialHandle {

    private final Material type;
    private final String name;

    protected MaterialHandle(Material type, String name) {
        this.type = type;
        this.name = name;
    }

    public Material getType() {
        return this.type;
    }

    public int getTypeId() {
        return this.type.getId();
    }

    public String getName(int data) {
        return this.name;
    }

    public abstract boolean isBlock();

    public int getMaxStackSize(int data) {
        return 64;
    }

    public int getAttackDamage() {
        return 1;
    }

    public BlockHandle asBlock() {
        return (BlockHandle) this;
    }

    public ItemHandle asItem() {
        return (ItemHandle) this;
    }

}
