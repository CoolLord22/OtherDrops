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

package com.gmail.zariust.otherdrops.event;

import com.gmail.zariust.otherdrops.parameters.Trigger;
import com.gmail.zariust.otherdrops.subject.Target;

public class GroupDropEvent extends CustomDrop {
    private String    name;
    private DropsList list = null;

    public GroupDropEvent(Target targ, Trigger trigger) {
        super(targ, trigger);
        setList(new DropsList());
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getDropName() {
        return "Dropgroup " + name;
    }

    public void setDrops(DropsList drops) {
        this.setList(drops);
    }

    public DropsList getDrops() {
        return getList();
    }

    public void add(CustomDrop drop) {
        getList().add(drop);
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    public void sort() {
        getList().sort();
    }

    @Override
    public void run() {
        ExclusiveMap exclusives = new ExclusiveMap(getList(), this);
        for (CustomDrop drop : getList()) {
            if (!drop.matches(currentEvent))
                continue;
            if (drop.willDrop(exclusives))
                drop.perform(currentEvent);
        }
    }

    public void setList(DropsList list) {
        this.list = list;
    }

    public DropsList getList() {
        return list;
    }

}
