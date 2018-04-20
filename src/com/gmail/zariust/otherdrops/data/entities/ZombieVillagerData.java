package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class ZombieVillagerData extends CreatureData {
    Boolean          villager = true; // Is this zombie a villager? null =
                                      // wildcard
    Boolean          adult    = null; // Zombie's are not "Ageable" - baby/adult
                                      // is handled differently
    LivingEntityData leData   = null;

    public ZombieVillagerData(Boolean villager, Boolean adult, LivingEntityData leData) {
        this.villager = villager;
        this.adult = adult;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof ZombieVillager) {
            Zombie z = (ZombieVillager) mob;
            if (adult != null)
                if (!adult)
                    z.setBaby(true);

            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof ZombieVillagerData))
            return false;

        ZombieVillagerData vd = (ZombieVillagerData) d;

        if (this.villager != null)
            if (this.villager != vd.villager)
                return true;
        if (this.adult != null)
            if (this.adult != vd.adult)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof ZombieVillager) {
            return new ZombieVillagerData(true, (!((ZombieVillager) entity).isBaby()), (LivingEntityData) LivingEntityData.parseFromEntity(entity));
        } else {
            Log.logInfo("ZombieVillager: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // state example: VILLAGER!BABY, BABY, BABY!NORMAL (order doesn't
        // matter)
        Boolean adult = null;
        Boolean villager = true;
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

        return new ZombieVillagerData(villager, adult, leData);
    }

    @Override
    public String toString() {
        String val = "";
        if (villager != null) {
            val += OtherDropsConfig.CreatureDataSeparator;
            val += villager ? "VILLAGER" : "NORMAL";
        }
        if (adult != null) {
            val += OtherDropsConfig.CreatureDataSeparator;
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
