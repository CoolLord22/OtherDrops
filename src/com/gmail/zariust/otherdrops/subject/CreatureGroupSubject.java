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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.EntityType;

import com.gmail.zariust.common.CreatureGroup;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.ToolDamage;

public class CreatureGroupSubject extends LivingSubject {
    private final CreatureGroup group;

    public CreatureGroupSubject(CreatureGroup creature) {
        super(null);
        group = creature;
    }

    @Override
    public boolean overrideOn100Percent() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof CreatureGroupSubject))
            return false;
        return group == ((CreatureGroupSubject) other).group;
    }

    @Override
    public int hashCode() {
        return new HashCode(this).get(group);
    }

    @Override
    public boolean matches(Subject block) {
        if (!(block instanceof CreatureSubject))
            return false;
        return group.contains(((CreatureSubject) block).getCreature());
    }

    @Override
    public List<Target> canMatch() {
        List<Target> all = new ArrayList<Target>();
        List<EntityType> creatures = group.creatures();
        for (EntityType type : creatures)
            all.add(new CreatureSubject(type));
        return all;
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.CREATURE;
    }

    @Override
    public void damage(int amount) {
    }

    @Override
    public void damageTool(ToolDamage amount, Random rng) {
    }

    @Override
    public String getKey() {
        return null;
    }

    public static CreatureGroupSubject parse(String name,
            @SuppressWarnings("unused") String state) {
        name = name.toUpperCase();
        if (!name.startsWith("CREATURE_"))
            name = "CREATURE_" + name;
        CreatureGroup creature = CreatureGroup.get(name);
        if (creature == null)
            return null;
        return new CreatureGroupSubject(creature);
    }

    @Override
    public String toString() {
        if (group == null)
            return "ANY_CREATURE";
        return group.toString();
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public String getReadableName() {
        return toString();
    }

}
