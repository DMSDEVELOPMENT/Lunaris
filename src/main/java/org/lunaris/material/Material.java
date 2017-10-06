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
    STONE(BlockStone.class, 1, true),
    GRASS(BlockGrass.class, 2),
    DIRT(BlockDirt.class, 3),
    COBBLESTONE(BlockCobblestone.class, 4),
    PLANKS(BlockPlanks.class, 5, true),
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
    //now items begin:
    IRON_SHOVEL(ItemShovelIron.class, 256, true),
    IRON_PICKAXE(ItemPickaxeIron.class, 257, true),
    IRON_AXE(ItemAxeIron.class, 258, true),
    //some items
    DIAMOND(ItemHandle.class, 264),
    IRON_INGOT(ItemHandle.class, 265),
    GOLD_INGOT(ItemHandle.class, 266),
    IRON_SWORD(ItemSwordIron.class, 267, true),
    WOODEN_SWORD(ItemSwordWooden.class, 268, true),
    WOODEN_SHOVEL(ItemShovelWooden.class, 269, true),
    WOODEN_PICKAXE(ItemPickaxeWooden.class, 270, true),
    WOODEN_AXE(ItemAxeWooden.class, 271, true),
    //some items
    STRING(ItemString.class, 287),
    BUCKET(ItemBucket.class, 325, true);

    private final static Map<Integer, Material> BY_ID = new HashMap<>();

    static {
        for (Material material : values()) {
            BY_ID.put(material.id, material);
            try {
                constructorCycle:
                for(Constructor<?> constructor : material.handleClass.getDeclaredConstructors()) {
                    switch(constructor.getParameterCount()) {
                        case 0: {
                            constructor.setAccessible(true);
                            material.handle = (MaterialHandle) constructor.newInstance();
                            break constructorCycle;
                        }case 1: {
                            if(constructor.getParameterTypes()[0] == String.class) {
                                constructor.setAccessible(true);
                                material.handle = (MaterialHandle) constructor.newInstance(material.name().toLowerCase());
                                break constructorCycle;
                            }else if(constructor.getParameterTypes()[0] == Material.class) {
                                constructor.setAccessible(true);
                                material.handle = (MaterialHandle) constructor.newInstance(material);
                                break constructorCycle;
                            }
                        }case 2: {
                            Class<?>[] types = constructor.getParameterTypes();
                            if(types[0] == Material.class && types[1] == String.class) {
                                constructor.setAccessible(true);
                                material.handle = (MaterialHandle) constructor.newInstance(material, material.name().toLowerCase());
                                break constructorCycle;
                            }
                        }default:
                            break;
                    }
                    throw new IllegalStateException("Can not find valid constructor for material handle");
                }
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
    private final Class<? extends MaterialHandle> handleClass;
    private MaterialHandle handle;

    Material(Class<? extends MaterialHandle> handleClass, int id) {
        this(handleClass, id, false);
    }

    Material(Class<? extends MaterialHandle> handleClass, int id, boolean hasMeta) {
        this.id = id;
        this.hasMeta = hasMeta;
        this.handleClass = handleClass;
    }

    public int getId() {
        return this.id;
    }

    public boolean hasMeta() {
        return this.hasMeta;
    }

    public MaterialHandle getHandle() {
        return this.handle;
    }

}
