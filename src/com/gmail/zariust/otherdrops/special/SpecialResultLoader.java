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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.gmail.zariust.common.Verbosity.*;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;

public class SpecialResultLoader {
    private static Map<String, SpecialResultHandler> knownEvents = new HashMap<String, SpecialResultHandler>();

    /*
     * Load all the external classes.
     */
    public static void loadEvents() {
        File dir = new File(OtherDrops.plugin.getDataFolder(), "events");
        ArrayList<String> loaded = new ArrayList<String>();
        dir.mkdir();
        boolean added = false;
        for (String f : dir.list()) {
            if (f.toLowerCase().contains(".jar")) {
                try {
                    SpecialResultHandler event = loadEvent(new File(dir, f));
                    if (event != null) {
                        event.onLoad();
                        if (!added) {
                            Log.logInfo("Collecting and loading events",
                                    HIGHEST);
                            added = true;
                        }
                        List<String> known = event.getEvents();
                        for (String e : known) {
                            // FIXME: ignore re-registration - update with
                            // latest event (or ignore if event is the same)
                            if (knownEvents.containsKey(e))
                                Log.logWarning(
                                        "Warning: handler "
                                                + event.getName()
                                                + " attempted to register event "
                                                + e
                                                + ", but that was already registered "
                                                + "by handler "
                                                + knownEvents.get(e).getName()
                                                + ". The event was not re-registered.",
                                        EXTREME);
                            else
                                knownEvents.put(e, event);
                        }
                        loaded.addAll(known);
                        Log.logInfo("Event group " + event.getName()
                                + " loaded", HIGHEST);
                    }
                } catch (Exception ex) {
                    Log.logWarning("Event file: " + f + " failed to load... ("
                            + ex.toString() + ")", NORMAL);
                    if (OtherDropsConfig.getVerbosity().exceeds(HIGH))
                        ex.printStackTrace();
                }
            }
        }
        if (added)
            Log.logInfo("Events loaded: " + loaded.toString(), HIGH);
    }

    private static SpecialResultHandler loadEvent(File file) {
        String name = file.getName();
        try {
            JarFile jarFile = new JarFile(file);
            JarEntry infoEntry = jarFile.getJarEntry("event.info");
            if (infoEntry == null)
                throw new SpecialResultLoadException(
                        "No event.info file found.");

            InputStream stream = jarFile.getInputStream(infoEntry);
            Properties info = new Properties();
            info.load(stream);
            String mainClass = info.getProperty("class");

            if (mainClass != null) {
                ClassLoader loader = URLClassLoader.newInstance(
                        new URL[] { file.toURI().toURL() },
                        SpecialResultHandler.class.getClassLoader());
                Class<?> clazz = Class.forName(mainClass, true, loader);
                for (Class<?> subclazz : clazz.getClasses()) {
                    Class.forName(subclazz.getName(), true, loader);
                }
                Class<? extends SpecialResultHandler> skillClass = clazz
                        .asSubclass(SpecialResultHandler.class);
                SpecialResultHandler event;
                try { // Try default constructor first
                    event = skillClass.newInstance();
                } catch (InstantiationException e) { // If that fails, try
                                                     // OtherDrops constructor
                    Constructor<? extends SpecialResultHandler> ctor = skillClass
                            .getConstructor(OtherDrops.class);
                    event = ctor.newInstance(OtherDrops.plugin);
                }
                event.info = info;
                event.version = info.getProperty("version");
                return event;
            } else
                throw new SpecialResultLoadException(
                        "Missing class= property in event.info.");
        } catch (IOException e) { // Failed to load jar or event.info
            Log.logWarning("Failed to load event from file " + name + ":");
            e.printStackTrace();
        } catch (ClassNotFoundException e) { // Couldn't find specified class
            Log.logWarning("The class specified in event.info for " + name
                    + " could not be found.");
        } catch (IllegalAccessException e) { // Constructor was inaccessible
                                             // (not public)
            Log.logWarning("The constructor for the event in " + name
                    + " was not public.");
        } catch (InvocationTargetException e) { // Constructor threw an
                                                // exception
            Log.logWarning("The event in " + name
                    + " threw an exception while loading:");
            e.getCause().printStackTrace();
        } catch (NoSuchMethodException e) { // Constructor does not exist
            Log.logWarning("The event in " + name
                    + " is missing a default or OtherDrops constructor.");
        } catch (SpecialResultLoadException e) {
            Log.logWarning("Could not load event in " + name + ": "
                    + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.logWarning("The events in " + name + " failed to load");
            e.printStackTrace();
        }
        return null;
    }

    public static SpecialResultHandler getHandlerFor(String name) {
        return knownEvents.get(name);
    }
}
