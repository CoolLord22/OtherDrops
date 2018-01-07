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

import org.bukkit.Location;
import org.bukkit.Material;

import com.gmail.zariust.common.MaterialGroup;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.ConfigOnly;

@ConfigOnly(BlockTarget.class)
public class BlocksTarget implements Target {
    private MaterialGroup group;

    public BlocksTarget(MaterialGroup grp) {
        group = grp;
    }

    public MaterialGroup getGroup() {
        return group;
    }

    @Override
    public boolean overrideOn100Percent() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BlocksTarget))
            return false;
        return group == ((BlocksTarget) other).group;
    }

    @Override
    public int hashCode() {
        return new HashCode(this).get(group);
    }

    @Override
    public boolean matches(Subject other) {
        if (!(other instanceof BlockTarget))
            return false;
        BlockTarget block = (BlockTarget) other;
        return group.contains(block.getMaterial());
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.BLOCK;
    }

    @Override
    public String toString() {
        if (group == null)
            return "ANY_BLOCK";
        return group.toString();
    }

    @Override
    public List<Target> canMatch() {
        List<Target> all = new ArrayList<Target>();
        List<Material> materials = group.materials();
        for (Material block : materials)
            all.add(new BlockTarget(block));
        return all;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    // Should never be called due to the annotation
    public Location getLocation() {
        return null;
    }

    @Override
    // It's a wildcard, so we don't need anything here. The annotation should
    // prevent it from being called.
    public void setTo(BlockTarget replacement) {
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
