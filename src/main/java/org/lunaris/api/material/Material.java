package org.lunaris.api.material;

import org.lunaris.material.LItemHandle;
import org.lunaris.material.LMaterialHandle;
import org.lunaris.material.block.*;
import org.lunaris.material.block.liquid.BlockLava;
import org.lunaris.material.block.liquid.BlockLavaStill;
import org.lunaris.material.block.liquid.BlockWater;
import org.lunaris.material.block.liquid.BlockWaterStill;
import org.lunaris.material.item.*;
import org.lunaris.material.item.tool.*;

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
    //sapling
    BEDROCK(BlockBedrock.class, 7),
    WATER(BlockWater.class, 8),
    WATER_STILL(BlockWaterStill.class, 9),
    LAVA(BlockLava.class, 10),
    LAVA_STILL(BlockLavaStill.class, 11),
    //sand
    //gravel
    GOLD_ORE(BlockGoldOre.class, 14),
    IRON_ORE(BlockIronOre.class, 15),
    //log
    //leaves
    //sponge
    GLASS(BlockGlass.class, 20),
    //something else
    WOOL(BlockWool.class, 35, true),
    COBWEB(BlockCobweb.class, 30),
    OBSIDIAN(BlockObsidian.class, 49),
    FIRE(BlockFire.class, 51),
    CHEST(BlockChest.class, 54),
    FARM_LAND(BlockFarmland.class, 60),
    NETHER_PORTAL(BlockStone.class, 60), //fix
    GRASS_PATH(BlockGrassPath.class, 198),
    //now items begin:
    IRON_SHOVEL(ItemShovelIron.class, 256, true),
    IRON_PICKAXE(ItemPickaxeIron.class, 257, true),
    IRON_AXE(ItemAxeIron.class, 258, true),
    //some items
    DIAMOND(LItemHandle.class, 264),
    IRON_INGOT(LItemHandle.class, 265),
    GOLD_INGOT(LItemHandle.class, 266),
    IRON_SWORD(ItemSwordIron.class, 267, true),
    WOODEN_SWORD(ItemSwordWooden.class, 268, true),
    WOODEN_SHOVEL(ItemShovelWooden.class, 269, true),
    WOODEN_PICKAXE(ItemPickaxeWooden.class, 270, true),
    WOODEN_AXE(ItemAxeWooden.class, 271, true),
    STONE_SWORD(ItemSwordStone.class, 272, true),
    STONE_SHOVEL(ItemShovelStone.class, 273, true),
    STONE_PICKAXE(ItemPickaxeStone.class, 274, true),
    STONE_AXE(ItemAxeStone.class, 275, true),
    DIAMOND_SWORD(ItemSwordDiamond.class, 276, true),
    DIAMOND_SHOVEL(ItemShovelDiamond.class, 277, true),
    DIAMOND_PICKAXE(ItemPickaxeDiamond.class, 278, true),
    DIAMOND_AXE(ItemAxeDiamond.class, 279, true),
    STICK(LItemHandle.class, 280),
    BOWL(LItemHandle.class, 281),
    //mushroom stew
    GOLDEN_SWORD(ItemSwordGold.class, 283, true),
    GOLDEN_SHOVEL(ItemShovelGold.class, 284, true),
    GOLDEN_PICKAXE(ItemPickaxeGold.class, 285, true),
    GOLDEN_AXE(ItemAxeGold.class, 286, true),
    STRING(ItemString.class, 287),
    FEATHER(LItemHandle.class, 288),
    GUNPOWDER(LItemHandle.class, 289),
    //hoes
    //seeds
    WHEAT(LItemHandle.class, 296),
    //armor
    FLINT(LItemHandle.class, 318),
    //some items
    BUCKET(ItemBucket.class, 325, true),
    //some items
    LEATHER(LItemHandle.class, 334),
    //milk?
    BRICK(LItemHandle.class, 336),
    CLAY_BALL(LItemHandle.class, 337),
//    SUGAR_CANE(ItemHandle.class, "reeds", 338),
    PAPER(LItemHandle.class, 339),
    BOOK(LItemHandle.class, 340),
    SLIME_BALL(LItemHandle.class, 341),
    //some items
    COMPASS(LItemHandle.class, 345),
    //fishing rod
    CLOCK(LItemHandle.class, 347),
    GLOWSTONE_DUST(LItemHandle.class, 348),
    //fishes
    //dyes
    BONE(LItemHandle.class, 352),
    SUGAR(LItemHandle.class, 353),
    //some items
    BLAZE_ROD(LItemHandle.class, 369),
    GHAST_TEAR(LItemHandle.class, 370),
    GOLD_NUGGET(LItemHandle.class, 371),
    //nether wart
    //potions
    //glass bottle
    SPIDER_EYE(LItemHandle.class, 375),
    FERMENTED_SPIDER_EYE(LItemHandle.class, 376),
    BLAZE_POWDER(LItemHandle.class, 377),
    MAGMA_CREAM(LItemHandle.class, 378),
    //some items
    SPECKLED_MELON(LItemHandle.class, 382),
    //some items
    EMERALD(LItemHandle.class, 388),
    //some items
    NETHER_STAR(LItemHandle.class, 399),
    //some items
    NETHER_BRICK(LItemHandle.class, "netherbrick", 405),
    QUARTZ(LItemHandle.class, 406),
    //2 minecarts
    PRISMARINE_SHARD(LItemHandle.class, 409),
    PRISMARINE_CRYSTALS(LItemHandle.class, 410),
    //3 rabbit meals
    RABBIT_FOOT(LItemHandle.class, 414),
    RABBIT_HOE(LItemHandle.class, 415),
    //some items
    IRON_NUGGET(LItemHandle.class, 452);
    //some items

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
                            material.handle = (LMaterialHandle) constructor.newInstance();
                            break constructorCycle;
                        }case 1: {
                            if(constructor.getParameterTypes()[0] == String.class) {
                                constructor.setAccessible(true);
                                material.handle = (LMaterialHandle) constructor.newInstance(material.vanillaMaterialName);
                                break constructorCycle;
                            }else if(constructor.getParameterTypes()[0] == Material.class) {
                                constructor.setAccessible(true);
                                material.handle = (LMaterialHandle) constructor.newInstance(material);
                                break constructorCycle;
                            }
                        }case 2: {
                            Class<?>[] types = constructor.getParameterTypes();
                            if(types[0] == Material.class && types[1] == String.class) {
                                constructor.setAccessible(true);
                                material.handle = (LMaterialHandle) constructor.newInstance(material, material.vanillaMaterialName);
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
    private final Class<? extends LMaterialHandle> handleClass;
    private final String vanillaMaterialName;
    private LMaterialHandle handle;

    Material(Class<? extends LMaterialHandle> handleClass, int id) {
        this(handleClass, null, id);
    }

    Material(Class<? extends LMaterialHandle> handleClass, String vanillaMaterialName, int id) {
        this(handleClass, vanillaMaterialName, id, false);
    }

    Material(Class<? extends LMaterialHandle> handleClass, int id, boolean hasMeta) {
        this(handleClass, null, id, hasMeta);
    }

    Material(Class<? extends LMaterialHandle> handleClass, String vanillaMaterialName, int id, boolean hasMeta) {
        this.id = id;
        this.hasMeta = hasMeta;
        this.handleClass = handleClass;
        this.vanillaMaterialName = vanillaMaterialName == null ? name().toLowerCase() : vanillaMaterialName;
    }

    /**
     * Get this material id.
     * @return this material id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Check whether this material has meta values (if it's max durability is not -1).
     * @return if this material has meta values.
     */
    public boolean hasMeta() {
        return this.hasMeta;
    }

    /**
     * Get this material's handle.
     * @return this material's handle.
     */
    public MaterialHandle getHandle() {
        return this.handle;
    }

}
