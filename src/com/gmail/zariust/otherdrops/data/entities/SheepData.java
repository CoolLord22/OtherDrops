package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class SheepData extends CreatureData {
    Boolean     sheared = null; // null = wildcard
    DyeColor    color   = null;
    AgeableData ageData = null;

    public SheepData(Boolean sheared, DyeColor color, AgeableData ageData) {
        this.sheared = sheared;
        this.color = color;
        if (color != null)
            data = color.getWoolData();
        this.ageData = ageData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Sheep) {
            Sheep z = (Sheep) mob;
            if (sheared != null)
                if (sheared)
                    z.setSheared(true);
            if (color != null)
                z.setColor(color);
            ageData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof SheepData))
            return false;
        SheepData vd = (SheepData) d;

        if (!ageData.matches(vd.ageData))
            return false;

        if (this.sheared != null)
            if (this.sheared != vd.sheared)
                return false;

        if (this.color != null)
            if (this.color != vd.color)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Sheep) {
            return new SheepData(((Sheep) entity).isSheared(),
                    ((Sheep) entity).getColor(),
                    (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("SheepData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        Log.logInfo("SheepData: parsing from string.", Verbosity.HIGHEST);
        Boolean sheared = null;
        DyeColor color = null;

        AgeableData ageData = (AgeableData) AgeableData.parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            // TODO:
            // support int and intrange : if(state.startsWith("RANGE")) return
            // RangeData.parse(state);
            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.matches("(sheared|shorn)"))
                    sheared = true;
                else if (sub.matches("(unsheared|unshorn)"))
                    sheared = false;

                try {
                    color = DyeColor.valueOf(sub.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // no need to do anything, leave color = null
                }
            }
        }

        return new SheepData(sheared, color, ageData);
    }

    @Override
    public String toString() {
        String val = "";
        if (sheared != null) {
            val += "!";
            val += sheared ? "SHEARED" : "UNSHEARED";
        }
        if (color != null) {
            val += "!";
            val += color.toString();
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
