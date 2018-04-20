package com.gmail.zariust.otherdrops;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

/**
 * EntityWrapper: this allows easier changing of health setting methods to revert
 * back to older 1.5.2 support if needed. Need to manually comment out one section
 * and uncomment the other, as well as change Bukkit.jar in the lib folder back
 * to an older version.
 * 
 * Other areas that need focus for building on an older version:
 * PlayerWrapper, HorseData, Stained_Glass/Clay in CommonMaterial class.
 * 
 * @author zarius
 * 
 */
public class EntityWrapper {
    // *********** Version 1.6.1 and later
    // Native support for doubles as health.
    public static void setHealth(LivingEntity ent, double health) {
        ent.setHealth(health);
    }

    public static void setMaxHealth(LivingEntity ent, Double health) {
        ent.setHealth(health);
    }

    public static AttributeInstance getMaxHealth(LivingEntity entity) {
        return entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
    }

    public static void damage(LivingEntity ent, Double damageVal, LivingEntity attacker) {
        ent.damage(damageVal, attacker);
    }

    public static void damage(LivingEntity ent, Double damageVal) {
        ent.damage(damageVal);
    }
    
    
    // *********** Version prior to 1.6.1 (e.g. 1.5.2 support)
    // Note: health values here were integer so need to convert to doubles 

    /*
    public static void setHealth(LivingEntity ent, double health) {
        ent.setHealth((int) (health + 0.5d));
    }

    public static void setMaxHealth(LivingEntity ent, Double health) {
        ent.setHealth((int) (health + 0.5d));

    }

    public static Double getMaxHealth(LivingEntity entity) {
        return Double.valueOf(entity.getMaxHealth());
    }

    public static void damage(LivingEntity ent, Double damageVal, LivingEntity attacker) {
        ent.damage((int) (damageVal + 0.5d), attacker);
    }

    public static void damage(LivingEntity ent, Double damageVal) {
        ent.damage((int) (damageVal + 0.5d));
    } */
}
