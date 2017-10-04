package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.EntityDataOption;
import org.lunaris.entity.misc.EntityType;
import org.lunaris.event.EventManager;
import org.lunaris.event.entity.EntityDamageByEntityEvent;
import org.lunaris.event.entity.EntityDamageEvent;
import org.lunaris.event.entity.EntityDeathEvent;
import org.lunaris.event.player.PlayerDeathEvent;
import org.lunaris.event.player.PlayerRespawnEvent;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.Packet0DAddEntity;

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
        EntityDamageEvent event = new EntityDamageEvent(this, cause, damage);
        Lunaris.getInstance().getEventManager().call(event);
        if(event.isCancelled())
            return;
        damage0(damage);
    }

    public void damage(Entity damager, double damage) {
        EntityDamageEvent event1 = new EntityDamageEvent(this, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        EventManager manager = Lunaris.getInstance().getEventManager();
        manager.call(event1);
        if(event1.isCancelled())
            return;
        EntityDamageByEntityEvent event2 = new EntityDamageByEntityEvent(damager, this, event1.getDamage());
        manager.call(event2);
        if(event2.isCancelled())
            return;
        damage0(event2.getFinalDamage());
    }

    void damage0(double damage) {
        setHealth(Math.min(getMaxHealth(), Math.max(0F, (float) (getHealth() - damage))));
    }

    @Override
    public void tick(long current, float dT) {
        super.tick(current, dT);
        if(getHealth() < 1F) {
            if(this instanceof Player) {
                Player p = (Player) this;
                PlayerDeathEvent event = new PlayerDeathEvent(p);
                Lunaris.getInstance().getEventManager().call(event);
                if(event.isCancelled()) {
                    setHealth(1F);
                    return;
                }
                //экстра-костыль
                PlayerRespawnEvent respawn = new PlayerRespawnEvent(p, p.getWorld().getSpawnLocation());
                Lunaris.getInstance().getEventManager().call(respawn);
                p.respawn(respawn.getLocation());
            }else {
                EntityDeathEvent event = new EntityDeathEvent(this);
                Lunaris.getInstance().getEventManager().call(event);
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
