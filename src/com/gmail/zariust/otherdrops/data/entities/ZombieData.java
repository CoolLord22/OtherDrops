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
    Boolean          adult    = null; // Zombie's are not "Ageable" - baby/adult
                                      // is handled differently
    LivingEntityData leData   = null;

    public ZombieData(Boolean adult, LivingEntityData leData) {
        this.adult = adult;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Zombie) {
            Zombie z = (Zombie) mob;
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

        if (this.adult != null)
            if (this.adult != vd.adult)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Zombie) {
            return new ZombieData((!((Zombie) entity).isBaby()),
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
        LivingEntityData leData = (LivingEntityData) LivingEntityData
                .parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.contains("!adult"))
                    adult = true;
                else if (sub.contains("!baby"))
                    adult = false;
            }
        }

        return new ZombieData(adult, leData);
    }

    @Override
    public String toString() {
        String val = "";
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
