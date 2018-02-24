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

package com.gmail.zariust.otherdrops.subject;

import static com.gmail.zariust.common.Verbosity.EXTREME;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import com.gmail.zariust.common.CommonEntity;
import com.gmail.zariust.common.CreatureGroup;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.ToolDamage;

public class CreatureSubject extends LivingSubject {
    private final EntityType creature;
    private final Data       data;
    private Entity           agent;
    private String           customName;

    public CreatureSubject() {
        this((EntityType) null);
    }

    public CreatureSubject(EntityType tool) {
        this(tool, null);
    }

    public CreatureSubject(EntityType tool, int d) {
        this(tool, CreatureData.parse(tool, d));
    }

    public CreatureSubject(EntityType tool, Data d) {
        this(tool, d, null);
    }

    public CreatureSubject(Entity damager) {
        this(damager.getType(), CreatureData.parse(damager), damager);
        agent = damager;
    }

    public CreatureSubject(EntityType tool, int d, Entity damager) {
        this(tool, CreatureData.parse(tool, d), damager);
    }

    public CreatureSubject(EntityType tool, Data d, Entity damager) {
        super(damager);
        creature = tool;
        data = d;
        agent = damager;
    }

    private CreatureSubject equalsHelper(Object other) {
        if (!(other instanceof CreatureSubject))
            return null;
        return (CreatureSubject) other;
    }

    private boolean isEqual(CreatureSubject tool) {
        if (tool == null)
            return false;

        // Integer thisData = null;
        // Integer toolData = null;
        boolean dataMatch = false;
        if (data != null)
            dataMatch = data.matches(tool.data);
        else if (tool.data == null)
            dataMatch = true;

        // Log.logInfo("CreatureSubject: checking isEqual: creature=tool.creature (" + creature.toString() + ", " + tool.creature.toString() + ": " + (creature == tool.creature) + ") && dataMatch = " + dataMatch, EXTREME);
        if (OtherDropsConfig.matchMobByNameOnly) {
            return creature.toString().equalsIgnoreCase(tool.creature.toString()) && dataMatch; // must be data.getData()
        } else {
            return creature == tool.creature && dataMatch; // must be data.getData() otherwise comparing different objects will always fail
        }
    }

    @Override
    public boolean equals(Object other) {
        CreatureSubject tool = equalsHelper(other);
        return isEqual(tool);
    }

    @Override
    public boolean matches(Subject other) {
        if (other instanceof ProjectileAgent)
            return matches(((ProjectileAgent) other).getShooter());
        CreatureSubject tool = equalsHelper(other);
        if (tool == null)
            return false;
        if (creature == null) {
            Log.logInfo("CreatureSubject.match - creature = null.", EXTREME);
            return true;
        } else if (tool.creature == null) {
            Log.logInfo("CreatureSubject.match - tool.creature = null.",
                    EXTREME);
            return true;
        }
        if (data == null) {
            boolean match = (creature == tool.creature);
            Log.logInfo(
                    "CreatureSubject.match - data = null. creature: "
                            + creature.toString() + ", tool.creature: "
                            + tool.creature.toString() + ", match=" + match,
                    EXTREME);
            return match;
        }

        boolean match = isEqual(tool);
        // Log.logInfo("CreatureSubject.match - tool.creature="+tool.creature.toString()+", creature="+creature.toString()+", tooldata="+tool.data.getData()+", data="+String.valueOf(data)+", match="
        // + match, EXTREME); // causes npe error
        return match;
    }

    @Override
    public int hashCode() {
        return new HashCode(this).get(creature);
    }

    public EntityType getCreature() {
        return creature;
    }

    public int getCreatureData() {
        return data.getData();
    }

    public Entity getAgent() {
        return agent;
    }

    @Override
    public void damage(int amount) {
        if (agent instanceof LivingEntity) {
            ((LivingEntity) agent).damage(amount);
        }
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.CREATURE;
    }

    @Override
    public boolean overrideOn100Percent() {
        return true;
    }

    @Override
    public void damageTool(ToolDamage amount, Random rng) {
    }

    public static LivingSubject parse(String name, String state, String displayName) {
        // TODO: Is there a way to detect non-vanilla creatures?

        // name = name.toUpperCase().replace("CREATURE_", "");
        String customName = displayName;
        String split[] = name.split("~", 2);
        name = split[0];
        if (split.length > 1) {
            customName = split[1];
        }

        if (customName != null && customName.isEmpty())
        	customName = "CoolLordsWayToEnsureNobodyUsesThisNameHAHA";
        
        if (customName != null && !customName.isEmpty()) 
        	state += "~" + customName;
        // replace comma with period is to support custom mobs (e.g. MyMod.Mob) due to
        // YAML interpreting the period in block headers differently.
        EntityType creature = CommonEntity.getCreatureEntityType(name.replaceAll("[,]", "."));
        // EntityType creature = enumValue(EntityType.class, name);

        if (creature == null) {
            return CreatureGroupSubject.parse(name.toUpperCase(), state);
        }
        Data data = CreatureData.parse(creature, state);
        return new CreatureSubject(creature, data);
    }

    @Override
    public List<Target> canMatch() {
        if (creature == null)
            return new CreatureGroupSubject(CreatureGroup.CREATURE_ANY)
                    .canMatch();
        return Collections.singletonList((Target) this);
    }

    @Override
    public String getKey() {
        if (creature != null)
            return creature.toString();
        return null;
    }

    @Override
    public String toString() {
        if (creature == null)
            return "ANY_CREATURE";
        String ret = "CREATURE_" + creature.toString();
        // TODO: Will data ever be null, or will it just be 0?
        if (data != null) {
            String dataString = data.get(creature);
            if (!dataString.isEmpty()) ret += "@" + data.get(creature);            
        }
        return ret;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public String getReadableName() {
        if (creature == null)
            return "ANY_CREATURE";
        return "a " + creature.toString().toLowerCase();
    }

}
