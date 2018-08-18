package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class SkeletonHorseData extends CreatureData {
    Boolean tamed = null;
    AgeableData ageData = null;

    public SkeletonHorseData(Boolean thisTamed, AgeableData ageData) {
        this.tamed = thisTamed;
        this.ageData = ageData;
    }

    public SkeletonHorseData(String state) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof SkeletonHorse) {
            SkeletonHorse z = (SkeletonHorse) mob;

            if (tamed != null)
                if (tamed)
                    z.setOwner(owner);

            ageData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof SkeletonHorseData))
            return false;

        SkeletonHorseData vd = (SkeletonHorseData) d;

        if (!ageData.matches(vd.ageData))
            return false;
        
        if (this.tamed != null)
            if (this.tamed != vd.tamed)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof SkeletonHorse) {
            return new SkeletonHorseData(((SkeletonHorse) entity).isTamed(), (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("SkeletonHorseData: error, parseFromEntity given different creature - this shouldn't happen.");
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
                if (sub.contains("tamed")) {
                    thisTamed = true;
                    continue;
            	} else if (sub.contains("untamed")) {
            		thisTamed = false;
            		continue;
            	}
            }
        }

        return new SkeletonHorseData(thisTamed, ageData);
    }

    @SuppressWarnings("unused")
	private static CreatureData getData(String state) {
        return new SkeletonHorseData(state);
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
