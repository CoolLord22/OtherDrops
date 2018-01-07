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

package com.gmail.zariust.otherdrops.options;

import java.util.Random;

public class IntRange extends Range<Integer> {
    public IntRange() {
        super(0);
    }

    public IntRange(Integer val) {
        super(val);
    }

    public IntRange(Integer lo, Integer hi) {
        super(lo, hi);
    }

    @Override
    public Integer getRandomIn(Random rng) {
        if (min.equals(max))
            return min;
        return min + rng.nextInt(max - min + 1);
    }

    @Override
    public Integer negate(Integer num) {
        return -num;
    }

    @Override
    protected Integer staticParse(String val) {
        return Integer.parseInt(val);
    }

    public static IntRange parse(String val) {
        return (IntRange) Range.parse(val, new IntRange());
    }

    public DoubleRange toDoubleRange() {
        return new DoubleRange(getMin().doubleValue(), getMax().doubleValue());
    }
}
