package org.lunaris.world.format.test;

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
    protected void save() {
        //Nothing
    }

    @Override
    protected boolean load() {
        return false;
    }
}
