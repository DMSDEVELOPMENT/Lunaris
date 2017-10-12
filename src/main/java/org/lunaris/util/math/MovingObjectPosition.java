package org.lunaris.util.math;

import org.lunaris.api.util.math.Vector3d;
import org.lunaris.entity.LEntity;

/**
 * Created by RINES on 24.09.17.
 */
public class MovingObjectPosition {
    /**
     * 0 = block, 1 = entity
     */
    public int typeOfHit;

    public int blockX;
    public int blockY;
    public int blockZ;

    /**
     * Which side was hit. If its -1 then it went the full length of the ray trace.
     * Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
     */
    public int sideHit;

    public Vector3d hitVector;

    public LEntity entityHit;

    public static MovingObjectPosition fromBlock(int x, int y, int z, int side, Vector3d hitVector) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 0;
        objectPosition.blockX = x;
        objectPosition.blockY = y;
        objectPosition.blockZ = z;
        objectPosition.hitVector = new Vector3d(hitVector.x, hitVector.y, hitVector.z);
        return objectPosition;
    }

    public static MovingObjectPosition fromEntity(LEntity entity) {
        MovingObjectPosition objectPosition = new MovingObjectPosition();
        objectPosition.typeOfHit = 1;
        objectPosition.entityHit = entity;
        objectPosition.hitVector = new Vector3d(entity.getX(), entity.getY(), entity.getZ());
        return objectPosition;
    }

}
