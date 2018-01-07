package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

/**
 * Used for Ageable mobs that don't have other data of interest (as at
 * 2013/02/09 - Chicken, Cow, MushroomCow, Pig
 * 
 * @author zarius
 * 
 */
public class AgeableData extends CreatureData {
    Boolean          adult  = null; // null = wildcard
    LivingEntityData leData = null;

    public AgeableData(Boolean adult, LivingEntityData leData) {
        this.adult = adult;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Ageable) {
            Ageable z = (Ageable) mob;
            if (adult != null)
                if (adult == false)
                    z.setBaby();

            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof AgeableData))
            return false;

        AgeableData vd = (AgeableData) d;

        if (this.adult != null)
            if (this.adult != vd.adult)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Ageable) {
            return new AgeableData(((Ageable) entity).isAdult(),
                    (LivingEntityData) LivingEntityData.parseFromEntity(entity));
        } else {
            Log.logInfo("AgeableData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // state example: VILLAGER!BABY, BABY, BABY!NORMAL (order doesn't
        // matter)
        Boolean adult = null;
        LivingEntityData leData = (LivingEntityData) LivingEntityData
                .parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.equalsIgnoreCase("adult"))
                    adult = true;
                else if (sub.equalsIgnoreCase("baby"))
                    adult = false;
            }
        }

        return new AgeableData(adult, leData);
    }

    @Override
    public String toString() {
        String val = "";
        if (adult != null) {
            val += "!";
            val += adult ? "ADULT" : "BABY";
        }
        val += leData.toString();
        return val;
    }

    @Override
    public String get(Enum<?> creature) {
        if (creature instanceof EntityType)
            return this.toString();
        return "";
    }

}
