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

import org.bukkit.entity.Sheep;

import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;
import com.gmail.zariust.otherdrops.special.SpecialResult;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;

public class ShearEvent extends SpecialResult {
    private Boolean state;

    public ShearEvent(SheepEvents source, Boolean b) {
        super(b == null ? "SHEARTOGGLE" : (b ? "" : "UN") + "SHEAR", source);
        state = b;
    }

    @Override
    public void executeAt(OccurredEvent event) {
        CreatureSubject target = (CreatureSubject) event.getTarget();
        Sheep sheep = (Sheep) target.getAgent();
        boolean newState;
        if (state == null)
            newState = !sheep.isSheared();
        else
            newState = state;
        sheep.setSheared(newState);
    }

    @Override
    public void interpretArguments(List<String> args) {
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
