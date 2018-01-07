package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class WolfData extends CreatureData {
    Boolean     angry       = null; // null = wildcard
    Boolean     tamed       = null;
    DyeColor    collarColor = null;
    AgeableData ageData     = null;

    public WolfData(Boolean angry, Boolean tamed, DyeColor collarColor,
            AgeableData ageData) {
        this.angry = angry;
        this.tamed = tamed;
        this.collarColor = collarColor;
        this.ageData = ageData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Wolf) {
            Wolf z = (Wolf) mob;
            if (angry != null)
                if (angry)
                    z.setAngry(true);
            if (tamed != null)
                if (tamed)
                    z.setOwner(owner);
            if (collarColor != null)
                z.setCollarColor(collarColor);
            ageData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof WolfData))
            return false;
        WolfData vd = (WolfData) d;

        if (!ageData.matches(vd.ageData))
            return false;

        if (this.angry != null)
            if (this.angry != vd.angry)
                return false;

        if (this.tamed != null)
            if (this.tamed != vd.tamed)
                return false;

        if (this.collarColor != null)
            if (this.collarColor != vd.collarColor)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Wolf) {
            return new WolfData(((Wolf) entity).isAngry(),
                    ((Wolf) entity).isTamed(),
                    ((Wolf) entity).getCollarColor(),
                    (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("WolfData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // return new CreatureData(((Wolf)entity).isAngry() ? 1 :
        // (((Wolf)entity).isTamed() ? 2 : 0));
        Boolean angry = null;
        Boolean tamed = null;
        DyeColor collarColor = null;
        AgeableData ageData = (AgeableData) AgeableData.parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.equalsIgnoreCase("angry"))
                    angry = true;
                else if (sub.matches("neutral"))
                    angry = false;
                else if (sub.matches("(tame[d]*)"))
                    tamed = true;
                else if (sub.matches("(untamed|wild)"))
                    tamed = false;
                else {
                    try {
                        collarColor = DyeColor.valueOf(sub.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        // do nothing
                    }
                }
            }
        }

        return new WolfData(angry, tamed, collarColor, ageData);
    }

    @Override
    public String toString() {
        String val = "";
        if (angry != null) {
            val += "!";
            val += angry ? "ANGRY" : "NEUTRAL";
        }
        if (tamed != null) {
            val += "!";
            val += tamed ? "TAME" : "UNTAMED";
        }
        if (collarColor != null) {
            val += "!";
            val += collarColor.name();
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
