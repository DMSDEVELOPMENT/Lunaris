package org.lunaris.entity;

import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.misc.EntityType;
import org.lunaris.entity.misc.Gamemode;
import org.lunaris.event.entity.EntityDamageByEntityEvent;
import org.lunaris.event.entity.EntityDamageEvent;
import org.lunaris.event.entity.EntityDeathEvent;
import org.lunaris.event.player.PlayerDeathEvent;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.Packet0DAddEntity;
import org.lunaris.network.protocol.packet.Packet1BEntityEvent;
import org.lunaris.network.protocol.packet.Packet2DRespawn;
import org.lunaris.world.Location;

/**
 * Created by RINES on 14.09.17.
 */
public abstract class LivingEntity extends Entity {

    protected LivingEntity(long entityID, EntityType entityType) {
        super(entityID, entityType);
    }

    public float getHealth() {
        return getAttribute(Attribute.MAX_HEALTH).getValue();
    }

    public float getMaxHealth() {
        return getAttribute(Attribute.MAX_HEALTH).getMaxValue();
    }

    public void setHealth(float value) {
        setAttribute(Attribute.MAX_HEALTH, value);
    }

    public void setMaxHealth(float value) {
        Attribute a = getAttribute(Attribute.MAX_HEALTH);
        a.setMaxValue(value);
        setHealth(a.getValue());
    }

    public void damage(double damage) {
        damage(EntityDamageEvent.DamageCause.UNKNOWN, damage);
    }

    public void damage(EntityDamageEvent.DamageCause cause, double damage) {
        if(getEntityType() == EntityType.PLAYER) {
            Player p = (Player) this;
            if(p.getGamemode() == Gamemode.CREATIVE || p.isInvulnerable())
                return;
        }
        EntityDamageEvent event = new EntityDamageEvent(this, cause, damage);
        event.call();
        if(event.isCancelled())
            return;
        damage0(damage);
    }

    public void damage(Entity damager, double damage) {
        if(getEntityType() == EntityType.PLAYER) {
            Player p = (Player) this;
            if(p.getGamemode() == Gamemode.CREATIVE || p.isInvulnerable())
                return;
        }
        EntityDamageEvent event1 = new EntityDamageEvent(this, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        event1.call();
        if(event1.isCancelled())
            return;
        EntityDamageByEntityEvent event2 = new EntityDamageByEntityEvent(damager, this, event1.getDamage());
        event2.call();
        if(event2.isCancelled())
            return;
        damage0(event2.getFinalDamage());
    }

    void damage0(double damage) {
        setHealth(Math.min(getMaxHealth(), Math.max(0F, (float) (getHealth() - damage))));
        sendPacketToNearbyPlayers(new Packet1BEntityEvent(this, getHealth() <= 0F ? Packet1BEntityEvent.EntityEvent.DEATH_ANIMATION : Packet1BEntityEvent.EntityEvent.HURT_ANIMATION));
    }

    @Override
    public void tick(long current, float dT) {
        super.tick(current, dT);
        if(getHealth() < 1F) {
            if(this instanceof Player) {
                Player p = (Player) this;
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
        double damage = getFallDistance() - 3;
        if(damage > 0D)
            damage(EntityDamageEvent.DamageCause.FALL, damage);
    }

    @Override
    public MinePacket createSpawnPacket() {
        return new Packet0DAddEntity(this);
    }

}
