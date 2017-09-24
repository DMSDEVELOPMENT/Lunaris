package org.lunaris.event.entity;

import org.lunaris.entity.Entity;
import org.lunaris.entity.LivingEntity;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 24.09.17.
 */
public class EntityDamageByEntityEvent extends Event implements Cancellable {

    private final Entity damager;
    private final LivingEntity victim;
    private final double baseDamage;
    private double finalDamage;
    private boolean cancelled;

    public EntityDamageByEntityEvent(Entity damager, LivingEntity victim, double baseDamage) {
        this.damager = damager;
        this.victim = victim;
        this.baseDamage = baseDamage;
        this.finalDamage = calculateFinalDamage();
    }

    private double calculateFinalDamage() {
        return this.baseDamage;
    }

    public Entity getDamager() {
        return damager;
    }

    public LivingEntity getVictim() {
        return victim;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public double getFinalDamage() {
        return finalDamage;
    }

    public void setFinalDamage(double finalDamage) {
        this.finalDamage = finalDamage;
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
