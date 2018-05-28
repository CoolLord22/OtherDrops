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

import static com.gmail.zariust.common.Verbosity.HIGHEST;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Dependencies;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class OdBlockListener implements Listener {
    private final OtherDrops parent;

    public OdBlockListener(OtherDrops instance) {
        parent = instance;
    }

    public Boolean checkWorldguardLeafDecayPermission(Block block) {
        if (Dependencies.hasWorldGuard()) {
            // WORLDGUARD: check to see if leaf decay is allowed...
            // Need to convert the block (it's location) to a WorldGuard Vector
            Location loc = block.getLocation();
            Vector pt = new Vector(loc.getX(), loc.getY(), loc.getZ());

            // Get the region manager for this world
            RegionManager regionManager = Dependencies.getWorldGuard().getRegionContainer().get(block.getWorld());
            // Get the "set" for this location
            ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
            // If leaf decay is not allowed, just exit this function
            if (!set.testState(null, DefaultFlag.LEAF_DECAY)) {
                Log.logWarning("Leaf decay denied - worldguard protected region.");
                return false;
            }
        }
        Log.logInfo("Leaf decay allowed.", HIGHEST);
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (event.isCancelled())
            return;
        if (!OtherDropsConfig.dropForBlocks)
            return;
        if (!checkWorldguardLeafDecayPermission(event.getBlock()))
            return;

        OccurredEvent drop = new OccurredEvent(event);
        parent.sectionManager.performDrop(drop);
    }

    private boolean checkBlockProtected(Block block) {
        if (Dependencies.hasMobArena()) {
            if (Dependencies.getMobArenaHandler().inEnabledRegion(
                    block.getLocation()))
                return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        if (checkBlockProtected(event.getBlock()))
            return;

        if (event.getPlayer() != null)
            if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                Log.logInfo(
                        "BlockBreak: player is null or in creative mode, skipping.",
                        Verbosity.EXTREME);
                // skip drops for creative mode - TODO: make this configurable?
            } else {
                OccurredEvent drop = new OccurredEvent(event);
                parent.sectionManager.performDrop(drop);
            }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.isCancelled())
            return;
        if (!OtherDropsConfig.enableBlockTo)
            return;

        OccurredEvent drop = new OccurredEvent(event);
        parent.sectionManager.performDrop(drop);
    }
}