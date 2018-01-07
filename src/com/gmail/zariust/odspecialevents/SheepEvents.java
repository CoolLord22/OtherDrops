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

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.EntityType;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.event.AbstractDropEvent;
import com.gmail.zariust.otherdrops.special.SpecialResult;
import com.gmail.zariust.otherdrops.special.SpecialResultHandler;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;
import com.gmail.zariust.otherdrops.subject.Target;

public class SheepEvents extends SpecialResultHandler {
    @Override
    public SpecialResult getNewEvent(String name) {
        if (name.equalsIgnoreCase("SHEAR"))
            return new ShearEvent(this, true);
        else if (name.equalsIgnoreCase("UNSHEAR"))
            return new ShearEvent(this, false);
        else if (name.equalsIgnoreCase("SHEARTOGGLE"))
            return new ShearEvent(this, null);
        else if (name.equalsIgnoreCase("DYE"))
            return new DyeEvent(this);
        return null;
    }

    @Override
    public void onLoad() {
        logInfo("Sheep v" + getVersion() + " loaded.", Verbosity.HIGH);
    }

    @Override
    public List<String> getEvents() {
        return Arrays.asList("SHEAR", "UNSHEAR", "SHEARTOGGLE", "DYE");
    }

    @Override
    public String getName() {
        return "Sheep";
    }

    public static boolean canRunFor(AbstractDropEvent drop) {
        Target target = drop.getTarget();
        if (!(target instanceof CreatureSubject))
            return false;
        CreatureSubject creature = (CreatureSubject) target;
        if (creature.getCreature() != EntityType.SHEEP)
            return false;
        if (creature.getAgent() != null && creature.getAgent().isDead())
            return false;
        return true;
    }

}
