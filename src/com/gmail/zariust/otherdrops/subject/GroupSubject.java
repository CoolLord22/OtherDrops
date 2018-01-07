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

import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.ConfigOnly;
import com.gmail.zariust.otherdrops.options.ToolDamage;

@ConfigOnly(PlayerSubject.class)
public class GroupSubject extends LivingSubject {
    private String group;

    public GroupSubject(String grp) {
        super(null);
        group = grp;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof GroupSubject))
            return false;
        GroupSubject targ = (GroupSubject) other;
        return group.equals(targ.group);
    }

    @Override
    public int hashCode() {
        return new HashCode(this).get(group);
    }

    @Override
    public boolean overrideOn100Percent() {
        return false;
    }

    @Override
    public boolean matches(Subject other) {
        if (!(other instanceof PlayerSubject))
            return false;
        PlayerSubject player = (PlayerSubject) other;
        List<String> playerGroups = OtherDrops.plugin.getGroups(player
                .getPlayer());
        return playerGroups.contains(group);
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.PLAYER;
    }

    @Override
    public void damage(int amount) {
    }

    @Override
    public void damageTool(ToolDamage amount, Random rng) {
    }

    @Override
    public List<Target> canMatch() {
        return Collections.singletonList((Target) new PlayerSubject());
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String toString() {
        if (group == null)
            return "PLAYERGROUP"; // shouldn't happen though
        return "PLAYERGROUP@" + group;
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
