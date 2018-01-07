package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class ZombieData extends CreatureData {
    Boolean          villager = null; // Is this zombie a villager? null =
                                      // wildcard
    Boolean          adult    = null; // Zombie's are not "Ageable" - baby/adult
                                      // is handled differently
    LivingEntityData leData   = null;

    public ZombieData(Boolean villager, Boolean adult, LivingEntityData leData) {
        this.villager = villager;
        this.adult = adult;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Zombie) {
            Zombie z = (Zombie) mob;
            if (villager != null)
                if (villager)
                    z.setVillager(true);
            if (adult != null)
                if (!adult)
                    z.setBaby(true);

            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof ZombieData))
            return false;

        ZombieData vd = (ZombieData) d;

        if (this.villager != null)
            if (this.villager != vd.villager)
                return false;
        if (this.adult != null)
            if (this.adult != vd.adult)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Zombie) {
            return new ZombieData(((Zombie) entity).isVillager(),
                    (!((Zombie) entity).isBaby()),
                    (LivingEntityData) LivingEntityData.parseFromEntity(entity));
        } else {
            Log.logInfo("ZombieData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // state example: VILLAGER!BABY, BABY, BABY!NORMAL (order doesn't
        // matter)
        Boolean adult = null;
        Boolean villager = null;
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
                else if (sub.equalsIgnoreCase("villager"))
                    villager = true;
                else if (sub.equalsIgnoreCase("normal"))
                    villager = false;
            }
        }

        return new ZombieData(villager, adult, leData);
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
