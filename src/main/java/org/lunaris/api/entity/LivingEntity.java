package org.lunaris.api.entity;

import org.lunaris.api.entity.damage.DamageSource;

/**
 * Created by RINES on 12.10.17.
 */
public interface LivingEntity extends Entity {

    /**
     * Get this entity's health.
     *
     * @return this entity's health.
     */
    double getHealth();

    /**
     * Get this entity's max health.
     *
     * @return this entity's max health;
     */
    double getMaxHealth();

    /**
     * Set this entity's health.
     *
     * @param health new entity's health value.
     */
    void setHealth(double health);

    /**
     * Set this entity's max health.
     *
     * @param maxHealth new entity's max health value.
     */
    void setMaxHealth(double maxHealth);

    /**
     * Check if this entity can take any damage.
     *
     * @return if this entity can take any damage.
     */
    boolean isInvulnerable();

    /**
     * Set if this entity can take any damage.
     *
     * @return if this entity can take any damage.
     */
    void setInvulnerable(boolean invulnerable);

    /**
     * Deal given amount of damage to this entity.
     *
     * @param damage the amount of damage.
     */
    void damage(double damage);

    /**
     * Deal given amount of damage and of specified damage source to this entity.
     *
     * @param source damage source (type of damage).
     * @param damage the amount of damage.
     */
    void damage(DamageSource source, double damage);

    /**
     * Damage this entity with given amount of damage from given damager-entity.
     *
     * @param damager damager-entity.
     * @param damage  the amount of damage.
     */
    void damage(Entity damager, double damage);

}
