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

package com.gmail.zariust.common;

public enum Verbosity {
    LOW(1), NORMAL(2), HIGH(3), HIGHEST(4), EXTREME(5), DEBUG(6);
    private int level;

    private Verbosity(int lvl) {
        level = lvl;
    }

    public boolean exceeds(Verbosity other) {
        if (level >= other.level)
            return true;
        return false;
    }
}
