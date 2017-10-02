package org.lunaris.material.item;

import org.lunaris.Lunaris;
import org.lunaris.block.Block;
import org.lunaris.block.BlockFace;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.event.player.PlayerBucketEmptyEvent;
import org.lunaris.event.player.PlayerBucketFillEvent;
import org.lunaris.item.ItemStack;
import org.lunaris.material.ItemHandle;
import org.lunaris.material.Material;
import org.lunaris.material.block.LiquidBlock;

/**
 * @author xtrafrancyz
 */
public class ItemBucket extends ItemHandle {
    protected ItemBucket() {
        super(Material.BUCKET, null);
    }

    @Override
    public int getMaxStackSize(int data) {
        return data == 0 ? 16 : 1;
    }

    @Override
    public String getName(int data) {
        switch (data) {
            case 1:
                return "Milk";
            case 8:
                return "Water Bucket";
            case 10:
                return "Lava Bucket";
            default:
                return "Bucket";
        }
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }

    @Override
    public boolean useOn(ItemStack item, Block block, BlockFace face, Player player) {
        // Bucket is empty
        Material containingType = Material.getById(item.getData());
        if (containingType == Material.AIR) {
            // Full liquid block
            if (block.getHandle() instanceof LiquidBlock && block.getData() == 0) {
                PlayerBucketFillEvent event = new PlayerBucketFillEvent(player, block);
                Lunaris.getInstance().getEventManager().call(event);
                if (!event.isCancelled()) {
                    ItemStack result = new ItemStack(Material.BUCKET, 1, getDataFromTarget(block.getType()));
                    block.setType(Material.AIR);
                    if (player.getGamemode() == Gamemode.SURVIVAL) {
                        item.setAmount(item.getAmount() - 1);
                        if(item.getAmount() == 0)
                            player.getInventory().setItemInHand(result);
                        else {
                            player.getInventory().setItemInHand(item);
                            player.getInventory().addItem(result);
                        }
                    }
                    return true;
                } else {
                    player.getInventory().sendContents(player);
                }
            }
        } else if (containingType.getHandle() instanceof LiquidBlock) {
            Block target = block.getSide(face);
            PlayerBucketEmptyEvent event = new PlayerBucketEmptyEvent(player, target);
            Lunaris.getInstance().getEventManager().call(event);
            if (!event.isCancelled()) {
                target.setType(containingType);
                if (player.getGamemode() == Gamemode.SURVIVAL) {
                    item.setAmount(item.getAmount() - 1);
                    if (item.getAmount() == 0)
                        player.getInventory().setItemInHand(null);
                    else
                        player.getInventory().setItemInHand(item);
                    player.getInventory().addItem(new ItemStack(Material.BUCKET));
                }
                return true;
            } else {
                player.getInventory().sendContents(player);
            }
        }
        return false;
    }

    private int getDataFromTarget(Material type) {
        switch (type) {
            case WATER:
            case WATER_STILL:
                return 8;
            case LAVA:
            case LAVA_STILL:
                return 10;
        }
        return 0;
    }
}
