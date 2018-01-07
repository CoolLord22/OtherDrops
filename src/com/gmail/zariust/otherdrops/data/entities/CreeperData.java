package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class CreeperData extends CreatureData {
    Creeper          dummy;         // used to represent main Entity class for
                                     // this data object
    Boolean          powered = null; // null = wildcard
    LivingEntityData leData  = null;

    public CreeperData(Boolean powered, LivingEntityData leData) {
        this.powered = powered;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Creeper) {
            if (powered != null)
                if (powered) {
                    ((Creeper) mob).setPowered(true);
                }
            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof CreeperData))
            return false;

        CreeperData vd = (CreeperData) d;
        if (this.powered != null)
            if (this.powered != vd.powered)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity == null)
            return null;
        if (entity instanceof Creeper) {
            return new CreeperData(((Creeper) entity).isPowered(),
                    (LivingEntityData) LivingEntityData.parseFromEntity(entity));
        } else {
            Log.logInfo("CreeperData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        Boolean powered = null;
        LivingEntityData leData = (LivingEntityData) LivingEntityData
                .parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.equalsIgnoreCase("powered"))
                    powered = true;
                if (sub.equalsIgnoreCase("unpowered"))
                    powered = false;
            }
        }

        return new CreeperData(powered, leData);
    }

    @Override
    public String toString() {
        String val = "";
        if (powered != null) {
            val += powered ? "POWERED" : "UNPOWERED";
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
