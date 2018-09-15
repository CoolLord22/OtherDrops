package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class PigData extends CreatureData {
    Boolean     saddled = null; // null = wildcard
    AgeableData ageData = null;

    public PigData(Boolean saddled, AgeableData ageData) {
        this.saddled = saddled;
        this.ageData = ageData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Pig) {
            Pig z = (Pig) mob;
            if (saddled != null)
                if (saddled)
                    z.setSaddle(true);
            ageData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof PigData))
            return false;
        PigData vd = (PigData) d;

        if (!ageData.matches(vd.ageData))
            return false;

        if (this.saddled != null)
            if (this.saddled != vd.saddled)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Pig) {
            return new PigData(((Pig) entity).hasSaddle(),
                    (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("PigData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // state example: VILLAGER!BABY, BABY, BABY!NORMAL (order doesn't
        // matter)
        Boolean saddled = null;
        AgeableData ageData = (AgeableData) AgeableData.parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.contains("saddled"))
                    saddled = true;
                else if (sub.contains("unsaddled"))
                    saddled = false;
            }
        }

        return new PigData(saddled, ageData);
    }

    @Override
    public String toString() {
        String val = "";
        if (saddled != null) {
            val += "!";
            val += saddled ? "SADDLED" : "UNSADDLED";
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
