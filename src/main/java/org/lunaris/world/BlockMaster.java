package org.lunaris.world;

import org.lunaris.LunarisServer;
import org.lunaris.api.entity.Gamemode;
import org.lunaris.api.event.block.BlockBreakEvent;
import org.lunaris.api.event.block.BlockPlaceEvent;
import org.lunaris.api.event.player.PlayerHitFireEvent;
import org.lunaris.api.event.player.PlayerInteractEvent;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemTier;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;
import org.lunaris.api.util.math.Vector3d;
import org.lunaris.api.world.BlockFace;
import org.lunaris.api.world.Sound;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LPlayer;
import org.lunaris.material.LBlockHandle;
import org.lunaris.material.LItemHandle;
import org.lunaris.network.packet.Packet15UpdateBlock;
import org.lunaris.network.packet.Packet18LevelSoundEvent;
import org.lunaris.network.packet.Packet19LevelEvent;
import org.lunaris.network.packet.Packet24PlayerAction;
import org.lunaris.util.math.Vector3f;
import org.lunaris.world.particle.DestroyBlockParticle;
import org.lunaris.world.particle.PunchBlockParticle;

import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockMaster {

    private final LunarisServer server;

    public BlockMaster(LunarisServer server) {
        this.server = server;
    }

    public void onRightClickBlock(LPlayer player, BlockVector blockPosition, BlockFace blockFace, Vector3f clickPosition) {
        //check whether can interact at this position
        ItemStack hand = player.getInventory().getItemInHand();
        LBlock target = player.getWorld().getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
        LBlock sider = target.getSide(blockFace);
        if (sider.getY() > 255 || sider.getY() < 0 || target.getType() == Material.AIR || player.getGamemode() == Gamemode.SPECTATOR)
            return;
        PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, target);
        this.server.getEventManager().call(event);
        if (event.isCancelled()) {
            //cancel things
            return;
        }
        LBlockHandle targetMaterial = target.getHandle();
        if (!player.isSneaking() && targetMaterial.canBeActivated() && targetMaterial.onActivate(target, hand, player))
            return;
        if (hand != null && hand.getType() != Material.AIR) {
            if (hand.isItem()) {
                LItemHandle itemHandle = (LItemHandle) hand.getItemHandle();
                if (itemHandle.canBeUsed() && itemHandle.useOn(hand, target, blockFace, player))
                    return;
            } else {
                LBlockHandle blockHandle = (LBlockHandle) hand.getBlockHandle();
                if (sider.getHandle().canBeReplaced() && blockHandle.canBePlaced()) {
                    BlockPlaceEvent placeEvent = new BlockPlaceEvent(player, hand, new BlockVector(sider.getX(), sider.getY(), sider.getZ()));
                    this.server.getEventManager().call(placeEvent);
                    if (placeEvent.isCancelled()) {
                        return;
                    }
                    if (blockHandle.place(hand, sider, target, blockFace, clickPosition.getX(), clickPosition.getY(), clickPosition.getZ(), player)) {
                        sider.getChunk().sendPacketImmediately(new Packet18LevelSoundEvent(Sound.PLACE, sider.getLocation(), hand.getType().getId(), 1, false, false));
                        if (player.getGamemode() != Gamemode.CREATIVE) {
                            hand.setAmount(hand.getAmount() - 1);
                            player.getInventory().setItemInHand(hand.getAmount() <= 0 ? null : hand);
                        }
                    }
                }
            }
        }
    }

    public void onBlockStartBreak(Packet24PlayerAction packet) {
        LPlayer player = packet.getConnection().getPlayer();
        Vector3d position = new Vector3d(packet.getX(), packet.getY(), packet.getZ());
        BlockFace face = BlockFace.fromIndex(packet.getFace());
        LWorld world = player.getWorld();
        if (player.getLocation().distanceSquared(position) > 100)
            return;
        LBlock block = world.getBlockAt(position);
        LBlock sider = block.getSide(face);
        PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, block);
        this.server.getEventManager().call(interactEvent);
        if (interactEvent.isCancelled())
            return;
        if (sider.getType() == Material.FIRE) {
            PlayerHitFireEvent hitFireEvent = new PlayerHitFireEvent(player, block, sider);
            this.server.getEventManager().call(hitFireEvent);
            if (hitFireEvent.isCancelled())
                return;
            sider.setType(Material.AIR);
            return;
        }
        switch (player.getGamemode()) {
            case SURVIVAL: {
                double breakTime = getBreakTimeInTicks(block, player);
                block.getChunk().sendPacketImmediately(new Packet19LevelEvent(
                        Packet19LevelEvent.EVENT_BLOCK_START_BREAK,
                        (float) position.x, (float) position.y, (float) position.z,
                        (int) (65535 / breakTime)
                ));
                player.getBlockBreakingData().startBreak(getExactBreakTimeInMillis(block, player));
            }
            case CREATIVE: {
                //not there
                break;
            }
            default: {
                //shall not happen
                break;
            }
        }
    }

    public void onBlockAbortBreak(Packet24PlayerAction packet) {
        onBlockStopBreak(packet);
    }

    public void onBlockStopBreak(Packet24PlayerAction packet) {
        LPlayer player = packet.getConnection().getPlayer();
        if (packet.getX() != 0 || packet.getY() != 0 || packet.getZ() != 0) {
            ((LChunk) player.getLocation().getChunk()).sendPacketImmediately(
                    new Packet19LevelEvent(
                            Packet19LevelEvent.EVENT_BLOCK_STOP_BREAK,
                            packet.getX(), packet.getY(), packet.getZ(),
                            0
                    )
            );
        }
        //clear breaking block data?
    }

    public void onBlockContinueBreak(Packet24PlayerAction packet) {
        LPlayer player = packet.getConnection().getPlayer();
        if (!player.isBreakingBlock())
            return;
        Vector3d position = new Vector3d(packet.getX(), packet.getY(), packet.getZ());
        LBlock block = player.getWorld().getBlockAt(position);
        long time = getExactBreakTimeInMillis(block, player);
        if (player.getBlockBreakingData().getBlockBreakingTime() != time) {
            player.getBlockBreakingData().updateBreak(time);
//            double breakTime = getBreakTimeInTicks(block, player) - player.getBlockBreakingData().getOvertime() / 50;
//            block.getChunk().sendPacketImmediately(new Packet19LevelEvent(
//                    Packet19LevelEvent.EVENT_BLOCK_STOP_BREAK,
//                    (float) position.x, (float) position.y, (float) position.z,
//                    0
//            ));
//            block.getChunk().sendPacketImmediately(new Packet19LevelEvent(
//                    Packet19LevelEvent.EVENT_BLOCK_START_BREAK,
//                    (float) position.x, (float) position.y, (float) position.z,
//                    (int) (65535 / breakTime)
//            ));
        }
        BlockFace face = BlockFace.fromIndex(packet.getFace());
        new PunchBlockParticle(player.getWorld().getBlockAt(position), face).sendToNearbyPlayersImmediately();
    }

    public void processBlockBreak(LPlayer player, LBlock block) {
        processBlockBreak(player, block, true);
    }

    public void processBlockBreak(LPlayer player, LBlock block, boolean withDrops) {
        //check somewhere whether this block can be broken by players tool
        BlockBreakEvent event = new BlockBreakEvent(player, block);
        this.server.getEventManager().call(event);
        if (event.isCancelled()) {
            player.sendPacketImmediately(new Packet15UpdateBlock(block)); //restore block to players
            return;
        }
        ItemStack hand = player.getInventory().getItemInHand();
        List<ItemStack> drops = withDrops ? block.getHandle().getDrops(block, hand) : Collections.emptyList();
        new DestroyBlockParticle(block).sendToNearbyPlayersImmediately();
        block.setType(Material.AIR);
        if (drops != null)
            drops.forEach(drop -> block.getWorld().dropItem(drop.clone(), block.getLocation().add(.5D, .5D, .5D)));
        if (!hand.getHandle().isBlock() && hand.getItemHandle().getToolType() != ItemToolType.NONE) {
            hand.setData(hand.getData() + 1);
            player.getInventory().setItemInHand(hand.getData() == hand.getItemHandle().getMaxDurability() ? null : hand);
        }
    }

    private long getExactBreakTimeInMillis(LBlock block, LPlayer player) {
        if (player.getGamemode() == Gamemode.CREATIVE)
            return 150L;
        return getBreakTimeInMillis(block, player);
    }

    private long getBreakTimeInMillis(LBlock block, LPlayer player) {
        return (long) (getBreakTime(block, player, player.getInventory().getItemInHand()) * 1000L);
    }

    private double getBreakTimeInTicks(LBlock block, LPlayer player) {
        return getBreakTime(block, player, player.getInventory().getItemInHand()) * 20D;
    }

    private double getBreakTime(LBlock block, LPlayer player, ItemStack hand) {
        if (hand == null)
            hand = ItemStack.AIR;
        LBlockHandle material = block.getHandle();
        double hardness = material.getHardness();
        boolean correctTool = hand.isOfToolType(material.getRequiredToolType());
        boolean canHarvestWithHand = material.canHarvestWithHand();
        ItemToolType toolType = hand.getToolType();
        ItemTier tier = hand.getTier();
        //handle enchantments
        int efficiencyLoreLevel = 0;
        int hasteEffectLevel = 0;
        boolean insideOfWaterWithoutAquaAffinity = player.isInsideOfWater();
        return getBreakTime(hardness, correctTool, canHarvestWithHand, material.getType(), toolType, tier, efficiencyLoreLevel, hasteEffectLevel, insideOfWaterWithoutAquaAffinity, player.isOnGround());
    }

    private double getBreakTime(double hardness, boolean correctTool, boolean canHarvestWithHand, Material blockType,
                                ItemToolType toolType, ItemTier tier, int efficiencyLoreLevel, int hasteEffectLevel,
                                boolean insideOfWaterWithoutAquaAffinity, boolean onGround) {
        double baseTime = ((correctTool || canHarvestWithHand) ? 1.5D : 5D) * hardness;
        double speed = 1D / baseTime;
        boolean isWoolBlock = blockType == Material.WOOL, isCobweb = blockType == Material.COBWEB;
        if (correctTool)
            speed *= getToolBreakTimeBonus(toolType, tier, isWoolBlock, isCobweb);
        speed += getSpeedBonusByEfficiencyLore(efficiencyLoreLevel);
        speed *= getSpeedRateByHasteLore(hasteEffectLevel);
        if (insideOfWaterWithoutAquaAffinity || !onGround)
            speed *= .2D;
        return 1D / speed;
    }

    private int getToolBreakTimeBonus(ItemToolType toolType, ItemTier tier, boolean wool, boolean cobweb) {
        switch (toolType) {
            case SWORD:
                return cobweb ? 15 : 1;
            case SHEARS:
                return wool ? 5 : 15;
            case NONE:
                return 1;
            default:
                switch (tier) {
                    case WOODEN:
                        return 2;
                    case STONE:
                        return 4;
                    case IRON:
                        return 6;
                    case DIAMOND:
                        return 8;
                    case GOLD:
                        return 16;
                    default:
                        return 1;
                }
        }
    }

    private double getSpeedBonusByEfficiencyLore(int efficiencyLoreLevel) {
        if (efficiencyLoreLevel == 0)
            return 0;
        return efficiencyLoreLevel * efficiencyLoreLevel + 1;
    }

    private double getSpeedRateByHasteLore(int hasteLoreLevel) {
        return 1.0 + (0.2 * hasteLoreLevel);
    }

}
