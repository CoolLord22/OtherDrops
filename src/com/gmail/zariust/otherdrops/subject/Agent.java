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

import com.gmail.zariust.otherdrops.options.ToolDamage;

/**
 * An agent which may affect or act on a target to produce a drop.
 */
public interface Agent extends Subject {
    /**
     * Do some damage to this agent.
     * 
     * @param amount
     *            The amount of damage.
     */
    public void damage(int amount);

    /**
     * Do some damage to this agent's tool, if it has one.
     * 
     * @param amount
     *            The amount of damage.
     * @param rng
     *            Random number generator
     */
    public void damageTool(ToolDamage amount, Random rng);
}
