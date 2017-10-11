package org.lunaris.entity.data;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.network.protocol.packet.Packet28SetEntityMotion;

/**
 * Created by RINES on 11.10.17.
 */
public class PlayerMovementData extends MovementData {

    private final Player player;
    private float prevX, prevY, prevZ;
    private float dX, dY, dZ;

    public PlayerMovementData(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public void setMotion(float x, float y, float z) {
        this.player.sendPacket(new Packet28SetEntityMotion(this.player.getEntityID(), x, y, z));
    }

    @Override
    public void setPositionAndRotation(float x, float y, float z, float yaw, float headYaw, float pitch) {
        super.setPositionAndRotation(x, y, z, yaw, headYaw, pitch);
        this.dX = x - this.prevX;
        this.dY = y - this.prevY;
        this.dZ = z = this.prevZ;
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.player.setupFallDistance(this.dY);
    }

    @Override
    public void tickMovement(long current, float dT) {
        double px = super.x, py = super.y, pz = super.z;
        super.tickMovement(current, dT);
//        if(this.motionX != 0 || this.motionY != 0 || this.motionZ != 0)
//            setMotion(this.motionX, this.motionY, this.motionZ);
        double x = super.x, y = super.y, z = super.z;
//        if(LMath.pow2(px - x) + LMath.pow2(py - y) + LMath.pow2(pz - z) >= 1)
//            this.player.sendPacket(new Packet13MovePlayer(this.player).mode(Packet13MovePlayer.MODE_RESET)); //teleporting when lagging
    }

    @Override
    protected void setupFallDistance(float dy) {
        //Handled in another method
    }

}
