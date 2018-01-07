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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;

import static java.lang.Math.abs;

public class Time extends Range<Long> {
    public final static Time DAY      = new Time(0, 12000 - 1);
    public final static Time NIGHT    = new Time(13800, 22200 - 1);
    public final static Time DUSK     = new Time(12000, 13800 - 1);
    public final static Time DAWN     = new Time(22200, 24000 - 1);
    public final static Time DARKNESS = new Time(12000, 24000 - 1);

    public Time() {
        super((long) 0);
    }

    public Time(Long val) {
        super(val);
    }

    public Time(Long lo, Long hi) {
        super(lo, hi);
    }

    public Time(int val) {
        this((long) val);
    }

    public Time(int lo, int hi) {
        this((long) lo, (long) hi);
    }

    @Override
    public Long getRandomIn(Random rng) {
        if (min.equals(max))
            return min;
        return min + abs(rng.nextLong() % (max - min + 1));
    }

    @Override
    public Long negate(Long num) {
        return -num;
    }

    @Override
    protected Long staticParse(String val) {
        return Long.parseLong(val);
    }

    public static Time parse(String range) {
        if (range.equalsIgnoreCase("day"))
            return DAY;
        else if (range.equalsIgnoreCase("night"))
            return NIGHT;
        else if (range.equalsIgnoreCase("darkness"))
            return DARKNESS;
        else if (range.equalsIgnoreCase("dawn"))
            return DAWN;
        else if (range.equalsIgnoreCase("dusk"))
            return DUSK;
        else
            return (Time) Range.parse(range, new Time());
    }

    public static Map<Time, Boolean> parseFrom(ConfigurationNode node,
            Map<Time, Boolean> def) {
        List<String> times = OtherDropsConfig.getMaybeList(node, "time",
                "times");
        if (times.isEmpty())
            return def;
        HashMap<Time, Boolean> result = new HashMap<Time, Boolean>();
        for (String name : times) {
            Time time = parse(name);
            if (time == null && name.startsWith("-")) {
                time = parse(name.substring(1));
                if (time == null) {
                    Log.logWarning("Invalid time " + name + "; skipping...");
                    continue;
                }
                result.put(time, false);
            } else
                result.put(time, true);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    @Override
    public String toString() {
        if (equals(DAY))
            return "DAY";
        else if (equals(NIGHT))
            return "NIGHT";
        else if (equals(DUSK))
            return "DUSK";
        else if (equals(DAWN))
            return "DAWN";
        else if (equals(DARKNESS))
            return "DARKNESS";
        return super.toString();
    }
}
