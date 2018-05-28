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

package com.gmail.zariust.otherdrops.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;

public class OdPistonListener implements Listener {
    @SuppressWarnings("unused")
	private OtherDrops parent;

    public OdPistonListener(OtherDrops instance) {
        parent = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled())
            return;

        // OccurredEvent drop = new OccurredEvent(event);
        // parent.performDrop(drop);
        Log.logInfo("PistonListener: extended.", Verbosity.HIGH);
        Log.logInfo(event.getBlock().getType().toString());
        Log.logInfo(event.getBlock().getRelative(event.getDirection())
                .getType().toString());
        for (Block block : event.getBlocks()) {
            Log.logInfo(block.toString());
        }

        // TODO: allow a custom list of blocks to monitor for piston events, eg.
        // melon, crops, pumpkin, etc
    }
}
