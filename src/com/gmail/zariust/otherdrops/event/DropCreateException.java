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
import java.util.List;

/**
 * Thrown when you try to create an OccurredDrop with a target or agent that
 * cannot fully represent the drop. Such targets or agents may be identified by
 * the ConfigOnly annotation.
 */
public class DropCreateException extends Exception {
    private static final long serialVersionUID = 6912695040940051952L;

    public DropCreateException(Class<?> error, Class<?>[] suggest) {
        super("Can't use the class " + error.getSimpleName()
                + " as the target or agent of an OccurredDrop; "
                + "try one of " + toList(suggest) + " instead.");
    }

    private static List<String> toList(Class<?>[] suggest) {
        List<String> suggestions = new ArrayList<String>();
        for (Class<?> clazz : suggest)
            suggestions.add(clazz.getSimpleName());
        return suggestions;
    }
}
