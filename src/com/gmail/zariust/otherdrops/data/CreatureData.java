// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.otherdrops.data;

import static com.gmail.zariust.common.Verbosity.EXTREME;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.data.entities.AgeableData;
import com.gmail.zariust.otherdrops.data.entities.CreeperData;
import com.gmail.zariust.otherdrops.data.entities.EndermanData;
import com.gmail.zariust.otherdrops.data.entities.HorseData;
import com.gmail.zariust.otherdrops.data.entities.LivingEntityData;
import com.gmail.zariust.otherdrops.data.entities.OcelotData;
import com.gmail.zariust.otherdrops.data.entities.PigData;
import com.gmail.zariust.otherdrops.data.entities.PigZombieData;
import com.gmail.zariust.otherdrops.data.entities.SheepData;
import com.gmail.zariust.otherdrops.data.entities.SkeletonData;
import com.gmail.zariust.otherdrops.data.entities.SlimeData;
import com.gmail.zariust.otherdrops.data.entities.VillagerData;
import com.gmail.zariust.otherdrops.data.entities.WolfData;
import com.gmail.zariust.otherdrops.data.entities.ZombieData;
import com.gmail.zariust.otherdrops.data.entities.ZombieVillagerData;
import com.gmail.zariust.otherdrops.data.entities.HuskData;
import com.gmail.zariust.otherdrops.data.entities.DonkeyData;
import com.gmail.zariust.otherdrops.data.entities.*;

// Range only allowed for SHEEP, SLIME, and PIG_ZOMBIE
public class CreatureData implements Data, RangeableData {

    // Create a map of entity types against data objects
    private static final Map<EntityType, Class> DATAMAP;

    // Map of EntityTypes to new class based creature data, for ease of lookup
    // later on
    // note: there should be only one line per entity, or things could get messy
    static {
        Map<EntityType, Class> aMap = new HashMap<EntityType, Class>();

        // Note: due to difficulties with alternate coding all specific data
        // classes need to manually include a call to either LivingEntityData or
        // AgeableData

        // Specific data (+LivingEntity)
        aMap.put(EntityType.ZOMBIE, ZombieData.class); // includes
                                                       // LivingEntityData
        aMap.put(EntityType.ZOMBIE_VILLAGER, ZombieVillagerData.class);
        aMap.put(EntityType.HUSK, HuskData.class);
        
        aMap.put(EntityType.PIG_ZOMBIE, PigZombieData.class); // extends Zombie
        aMap.put(EntityType.CREEPER, CreeperData.class);
        aMap.put(EntityType.SKELETON, SkeletonData.class); // includes
                                                           // LivingEntityData
        // Specific data (+Ageable(+LivingEntity))
        aMap.put(EntityType.OCELOT, OcelotData.class);
        aMap.put(EntityType.PIG, PigData.class);
        aMap.put(EntityType.SHEEP, SheepData.class);
        aMap.put(EntityType.VILLAGER, VillagerData.class);
        aMap.put(EntityType.WOLF, WolfData.class);
        aMap.put(EntityType.SLIME, SlimeData.class);
        aMap.put(EntityType.MAGMA_CUBE, SlimeData.class);
        aMap.put(EntityType.ENDERMAN, EndermanData.class);
        aMap.put(EntityType.HORSE, HorseData.class);
        aMap.put(EntityType.DONKEY, DonkeyData.class);
        aMap.put(EntityType.MULE, MuleData.class);
        aMap.put(EntityType.ZOMBIE_HORSE, ZombieHorseData.class);
        aMap.put(EntityType.SKELETON_HORSE, SkeletonHorseData.class);

        // Scan through all entity types and if there's no current mapping
        // then check if it's an Ageable or LivingEntity and assign a mapping
        for (EntityType type : EntityType.values()) {
            if (aMap.get(type) == null) {
                Class typeClass = type.getEntityClass();
                if (typeClass != null) {
                    if (Ageable.class.isAssignableFrom(type.getEntityClass())) {
                        aMap.put(type, AgeableData.class);
                    } else if (LivingEntity.class.isAssignableFrom(type
                            .getEntityClass())) {
                        aMap.put(type, LivingEntityData.class);
                    }
                }

            }
        }
        DATAMAP = Collections.unmodifiableMap(aMap);
        Log.logInfo("CreatureData map: " + aMap.toString(), Verbosity.EXTREME);
    }
    public int                                  data;
    private Boolean                             sheared;
    private List<CreatureData>                  subData;

    public CreatureData(int mobData) {
        this(mobData, null);
    }

    public CreatureData(int mobData, Boolean sheared) {
        data = mobData;
        this.sheared = sheared;
    }

    public CreatureData() {
        this(0);
    }

    public CreatureData(List<CreatureData> dataList) {
        this.subData = dataList;
    }

    @Override
    public int getData() {
        return data;
    }

    @Override
    public void setData(int d) {
        data = d;
    }

    @Override
    public Boolean getSheared() {
        return sheared;
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof CreatureData))
            return false;
        // OtherDrops.logInfo("Checking data = "+data+" this.getsheared: "+sheared+" othersheared:"+d.getSheared());
        if (data == -2) { // for sheep with no color specified
            if (d.getSheared() == null)
                return true; // null is like a wildcard
            return sheared == d.getSheared();
        }

        boolean shearMatch = false;
        if (sheared == null)
            shearMatch = true;
        else if (sheared == d.getSheared())
            shearMatch = true;
        return (data == d.getData() && shearMatch);
    }

    @Override
    public String get(Enum<?> creature) {
        if (creature instanceof EntityType)
            return get((EntityType) creature);
        return "";
    }

    private String get(EntityType type) {
        Log.logInfo(
                "CreatureData: get("
                        + type.toString()
                        + "), shouldn't be here (should be in specific mob data) - please let developer know.",
                Verbosity.HIGH);
        return "";
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        // nothing to do here, this code shouldn't be reached
        Log.logInfo(
                "CreatureData: setOn("
                        + mob.toString()
                        + ", "
                        + owner.toString()
                        + "), shouldn't be here (should be in specific mob data) - please let developer know.",
                Verbosity.HIGH);
    }

    @Override
    // No creature has a block state, so nothing to do here.
    public void setOn(BlockState state) {
    }

    @SuppressWarnings("incomplete-switch")
    public static Data parse(EntityType creature, String state) {
        // state = state.toUpperCase().replaceAll("[ _-]", "");

        if (DATAMAP.get(creature) != null) {
            CreatureData cData = null;
            try {
                cData = (CreatureData) DATAMAP.get(creature)
                        .getMethod("parseFromString", String.class)
                        .invoke(null, state);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ;

            /*
             * Attempting to set a list of data classes so we can automatically
             * cover livingentity/ageable/specific data Doesn't work currently!
             * Difficult to work out how to compare two
             * 
             * List<CreatureData> dataList = new ArrayList<CreatureData>();
             * dataList.add(cData); if
             * (LivingEntity.class.isAssignableFrom(creature.getEntityClass()))
             * { dataList.add(LivingEntityData.parseFromString(state)); } if
             * (Ageable.class.isAssignableFrom(creature.getEntityClass())) {
             * dataList.add(AgeableData.parseFromString(state)); } return new
             * CreatureData(dataList);
             */

            if (cData == null)
                return new CreatureData(0);
            return cData;

        } else {
            if (state == null || state.isEmpty())
                return new CreatureData(0);
        }
        return new CreatureData();
    }

    public static CreatureData parseFromString(String state) {
        Log.logInfo(
                "CreatureData: parseFromString("
                        + state
                        + "), shouldn't be here (should be in specific mob data) - please let developer know.",
                Verbosity.HIGH);
        return null;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        Log.logInfo(
                "CreatureData: parseFromEntity("
                        + entity.toString()
                        + "), shouldn't be here (should be in specific mob data) - please let developer know.",
                Verbosity.HIGH);
        return null;
    }

    @Override
    public String toString() {
        // TODO: Should probably make sure this is not used, and always use the
        // get method instead
        Log.logWarning("CreatureData.toString() was called! Is this right?",
                EXTREME);
        Log.stackTrace();
        return String.valueOf(data);
    }

    @Override
    public int hashCode() {
        return data;
    }

    public static Data parse(Entity entity) {
        if (entity == null)
            return new CreatureData(0);
        EntityType creatureType = entity.getType();
        if (creatureType == null)
            return new CreatureData(0);

        if (DATAMAP.get(entity.getType()) != null) {
            CreatureData cData = null;
            try {
                cData = (CreatureData) DATAMAP.get(entity.getType())
                        .getMethod("parseFromEntity", Entity.class)
                        .invoke(null, entity);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ;
            if (cData == null)
                return new CreatureData(0);

            return cData;
        }
        return new CreatureData(0);
    }

    public static Data parse(EntityType mob, int mobData) {
        return CreatureData.parse(mob, String.valueOf(mobData));
    }
}
