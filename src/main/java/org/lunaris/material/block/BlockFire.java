package org.lunaris.material.block;

import org.lunaris.Lunaris;
import org.lunaris.api.entity.Entity;
import org.lunaris.api.world.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.entity.LPlayer;
import org.lunaris.api.entity.Gamemode;
import org.lunaris.event.entity.EntityBurnEvent;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.material.block.liquid.FlowableBlock;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockFire extends FlowableBlock {

    protected BlockFire() {
        super(Material.FIRE, "Fire");
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public boolean isBreakable(ItemStack item) {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public int tickRate(Block block) {
        return 30;
    }

    @Override
    public void onEntityCollide(Block block, Entity entity) {
        //check fire resistance
        if (entity instanceof LPlayer && ((LPlayer) entity).getGamemode() == Gamemode.CREATIVE)
            return;
        EntityBurnEvent event = new EntityBurnEvent(entity, 20 << 3);
        Lunaris.getInstance().getEventManager().call(event);
        if (event.isCancelled())
            return;
        entity.setOnFire(event.getFireTicks());
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.AIR_BLOCK_COLOR;
    }

}
