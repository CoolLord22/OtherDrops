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

package com.gmail.zariust.otherdrops.drop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import com.gmail.zariust.common.CreatureGroup;
import com.gmail.zariust.common.MaterialGroup;
import com.gmail.zariust.otherdrops.options.DoubleRange;
import com.gmail.zariust.otherdrops.options.IntRange;
import com.gmail.zariust.otherdrops.subject.Target;

/**
 * This class represents an set of exclusive drops, eg. drop: {blah, blah2, etc}
 * # drops only one item in list
 * 
 * @author Celtic Minstrel, Zarius
 * 
 */
public class DropListExclusive extends DropType {
    private final List<DropType> group;
    private double               percentTotal;

    public DropListExclusive(DropType... drops) {
        this(Arrays.asList(drops));
    }

    public DropListExclusive(List<DropType> drops) {
        super(DropCategory.GROUP);
        group = drops;
        percentTotal = 0;
        for (DropType drop : group)
            percentTotal += drop.getChance();
        if (percentTotal < 100)
            percentTotal = 100;

    }

    public DropListExclusive(List<Material> materials, int defaultData,
            IntRange amount, double chance) {
        this(materialsToDrops(materials, defaultData, amount, chance));
    }

    public DropListExclusive(List<EntityType> creatures, IntRange amount,
            double chance) {
        this(creaturesToDrops(creatures, amount, chance));
    }

    private static DropType[] materialsToDrops(List<Material> materials,
            int defaultData, IntRange amount, double chance) {
        DropType[] drops = new DropType[materials.size()];
        for (int i = 0; i < drops.length; i++) {
            drops[i] = new ItemDrop(amount, materials.get(i), defaultData,
                    chance, null, "");
        }
        return drops;
    }

    private static DropType[] creaturesToDrops(List<EntityType> creatures,
            IntRange amount, double chance) {
        DropType[] drops = new DropType[creatures.size()];
        for (int i = 0; i < drops.length; i++) {
            drops[i] = new CreatureDrop(amount, creatures.get(i), chance);
        }
        return drops;
    }

    public List<DropType> getGroup() {
        return group;
    }

    @Override
    protected DropResult performDrop(Target source, Location where,
            DropFlags flags) {
        // don't set override default here - it's set for each individual drop
        DropResult returnRes = DropResult.fromQuantity(0);
        double select = flags.rng.nextDouble() * percentTotal, cumul = 0;
        for (DropType drop : group) {
            cumul += drop.getChance();
            if (select <= cumul) {
                returnRes.add(drop.dropLocal(source, where, 1, flags));
                break;
            }
        }

        return returnRes;
    }

    public static DropType parse(List<String> dropList, String defaultData) {
        List<DropType> drops = new ArrayList<DropType>();
        for (String dropName : dropList) {
            DropType drop = DropType.parse(dropName, defaultData);
            if (drop != null)
                drops.add(drop);
        }
        return new DropListExclusive(drops);
    }

    public static DropType parse(String drop, String data, IntRange amount,
            double chance) {
        drop = drop.toUpperCase();
        MaterialGroup group = MaterialGroup.get(drop);
        if (group == null) {
            if (drop.equals("ANY_CREATURE"))
                return new DropListExclusive(
                        CreatureGroup.CREATURE_ANY.creatures(), amount, chance);
            else if (drop.equals("ANY_VEHICLE_SPAWN"))
                return new DropListExclusive(new DropType[] {
                        new VehicleDrop(amount, Material.MINECART, chance),
                        new VehicleDrop(amount, Material.POWERED_MINECART,
                                chance),
                        new VehicleDrop(amount, Material.STORAGE_MINECART,
                                chance),
                        new VehicleDrop(amount, Material.BOAT, chance) });
            else {
                drop = drop.replace("^ANY_", "^");
                CreatureGroup cgroup = CreatureGroup.get(drop.substring(1));
                if (cgroup != null)
                    return new DropListExclusive(cgroup.creatures(), amount,
                            chance);
            }
            return null;
        }
        int intData = 0;
        try {
            intData = Integer.parseInt(data);
        } catch (NumberFormatException e) {
        }
        return new DropListExclusive(group.materials(), intData, amount, chance);
    }

    @Override
    public String getName() {
        return group.toString().replace('[', '{').replace(']', '}');
    }

    @Override
    public double getAmount() {
        return 1;
    }

    @Override
    public DoubleRange getAmountRange() {
        return new DoubleRange(1.0);
    }
}
