package org.lunaris.event.entity;

import org.lunaris.entity.Entity;
import org.lunaris.entity.LivingEntity;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;
import org.lunaris.util.Validate;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by RINES on 24.09.17.
 */
public class EntityDamageByEntityEvent extends Event implements Cancellable {

    private final Entity damager;
    private final LivingEntity victim;
    private double baseDamage;
    private boolean cancelled;

    private Map<EntityDamageEvent.DamageModifier, Double> modifiers;
    private Map<EntityDamageEvent.DamageModifier, Function<Double, Double>> modifierFunctions;

    public EntityDamageByEntityEvent(Entity damager, LivingEntity victim, double baseDamage) {
        this.damager = damager;
        this.victim = victim;
        setBaseDamage(baseDamage);
    }

    public void setBaseDamage(double damage) {
        this.baseDamage = damage;
        calculateFinalDamage();
    }

    public double getFinalDamage() {
        double damage = 0D;
        for(EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values())
            damage += getDamage(modifier);
        return Math.max(0D, damage);
    }

    public double getDamage(EntityDamageEvent.DamageModifier modifier) {
        Validate.notNull(modifier, "Cannot have null DamageModifier");
        Double damage = this.modifiers.get(modifier);
        return damage == null ? 0D : damage;
    }

    public Entity getDamager() {
        return this.damager;
    }

    public LivingEntity getVictim() {
        return this.victim;
    }

    public double getBaseDamage() {
        return this.baseDamage;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    private void calculateFinalDamage() {
        if(this.modifiers != null)
            this.modifiers.clear();
        if(this.modifierFunctions != null)
            this.modifierFunctions.clear();
        setupModifiers();
    }

    private void setupModifiers() {

    }

}
