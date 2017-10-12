package org.lunaris.material;

import org.lunaris.api.material.Material;
import org.lunaris.api.material.MaterialHandle;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class LMaterialHandle implements MaterialHandle {

    private final Material type;
    private final String name;

    protected LMaterialHandle(Material type, String name) {
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

    public LBlockHandle asBlock() {
        return (LBlockHandle) this;
    }

    public LItemHandle asItem() {
        return (LItemHandle) this;
    }

}
