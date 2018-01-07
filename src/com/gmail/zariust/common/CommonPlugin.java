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

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventPriority;

public final class CommonPlugin {
    static public Verbosity getConfigVerbosity(YamlConfiguration config) {
        String verb_string = config.getString("verbosity", "normal");
        if (verb_string.equalsIgnoreCase("low"))
            return Verbosity.LOW;
        else if (verb_string.equalsIgnoreCase("high"))
            return Verbosity.HIGH;
        else if (verb_string.equalsIgnoreCase("highest"))
            return Verbosity.HIGHEST;
        else if (verb_string.equalsIgnoreCase("extreme"))
            return Verbosity.EXTREME;
        else
            return Verbosity.NORMAL;
    }

    static public EventPriority getConfigPriority(YamlConfiguration config) {
        String priority_string = config.getString("priority", "lowest");
        if (priority_string.equalsIgnoreCase("low"))
            return EventPriority.LOW;
        else if (priority_string.equalsIgnoreCase("normal"))
            return EventPriority.NORMAL;
        else if (priority_string.equalsIgnoreCase("high"))
            return EventPriority.HIGH;
        else if (priority_string.equalsIgnoreCase("highest"))
            return EventPriority.HIGHEST;
        else
            return EventPriority.LOWEST;
    }

    static public <E extends Enum<E>> E enumValue(Class<E> clazz, String name) {
        try {
            return Enum.valueOf(clazz, name);
        } catch (IllegalArgumentException e) {
        }
        return null;
    }
}
