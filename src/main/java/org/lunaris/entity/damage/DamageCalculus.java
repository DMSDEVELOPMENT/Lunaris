package org.lunaris.entity.damage;

import org.lunaris.api.entity.Entity;
import org.lunaris.api.entity.LivingEntity;
import org.lunaris.api.entity.damage.DamageSource;
import org.lunaris.entity.LLivingEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.api.entity.EntityType;
import org.lunaris.inventory.PlayerInventory;
import org.lunaris.api.item.ItemStack;
import org.lunaris.item.potion.PotionEffect;
import org.lunaris.item.potion.PotionEffectType;
import org.lunaris.api.material.Material;
import org.lunaris.api.util.math.Vector3d;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by RINES on 11.10.17.
 */
public class DamageCalculus {

    public static double calculateIncomingDamage(LivingEntity victim, DamageSource source, double damage) {
        damage = applyArmorModifiers(victim, source, damage);
        damage = applyPotionModifiers(victim, source, damage);
        damage = applyEnchantmentsModifiers(victim, source, damage);
        return Math.max(0F, damage);
    }

    public static Vector3d calculateAttackVelocity(Entity damager, LivingEntity victim) {
        if(damager.getWorld() != victim.getWorld())
            return new Vector3d(0D, 0D, 0D);
        int knockbackLevel = 5;
        int sprinting = 0;
        if(damager.getEntityType() == EntityType.PLAYER) {
            LPlayer p = (LPlayer) damager;
            ItemStack hand = p.getInventory().getItemInHand();
            if(hand != null && hand.getType() != Material.AIR) {
                //check if hand has knockback enchantment
            }
            if(p.isSprinting())
                sprinting = 1;
        }
        double amplitude = sprinting + knockbackLevel;
        Vector3d velocity = victim.getLocation().subtract(damager.getLocation()).normalize().multiply(amplitude * 0.1);
        velocity.y += 0.2;
        velocity.y = Math.max(-0.4, Math.min(0.5, velocity.y));
        return velocity;
    }

    private static double applyArmorModifiers(LivingEntity victim, DamageSource source, double damage) {
        if(source.isBypassesArmor())
            return damage;
        int armor = 0;
        if(victim.getEntityType() == EntityType.PLAYER) {
            PlayerInventory inventory = ((LPlayer) victim).getInventory();
            for(ItemStack is : inventory.getArmorContents()) {
                if(is != null && is.isItem())
                    armor += is.getItemHandle().getArmorPoints();
            }
        }
        return damage * (25F - armor) / 25F;
    }

    private static double applyPotionModifiers(LivingEntity v, DamageSource source, double damage) {
        if(source.isPure())
            return damage;
        LLivingEntity victim = (LLivingEntity) v;
        PotionEffect effect = victim.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        if (effect == null)
            return damage;
        return Math.max(0D, damage * (25F - effect.getLevel() * 5) / 25F);
    }

    private static double applyEnchantmentsModifiers(LivingEntity victim, DamageSource source, double damage) {
        if(source.isPure())
            return damage;
        int modifier = Math.min(20, getEnchantmentModifier(victim, source));
        return modifier > 0 ? damage * (25F - modifier) / 25F : damage;
    }

    private static int getEnchantmentModifier(LivingEntity victim, DamageSource source) {
        if(victim.getEntityType() == EntityType.PLAYER)
            return getEnchantmentModifier(((LPlayer) victim).getInventory().getArmorContents(), source);
        return 0;
    }

    private static int getEnchantmentModifier(ItemStack[] armor, DamageSource source) {
        int modifier = 0;
        for(ItemStack item : armor)
            modifier += getEnchantmentModifier(item, source);
        if(modifier > 25)
            modifier = 25;
        else if(modifier < 0)
            modifier = 0;
        return (modifier + 1 >> 1) + ThreadLocalRandom.current().nextInt((modifier >> 1) + 1);
    }

    private static int getEnchantmentModifier(ItemStack item, DamageSource source) {
        if(item == null || item.getType() == Material.AIR)
            return 0;
        //TODO:
        return 0;
    }

}
