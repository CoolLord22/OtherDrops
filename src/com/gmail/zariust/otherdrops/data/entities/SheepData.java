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
    Boolean     adult = null;

    public SheepData(Boolean sheared, DyeColor color, Boolean adult) {
        this.sheared = sheared;
        this.color = color;
        if (color != null)
            data = color.getWoolData();
        this.adult = adult;
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
            if (adult != null)
                if (!adult)
                    z.setBaby();
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof SheepData))
            return false;
        
        SheepData vd = (SheepData) d;
        if (this.adult != null)
            if (this.adult != vd.adult)
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
                    (((Sheep) entity).isAdult()));
        } else {
            Log.logInfo("SheepData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        Log.logInfo("SheepData: parsing from string.", Verbosity.HIGHEST);
        Boolean adult = null;
        Boolean sheared = null;
        DyeColor color = null;

        if (!state.isEmpty() && !state.equals("0")) {
            String splitAge[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);
            
            String splitShear[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);
            
            String splitColor[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            
            for (String subAge : splitAge) {
                subAge = subAge.toLowerCase().replaceAll("[\\s-_]", "");
                
                if (subAge.contains("!adult"))
                    adult = true;
                else if (subAge.contains("!baby"))
                    adult = false;
                else {
                	adult = null;
                }
            }
            
            for (String subShear : splitShear) {
            	subShear = subShear.toLowerCase().replaceAll("[\\s-_]", "");

                if (subShear.contains("!sheared"))
                	sheared = true;
                else if (subShear.contains("!unsheared"))
                	sheared = false;
                else {
                	sheared = null;
                }
                	
            }
            
            for (String subColor : splitColor) {
            	subColor = subColor.toLowerCase().replaceAll("[\\s-_]", "");
                if (subColor.contains("!white"))
                	color = DyeColor.WHITE;
                
                else if (subColor.contains("!orange"))
                	color = DyeColor.ORANGE;
                
                else if (subColor.contains("!magenta"))
                	color = DyeColor.MAGENTA;
                
                else if (subColor.contains("!lightblue"))
                	color = DyeColor.LIGHT_BLUE;
                
                else if (subColor.contains("!yellow"))
                	color = DyeColor.YELLOW;
                
                else if (subColor.contains("!lime"))
                	color = DyeColor.LIME;
                
                else if (subColor.contains("!pink"))
                	color = DyeColor.PINK;
                
                else if (subColor.contains("!gray"))
                	color = DyeColor.GRAY;
                
                else if (subColor.contains("!silver"))
                	color = DyeColor.SILVER;
                
                else if (subColor.contains("!cyan"))
                	color = DyeColor.CYAN;
                
                else if (subColor.contains("!purple"))
                	color = DyeColor.PURPLE;
                
                else if (subColor.contains("!blue"))
                	color = DyeColor.BLUE;
                
                else if (subColor.contains("!brown"))
                	color = DyeColor.BROWN;
                
                else if (subColor.contains("!green"))
                	color = DyeColor.GREEN;
                
                else if (subColor.contains("!red"))
                	color = DyeColor.RED;
                
                else if (subColor.contains("!black"))
                	color = DyeColor.BLACK;
                
                else {
                	color = null;
                }
                
            }
        }

        return new SheepData(sheared, color, adult);
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
        if (adult != null) {
            val += "!";
            val += adult ? "ADULT" : "BABY";
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
