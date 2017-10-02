package org.lunaris.world.format.test;

import org.lunaris.material.Material;
import org.lunaris.world.Chunk;
import org.lunaris.world.World;

/**
 * Created by RINES on 14.09.17.
 */
public class TestChunk extends Chunk {

    public TestChunk(World world, int x, int z) {
        super(world, x, z);
    }

    @Override
    protected void save0() {
        //Nothing
    }

    @Override
    protected void load0() {
        generate();
    }

    private void generate() {
        if (Math.abs(this.x) < 2 && Math.abs(this.z) < 12) {
            for (int x = 0; x < 16; ++x)
                for (int z = 0; z < 16; ++z) {
                    setBlock(x, 32, z, x == 7 || x == 8 || z == 7 || z == 8 ? Material.STONE : Material.GRASS);
                    setBlock(x, 31, z, x == 7 || x == 8 || z == 7 || z == 8 ? Material.STONE : Material.DIRT);
                    setBlock(x, 30, z, Material.DIRT);
                }
            setBlock(0, 32, 0, Material.WOOL, 1);
            setBlock(0, 32, 15, Material.WOOL, 2);
            setBlock(15, 32, 0, Material.WOOL, 3);
            setBlock(15, 32, 15, Material.WOOL, 4);
        }
        if (this.x == 1 && this.z == 0) {
            setBlock(0, 32, 9, Material.WATER_STILL);
            setBlock(0, 32, 11, Material.WATER_STILL);

            setBlock(5, 32, 9, Material.WATER);
            setBlock(5, 32, 11, Material.WATER);

            setBlock(10, 32, 9, Material.LAVA_STILL);
            setBlock(10, 32, 11, Material.LAVA_STILL);
        }
    }
}
