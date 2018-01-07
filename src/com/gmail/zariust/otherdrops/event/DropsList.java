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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.options.Flag;

public class DropsList implements Iterable<CustomDrop> {
    private List<CustomDrop>               list;
    private Map<String, Map<Data, Double>> keys;

    public DropsList() {
        list = new ArrayList<CustomDrop>();
    }

    @Override
    public String toString() {
        return list.toString();
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DropsList))
            return false;
        return list.equals(((DropsList) other).list);
    }

    public void add(CustomDrop drop) {
        list.add(drop);
    }

    @Override
    public Iterator<CustomDrop> iterator() {
        return list.listIterator();
    }

    public void sort() {
        // If we want to apply other sorting to the drops list, here is the
        // place to do so.
        Collections.sort(list, new UniqueSorter());
        // We also build up the exclusive keys data here
        keys = new HashMap<String, Map<Data, Double>>();
        for (CustomDrop event : list) {
            String key = event.getExclusiveKey();
            if (!keys.containsKey(key)) {
                keys.put(key, new HashMap<Data, Double>());
                keys.get(key).put(null, 0.0);
            }
            Data data = event.getTarget().getData();
            if (!keys.get(key).containsKey(data))
                keys.get(key).put(data, 0.0);
            double cumul = keys.get(key).get(data) + event.getChance();
            keys.get(key).put(data, cumul);
        }
        for (String key : keys.keySet()) {
            double cumul = keys.get(key).containsKey(null) ? keys.get(key).get(
                    null) : 0.0;
            for (Data data : keys.get(key).keySet()) {
                keys.get(key).put(data, cumul + keys.get(key).get(data));
                if (keys.get(key).get(data) < 100)
                    keys.get(key).put(data, 100.0);
            }
        }
    }

    public double getExclusiveTotal(String key, Data data) {
        if (!keys.containsKey(key))
            return 0;
        if (!keys.get(key).containsKey(data))
            return keys.get(key).get(null);
        return keys.get(key).get(data);
    }

    public class UniqueSorter implements Comparator<CustomDrop> {
        @Override
        public int compare(CustomDrop lhs, CustomDrop rhs) {
            boolean leftUnique = lhs.hasFlag(Flag.UNIQUE);
            boolean rightUnique = rhs.hasFlag(Flag.UNIQUE);
            if (leftUnique == rightUnique)
                return 0;
            else if (leftUnique)
                return -1;
            else if (rightUnique)
                return 1;
            return 0;
        }
    }
}
