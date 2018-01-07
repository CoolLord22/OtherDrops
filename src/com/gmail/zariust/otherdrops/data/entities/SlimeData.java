package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class SlimeData extends CreatureData {
    Integer          slimeSize = null; // null = wildcard
    LivingEntityData leData    = null;

    public SlimeData(Integer type, LivingEntityData leData) {
        this.slimeSize = type;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Slime) {
            Slime z = (Slime) mob;
            if (slimeSize != null)
                z.setSize(slimeSize);
            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof SlimeData))
            return false;
        SlimeData vd = (SlimeData) d;

        if (this.slimeSize != null)
            if (this.slimeSize != vd.slimeSize)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Slime) {
            return new SlimeData(((Slime) entity).getSize(),
                    (LivingEntityData) LivingEntityData.parseFromEntity(entity));
        } else {
            Log.logInfo("SlimeData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        Log.logInfo("SlimeData: parsing from string.", Verbosity.HIGHEST);
        Integer slimeSize = null;
        LivingEntityData leData = (LivingEntityData) LivingEntityData
                .parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.equalsIgnoreCase("TINY"))
                    slimeSize = 1;
                else if (sub.equalsIgnoreCase("SMALL"))
                    slimeSize = 2;
                else if (sub.equalsIgnoreCase("BIG"))
                    slimeSize = 3;
                else if (sub.equalsIgnoreCase("HUGE"))
                    slimeSize = 4;
                else if (sub.matches("[0-9]+"))
                    slimeSize = Integer.valueOf(sub);
            }
        }
        return new SlimeData(slimeSize, leData);
    }

    @Override
    public String toString() {
        String val = "";
        if (slimeSize != null) {
            String slimeSizeMsg = slimeSize.toString();
            // TODO: make this an enum rather than ints to strings?
            if (slimeSize == 0 || slimeSize == 1)
                slimeSizeMsg = "TINY";
            else if (slimeSize == 2)
                slimeSizeMsg = "SMALL";
            else if (slimeSize == 3)
                slimeSizeMsg = "BIG";
            else if (slimeSize == 4)
                slimeSizeMsg = "HUGE";
            val += "!!" + slimeSizeMsg;
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
