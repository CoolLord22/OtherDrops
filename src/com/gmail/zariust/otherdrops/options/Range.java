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

public abstract class Range<T extends Number & Comparable<T>> {
    protected T min, max;

    public Range(T val) {
        min = max = val;
    }

    public Range(T lo, T hi) {
        if (lo == null) {
            if (hi == null)
                min = max = null;
            else
                min = max = hi;
        } else if (hi == null)
            min = max = lo;
        else if (lo.compareTo(hi) < 0) {
            min = lo;
            max = hi;
        } else {
            min = hi;
            max = lo;
        }
    }

    public T getMin() {
        return min;
    }

    public void setMin(T newMin) {
        min = newMin;
        if (min.compareTo(max) > 0)
            max = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T newMax) {
        max = newMax;
        if (max.compareTo(min) < 0)
            min = max;
    }

    protected abstract T negate(T num);

    public Range<T> negate() {
        T tmp = negate(min);
        min = negate(max);
        max = tmp;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <S extends Range<T>> S negate(@SuppressWarnings("unused") S dummy) {
        return (S) negate();
    }

    public boolean contains(T val) {
        if (min == null || max == null || val == null)
            return true;
        return val.compareTo(min) >= 0 && val.compareTo(max) <= 0;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Range))
            return false;
        Range<?> range = (Range<?>) other;
        if (min == null) {
            if (max == null) {
                return range.min == null && range.max == null;
            } else {
                return range.min == null && max.equals(range.max);
            }
        } else {
            if (max == null) {
                return range.max == null && min.equals(range.min);
            } else {
                return min.equals(range.min) && max.equals(range.max);
            }
        }
    }

    @Override
    public int hashCode() {
        if (max == null)
            return min == null ? 0 : min.hashCode();
        return min.hashCode() ^ max.hashCode();
    }

    @Override
    public String toString() {
        if (min.equals(max))
            return min.toString();
        return min.toString() + "~" + max.toString();
    }

    public abstract T getRandomIn(Random rng);

    protected abstract T staticParse(String val);

    static String[] splitRange(String range) {
        return range.split("[~-]", 2);
    }

    protected static <T extends Number & Comparable<T>> Range<T> parse(
            String range, Range<T> template) {
        try {
            String splitString = range;
            String firstChar = "";
            if (range.startsWith("-")) {
                splitString = range.substring(1);
                firstChar = "-";
            }

            String[] split = splitRange(splitString);
            T hi, lo = template.staticParse(firstChar + split[0]);
            if (split.length == 1)
                hi = lo;
            else
                hi = template.staticParse(split[1]);
            if (lo.compareTo(hi) < 0) {
                template.min = lo;
                template.max = hi;
            } else {
                template.min = hi;
                template.max = lo;
            }
            return template;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
