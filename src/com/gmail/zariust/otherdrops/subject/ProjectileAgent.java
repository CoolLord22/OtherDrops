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

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.Inventory;

import static com.gmail.zariust.common.Verbosity.*;
import com.gmail.zariust.common.CommonEntity;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.ToolDamage;

public class ProjectileAgent implements Agent {
    private LivingSubject creature;
    private boolean       dispenser;
    private Material      mat;
    private Integer       durability;
    Projectile            agent;

    public ProjectileAgent() { // The wildcard
        this(null, false);
    }

    public ProjectileAgent(Material missile, boolean isDispenser) { // True =
                                                                    // dispenser,
                                                                    // false =
                                                                    // partial
                                                                    // wildcard
        this(null, missile, null, isDispenser);
    }

    public ProjectileAgent(Material missile, EntityType shooter) { // Shot by a
                                                                   // creature
        this(null, missile, new CreatureSubject(shooter), false);
    }

    public ProjectileAgent(Material missile, String shooter) { // Shot by a
                                                               // player
        this(null, missile, new PlayerSubject(shooter), false);
    }

    public ProjectileAgent(Projectile missile) { // For actual drops that have
                                                 // already occurred
        this( // Sorry, this is kinda complex here; why must Java insist this()
              // be on the first line?
                missile, getProjectileType(missile), // Get the Material
                                                     // representing the type of
                                                     // projectile
                getShooterAgent(missile), // Get the LivingAgent representing
                                          // the shooter
                missile.getShooter() == null // If shooter is null, it's a
                                             // dispenser
        );
    }

    private ProjectileAgent(Projectile missile, Material missileMat,
            LivingSubject shooter, boolean isDispenser) { // The Rome
                                                          // constructor
        agent = missile;
        mat = missileMat;
        creature = shooter;
        dispenser = isDispenser;
    }

    public ProjectileAgent(Material missile, int parseInt) {
        this(null, missile, null, false);
        durability = parseInt;
    }

    private static Material getProjectileType(Projectile missile) {
        return CommonEntity.getProjectileType(missile);
    }

    private static LivingSubject getShooterAgent(Projectile missile) {
        // Get the LivingAgent representing the shooter, which could be null, a
        // CreatureAgent, or a PlayerAgent
    	LivingEntity shooter = null;
    	if (missile.getShooter() instanceof LivingEntity)
    		shooter = (LivingEntity) missile.getShooter();
        if (shooter == null)
            return null;
        else if (shooter instanceof Player)
            return new PlayerSubject((Player) shooter);
        else
            return new CreatureSubject(shooter);

    }

    private static Data getShooterData(LivingEntity shooter) {
        return CreatureData.parse(shooter);
    }

    private static EntityType getShooterType(LivingEntity shooter) {
        return shooter.getType();
    }

    private ProjectileAgent equalsHelper(Object other) {
        if (!(other instanceof ProjectileAgent))
            return null;
        return (ProjectileAgent) other;
    }

    private boolean isEqual(ProjectileAgent tool) {
        if (tool == null)
            return false;

        // if mat = null treat as wildcard, ie. match true, otherwise compare
        // mat vs tool.mat
        boolean matMatches = (mat == null) ? true : mat == tool.mat;

        if (durability != null) {
            if (tool.agent != null && tool.agent instanceof ThrownPotion) {
                if (!(durability == ((ThrownPotion)tool.agent).getItem().getDurability())) return false;
            }
        }

        if (dispenser) {
            if (tool.creature == null) { // FIXME: confirm this works -
                                         // DISPENSERs return null?
                return matMatches;
            } else {
                return false;
            }
        }

        if (creature == null) { // this means no values attached after config
                                // (eg. not PROJECTILE_ARROW@PLAYER), or
                                // DISPENSER
            return matMatches;

        } else {
            // TODO: here we want to check if "tool.creature" is a player to
            // match PROJECTILE_ARROW@PLAYER
            if (creature instanceof PlayerSubject) {
                if (((PlayerSubject) creature).getPlayer() == null) {
                    // match any player
                    if ((tool.creature instanceof PlayerSubject)) {
                        return matMatches;
                    } else {
                        return false;
                    }
                } else {
                    return creature.equals(tool.creature) && matMatches;
                }
            } else {
                return creature.equals(tool.creature) && matMatches;
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        ProjectileAgent tool = equalsHelper(other);
        return isEqual(tool);
    }

    @Override
    public boolean matches(Subject other) {
        ProjectileAgent tool = equalsHelper(other);
        // if(mat == null) return true;
        if (tool == null) {
            Log.logInfo("ProjectileAgent.matches - tool is null...", HIGH);
            return false; // No tool = false?
        }
        if (dispenser && tool.dispenser)
            return true; // FIXME: npe on this line sometimes (skeleton kills
                         // skeleton?)
        else
            return isEqual(tool);
    }

    @Override
    public int hashCode() {
        return new HashCode(this).setData(creature).get(mat);
    }

    public LivingSubject getShooter() {
        return creature;
    }

    public Material getProjectile() {
        return mat;
    }

    @Override
    public void damageTool(ToolDamage damage, Random rng) {
        // TODO: Probably the best move here is to drain items much like a bow
        // drains arrows? But how to know which item?
        // Currently defaulting to the materials associated with each projectile
        // in CommonEntity
        @SuppressWarnings("unused")
		Inventory inven;
        if (agent.getShooter() == null) { // Dispenser!
            // TODO: How to retrieve the source dispenser?
            inven = null;
        } else if (agent.getShooter() instanceof Player) {
            inven = ((Player) agent.getShooter()).getInventory();
        } else
            return;
        // TODO: Now remove damage-1 of mat from inven

        // TODO: Option of failure if damage is greater that the amount
        // remaining?
    }

    @Override
    public void damage(int amount) {
        // FIXME: why is this sometimes null? Is it ok?
        if (agent.getShooter() == null)
            return;
        ((LivingEntity) agent.getShooter()).damage(amount);
    }

    public EntityType getCreature() {
        return getShooterType((LivingEntity) agent.getShooter());
    }

    public Data getCreatureData() {
        return getShooterData((LivingEntity) agent.getShooter());
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.PROJECTILE;
    }

    public static Agent parse(String name, String data) {
        if (name.equalsIgnoreCase("PROJECTILE"))
            name = "PROJECTILE_ANY";

        name = name.toUpperCase().replace("PROJECTILE_", "");
        Material mat;
        @SuppressWarnings("unused")
		String checkName = name.toUpperCase().replaceAll("[\\s-_]", "");
        // TODO: parse by projectile names for future compatibility
        if (name.equals("FIRE") || name.equals("FIREBALL"))
            mat = Material.FIRE;
        else if (name.equals("POTION"))
            mat = Material.POTION;
        else if (name.equals("SNOWBALL"))
            mat = Material.SNOW_BALL;
        else if (name.equals("EGG"))
            mat = Material.EGG;
        else if (name.equals("FISH") || name.equals("FISHINGROD"))
            mat = Material.FISHING_ROD;
        else if (name.equals("ARROW"))
            mat = Material.ARROW;
        else if (name.equals("SPECTRALARROW"))
            mat = Material.SPECTRAL_ARROW;
        else if (name.equals("TIPPEDARROW"))
            mat = Material.TIPPED_ARROW;
        else if (name.equals("ENDERPEARL"))
            mat = Material.ENDER_PEARL;
        else if (name.equals("WITHERSKULL"))
            mat = Material.SKULL;
        else if (name.equals("EXPBOTTLE"))
            mat = Material.EXP_BOTTLE;
        else if (name.equals("ANY"))
            mat = null;
        else {
            Log.logInfo("Unknown projectile: " + name, Verbosity.NORMAL);
            return null;
        }
        // Parse data, which is one of the following
        // - A EntityType constant (note that only GHAST and SKELETON will
        // actually do anything
        // unless there's some other plugin making entities shoot things)
        // - One of the special words PLAYER or DISPENSER
        // - Something else, which is taken to be a player name
        // - Nothing
        if (data.isEmpty())
            return new ProjectileAgent(mat, false); // Specific projectile, any
                                                    // shooter
        if (data.equalsIgnoreCase("DISPENSER"))
            return new ProjectileAgent(mat, true);
        else if (data.startsWith("PLAYER")) {
            String[] dataSplit = data.split(";");
            String playerName = null;
            if (dataSplit.length > 1) {
                playerName = dataSplit[1];
            }

            return new ProjectileAgent(mat, playerName);

        }

        EntityType creature = CommonEntity.getCreatureEntityType(data);
        if (creature != null)
            return new ProjectileAgent(mat, creature);
        else if (data.matches("[0-9]+"))
            return new ProjectileAgent(mat, Integer.parseInt(data));
        else
            return new ProjectileAgent(mat, data);
    }

    @Override
    public Location getLocation() {
        if (agent == null) {
            Log.logInfo(
                    "ProjectileAgent.getLocation() - agent is null, this shouldn't happen.",
                    HIGH);
            return null;
        }
        if (agent.getShooter() instanceof LivingEntity)
            return ((LivingEntity) agent.getShooter()).getLocation();
        return null;
    }

    @Override
    public String toString() {
        String ret = "";
        if (mat == null)
            ret = "ANY_PROJECTILE";
        else
            ret = "PROJECTILE_" + mat.toString();
        if (dispenser)
            ret += "@DISPENSER";
        else if (creature != null) {
            ret += "@";
            if (creature instanceof PlayerSubject)
                ret += "PLAYER";
            else if (creature instanceof CreatureSubject)
                ret += ((CreatureSubject) creature).getCreature();
            else
                ret += "???";
        }
        return ret;
    }

    @Override
    public Data getData() {
        return null;
    }

    @Override
    public String getReadableName() {
        if (mat == null)
            return "ANY_PROJECTILE";
        String prefix = "a ";
        if (mat == Material.ARROW)
            prefix = "an ";
        String readableName = prefix
                + mat.toString().toLowerCase().replaceAll("[-_]", " ");
        return readableName;
    }

}
