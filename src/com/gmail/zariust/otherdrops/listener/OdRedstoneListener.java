package com.gmail.zariust.otherdrops.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.MaterialData;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.event.OccurredEvent;

public class OdRedstoneListener implements Listener {
    private final OtherDrops parent;

    public OdRedstoneListener(OtherDrops instance) {
        parent = instance;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        Log.logInfo("RedstoneEvent: before checks.", Verbosity.EXTREME);

        Block poweredBlock = event.getBlock();
        Material poweredBlockDataValue = poweredBlock.getType();
        MaterialData poweredBlockMetaValue = poweredBlock.getState().getData();
        Log.dMsg("Block Type: " + poweredBlockDataValue + ":" + poweredBlockMetaValue + " (current=" + event.getNewCurrent() + ")");

        if (OtherDropsConfig.dropForRedstoneTrigger) {
            if ((event.getOldCurrent() - event.getNewCurrent()) > 0) { // POWER
                                                                       // decreasing
                OccurredEvent drop = new OccurredEvent(event, poweredBlock);
                parent.sectionManager.performDrop(drop);
            } else { // POWER increasing
                OccurredEvent drop = new OccurredEvent(event, poweredBlock,
                        "UP");
                parent.sectionManager.performDrop(drop);
            }
            // Nothing done if newcurrent == oldcurrent as this wouldn't trigger
            // the event

            if (OtherDropsConfig.globalRedstonewireTriggersSurrounding
                    && poweredBlock.getType() == Material.REDSTONE_WIRE) {
                callOdEvent(event, poweredBlock.getRelative(BlockFace.NORTH));
                callOdEvent(event, poweredBlock.getRelative(BlockFace.EAST));
                callOdEvent(event, poweredBlock.getRelative(BlockFace.WEST));
                callOdEvent(event, poweredBlock.getRelative(BlockFace.SOUTH));
            }
        }
    }

    private void callOdEvent(BlockRedstoneEvent event, Block block) {
        // avoid powerable blocks (otherwise we'd double up since they also get
        // a redstonechange event) and AIR
        if (!isRedStone(block.getType()) && block.getType() != Material.AIR) {
            if ((event.getOldCurrent() - event.getNewCurrent()) > 0) { // POWER
                                                                       // decreasing
                OccurredEvent drop = new OccurredEvent(event, block);
                parent.sectionManager.performDrop(drop);
            } else { // POWER increasing
                OccurredEvent drop = new OccurredEvent(event, block, "UP");
                parent.sectionManager.performDrop(drop);

            }
        }

    }

    /**
     * Return if the type of material is "powerable". Bit of a hack but best we
     * can do for now.
     * 
     * @param type
     * @return
     */
    private boolean isRedStone(Material type) {
        if (type == Material.REDSTONE_WIRE || type == Material.STONE_BUTTON
                || type == Material.WOOD_BUTTON || type == Material.LEVER
                || type == Material.TRIPWIRE_HOOK
                || type == Material.POWERED_RAIL
                || type == Material.PISTON_BASE
                || type == Material.PISTON_STICKY_BASE
                || type == Material.REDSTONE_TORCH_OFF
                || type == Material.REDSTONE_TORCH_ON) {
            return true;
        }
        return false;
    }
}
