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

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Sheep;

import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;
import com.gmail.zariust.otherdrops.special.SpecialResult;
import com.gmail.zariust.otherdrops.subject.Agent;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.ToolAgent;

public class DyeEvent extends SpecialResult {
    private DyeColor colour = null;

    public DyeEvent(SheepEvents source) {
        super("DYE", source);
    }

    @Override
    public void executeAt(OccurredEvent event) {
        DyeColor dye = DyeColor.PINK;
        if (colour == null) {
            Agent agent = event.getTool();
            if (agent instanceof PlayerSubject) {
                ToolAgent tool = ((PlayerSubject) agent).getTool();
                if (tool.getMaterial() == Material.INK_SACK)
                    dye = DyeColor.getByDyeData((byte) (0xF - tool.getData()
                            .getData()));
            }
            if (colour == null)
                dye = DyeColor.getByDyeData((byte) event.getRandom(16));
        } else
            dye = colour;
        CreatureSubject target = (CreatureSubject) event.getTarget();
        Sheep sheep = (Sheep) target.getAgent();
        sheep.setColor(dye);
    }

    @Override
    public void interpretArguments(List<String> args) {
        for (String arg : args) {
            try {
                colour = DyeColor.valueOf(arg);
                used(arg);
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Override
    public boolean canRunFor(SimpleDrop drop) {
        return SheepEvents.canRunFor(drop);
    }

    @Override
    public boolean canRunFor(OccurredEvent drop) {
        return SheepEvents.canRunFor(drop);
    }

}
