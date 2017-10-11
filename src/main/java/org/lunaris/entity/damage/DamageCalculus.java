package org.lunaris.entity.damage;

import org.lunaris.entity.Entity;
import org.lunaris.entity.LivingEntity;
import org.lunaris.entity.Player;
import org.lunaris.entity.misc.EntityType;
import org.lunaris.inventory.PlayerInventory;
import org.lunaris.item.ItemStack;
import org.lunaris.item.potion.PotionEffect;
import org.lunaris.item.potion.PotionEffectType;
import org.lunaris.material.Material;
import org.lunaris.util.math.Vector3d;

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
        int knockbackLevel = 0;
        int sprinting = 0;
        if(damager.getEntityType() == EntityType.PLAYER) {
            Player p = (Player) damager;
            ItemStack hand = p.getInventory().getItemInHand();
            if(hand != null && hand.getType() != Material.AIR) {
                //check if hand has knockback enchantment
            }
            if(p.isSprinting())
                sprinting = 1;
        }
        double amplitude = sprinting + knockbackLevel;
        return damager.getLocation().subtract(victim.getLocation()).normalize().multiply(amplitude);
    }

    private static double applyArmorModifiers(LivingEntity victim, DamageSource source, double damage) {
        if(source.isBypassesArmor())
            return damage;
        int armor = 0;
        if(victim.getEntityType() == EntityType.PLAYER) {
            PlayerInventory inventory = ((Player) victim).getInventory();
            for(ItemStack is : inventory.getArmorContents()) {
                if(is != null)
                    armor += is.getItemHandle().getArmorPoints();
            }
        }
        return damage * (25F - armor) / 25F;
    }

    private static double applyPotionModifiers(LivingEntity victim, DamageSource source, double damage) {
        if(source.isPure())
            return damage;
        PotionEffect effect = victim.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
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
            return getEnchantmentModifier(((Player) victim).getInventory().getArmorContents(), source);
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
