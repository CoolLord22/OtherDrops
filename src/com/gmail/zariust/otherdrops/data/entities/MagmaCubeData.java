package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.MagmaCube;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class MagmaCubeData extends CreatureData {
    Integer          MagmaCubeSize = null; // null = wildcard
    LivingEntityData leData        = null;

    public MagmaCubeData(Integer type, LivingEntityData leData) {
        this.MagmaCubeSize = type;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof MagmaCube) {
            MagmaCube z = (MagmaCube) mob;
            if (MagmaCubeSize != null)
                z.setSize(MagmaCubeSize);
            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof MagmaCubeData))
            return false;
        MagmaCubeData vd = (MagmaCubeData) d;

        if (this.MagmaCubeSize != null)
            if (this.MagmaCubeSize != vd.MagmaCubeSize)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof MagmaCube) {
            return new MagmaCubeData(((MagmaCube) entity).getSize(),
                    (LivingEntityData) LivingEntityData.parseFromEntity(entity));
        } else {
            Log.logInfo("MagmaCubeData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        Log.logInfo("MagmaCubeData: parsing from string.", Verbosity.HIGHEST);
        Integer MagmaCubeSize = null;
        LivingEntityData leData = (LivingEntityData) LivingEntityData
                .parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.equalsIgnoreCase("TINY"))
                    MagmaCubeSize = 1;
                else if (sub.equalsIgnoreCase("SMALL"))
                    MagmaCubeSize = 2;
                else if (sub.equalsIgnoreCase("BIG"))
                    MagmaCubeSize = 3;
                else if (sub.equalsIgnoreCase("HUGE"))
                    MagmaCubeSize = 4;
            }
        }

        return new MagmaCubeData(MagmaCubeSize, leData);
    }

    @Override
    public String toString() {
        String val = "";
        if (MagmaCubeSize != null) {
            String MagmaCubeSizeMsg = MagmaCubeSize.toString();
            // TODO: make this an enum rather than ints to strings?
            if (MagmaCubeSize == 0 || MagmaCubeSize == 1)
                MagmaCubeSizeMsg = "TINY";
            else if (MagmaCubeSize == 2)
                MagmaCubeSizeMsg = "SMALL";
            else if (MagmaCubeSize == 3)
                MagmaCubeSizeMsg = "BIG";
            else if (MagmaCubeSize == 4)
                MagmaCubeSizeMsg = "HUGE";
            val += "!!" + MagmaCubeSizeMsg;
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
