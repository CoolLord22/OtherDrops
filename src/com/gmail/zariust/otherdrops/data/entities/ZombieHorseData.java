package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class ZombieHorseData extends CreatureData {
    Boolean tamed = null;
    AgeableData ageData = null;

    public ZombieHorseData(Boolean thisTamed, AgeableData ageData) {
        this.tamed = thisTamed;
        this.ageData = ageData;
    }

    public ZombieHorseData(String state) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof ZombieHorse) {
            ZombieHorse z = (ZombieHorse) mob;

            if (tamed != null)
                if (tamed)
                    z.setOwner(owner);

            ageData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof ZombieHorseData))
            return false;

        ZombieHorseData vd = (ZombieHorseData) d;

        if (!ageData.matches(vd.ageData))
            return false;
        
        if (this.tamed != null)
            if (this.tamed != vd.tamed)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof ZombieHorse) {
            return new ZombieHorseData(((ZombieHorse) entity).isTamed(), (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("ZombieHorseData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // return getData(state);
        Boolean thisTamed = null;

        AgeableData ageData = (AgeableData) AgeableData.parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.contains("!tamed")) {
                    thisTamed = true;
                    continue;
            	} else if (sub.contains("!untamed")) {
            		thisTamed = false;
            		continue;
            	}
            }
        }

        return new ZombieHorseData(thisTamed, ageData);
    }

    private static CreatureData getData(String state) {
        return new ZombieHorseData(state);

    }

    @Override
    public String toString() {
        String val = "";
        if (tamed != null) {
            val += "!";
            val += "!!" + (tamed ? "TAME" : "UNTAMED");
        }
        val += ageData.toString();
        return val;
    }

    @Override
    public String get(Enum<?> creature) {
        if (creature instanceof EntityType)
            return this.toString();
        return "";
    }

}
