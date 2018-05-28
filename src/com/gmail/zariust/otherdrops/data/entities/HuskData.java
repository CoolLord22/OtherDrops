package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class HuskData extends CreatureData {
    ZombieData leData = null;

    public HuskData(ZombieData leData) {
        this.leData = leData;
    }

    @SuppressWarnings("unused")
	@Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Husk) {
        	Husk z = (Husk) mob;
            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof HuskData))
            return false;
        HuskData vd = (HuskData) d;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Husk) {
            return new HuskData((ZombieData) ZombieData.parseFromEntity(entity));
        } else {
            Log.logInfo("HuskData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        Log.logInfo("HuskData: parsing from string.", Verbosity.HIGHEST);
        ZombieData leData = (ZombieData) ZombieData.parseFromString(state);

        return new HuskData(leData);
    }

    @Override
    public String toString() {
        String val = "";
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
