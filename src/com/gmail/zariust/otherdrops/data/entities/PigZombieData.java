package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class PigZombieData extends CreatureData {
    Integer    anger  = null; // null = wildcard
    ZombieData leData = null;

    public PigZombieData(Integer type, ZombieData leData) {
        this.anger = type;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof PigZombie) {
            PigZombie z = (PigZombie) mob;
            if (anger != null)
                z.setAnger(anger);
            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof PigZombieData))
            return false;
        PigZombieData vd = (PigZombieData) d;

        if (this.anger != null)
            if (this.anger != vd.anger)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof PigZombie) {
            return new PigZombieData(((PigZombie) entity).getAnger(),
                    (ZombieData) ZombieData.parseFromEntity(entity));
        } else {
            Log.logInfo("PigZombieData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        Log.logInfo("PigZombieData: parsing from string.", Verbosity.HIGHEST);
        Integer anger = null;
        ZombieData leData = (ZombieData) ZombieData.parseFromString(state);

        // TODO: support range:

        /*
         * if(state.startsWith("RANGE")) return RangeData.parse(state); try {
         * int sz = Integer.parseInt(state); return new CreatureData(sz); }
         * catch(NumberFormatException e) {} break;
         */
        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {

                if (sub.matches("[0-9]+")) { // need to check numbers before any
                                             // .toLowerCase()
                    anger = Integer.valueOf(sub);
                }
            }

        }

        return new PigZombieData(anger, leData);
    }

    @Override
    public String toString() {
        String val = "";
        if (anger != null) {
            val += "!!" + anger.toString();
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
