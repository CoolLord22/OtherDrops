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

import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.options.IntRange;

import org.bukkit.DyeColor;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.material.MaterialData;

public class RangeData implements Data {
    private IntRange range;
    private Integer  val;

    public RangeData(int lo, int hi) {
        this(new IntRange(lo, hi));
    }

    public RangeData(IntRange r) {
        range = r;
    }

    @Override
    public int getData() {
        return range.getRandomIn(OtherDrops.rng);
    }

    private void denullifyVal() {
        if (val == null)
            val = range.getRandomIn(OtherDrops.rng);
    }

    @Override
    public void setData(int d) {
        val = d;
    }

    @Override
    public boolean matches(Data d) {
        // TODO: Allow range to match other sorts of data?
        // I don't think other sorts really work though.
        if (!(d instanceof RangeableData))
            return false;
        return range.contains(d.getData());
    }

    @Override
    public String get(Enum<?> mat) {
        return "RANGE-" + range.toString();
    }

    @Override
    public void setOn(BlockState state) {
        denullifyVal();
        state.setData(new MaterialData(state.getType(), val.byteValue()));
    }

    @Override
    public void setOn(Entity mob, Player witness) {
        denullifyVal();
        switch (mob.getType()) {
        case SHEEP:
            if (val >= 32)
                ((Sheep) mob).setSheared(true);
            val -= 32;
            if (val > 0)
                ((Sheep) mob).setColor(DyeColor.getByDyeData((byte) (val - 1)));
            break;
        case SLIME:
            if (val > 0)
                ((Slime) mob).setSize(val);
            break;
        case PIG_ZOMBIE:
            if (val > 0)
                ((PigZombie) mob).setAnger(val);
            break;
        default:
        }
    }

    public static RangeData parse(String state) {
        if (state == null || state.isEmpty())
            return null;
        state = state.toUpperCase().replace("RANGE-", "");
        return new RangeData(IntRange.parse(state));
    }

    public IntRange getRange() {
        return range;
    }

    public void setRange(IntRange newRange) {
        range = newRange;
    }

    public void setRange(int lo, int hi) {
        setRange(new IntRange(lo, hi));
    }

    @Override
    public int hashCode() {
        return range == null ? 0 : range.hashCode();
    }

    @Override
    public Boolean getSheared() {
        // TODO Auto-generated method stub
        return null;
    }
}
