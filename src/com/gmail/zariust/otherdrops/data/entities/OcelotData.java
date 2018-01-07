package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class OcelotData extends CreatureData {
    Ocelot.Type type  = null; // null = wildcard
    Boolean     adult = null;
    Boolean     tamed = null;

    public OcelotData(Ocelot.Type type, Boolean adult, Boolean tamed) {
        this.type = type;
        this.adult = adult;
        this.tamed = tamed;
    }

    @Override
    public void setOn(Entity entity, Player owner) {
        if (entity instanceof Ocelot) {
            Ocelot ocelot = (Ocelot) entity;
            if (type != null)
                ocelot.setCatType(type);
            if (adult != null)
                if (!adult)
                    ocelot.setBaby();
            if (tamed != null) {
                if (tamed)
                    ocelot.setOwner(owner);
            } else {
                // default to tamed if we match a "CAT"
                if (type.name().matches(".*_CAT")) {
                    ocelot.setOwner(owner);
                }
            }
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof OcelotData))
            return false;
        OcelotData vd = (OcelotData) d;

        if (this.type != null)
            if (this.type != vd.type)
                return false;
        if (this.adult != null)
            if (this.adult != vd.adult)
                return false;
        if (this.tamed != null)
            if (this.tamed != vd.tamed)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Ocelot) {
            Ocelot ocelot = (Ocelot) entity;
            return new OcelotData(ocelot.getCatType(), ocelot.isAdult(),
                    ocelot.isTamed());
        } else {
            Log.logInfo("OcelotData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // state example: BLACK_CAT!BABY!WILD, or TAME!REDCAT!ADULT (order
        // doesn't matter)
        Boolean adult = null;
        Boolean tamed = null;
        Ocelot.Type thisProf = null;

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "")
                        .replaceAll("cat", "");
                if (sub.equalsIgnoreCase("adult"))
                    adult = true;
                else if (sub.equalsIgnoreCase("baby"))
                    adult = false;
                else if (sub.matches("tame[d]*"))
                    tamed = true;
                else if (sub.equalsIgnoreCase("wild"))
                    tamed = false;
                else {
                    // aliases
                    if (sub.equals("ocelot"))
                        sub = "wildocelot";

                    // loop through types looking for match (remove "cat" so
                    // "black" or "siamese" matches)
                    for (Ocelot.Type type : Ocelot.Type.values()) {
                        if (sub.equals(type.name().toLowerCase()
                                .replaceAll("[\\s-_]", "")
                                .replaceAll("cat", "")))
                            thisProf = type;
                    }
                    if (thisProf == null)
                        Log.logInfo("OcelotData: type not found (" + sub + ")");
                }
            }
        }

        return new OcelotData(thisProf, adult, tamed);
    }

    @Override
    public String toString() {
        String val = "";
        if (type != null)
            val += type.toString();
        if (adult != null) {
            val += "!";
            val += adult ? "ADULT" : "BABY";
        }
        if (tamed != null) {
            val += "!";
            val += tamed ? "TAMED" : "WILD";
        }
        return val;
    }

    @Override
    public String get(Enum<?> creature) {
        if (creature instanceof EntityType)
            return this.toString();
        return "";
    }

}
