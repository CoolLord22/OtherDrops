package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;

import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class SkeletonData extends CreatureData {
    SkeletonType     type   = null; // null = wildcard
    LivingEntityData leData = null;

    public SkeletonData(SkeletonType type, LivingEntityData leData) {
        this.type = type;
        this.leData = leData;
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Skeleton) {
            Skeleton z = (Skeleton) mob;
            if (type != null)
                z.setSkeletonType(type);
            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof SkeletonData))
            return false;
        SkeletonData vd = (SkeletonData) d;

        if (this.type != null)
            if (this.type != vd.type)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Skeleton) {
            return new SkeletonData(((Skeleton) entity).getSkeletonType(),
                    (LivingEntityData) LivingEntityData.parseFromEntity(entity));
        } else {
            Log.logInfo("SkeletonData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        SkeletonType type = null;
        LivingEntityData leData = (LivingEntityData) LivingEntityData
                .parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                for (SkeletonType sType : SkeletonType.values()) {
                    if (CommonMaterial.fuzzyMatchString(sType.toString(), sub)) {
                        type = sType;
                    }
                }
            }
        }

        return new SkeletonData(type, leData);
    }

    @Override
    public String toString() {
        String val = "";
        if (type != null) {
            val += "!!" + type.name();
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
