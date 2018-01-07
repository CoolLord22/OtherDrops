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

import com.gmail.zariust.otherdrops.ConfigurationNode;

public class Comparative {
    private final int      compare;
    private final int      val;
    private final IntRange range;

    public Comparative(int v) {
        this(v, 0, null);
    }

    public Comparative(int v, int cmp, IntRange range) {
        val = v;
        compare = cmp;
        this.range = range;
    }

    public boolean matches(int v) {
        if (range != null)
            return range.contains(v);
        return Integer.valueOf(v).compareTo(val) == compare;
    }

    public static String[] splitRange(String range) {
        return range.split("[~-]", 2);
    }

    public static Comparative parse(String cmp) {
        if (cmp == null)
            return null;

        // Allow a comparative to support intranges
        String splitString = cmp;
        if (cmp.startsWith("-")) {
            splitString = cmp.substring(1);
        }
        String[] split = splitRange(splitString);
        if (split.length > 1) {
            return new Comparative(0, 0, IntRange.parse(cmp));
        } else {
            try {
                switch (cmp.charAt(0)) {
                case '<':
                    return new Comparative(Integer.parseInt(cmp.substring(1)),
                            -1, null);
                case '>':
                    return new Comparative(Integer.parseInt(cmp.substring(1)),
                            1, null);
                case '=':
                    return new Comparative(Integer.parseInt(cmp.substring(1)));
                default:
                    return new Comparative(Integer.parseInt(cmp));
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static Comparative parseFrom(ConfigurationNode node, String key,
            Comparative def) {
        Comparative cmp = parse(node.getString(key));
        if (cmp == null)
            return def;
        return cmp;
    }

    @Override
    public String toString() {
        char sep = '?';
        if (compare == -1)
            sep = '<';
        else if (compare == 0)
            sep = '=';
        else if (compare == 1)
            sep = '>';
        return sep + "" + val;
    }
}
