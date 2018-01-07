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

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.zariust.common.MaterialGroup;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.ConfigOnly;
import com.gmail.zariust.otherdrops.options.ToolDamage;

@ConfigOnly(PlayerSubject.class)
public class MaterialGroupAgent implements Agent {
    private MaterialGroup group;

    public MaterialGroupAgent(MaterialGroup g) {
        group = g;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MaterialGroupAgent))
            return false;
        return group == ((MaterialGroupAgent) other).group;
    }

    @Override
    public boolean matches(Subject other) {
        if (!(other instanceof PlayerSubject))
            return false;
        return group.contains(((PlayerSubject) other).getMaterial());
    }

    @Override
    public int hashCode() {
        return new HashCode(this).get(group);
    }

    public List<Material> getMaterials() {
        return group.materials();
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.PLAYER;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void damage(int amount) {
    }

    @Override
    public void damageTool(ToolDamage amount, Random rng) {
    }

    @Override
    public String toString() {
        if (group == null)
            return "ANY_OBJECT";
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