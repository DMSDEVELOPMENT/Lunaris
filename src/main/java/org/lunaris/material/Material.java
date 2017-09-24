package org.lunaris.material;

import org.lunaris.material.block.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RINES on 13.09.17.
 */
public enum Material {
    AIR(BlockAir.class, 0),
    STONE(BlockStone.class, 1),
    GRASS(BlockGrass.class, 2),
    DIRT(BlockDirt.class, 3),
    COBBLESTONE(BlockCobblestone.class, 4),
    WOOL(BlockDirt.class, 35), //fix
    COBWEB(BlockDirt.class, -1), //fix
    FIRE(BlockFire.class, 51),
    FARM_LAND(BlockFarmland.class, 60),
    GRASS_PATH(BlockGrassPath.class, 198);

    private final static Map<Integer, Material> BY_ID = new HashMap<>();

    static {
        for(Material material : values())
            BY_ID.put(material.id, material);
    }

    public static Material getById(int id) {
        return BY_ID.get(id);
    }

    private final int id;
    private final boolean hasMeta;
    private final SpecifiedMaterial specifiedMaterial;

    Material(Class<? extends SpecifiedMaterial> specifiedMaterialClass, int id) {
        this(specifiedMaterialClass, id, false);
    }

    Material(Class<? extends SpecifiedMaterial> specifiedMaterialClass, int id, boolean hasMeta) {
        this.id = id;
        this.hasMeta = hasMeta;
        try {
            this.specifiedMaterial = specifiedMaterialClass.newInstance();
        }catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public int getId() {
        return this.id;
    }

    public boolean hasMeta() {
        return this.hasMeta;
    }

    public SpecifiedMaterial getSpecifiedMaterial() {
        return this.specifiedMaterial;
    }

}
