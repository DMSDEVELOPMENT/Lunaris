package org.lunaris.entity.data;

import org.lunaris.api.util.math.Vector3d;
import org.lunaris.world.BlockVector;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class IntPositionEntityData extends EntityData<BlockVector> {
    public int x;
    public int y;
    public int z;

    public IntPositionEntityData(int id, int x, int y, int z) {
        super(id);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public IntPositionEntityData(int id, Vector3d pos) {
        this(id, (int) pos.x, (int) pos.y, (int) pos.z);
    }

    @Override
    public BlockVector getData() {
        return new BlockVector(x, y, z);
    }

    @Override
    public void setData(BlockVector data) {
        if (data != null) {
            this.x = data.x;
            this.y = data.y;
            this.z = data.z;
        }
    }

    @Override
    public EntityDataType getType() {
        return EntityDataType.POS;
    }
}
