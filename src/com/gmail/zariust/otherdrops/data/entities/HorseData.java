package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class HorseData extends CreatureData {
    Horse.Color horseColor = null; // null = wildcard
    Horse.Style horseStyle = null; // null = wildcard
    Boolean tamed = null;

    AgeableData ageData = null;

    public HorseData(Horse.Color horseColor, Horse.Style horseStyle, Boolean thisTamed, AgeableData ageData) {
        this.horseColor = horseColor;
        this.horseStyle = horseStyle;
        this.tamed = thisTamed;
        this.ageData = ageData;
    }

    public HorseData(String state) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Horse) {
            Horse z = (Horse) mob;
            if (horseColor != null)
                z.setColor(horseColor);
            if (horseStyle != null)
                z.setStyle(horseStyle);

            if (tamed != null)
                if (tamed)
                    z.setOwner(owner);

            ageData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof HorseData))
            return false;

        HorseData vd = (HorseData) d;

        if (!ageData.matches(vd.ageData))
            return false;

        if (this.horseColor != null)
            if (this.horseColor != vd.horseColor)
                return false;

        if (this.horseStyle != null)
            if (this.horseStyle != vd.horseStyle)
                return false;
        
        if (this.tamed != null)
            if (this.tamed != vd.tamed)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Horse) {
            return new HorseData(((Horse) entity).getColor(), ((Horse) entity).getStyle(), ((Horse) entity).isTamed(), (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("HorseData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // return getData(state);

        Horse.Color thisColor = null; // null = wildcard
        Horse.Style thisStyle = null; // null = wildcard
        Boolean thisTamed = null;

        AgeableData ageData = (AgeableData) AgeableData.parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.contains("!tamed")) {
                    thisTamed = true;
                }
                if (sub.contains("!untamed")) {
                    thisTamed = false;
                }

                //start color matching
                if (sub.contains("!colorblack")) {
                	thisColor = (Horse.Color.BLACK);
                }
                if (sub.contains("!colorbrown")) {
                	thisColor = (Horse.Color.BROWN);
                }
                if (sub.contains("!colordarkbrown")) {
                	thisColor = (Horse.Color.DARK_BROWN);
                }
                if (sub.contains("!colorchestnut")) {
                	thisColor = (Horse.Color.CHESTNUT);
                }
                if (sub.contains("!colorcreamy")) {
                	thisColor = (Horse.Color.CREAMY);
                }
                if (sub.contains("!colorgray")) {
                	thisColor = (Horse.Color.GRAY);
                }
                if (sub.contains("!colorwhite")) {
                	thisColor = (Horse.Color.WHITE);
                }
                
                if (sub.contains("!styleblackdots")) {
                	thisStyle = (Horse.Style.BLACK_DOTS);
                }
                if (sub.contains("!stylenone")) {
                	thisStyle = (Horse.Style.NONE);
                }
                if (sub.contains("!stylewhite")) {
                	thisStyle = (Horse.Style.WHITE);
                }
                if (sub.contains("!stylewhitefield")) {
                	thisStyle = (Horse.Style.WHITEFIELD);
                }
                if (sub.contains("!stylewhitedots")) {
                	thisStyle = (Horse.Style.WHITE_DOTS);
                }
            }
        }

        return new HorseData(thisColor, thisStyle, thisTamed, ageData);
    }

    @SuppressWarnings("unused")
	private static CreatureData getData(String state) {
        return new HorseData(state);

    }

    @Override
    public String toString() {
        String val = "";
        if (horseColor != null)
            val += "!!" + horseColor.toString();
        if (horseStyle != null)
            val += "!!" + horseStyle.toString();
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
