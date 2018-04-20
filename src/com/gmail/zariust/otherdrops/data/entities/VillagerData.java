package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class VillagerData extends CreatureData {
    Profession  prof    = null; // null = wildcard
    AgeableData ageData = null;

    public VillagerData(Profession prof, AgeableData ageData) {
        this.prof = prof;
        this.ageData = ageData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Villager) {
            if (prof != null)
                ((Villager) mob).setProfession(prof);
            ageData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof VillagerData))
            return false;

        VillagerData vd = (VillagerData) d;

        if (!ageData.matches(vd.ageData))
            return false;

        if (this.prof != null)
            if (this.prof != vd.prof)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Villager) {
            return new VillagerData(((Villager) entity).getProfession(),
                    (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("VillagerData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // state example: BLACK_CAT!BABY!WILD, or TAME!REDCAT!ADULT (order
        // doesn't matter)
        @SuppressWarnings("unused")
		Boolean adult = null;
        Profession thisProf = null;
        AgeableData ageData = (AgeableData) AgeableData.parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");

                // loop through types looking for match
                for (Profession type : Profession.values()) {
                    if (sub.equals(type.name().toLowerCase()
                            .replaceAll("[\\s-_]", "")))
                        thisProf = type;
                }
                if (thisProf == null)
                    Log.logInfo("VillagerData: type not found (" + sub + ")");
            }
        }

        return new VillagerData(thisProf, ageData);
    }

    @Override
    public String toString() {
        String val = "";
        if (prof != null)
            val += prof.toString();
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
