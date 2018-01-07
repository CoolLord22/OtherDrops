package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class HorseData extends CreatureData {
    Horse.Color horseColor = null; // null = wildcard
    Horse.Style horseStyle = null; // null = wildcard
    Horse.Variant horseVariant = null; // null = wildcard
    Boolean carryingChest = null;
    Double jumpStrength = null;
    Boolean tamed = null;

    AgeableData ageData = null;

    public HorseData(Horse.Color horseColor, Horse.Style horseStyle, Horse.Variant horseVariant, Boolean thisChest, Double thisJump, Boolean thisTamed, AgeableData ageData) {
        this.horseColor = horseColor;
        this.horseStyle = horseStyle;
        this.horseVariant = horseVariant;
        this.carryingChest = thisChest;
        this.jumpStrength = thisJump;
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
            if (horseVariant != null)
                z.setVariant(horseVariant);
            if (carryingChest != null)
                z.setCarryingChest(carryingChest);
            if (jumpStrength != null)
                z.setJumpStrength(jumpStrength);

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

        if (this.horseVariant != null)
            if (this.horseVariant != vd.horseVariant)
                return false;

        if (this.carryingChest != null)
            if (this.carryingChest != vd.carryingChest)
                return false;

        if (this.jumpStrength != null)
            if (this.jumpStrength != vd.jumpStrength)
                return false;
        
        if (this.tamed != null)
            if (this.tamed != vd.tamed)
                return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Horse) {
            return new HorseData(((Horse) entity).getColor(), ((Horse) entity).getStyle(), ((Horse) entity).getVariant(), ((Horse) entity).isCarryingChest(),
                    ((Horse) entity).getJumpStrength(), ((Horse) entity).isTamed(), (AgeableData) AgeableData.parseFromEntity(entity));
        } else {
            Log.logInfo("HorseData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        // return getData(state);

        Horse.Color thisColor = null; // null = wildcard
        Horse.Style thisStyle = null; // null = wildcard
        Horse.Variant thisVariant = null; // null = wildcard
        Boolean thisChest = null;
        Double thisJump = null;
        Boolean thisTamed = null;

        AgeableData ageData = (AgeableData) AgeableData.parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.matches("[0-9,.]*j")) {
                    thisJump = Double.valueOf(sub.substring(0, sub.length() - 1));
                    continue;
                } else if (sub.equals("chest")) {
                    thisChest = true;
                    continue;
                } else if (sub.equals("nochest")) {
                    thisChest = false;
                    continue;
                } else if (sub.matches("(tame[d]?)")) {
                    thisTamed = true;
                    continue;
                }

                Horse.Color tempColor = null; // null = wildcard
                Horse.Style tempStyle = null; // null = wildcard
                Horse.Variant tempVariant = null; // null = wildcard
                tempColor = matchColor(sub);
                if (tempColor != null && thisColor == null) {
                    thisColor = tempColor;
                    Log.logInfo("HorseData: setting color" + thisColor.toString(), Verbosity.HIGHEST);
                } else {
                    tempStyle = matchStyle(sub);
                    if (tempStyle != null && thisStyle == null) {
                        thisStyle = tempStyle;
                        Log.logInfo("HorseData: setting style" + thisStyle.toString(), Verbosity.HIGHEST);
                    } else {
                        tempVariant = matchVariant(sub);
                        if (tempVariant != null && thisVariant == null) {
                            thisVariant = tempVariant;
                            Log.logInfo("HorseData: setting variant" + thisVariant.toString(), Verbosity.HIGHEST);
                        }
                    }
                }
            }
        }

        return new HorseData(thisColor, thisStyle, thisVariant, thisChest, thisJump, thisTamed, ageData);
    }

    private static CreatureData getData(String state) {
        return new HorseData(state);

    }

    /**
     * @param sub
     */
    public static Horse.Style matchStyle(String sub) {
        for (Horse.Style type : Horse.Style.values()) {
            if (sub.equals(type.name().toLowerCase()
                    .replaceAll("[\\s-_]", "")))
                return type;
        }
        Log.logInfo("HorseData: style not found (" + sub + ")", Verbosity.HIGHEST);
        return null;
    }

    /**
     * @param sub
     */
    public static Horse.Color matchColor(String sub) {
        for (Horse.Color type : Horse.Color.values()) {
            if (sub.equals(type.name().toLowerCase()
                    .replaceAll("[\\s-_]", "")))
                return type;
        }
        Log.logInfo("HorseData: color not found (" + sub + ")", Verbosity.HIGHEST);
        return null;
    }

    /**
     * @param sub
     */
    public static Horse.Variant matchVariant(String sub) {
        for (Horse.Variant type : Horse.Variant.values()) {
            if (sub.equals(type.name().toLowerCase()
                    .replaceAll("[\\s-_]", "")))
                return type;
        }
        Log.logInfo("HorseData: variant not found (" + sub + ")", Verbosity.HIGHEST);
        return null;
    }

    @Override
    public String toString() {
        String val = "";
        if (horseColor != null)
            val += "!!" + horseColor.toString();
        if (horseStyle != null)
            val += "!!" + horseStyle.toString();
        if (horseVariant != null)
            val += "!!" + horseVariant.toString();
        if (carryingChest != null && carryingChest)
            val += "!!CHEST";
        if (carryingChest != null && !carryingChest)
            val += "!!NOCHEST";
        if (jumpStrength != null)
            val += "!!" + jumpStrength.toString() + "j";
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
