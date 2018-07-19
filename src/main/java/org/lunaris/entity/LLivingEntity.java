package org.lunaris.entity;

import org.lunaris.api.entity.Entity;
import org.lunaris.api.entity.LivingEntity;
import org.lunaris.api.entity.damage.DamageSource;
import org.lunaris.entity.data.Attribute;
import org.lunaris.api.entity.EntityType;
import org.lunaris.api.entity.Gamemode;
import org.lunaris.api.event.entity.EntityDamageByEntityEvent;
import org.lunaris.api.event.entity.EntityDamageEvent;
import org.lunaris.api.event.entity.EntityDeathEvent;
import org.lunaris.api.event.player.PlayerDeathEvent;
import org.lunaris.item.potion.PotionEffect;
import org.lunaris.item.potion.PotionEffectType;
import org.lunaris.network_old.protocol.MinePacket;
import org.lunaris.network.packet.Packet0DAddEntity;
import org.lunaris.network.packet.Packet1BEntityEvent;
import org.lunaris.network.packet.Packet2DRespawn;
import org.lunaris.api.world.Location;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by RINES on 14.09.17.
 */
public abstract class LLivingEntity extends LEntity implements LivingEntity {

    private boolean invulnerable = false;

    protected LLivingEntity(long entityID, EntityType entityType) {
        super(entityID, entityType);
    }

    @Override
    public double getHealth() {
        return getAttribute(Attribute.MAX_HEALTH).getValue();
    }

    @Override
    public double getMaxHealth() {
        return getAttribute(Attribute.MAX_HEALTH).getMaxValue();
    }

    @Override
    public void setHealth(double value) {
        setAttribute(Attribute.MAX_HEALTH, (float) value);
    }

    @Override
    public void setMaxHealth(double value) {
        Attribute a = getAttribute(Attribute.MAX_HEALTH);
        a.setMaxValue((float) value);
        setHealth(a.getValue());
    }

    public void setInvulnerable(boolean value) {
        this.invulnerable = value;
    }

    public boolean isInvulnerable() {
        return this.invulnerable || System.currentTimeMillis() < getCreationTime() + 1000L;
    }

    public void damage(double damage) {
        damage(DamageSource.UNKNOWN, damage);
    }

    public void damage(DamageSource source, double damage) {
        if(isInvulnerable())
            return;
        if(getEntityType() == EntityType.PLAYER) {
            LPlayer p = (LPlayer) this;
            if(p.getGamemode() == Gamemode.CREATIVE)
                return;
        }
        EntityDamageEvent event = new EntityDamageEvent(this, source, damage);
        event.call();
        if(event.isCancelled())
            return;
        damage0(event.getFinalDamage());
    }

    public void damage(Entity damager, double damage) {
        if(isInvulnerable())
            return;
        if(getEntityType() == EntityType.PLAYER) {
            LPlayer p = (LPlayer) this;
            if(p.getGamemode() == Gamemode.CREATIVE)
                return;
        }
        EntityDamageEvent event1 = new EntityDamageEvent(this, DamageSource.ENTITY_ATTACK, damage);
        event1.call();
        if(event1.isCancelled())
            return;
        EntityDamageByEntityEvent event2 = new EntityDamageByEntityEvent(damager, this, event1.getFinalDamage());
        event2.call();
        if(event2.isCancelled())
            return;
        if(damager.getEntityType() == EntityType.PLAYER)
            ((LPlayer) damager).getInventory().decreaseHandDurability();
        if(getEntityType() == EntityType.PLAYER)
            ((LPlayer) this).getInventory().decreaseArmorDurability();
        changeMotion(event2.getVictimVelocity());
        damage0(event2.getDamage());
    }

    void damage0(double damage) {
        setHealth(Math.min(getMaxHealth(), Math.max(0F, (float) (getHealth() - damage))));
        sendPacketToWatchersAndMe(new Packet1BEntityEvent(this, getHealth() <= 0F ? Packet1BEntityEvent.EntityEvent.DEATH_ANIMATION : Packet1BEntityEvent.EntityEvent.HURT_ANIMATION));
    }

    @Override
    public void tick(long current, float dT) {
        super.tick(current, dT);
        if(getHealth() < 1F) {
            if(this instanceof LPlayer) {
                LPlayer p = (LPlayer) this;
                PlayerDeathEvent event = new PlayerDeathEvent(p);
                event.call();
                if(event.isCancelled()) {
                    setHealth(1F);
                    return;
                }
                Location loc = p.getWorld().getSpawnLocation();
                p.sendPacket(new Packet2DRespawn((float) loc.getX(), (float) loc.getY(), (float) loc.getZ())); //чтобы кнопка респавна в клиенте отсылала пакет на сервер
            }else {
                EntityDeathEvent event = new EntityDeathEvent(this);
                event.call();
                remove();
            }
        }
    }

    @Override
    public void fall() {
//        Lunaris.getInstance().broadcastMessage(getFallDistance() + "");
        double damage = getFallDistance() - 3;
        if(damage > 0D)
            damage(DamageSource.FALL, damage);
    }

    @Override
    public MinePacket createSpawnPacket() {
        return new Packet0DAddEntity(this);
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        //TODO:
        return false;
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        //TODO:
        return Collections.emptySet();
    }

    public PotionEffect getPotionEffect(PotionEffectType type) {
        //TODO:
        return null;
    }

}
