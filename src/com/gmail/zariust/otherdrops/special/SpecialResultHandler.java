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

package com.gmail.zariust.otherdrops.special;

import java.util.List;
import java.util.Properties;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;

/**
 * A plugin providing one or more events as an extension to OtherDrops.
 */
public abstract class SpecialResultHandler {
    Properties info;
    String     version;

    /**
     * Get a new event with the specified tag.
     * 
     * @param name
     *            The event tag
     * @return The new event, or null if the tag is not recognized
     */
    public abstract SpecialResult getNewEvent(String name);

    /**
     * The place to do any initialization.
     */
    public abstract void onLoad();

    /**
     * Get a list of recognized tags; this is used to register the tags to your
     * plugin.
     * 
     * @return A list of tags.
     */
    public abstract List<String> getEvents();

    /**
     * The name of the event handler.
     * 
     * @return An identifiable name.
     */
    public abstract String getName();

    /**
     * The version of your plugin.
     * 
     * @return The version string, or "1.0" if not defined.
     */
    public final String getVersion() {
        return version;
    }

    /**
     * The event plugin info file (event.info); you can obtain arbitrary
     * information from it if you wish.
     * 
     * @return The Properties instance.
     */
    public Properties getInfo() {
        return info;
    }

    /**
     * The event plugin's node in the otherdrops-config.yml file. If it doesn't
     * exist, it will be created.
     * 
     * @return The configuration node.
     */
    public ConfigurationNode getConfiguration() {
        return OtherDrops.plugin.config.getEventNode(this);
    }

    private String prefix() {
        return "[Event " + getName() + "] ";
    }

    /**
     * Log an info message with default verbosity.
     * 
     * @param msg
     *            The message to log.
     */
    protected void logInfo(String msg) {
        Log.logInfo(prefix() + msg);
    }

    /**
     * Log an info message with the specified verbosity.
     * 
     * @param msg
     *            The message to log.
     * @param verbosity
     *            The minimum verbosity for which it should appear.
     */
    protected void logInfo(String msg, Verbosity verbosity) {
        Log.logInfo(prefix() + msg, verbosity);
    }

    /**
     * Log a warning message with default verbosity.
     * 
     * @param msg
     *            The message to log.
     * @param msg
     */
    protected void logWarning(String msg) {
        Log.logWarning(prefix() + msg);
    }

    /**
     * Log a warning message with the specified verbosity.
     * 
     * @param msg
     *            The message to log.
     * @param verbosity
     *            The minimum verbosity for which it should appear.
     */
    protected void logWarning(String msg, Verbosity verbosity) {
        Log.logWarning(prefix() + msg, verbosity);
    }
}
