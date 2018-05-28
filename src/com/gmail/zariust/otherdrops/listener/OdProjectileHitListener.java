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

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.event.OccurredEvent;

public class OdProjectileHitListener implements Listener {
    private final OtherDrops parent;

    public OdProjectileHitListener(OtherDrops instance) {
        parent = instance;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        World world = projectile.getWorld();
        BlockIterator iterator = new BlockIterator(world, projectile.getLocation().toVector(), projectile.getVelocity().normalize(), 0, 4);
        Block hitBlock = null;

        while(iterator.hasNext()) {
            hitBlock = iterator.next();
            if(hitBlock.getType() != Material.AIR) //Check all non-solid blockid's here.
                break;
        }

//        Log.logInfo("ProjectileHitEvent: "+projectile.toString() +" hit "+ hitBlock.toString());

        OccurredEvent drop = new OccurredEvent(event, hitBlock);
        parent.sectionManager.performDrop(drop);
    }

}
