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

import static com.gmail.zariust.common.CommonPlugin.enumValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.gmail.zariust.common.CommonEntity;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.ToolDamage;

public class EnvironmentAgent implements Agent {
    private final List<DamageCause> dmg;
    private final Object            extra;

    // TODO: Need auxiliary data?

    public EnvironmentAgent() {
        this(null, null);
    }

    public EnvironmentAgent(DamageCause tool) {
        dmg = new ArrayList<DamageCause>();
        dmg.add(tool);
        this.extra = null;
    }

    public EnvironmentAgent(List<DamageCause> tool) {
        this(tool, null);
    }

    public EnvironmentAgent(List<DamageCause> tool, Object extra) {
        dmg = tool;
        this.extra = extra;
    }

    private EnvironmentAgent equalsHelper(Object other) {
        if (!(other instanceof EnvironmentAgent))
            return null;
        return (EnvironmentAgent) other;
    }

    private boolean isEqual(EnvironmentAgent tool) {
        if (tool == null)
            return false;
        boolean match = false;
        for (DamageCause cause : tool.dmg) {
            if (dmg.contains(cause))
                match = true;
        }
        return match;
    }

    @Override
    public boolean equals(Object other) {
        EnvironmentAgent tool = equalsHelper(other);
        return isEqual(tool);
    }

    @Override
    public boolean matches(Subject other) {
        // TODO: Is this right? Will all creature/player agents coincide with
        // ENTITY_ATTACK and all projectile
        // agents with PROJECTILE?
        if (dmg == null)
            return true;
        if (dmg.contains(DamageCause.ENTITY_ATTACK)
                && (other instanceof CreatureSubject || other instanceof PlayerSubject))
            return true;
        else if (dmg.contains(DamageCause.PROJECTILE)
                && other instanceof ProjectileAgent)
            return true;
        EnvironmentAgent tool = equalsHelper(other);
        return isEqual(tool);
    }

    @Override
    public int hashCode() {
        return new HashCode(this).get(dmg);
    }

    public List<DamageCause> getDamageCauses() {
        return dmg;
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.DAMAGE;
    }

    @Override
    public void damage(int amount) {
    }

    @Override
    public void damageTool(ToolDamage amount, Random rng) {
    }

    public static EnvironmentAgent parse(String name, String data) {
        name = name.toUpperCase().replace("DAMAGE_", "");
        List<DamageCause> causes = new ArrayList<DamageCause>();
        try {
            DamageCause enumCause = enumValue(DamageCause.class, name);
            if (enumCause != null)
                causes.add(enumCause);
            // if(cause == DamageCause.FIRE_TICK || cause == DamageCause.CUSTOM)
            // return null;
            // else if(cause == DamageCause.FIRE) cause = DamageCause.FIRE_TICK;
            // // FIRE can be a valid environmental death
        } catch (IllegalArgumentException e) {
        }
        if (causes.isEmpty()) {
            if (name.equals("WATER"))
                causes.add(DamageCause.CUSTOM);
            else if (name.equals("BURN")) {
                causes.add(DamageCause.FIRE_TICK);
                causes.add(DamageCause.FIRE);
                causes.add(DamageCause.LAVA);
            } else
                return null;
        }
        // else return null;
        // TODO: Make use of this, somehow
        Object extra = parseData(name, data);
        return new EnvironmentAgent(causes, extra);
    }

    private static Object parseData(String name, String data) {
        if (name.equalsIgnoreCase("SUFFOCATION")
                || name.equalsIgnoreCase("BLOCK_EXPLOSION")
                || name.equalsIgnoreCase("CONTACT")) {
            // TODO: Specify block?
            return Material.getMaterial(data);
        } else if (name.equalsIgnoreCase("ENTITY_ATTACK")
                || name.equalsIgnoreCase("ENTITY_EXPLOSION")) {
            // TODO: Specify entity?
            EntityType creature = CommonEntity.getCreatureEntityType(data);
            if (creature != null)
                return creature;
            if (data.equalsIgnoreCase("PLAYER"))
                return ItemCategory.PLAYER;
            if (data.equalsIgnoreCase("FIREBALL"))
                return ItemCategory.EXPLOSION;
        } else if (name.equalsIgnoreCase("FALL")) {
            // TODO: Specify distance?
            if (data.isEmpty())
                data = "0";
            return Integer.parseInt(data);
        }
        return null;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public String toString() {
        if (dmg == null)
            return "ANY_DAMAGE";
        return dmg.toString();
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public String getReadableName() {
        return toString();
    }

}
