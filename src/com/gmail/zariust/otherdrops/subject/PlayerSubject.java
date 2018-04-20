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

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.ToolDamage;

public class PlayerSubject extends LivingSubject {
    private ToolAgent tool;
    private String    name;
    private Player    agent;
    private boolean   anyObject;

    public PlayerSubject(boolean anyObject) {
        this((String) null);
        this.anyObject = anyObject;
    }

    public PlayerSubject() {
        this((String) null);
    }

    public PlayerSubject(String attacker) {
        this(null, attacker);
    }

    public PlayerSubject(Player attacker) {
        this(attacker.getInventory().getItemInMainHand(), attacker.getName(), attacker);
    }

    public PlayerSubject(ItemStack item, String attacker) {
        this(item, attacker, null);
    }

    public PlayerSubject(ItemStack item, String who, Player attacker) {
        super(attacker);
        tool = new ToolAgent(item);
        name = who;
        agent = attacker;
    }

    private PlayerSubject equalsHelper(Object other) {
        if (!(other instanceof PlayerSubject))
            return null;
        return (PlayerSubject) other;
    }

    private boolean isEqual(PlayerSubject player) {
        if (player == null)
            return false;
        return tool.equals(player.tool)
                && name.toUpperCase().equals(player.name.toUpperCase());
    }

    @Override
    public boolean equals(Object other) {
        PlayerSubject player = equalsHelper(other);
        return isEqual(player);
    }

    @Override
    public boolean matches(Subject other) {
        // ProjectileAgent could be a player, so check against it if neccessary
        if (!anyObject && other instanceof ProjectileAgent) {
            if (name == null)
                return ProjectileAgent.parse("PROJECTILE_ANY", "PLAYER")
                        .matches(other);
            else
                return ProjectileAgent
                        .parse("PROJECTILE_ANY", "PLAYER;" + name).matches(
                                other);
        }

        if (!(other instanceof PlayerSubject))
            return false;
        PlayerSubject player = equalsHelper(other);
        if (name == null)
            return true;
        else
            return isEqual(player);
    }

    @Override
    public int hashCode() {
        return new HashCode(this).get(name);
    }

    public Material getMaterial() {
        return tool.getMaterial();
    }

    @SuppressWarnings("deprecation")
	public Player getPlayer() {
        if (name == null)
            return null;
        if (agent == null)
            agent = Bukkit.getServer().getPlayer(name);
        return agent;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void damageTool(ToolDamage damage, Random rng) {
        if (damage == null)
            return;
        ItemStack stack = agent.getInventory().getItemInMainHand();
        if (stack == null)
            return;
        if (damage.apply(stack, rng))
            agent.getInventory().setItemInMainHand(null);
        else
            agent.updateInventory(); // because we've edited the stack directly
        // TODO: Option of failure if damage is greater that the amount
        // remaining?
    }

    @Override
    public void damage(int amount) {
        agent.damage(amount);
    }

    public ToolAgent getTool() {
        return tool;
    }

    @Override
    public Data getData() {
        return tool.getData();
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.PLAYER;
    }

    @Override
    public boolean overrideOn100Percent() {
        return false;
    }

    @Override
    public List<Target> canMatch() {
        return Collections.singletonList((Target) this);
    }

    @Override
    public String getKey() {
        return "PLAYER";
    }

    @Override
    public String toString() {
        if (name == null) {
            if (tool == null)
                return "PLAYER";
            return tool.toString();
        }
        return "PLAYER@" + name + " with " + tool.toString(); // TODO: does
                                                              // adding the tool
                                                              // here break
                                                              // anything?
    }

    public static PlayerSubject parse(String data) {
        if (data == null || data.isEmpty())
            return new PlayerSubject();
        return new PlayerSubject(data);
    }

    @Override
    public String getReadableName() {
        if (name == null)
            return "unknown player";

        return name;
    }

}
