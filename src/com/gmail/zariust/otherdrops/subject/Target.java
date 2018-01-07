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

import java.util.List;

/**
 * A target which may be affected or acted on in some way to produce a drop.
 */
public interface Target extends Subject {
    /**
     * Whether this target should drop its default drop even if a configured
     * drop has been triggered with a 100% chance. If it returns false, the only
     * way to prevent the target from producing its default drops is to provide
     * a specific NOTHING drop at 100%; if true, any drop at 100% cancels the
     * default drops.
     * 
     * @return True or false.
     */
    abstract boolean overrideOn100Percent();

    /**
     * A list of targets that this target can match; if it's not a wildcard, it
     * should return a singleton list. Otherwise, it should return a list of
     * non-equal targets such that {@code this.matches(target)} will return true
     * for each target on the list and false for any target not on the list.
     * 
     * @return A list of targets
     */
    List<Target> canMatch();

    /**
     * Gets a key representing the basic substance of the target, sans any
     * accompanying data. If this is a wildcard target, it can safely just
     * return null.
     * 
     * @return A key for storage in a hash map.
     */
    String getKey();

    /**
     * Replaces the target with a block of some sort.
     * 
     * @param replacement
     *            The block target to replace it with.
     */
    void setTo(BlockTarget replacement);
}
