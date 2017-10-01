package org.lunaris.material;

import org.lunaris.material.block.*;
import org.lunaris.material.item.*;

import java.lang.reflect.Constructor;
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
    PLANKS(BlockPlanks.class, 5),
    BEDROCK(BlockBedrock.class, 7),
    WATER(BlockWater.class, 8),
    WATER_STILL(BlockWaterStill.class, 9),
    LAVA(BlockLava.class, 10),
    LAVA_STILL(BlockLavaStill.class, 11),
    WOOL(BlockWool.class, 35, true),
    COBWEB(BlockCobweb.class, 30),
    OBSIDIAN(BlockObsidian.class, 49),
    FIRE(BlockFire.class, 51),
    FARM_LAND(BlockFarmland.class, 60),
    NETHER_PORTAL(BlockStone.class, 60), //fix
    GRASS_PATH(BlockGrassPath.class, 198),
    STRING(ItemString.class, 287);

    private final static Map<Integer, Material> BY_ID = new HashMap<>();

    static {
        for (Material material : values()) {
            BY_ID.put(material.id, material);
            try {
                Constructor<? extends SpecifiedMaterial> constructor = material.matClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                material.specifiedMaterial = constructor.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Material getById(int id) {
        return BY_ID.get(id);
    }

    private final int id;
    private final boolean hasMeta;
    private final Class<? extends SpecifiedMaterial> matClass;
    private SpecifiedMaterial specifiedMaterial;

    Material(Class<? extends SpecifiedMaterial> specifiedMaterialClass, int id) {
        this(specifiedMaterialClass, id, false);
    }

    Material(Class<? extends SpecifiedMaterial> specifiedMaterialClass, int id, boolean hasMeta) {
        this.id = id;
        this.hasMeta = hasMeta;
        this.matClass = specifiedMaterialClass;
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
