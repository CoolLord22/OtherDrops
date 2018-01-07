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

package com.gmail.zariust.otherdrops.subject;

import static com.gmail.zariust.common.Verbosity.*;

import com.gmail.zariust.otherdrops.Log;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public abstract class LivingSubject implements Agent, Target {
    private Entity entity;

    protected LivingSubject(Entity e) {
        entity = e;
    }

    @Override
    public void setTo(BlockTarget replacement) {
        if (entity == null) {
            Log.logWarning("LivingSubject had a null entity; could not remove it and replace with blocks.");
            return;
        }
        // TODO: A way to replace the blocks in all the locations they occupy?
        Block bl = entity.getLocation().getBlock();
        new BlockTarget(bl).setTo(replacement);
        entity.remove();
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    public Location getLocation() {
        if (entity == null) {
            Log.logInfo(
                    "LivingSubject.getLocation() - agent is null, this shouldn't happen.",
                    HIGH);
            return null;
        }
        if (entity != null)
            return entity.getLocation();
        return null;
    }
}
