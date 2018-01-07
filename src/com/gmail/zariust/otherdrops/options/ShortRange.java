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

public class ShortRange extends Range<Short> {
    public ShortRange() {
        super((short) 0);
    }

    public ShortRange(Short val) {
        super(val);
    }

    public ShortRange(Short lo, Short hi) {
        super(lo, hi);
    }

    @Override
    public Short getRandomIn(Random rng) {
        if (min.equals(max))
            return min;
        return (short) (min + rng.nextInt(max - min + 1));
    }

    @Override
    public Short negate(Short num) {
        return (short) -num;
    }

    @Override
    protected Short staticParse(String val) {
        return Short.parseShort(val);
    }

    public static ShortRange parse(String val) {
        return (ShortRange) Range.parse(val, new ShortRange());
    }
}
