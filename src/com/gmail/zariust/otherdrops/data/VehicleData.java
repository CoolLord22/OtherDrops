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

package com.gmail.zariust.otherdrops.data;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.drop.CreatureDrop;
import com.gmail.zariust.otherdrops.drop.DropResult;
import com.gmail.zariust.otherdrops.drop.DropType;
import com.gmail.zariust.otherdrops.drop.DropType.DropFlags;
import com.gmail.zariust.otherdrops.options.IntRange;
import com.gmail.zariust.otherdrops.subject.Target;

public class VehicleData implements Data {
    public enum VehicleState {
        EMPTY, PLAYER, OCCUPIED
    };

    CreatureDrop creature;
    // This flag has meaning only if creature is null
    // null = occupied by something, false = empty, true = occupied by player
    // null = may or may not be occupied
    VehicleState state;

    public VehicleData(Vehicle vehicle) {
        List<Entity> passenger = vehicle.getPassengers();
        for(Entity ent : passenger) {
            if (ent instanceof Player)
                state = VehicleState.PLAYER;
            else if (ent != null)
                creature = new CreatureDrop(ent.getType());
            if (creature == null && state == null)
                state = VehicleState.EMPTY;
        }
    }

    public VehicleData(VehicleState flag) {
        creature = null;
        state = flag;
    }

    public VehicleData(CreatureDrop type) {
        creature = type;
    }

    @Override
    public int getData() {
        if (creature == null)
            return state == null ? 0 : -state.ordinal();
        return creature.getCreature().ordinal() + 1;
    }

    @Override
    public void setData(int d) {
        if (d > 0)
            creature = new CreatureDrop(EntityType.values()[d - 1]);
        else {
            creature = null;
            if (d > -VehicleState.values().length)
                state = VehicleState.values()[-d];
            else
                state = VehicleState.EMPTY;
        }
    }

    @Override
    public boolean matches(Data d) {
        // TODO: This comparison is a bit convoluted; need to verify it really
        // works
        if (!(d instanceof VehicleData))
            return false;
        VehicleData vehicle = (VehicleData) d;
        if (creature == null) {
            // If creature and state are both null, it matches any vehicle data
            // (Though this should not occur in practice.)
            if (state == null)
                return true;
            switch (state) {
            case EMPTY: // If state is empty, it only matches empty
                return vehicle.creature == null
                        && vehicle.state == VehicleState.EMPTY;
            case OCCUPIED: // If state is occupied, it matches anything except
                           // empty
                return vehicle.creature != null
                        || vehicle.state != VehicleState.EMPTY;
            case PLAYER: // If state is player, it only matches player
                return vehicle.creature == null
                        && vehicle.state == VehicleState.PLAYER;
            }
        }
        // Otherwise, must be the same creature
        return creature == vehicle.creature;
    }

    @Override
    public String get(Enum<?> mat) {
        if (mat == Material.BOAT || mat == Material.MINECART)
            return creature == null ? (state == null ? "" : state.toString()) : creature.toString();
        return "";
    }

    @Override
    public void setOn(Entity entity, Player witness) {
        Entity mob;
        if (creature == null) {
            if (state == VehicleState.EMPTY)
                return;
            mob = witness;
        } else {
            DropFlags flags = DropType.flags(witness, null, true, false,
                    OtherDrops.rng, "", "", "");
            DropResult dropResult = creature.drop(entity.getLocation(),
                    (Target) null, (Location) null, 1, flags);
            mob = dropResult.getDropped().get(dropResult.getDropped().size() - 1);
            // mob = entity.getWorld().spawnCreature(entity.getLocation(), creature);
        }
        entity.addPassenger(mob);
    }

    @Override
    // No vehicle has a block state, so nothing to do here.
    public void setOn(BlockState dummy) {
    }

    @SuppressWarnings("incomplete-switch")
    public static Data parse(Material mat, String state) {
        if (state == null || state.isEmpty())
            return null;
        switch (mat) {
        case MINECART:
            CreatureDrop creature = (CreatureDrop) CreatureDrop.parse(state,
                    "", new IntRange(1), 100);// CommonEntity.getCreatureEntityType(state);
            if (creature != null)
                return new VehicleData(creature);
            // Fallthrough intentional
        case BOAT:
            try {
                VehicleState vs = VehicleState.valueOf(state);
                return new VehicleData(vs);
            } catch (IllegalArgumentException e) {
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        if (creature == null) {
            if (state == null)
                return 0;
            return state.hashCode();
        }
        return creature.hashCode();
    }

    @Override
    public Boolean getSheared() {
        // TODO Auto-generated method stub
        return null;
    }
}
