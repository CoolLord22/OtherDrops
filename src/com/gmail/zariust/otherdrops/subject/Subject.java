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

import org.bukkit.Location;

import com.gmail.zariust.otherdrops.data.Data;

/**
 * A subject which may be either a target or agent
 * 
 * @see Target Agent
 */
public interface Subject {
    /**
     * The category of subject represented
     */
    public enum ItemCategory {
        /**
         * Represents a block or block-like entity.
         */
        BLOCK,
        /**
         * Represents a living entity that is not a player.
         */
        CREATURE,
        /**
         * Represents a player.
         */
        PLAYER,
        /**
         * Represents a projectile.
         */
        PROJECTILE,
        /**
         * Represents a source of damage.
         */
        DAMAGE,
        /**
         * Represents an explosion.
         */
        EXPLOSION,
        /**
         * Something that doesn't fit any of the others.
         */
        SPECIAL,
    }

    /**
     * @return The basic type of the subject
     */
    ItemCategory getType(); // used in OtherDropsConfig to determine target type

    /**
     * Whether this subject matches the other one. If this is not a wildcard, it
     * should return whether the two are equal.
     * 
     * @param other
     *            The subject to match against.
     * @return Whether they match.
     */
    boolean matches(Subject other);

    /**
     * The data associated with the subject, if any.
     * 
     * @return Some data
     */
    Data getData();

    /**
     * @return The subject's location.
     */
    Location getLocation();

    class HashCode {
        private final Subject what;
        private Object        dataObj;

        public HashCode(Subject subj) {
            what = subj;
            dataObj = what.getData();
        }

        public HashCode setData(Object data) {
            dataObj = data;
            return this;
        }

        public int get(Object info) {
            ItemCategory type = what.getType();
            int data = dataObj == null ? 0 : dataObj.hashCode();
            int v = info == null ? 0 : info.hashCode();
            int t = type == null ? (short) 0 : (short) type.hashCode();
            return (v << 16) | t | (data << 3);
        }
    }

    abstract String getReadableName();

}
