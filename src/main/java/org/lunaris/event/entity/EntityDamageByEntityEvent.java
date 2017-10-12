package org.lunaris.event.entity;

import org.lunaris.api.entity.Entity;
import org.lunaris.api.entity.LivingEntity;
import org.lunaris.entity.damage.DamageCalculus;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;
import org.lunaris.api.util.math.Vector3d;

/**
 * Created by RINES on 24.09.17.
 */
public class EntityDamageByEntityEvent extends Event implements Cancellable {

    private final Entity damager;
    private final LivingEntity victim;
    private double damage;
    private Vector3d victimVelocity;
    private boolean cancelled;

    public EntityDamageByEntityEvent(Entity damager, LivingEntity victim, double damage) {
        this.damager = damager;
        this.victim = victim;
        this.damage = damage;
        this.victimVelocity = DamageCalculus.calculateAttackVelocity(damager, victim);
    }

    public Entity getDamager() {
        return this.damager;
    }

    public LivingEntity getVictim() {
        return this.victim;
    }

    public double getDamage() {
        return this.damage;
    }

    public Vector3d getVictimVelocity() {
        return this.victimVelocity;
    }

    public void setVictimVelocity(Vector3d velocity) {
        this.victimVelocity = velocity;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

}
