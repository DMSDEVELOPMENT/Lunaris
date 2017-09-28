package org.lunaris.world;

import org.lunaris.Lunaris;
import org.lunaris.block.Block;
import org.lunaris.block.BlockFace;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.event.block.BlockBreakEvent;
import org.lunaris.material.BlockMaterial;
import org.lunaris.material.Material;
import org.lunaris.entity.Player;
import org.lunaris.event.player.PlayerHitFireEvent;
import org.lunaris.event.player.PlayerInteractEvent;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;
import org.lunaris.network.protocol.packet.Packet15UpdateBlock;
import org.lunaris.network.protocol.packet.Packet19LevelEvent;
import org.lunaris.network.protocol.packet.Packet24PlayerAction;
import org.lunaris.server.Scheduler;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.particle.DestroyBlockParticle;
import org.lunaris.world.particle.PunchBlockParticle;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockMaster {

    private final Lunaris server;

    public BlockMaster(Lunaris server) {
        this.server = server;
    }

    public void onBlockStartBreak(Packet24PlayerAction packet) {
        Player player = packet.getPlayer();
        Vector3d position = new Vector3d(packet.getX(), packet.getY(), packet.getZ());
        BlockFace face = BlockFace.fromIndex(packet.getFace());
        World world = player.getWorld();
        if(player.getLastBreak() != Long.MAX_VALUE || player.getLocation().distanceSquared(position) > 100)
            return;
        Block block = world.getBlockAt(position);
        Block sider = block.getSide(face);
        PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, PlayerInteractEvent.Action.LEFT_CLICK_BLOCK, block);
        this.server.getEventManager().call(interactEvent);
        if(interactEvent.isCancelled())
            return;
        if(sider.getMaterial() == Material.FIRE) {
            PlayerHitFireEvent hitFireEvent = new PlayerHitFireEvent(player, block, sider);
            this.server.getEventManager().call(hitFireEvent);
            if(hitFireEvent.isCancelled())
                return;
            sider.setType(Material.AIR);
            return;
        }
        switch(player.getGamemode()) {
            case CREATIVE: {

                break;
            }case SURVIVAL: {
                double breakTime = getBreakTimeInTicks(block, player);
                block.getChunk().sendPacket(new Packet19LevelEvent(
                        Packet19LevelEvent.EVENT_BLOCK_START_BREAK,
                        (float) position.x, (float) position.y, (float) position.z,
                        (int) (65535 / breakTime)
                ));
                player.setBreakingBlock(block);
                player.setLastBreak(System.currentTimeMillis());
                break;
            }default: {
                //shall not happen
                break;
            }
        }
    }

    public void onBlockAbortBreak(Packet24PlayerAction packet) {
        Player p = packet.getPlayer();
        p.setBreakingBlock(null);
        p.setLastBreak(Long.MAX_VALUE);
        onBlockStopBreak(packet);
    }

    public void onBlockStopBreak(Packet24PlayerAction packet) {
        Player player = packet.getPlayer();
        Vector3d position = new Vector3d(packet.getX(), packet.getY(), packet.getZ());
        player.getWorld().getChunkAt(packet.getX() >> 4, packet.getZ() >> 4).sendPacket(
                new Packet19LevelEvent(
                        Packet19LevelEvent.EVENT_BLOCK_STOP_BREAK,
                        (float) position.x, (float) position.y, (float) position.z,
                        0
                )
        );
        player.setBreakingBlock(null);
    }

    public void onBlockContinueBreak(Packet24PlayerAction packet) {
        Player player = packet.getPlayer();
        if(player.getBreakingBlock() == null)
            return;
        Vector3d position = new Vector3d(packet.getX(), packet.getY(), packet.getZ());
        BlockFace face = BlockFace.fromIndex(packet.getFace());
        new PunchBlockParticle(player.getWorld().getBlockAt(position), face).sendToNearbyPlayers();
    }

    public void tickPlayersBreak(Player player) {
        Block breaking = player.getBreakingBlock();
        if(breaking == null)
            return;
        long breakTime = getBreakTimeInMillis(breaking, player);
        if(player.getGamemode() == Gamemode.CREATIVE && breakTime > 150L)
            breakTime = 150L;
        //check potion effects
        //check item enchantments
        long current = System.currentTimeMillis();
        boolean broken = (player.getGamemode() == Gamemode.CREATIVE || breaking.getSpecifiedMaterial().isBreakable(player.getInventory().getItemInHand()))
                && player.getLastBreak() + breakTime - Scheduler.ONE_TICK_IN_MILLIS <= current;
//        System.out.println((player.getLastBreak() + breakTime - current - Scheduler.ONE_TICK_IN_MILLIS) + "ms left (" + breakTime + "ms total)");
        if(!broken)
            return;
        BlockBreakEvent event = new BlockBreakEvent(player, breaking);
        this.server.getEventManager().call(event);
        if(event.isCancelled()) {
            player.sendPacket(new Packet15UpdateBlock(breaking)); //restore block to players
            return;
        }
        player.setLastBreak(current);
        //drop drops
        new DestroyBlockParticle(breaking).sendToNearbyPlayers();
        breaking.setType(Material.AIR);
    }

    private long getBreakTimeInMillis(Block block, Player player) {
        return (long) (getBreakTime(block, player, player.getInventory().getItemInHand()) * 1000L);
    }

    private double getBreakTimeInTicks(Block block, Player player) {
        return Math.ceil(getBreakTime(block, player, player.getInventory().getItemInHand())) * 20D;
    }

    private double getBreakTime(Block block, Player player, ItemStack hand) {
        if(hand == null)
            hand = ItemStack.AIR;
        BlockMaterial material = block.getSpecifiedMaterial();
        double hardness = material.getHardness();
        boolean correctTool = hand.isOfToolType(material.getRequiredToolType());
        boolean canHarvestWithHand = material.canHarvestWithHand();
        ItemToolType toolType = hand.getToolType();
        ItemTier tier = hand.getTier();
        //handle enchantments
        int efficiencyLoreLevel = 0;
        int hasteEffectLevel = 0;
//        boolean insideOfWaterWithoutAquaAffinity = player.isInsideOfWater();
//        boolean outOfWaterButNotOnGround = !player.isInsideOfWater() && !player.isOnGround();
        boolean insideOfWaterWithoutAquaAffinity = false;
        boolean outOfWaterButNotOnGround = false;
        return getBreakTime(hardness, correctTool, canHarvestWithHand, material.getMaterial(), toolType, tier, efficiencyLoreLevel, hasteEffectLevel, insideOfWaterWithoutAquaAffinity, outOfWaterButNotOnGround);
    }

    private double getBreakTime(double hardness, boolean correctTool, boolean canHarvestWithHand, Material blockType,
                                ItemToolType toolType, ItemTier tier, int efficiencyLoreLevel, int hasteEffectLevel,
                                boolean insideOfWaterWithoutAquaAffinity, boolean outOfWaterButNotOnGround) {
        double baseTime = ((correctTool || canHarvestWithHand) ? 1.5D : 5D) * hardness;
        double speed = 1D / baseTime;
        boolean isWoolBlock = blockType == Material.WOOL, isCobweb = blockType == Material.COBWEB;
        if(correctTool)
            speed *= getToolBreakTimeBonus(toolType, tier, isWoolBlock, isCobweb);
        speed += getSpeedBonusByEfficiencyLore(efficiencyLoreLevel);
        speed *= getSpeedRateByHasteLore(hasteEffectLevel);
        if(insideOfWaterWithoutAquaAffinity || outOfWaterButNotOnGround)
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
