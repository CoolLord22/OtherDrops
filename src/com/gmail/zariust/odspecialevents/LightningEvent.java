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

package com.gmail.zariust.odspecialevents;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;
import com.gmail.zariust.otherdrops.special.SpecialResult;

public class LightningEvent extends SpecialResult {
    private boolean harmless, player;

    public LightningEvent(WeatherEvents source) {
        super("LIGHTNING", source);
    }

    @Override
    public void executeAt(OccurredEvent event) {
        Location location = null;
        if (player)
            location = event.getTool().getLocation();
        if (location == null)
            location = event.getLocation();
        World world = location.getWorld();
        if (harmless)
            world.strikeLightningEffect(location);
        else
            world.strikeLightning(location);
    }

    @Override
    public void interpretArguments(List<String> args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("HARMLESS")) {
                harmless = true;
                used(arg);
            } else if (arg.equalsIgnoreCase("PLAYER")) {
                player = true;
                used(arg);
            }
        }
    }

    @Override
    public boolean canRunFor(SimpleDrop drop) {
        return true;
    }

    @Override
    public boolean canRunFor(OccurredEvent drop) {
        return true;
    }
}
