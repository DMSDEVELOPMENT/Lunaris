package org.lunaris.material.item;

import org.lunaris.LunarisServer;
import org.lunaris.api.entity.Gamemode;
import org.lunaris.api.entity.Player;
import org.lunaris.api.event.player.PlayerBucketEmptyEvent;
import org.lunaris.api.event.player.PlayerBucketFillEvent;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.BlockFace;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LPlayer;
import org.lunaris.material.LItemHandle;
import org.lunaris.material.block.liquid.LiquidBlock;

/**
 * @author xtrafrancyz
 */
public class ItemBucket extends LItemHandle {

    ItemBucket() {
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
    public boolean useOn(ItemStack item, Block block, BlockFace face, Player p) {
        LPlayer player = (LPlayer) p;
        // Bucket is empty
        Material containingType = Material.getById(item.getData());
        if (containingType == Material.AIR) {
            // Full liquid block
            if (block.getHandle() instanceof LiquidBlock && block.getData() == 0) {
                PlayerBucketFillEvent event = new PlayerBucketFillEvent(player, block);
                LunarisServer.getInstance().getEventManager().call(event);
                if (!event.isCancelled()) {
                    ItemStack result = new ItemStack(Material.BUCKET, 1, getDataFromTarget(block.getType()));
                    block.setType(Material.AIR);
                    if (player.getGamemode() == Gamemode.SURVIVAL) {
                        item.setAmount(item.getAmount() - 1);
                        if (item.getAmount() == 0)
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
            LBlock target = (LBlock) block.getSide(face);
            PlayerBucketEmptyEvent event = new PlayerBucketEmptyEvent(player, target);
            LunarisServer.getInstance().getEventManager().call(event);
            if (!event.isCancelled()) {
                target.setType(containingType);
                if (player.getGamemode() == Gamemode.SURVIVAL) {
                    item.setAmount(item.getAmount() - 1);
                    if (item.getAmount() == 0) {
                        player.getInventory().setItemInHand(new ItemStack(Material.BUCKET));
                    } else {
                        player.getInventory().setItemInHand(item);
                        player.getInventory().addItem(new ItemStack(Material.BUCKET));
                    }
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
