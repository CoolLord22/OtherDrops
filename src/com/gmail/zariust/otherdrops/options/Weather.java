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

import org.bukkit.block.Biome;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;

public enum Weather {
    RAIN(true), SNOW(true), THUNDER(true), CLEAR(false), CLOUD(true), NONE(
            false), STORM(true) {
        @Override
        public boolean matches(Weather sky) {
            if (sky.stormy && sky != THUNDER)
                return true;
            return false;
        }
    };
    private boolean                     stormy;
    private static Map<String, Weather> nameLookup = new HashMap<String, Weather>();

    static {
        for (Weather storm : values())
            nameLookup.put(storm.name(), storm);
    }

    private Weather(boolean storm) {
        stormy = storm;
    }

    public static Weather match(Biome biome, boolean hasStorm,
            boolean thundering) {
        if (biome == null)
            biome = Biome.PLAINS;
        switch (biome) {
        case HELL:
            return NONE;
        case SKY:
        case DESERT:
        case TAIGA:
        case FROZEN_OCEAN:
        case FROZEN_RIVER:
        case ICE_FLATS:
        case ICE_MOUNTAINS:
            if (hasStorm)
                return SNOW;
            return CLEAR;
        default:
            if (hasStorm)
                return thundering ? THUNDER : RAIN;
            return CLEAR;
        }
    }

    public boolean isStormy() {
        return stormy;
    }

    public boolean matches(Weather sky) {
        if (stormy && sky == STORM)
            return true;
        return this == sky;
    }

    public static Weather parse(String storm) {
        return nameLookup.get(storm.toUpperCase());
    }

    public static Map<Weather, Boolean> parseFrom(ConfigurationNode node,
            Map<Weather, Boolean> def) {
        List<String> weather = OtherDropsConfig.getMaybeList(node, "weather");
        if (weather.isEmpty())
            return def;
        Map<Weather, Boolean> result = new HashMap<Weather, Boolean>();
        result.put(null, OtherDropsConfig.containsAll(weather));
        for (String name : weather) {
            Weather storm = parse(name);
            if (storm != null)
                result.put(storm, true);
            else if (name.startsWith("-")) {
                result.put(null, true);
                storm = parse(name.substring(1));
                if (storm == null) {
                    Log.logWarning("Invalid weather " + name + "; skipping...");
                    continue;
                }
                result.put(storm, false);
            } else {
                Log.logWarning("Invalid weather " + name + "; skipping...");
            }
        }
        if (result.isEmpty())
            return null;
        return result;
    }
}
