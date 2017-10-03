package org.lunaris.entity.data;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.event.player.PlayerMoveEvent;
import org.lunaris.network.NetworkManager;
import org.lunaris.network.protocol.packet.Packet13MovePlayer;

import java.util.Collection;

/**
 * Created by RINES on 30.09.17.
 */
public class PlayerMovement extends EntityMovement {

    private float speedX, speedY, speedZ;

    public PlayerMovement(Player player) {
        super(player);
    }

    @Override
    public void update() {
        float dpos = pow2(this.x - this.lastX) + pow2(this.y - this.lastY) + pow2(this.z - this.lastZ);
        float drot = pow2(this.yaw - this.lastYaw) + pow2(this.pitch - this.lastPitch);
        float dmotion = pow2(this.motionX - this.lastMotionX) + pow2(this.motionY - this.lastMotionY) + pow2(this.motionZ - this.lastMotionZ);
        if(dpos > .0001F || drot > 1F) {
            if(addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.headYaw, dpos > 2F)) {
                this.lastX = x;
                this.lastY = y;
                this.lastZ = z;
                this.lastYaw = yaw;
                this.lastPitch = pitch;
            }
            this.speedX = this.lastX - this.x;
            this.speedY = this.lastY - this.y;
            this.speedZ = this.lastZ - this.z;
        }else {
            this.speedX = this.speedY = this.speedZ = 0F;
        }
        if(dmotion > .0025F || dmotion > .0001F && getMotion().lengthSquared() <= .0001F) {
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;
            addMotion(this.motionX, this.motionY, this.motionZ);
        }
    }

    @Override
    protected boolean addMovement(float x, float y, float z, float yaw, float pitch, float headYaw, boolean forced) {
        long current = System.currentTimeMillis();
        if(!forced && current < this.lastMovementTick + NetworkManager.NETWORK_TICK)
            return false;
        this.lastMovementTick = current;
        PlayerMoveEvent event = new PlayerMoveEvent((Player) getEntity(), x, y, z, yaw, pitch);
        Lunaris.getInstance().getEventManager().call(event);
        if(event.isCancelled())
            return false;
        Collection<Player> players = getEntity().getLocation().getChunk().getApplicablePlayers();
        players.remove(getEntity());
        Lunaris.getInstance().getNetworkManager().sendPacket(players, new Packet13MovePlayer(getEntity().getEntityID(), x, y, z, yaw, pitch, headYaw));
        return true;
    }

}
