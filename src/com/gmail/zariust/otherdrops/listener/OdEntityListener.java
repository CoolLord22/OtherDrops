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

import static com.gmail.zariust.common.Verbosity.EXTREME;
import static com.gmail.zariust.common.Verbosity.HIGH;
import static com.gmail.zariust.common.Verbosity.HIGHEST;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.event.OccurredEvent;

public class OdEntityListener implements Listener {
    private final OtherDrops parent;

    public OdEntityListener(OtherDrops instance) {
        parent = instance;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;
        Log.logInfo("OnEntityDamage (victim: " + event.getEntity().toString()
                + ")", EXTREME);

        // Check if the damager is a player - if so, weapon is the held tool
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if (e.getEntity() == null) {
                Log.logInfo("EntityDamageByEntity but .getEntity() is null?");
                return;
            }
        }
        OccurredEvent drop = new OccurredEvent(event, "hit");
        parent.sectionManager.performDrop(drop);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        // TODO: use get getLastDamageCause rather than checking on each
        // getdamage?
        Log.logInfo("*** OnEntityDeath, before checks (victim: "
                + event.getEntity().toString() + ")", HIGHEST);
        Entity entity = event.getEntity();

        // If there's no damage record, ignore
        if (entity.getLastDamageCause() == null) {
            Log.logWarning("OnEntityDeath: entity " + entity.toString()
                    + " has no 'lastDamageCause'.", HIGH);
            return;
        }

        OccurredEvent drop = new OccurredEvent(event);
        Log.logInfo("EntityDeath drop occurance created. (" + drop.toString()
                + ")", HIGHEST);
        parent.sectionManager.performDrop(drop);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        // TODO: Why was this commented out?
        if (!parent.config.customDropsForExplosions)
            return;
        if (event.isCancelled())
            return;

        // Disable certain types of drops temporarily since they can cause
        // feedback loops
        // Note: This will disable ALL plugins that create explosions in the
        // same way as the explosion event
        if (event.getEntity() == null) {
            Log.logInfo("EntityExplode - no entity found, skipping.", HIGHEST);
            return; // skip recursive explosions, for now (explosion event has
                    // no entity) TODO: add an option?
        }

        // TODO: add a config item to enable enderdragon explosions if people
        // want to use it with v.low chance drops
        if (event.getEntity() instanceof EnderDragon)
            return; // Enderdragon explosion drops will lag out the server....

        Log.logInfo("Processing explosion...", HIGHEST);
        parent.sectionManager.performDrop(new OccurredEvent(event, event.getEntity()));

        Log.logInfo(
                "EntityExplode occurance detected - drop occurences will be created for each block.",
                HIGHEST);

        List<Block> blockListCopy = new ArrayList<Block>();
        blockListCopy.addAll(event.blockList());

        for (Block block : blockListCopy) {
            OccurredEvent drop = new OccurredEvent(event, block);
            parent.sectionManager.performDrop(drop);
            if (drop.isDenied())
                event.blockList().remove(block);
        }
    }
}
