package com.gmail.zariust.otherdrops.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.event.OccurredEvent;

public class OdSpawnListener implements Listener {
    private final OtherDrops                    parent;

    /**
     * otherdropSpawned: is a map of location.toString against entitytype.
     * OtherDrops DropType.drop() function should put an entry here of the
     * intended location and entitytype before it is spawned. This is to avoid
     * an infinite loop that can occur (eg. with config
     * "zombie: {- action: mobspawn, drop: zombie}")
     */
    public static final Map<String, EntityType> otherdropsSpawned = new HashMap<String, EntityType>();

    public OdSpawnListener(OtherDrops instance) {
        parent = instance;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled())
            return;
        Log.logInfo("SpawnEvent: before checks.  Spawned by "
                + event.getSpawnReason().toString(), Verbosity.EXTREME);

        // This listener should only be registered if "spawned" condition
        // exists, so tag creature
        event.getEntity().setMetadata(
                "CreatureSpawnedBy",
                new FixedMetadataValue(OtherDrops.plugin, event
                        .getSpawnReason().toString()));

        // Only run OccurredEvent/performDrop if "action: SPAWN" trigger used
        if (OtherDropsConfig.dropForSpawnTrigger) {
            if (event.getSpawnReason().equals(SpawnReason.CUSTOM)) {
                // If this is a custom drop make sure that there are no custom
                // drops using
                // this entity, to avoid an *infinite loop*!
                if (otherdropsSpawned.get(OdSpawnListener.getSpawnLocKey(event
                        .getLocation())) == event.getEntityType()) {
                    if (OtherDropsConfig.spawnTriggerIgnoreOtherDropsSpawn) { // defaults
                                                                              // to
                                                                              // true
                                                                              // unless
                                                                              // configured
                        Log.logInfo(
                                "SpawnEvent: ignoring spawn from OtherDrops (add spawntrigger_ignores_otherdrops_spawn: false to the config to override, but beware infinite loops).",
                                Verbosity.HIGH);
                        return;
                    }

                }
            }

            OccurredEvent drop = new OccurredEvent(event);
            parent.sectionManager.performDrop(drop);
        }
    }

    public static String getSpawnLocKey(Location loc) {
        return (loc.getWorld().toString() + "," + loc.getX() + "/" + loc.getY()
                + "/" + loc.getZ());
    }

}
