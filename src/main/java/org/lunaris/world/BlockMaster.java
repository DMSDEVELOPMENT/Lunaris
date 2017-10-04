package org.lunaris.world;

import org.lunaris.Lunaris;
import org.lunaris.block.Block;
import org.lunaris.block.BlockFace;
import org.lunaris.entity.Player;
import org.lunaris.entity.misc.Gamemode;
import org.lunaris.event.block.BlockBreakEvent;
import org.lunaris.event.block.BlockPlaceEvent;
import org.lunaris.event.player.PlayerHitFireEvent;
import org.lunaris.event.player.PlayerInteractEvent;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;
import org.lunaris.material.BlockHandle;
import org.lunaris.material.ItemHandle;
import org.lunaris.material.Material;
import org.lunaris.network.protocol.packet.Packet15UpdateBlock;
import org.lunaris.network.protocol.packet.Packet18LevelSoundEvent;
import org.lunaris.network.protocol.packet.Packet19LevelEvent;
import org.lunaris.network.protocol.packet.Packet24PlayerAction;
import org.lunaris.server.Scheduler;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.particle.DestroyBlockParticle;
import org.lunaris.world.particle.PunchBlockParticle;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockMaster {

    private final Lunaris server;

    public BlockMaster(Lunaris server) {
        this.server = server;
    }

    public void onRightClickBlock(Player player, BlockVector blockPosition, BlockFace blockFace, Vector3d clickPosition) {
        //check whether can interact at this position
        ItemStack hand = player.getInventory().getItemInHand();
        Block target = player.getWorld().getBlockAt(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
        Block sider = target.getSide(blockFace);
        if(sider.getY() > 255 || sider.getY() < 0 || target.getType() == Material.AIR || player.getGamemode() == Gamemode.SPECTATOR)
            return;
        PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, target);
        this.server.getEventManager().call(event);
        if(event.isCancelled()) {
            //cancel things
            return;
        }
        BlockHandle targetMaterial = target.getHandle();
        if(!player.isSneaking() && targetMaterial.canBeActivated() && targetMaterial.onActivate(target, hand, player))
            return;
        if(hand != null && hand.getType() != Material.AIR) {
            if(hand.isItem()) {
                ItemHandle itemHandle = hand.getItemHandle();
                if(itemHandle.canBeUsed() && itemHandle.useOn(hand, target, blockFace, player))
                    return;
            }else {
                BlockHandle blockHandle = hand.getBlockHandle();
                if(sider.getHandle().canBeReplaced() && blockHandle.canBePlaced()) {
                    BlockPlaceEvent placeEvent = new BlockPlaceEvent(player, hand, new BlockVector(sider.getX(), sider.getY(), sider.getZ()));
                    this.server.getEventManager().call(placeEvent);
                    if(placeEvent.isCancelled()) {
                        return;
                    }
                    if(blockHandle.place(hand, sider, target, blockFace, clickPosition.getX(), clickPosition.getY(), clickPosition.getZ(), player)) {
                        sider.getChunk().sendPacket(new Packet18LevelSoundEvent(Sound.PLACE, sider.getLocation(), hand.getType().getId(), 1, false, false));
                        if(player.getGamemode() != Gamemode.CREATIVE) {
                            hand.setAmount(hand.getAmount() - 1);
                            player.getInventory().setItemInHand(hand.getAmount() == 0 ? null : hand);
                        }
                    }
                }
            }
        }
    }

    public void onBlockStartBreak(Packet24PlayerAction packet) {
        Player player = packet.getPlayer();
        Vector3d position = new Vector3d(packet.getX(), packet.getY(), packet.getZ());
        BlockFace face = BlockFace.fromIndex(packet.getFace());
        World world = player.getWorld();
        if(player.getBreakingBlockTask() != null || player.getLocation().distanceSquared(position) > 100)
            return;
        Block block = world.getBlockAt(position);
        Block sider = block.getSide(face);
        PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, block);
        this.server.getEventManager().call(interactEvent);
        if(interactEvent.isCancelled())
            return;
        if(sider.getType() == Material.FIRE) {
            PlayerHitFireEvent hitFireEvent = new PlayerHitFireEvent(player, block, sider);
            this.server.getEventManager().call(hitFireEvent);
            if(hitFireEvent.isCancelled())
                return;
            sider.setType(Material.AIR);
            return;
        }
        switch(player.getGamemode()) {
            case SURVIVAL: {
                double breakTime = getBreakTimeInTicks(block, player);
                block.getChunk().sendPacket(new Packet19LevelEvent(
                        Packet19LevelEvent.EVENT_BLOCK_START_BREAK,
                        (float) position.x, (float) position.y, (float) position.z,
                        (int) (65535 / breakTime)
                ));
                player.setBreakingBlockTask(this.server.getScheduler().schedule(() -> processBlockBreak(player, block), getExactBreakTimeInMillis(block, player) - Scheduler.ONE_TICK_IN_MILLIS, TimeUnit.MILLISECONDS));
            }case CREATIVE: {
                //not there
                break;
            }default: {
                //shall not happen
                break;
            }
        }
    }

    public void onBlockAbortBreak(Packet24PlayerAction packet) {
        onBlockStopBreak(packet);
    }

    public void onBlockStopBreak(Packet24PlayerAction packet) {
        Player player = packet.getPlayer();
        if (packet.getX() != 0 || packet.getY() != 0 || packet.getZ() != 0) {
            player.getLocation().getChunk().sendPacket(
                new Packet19LevelEvent(
                    Packet19LevelEvent.EVENT_BLOCK_STOP_BREAK,
                    packet.getX(), packet.getY(), packet.getZ(),
                    0
                )
            );
        }
        Scheduler.Task task = player.getBreakingBlockTask();
        if (task != null) {
            task.cancel();
            player.setBreakingBlockTask(null);
        }
    }

    public void onBlockContinueBreak(Packet24PlayerAction packet) {
        Player player = packet.getPlayer();
        if(player.getBreakingBlockTask() == null)
            return;
        Vector3d position = new Vector3d(packet.getX(), packet.getY(), packet.getZ());
        BlockFace face = BlockFace.fromIndex(packet.getFace());
        new PunchBlockParticle(player.getWorld().getBlockAt(position), face).sendToNearbyPlayers();
    }

    public void processBlockBreak(Player player, Block block) {
        processBlockBreak(player, block, true);
    }

    public void processBlockBreak(Player player, Block block, boolean withDrops) {
        //check somewhere whether this block can be broken by players tool
        BlockBreakEvent event = new BlockBreakEvent(player, block);
        this.server.getEventManager().call(event);
        if(event.isCancelled()) {
            player.sendPacket(new Packet15UpdateBlock(block)); //restore block to players
            return;
        }
        if(withDrops) {
            List<ItemStack> drops = block.getHandle().getDrops(block, player.getInventory().getItemInHand());
            if(drops != null)
                drops.forEach(drop -> block.getWorld().dropItem(drop, block.getLocation().add(0D, 1D, 0D)));
        }
        new DestroyBlockParticle(block).sendToNearbyPlayers();
        block.setType(Material.AIR);
        Scheduler.Task task = player.getBreakingBlockTask();
        if (task != null) {
            task.cancel();
            player.setBreakingBlockTask(null);
        }
    }

    private long getExactBreakTimeInMillis(Block block, Player player) {
        if(player.getGamemode() == Gamemode.CREATIVE)
            return 150L;
        return getBreakTimeInMillis(block, player);
    }

    private long getBreakTimeInMillis(Block block, Player player) {
        return (long) (getBreakTime(block, player, player.getInventory().getItemInHand()) * 1000L);
    }

    private double getBreakTimeInTicks(Block block, Player player) {
        return getBreakTime(block, player, player.getInventory().getItemInHand()) * 20D;
    }

    private double getBreakTime(Block block, Player player, ItemStack hand) {
        if(hand == null)
            hand = ItemStack.AIR;
        BlockHandle material = block.getHandle();
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
        if(correctTool)
            speed *= getToolBreakTimeBonus(toolType, tier, isWoolBlock, isCobweb);
        speed += getSpeedBonusByEfficiencyLore(efficiencyLoreLevel);
        speed *= getSpeedRateByHasteLore(hasteEffectLevel);
        if(insideOfWaterWithoutAquaAffinity || !onGround)
            speed *= .2D;
        return 1D / speed;
    }

    private int getToolBreakTimeBonus(ItemToolType toolType, ItemTier tier, boolean wool, boolean cobweb) {
        switch(toolType) {
            case SWORD:
                return cobweb ? 15 : 1;
            case SHEARS:
                return wool ? 5 : 15;
            case NONE:
                return 1;
            default:
                switch(tier) {
                    case WOODEN:
                        return 2;
                    case STONE:
                        return 4;
                    case IRON:
                        return 6;
                    case DIAMOND:
                        return 8;
                    case GOLD:
                        return 12;
                    default:
                        return 1;
                }
        }
    }

    private double getSpeedBonusByEfficiencyLore(int efficiencyLoreLevel) {
        if(efficiencyLoreLevel == 0)
            return 0;
        return efficiencyLoreLevel * efficiencyLoreLevel + 1;
    }

    private double getSpeedRateByHasteLore(int hasteLoreLevel) {
        return 1.0 + (0.2 * hasteLoreLevel);
    }

}
