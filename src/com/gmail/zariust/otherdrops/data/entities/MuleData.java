package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class MuleData extends CreatureData {
    Boolean tamed = null;
    AgeableData ageData = null;

    public MuleData(Boolean thisTamed, AgeableData ageData) {
        this.tamed = thisTamed;
        this.ageData = ageData;
    }

    public MuleData(String state) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Mule) {
            Mule z = (Mule) mob;

            if (tamed != null)
                if (tamed)
                    z.setOwner(owner);

            ageData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof MuleData))
            return false;

        MuleData vd = (MuleData) d;

        if (!ageData.matches(vd.ageData))
            return false;
        
        if (this.tamed != null)
            if (this.tamed != vd.tamed)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Mule) {
            return new MuleData(((Mule) entity).isTamed(), (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("MuleData: error, parseFromEntity given different creature - this shouldn't happen.");
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

        return new MuleData(thisTamed, ageData);
    }

    private static CreatureData getData(String state) {
        return new MuleData(state);

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
