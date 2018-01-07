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

package com.gmail.zariust.otherdrops.drop;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.options.DoubleRange;
import com.gmail.zariust.otherdrops.options.IntRange;
import com.gmail.zariust.otherdrops.subject.Target;

import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;

public class ExperienceDrop extends DropType {
    private IntRange total;
    private int      rolledXP;

    public ExperienceDrop(IntRange amount, double chance) {
        super(DropCategory.EXPERIENCE, chance);
        total = amount;
    }

    @Override
    protected DropResult performDrop(Target source, Location from,
            DropFlags flags) {
        DropResult dropResult = DropResult.fromOverride(this.overrideDefault);
        dropResult.setOverrideDefaultXp(true);

        rolledXP = total.getRandomIn(flags.rng);
        if (flags.spread) {
            int amount = rolledXP, digit = 10;
            while (amount > 0) {
                int inThis = amount % digit;
                amount -= inThis;
                digit *= 10;
                if (inThis > 0) {
                    ExperienceOrb orb = from.getWorld().spawn(from,
                            ExperienceOrb.class);
                    orb.setExperience(inThis);
                }
            }
        } else {
            ExperienceOrb orb = from.getWorld()
                    .spawn(from, ExperienceOrb.class);
            orb.setExperience(rolledXP);
        }
        dropResult.setQuantity(1);
        return dropResult;
    }

    @Override
    public double getAmount() {
        return rolledXP;
    }

    @Override
    public String getName() {
        return "XP";
    }

    public static DropType parse(String drop, String data, IntRange amount,
            double chance) {
        String[] split = null;
        if (drop.matches("\\w+:.*")) {
            split = drop.split(":", 2);
        } else
            split = drop.split("@", 2);

        if (split.length > 1)
            data = split[1];
        if (!split[0].equalsIgnoreCase("XP"))
            return null;
        if (!data.isEmpty())
            Log.logWarning("Possible invalid data for " + split[0] + ": "
                    + data + " (data not currently supported)",
                    Verbosity.HIGHEST);
        return new ExperienceDrop(amount, chance);
    }

    @Override
    public DoubleRange getAmountRange() {
        return total.toDoubleRange();
    }

}
