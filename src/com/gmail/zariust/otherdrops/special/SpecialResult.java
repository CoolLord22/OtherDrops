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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.gmail.zariust.common.Verbosity.*;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;

/**
 * Represents some kind of event that can occur alongside or instead of a drop.
 */
public abstract class SpecialResult {
    private String               tag;
    private SpecialResultHandler handler;
    private List<String>         usedArgs;
    private List<String>         arguments;

    /**
     * Initialize the event with its name and handler.
     * 
     * @param name
     *            The event's unique tag.
     * @param source
     *            The handler that provides the event.
     */
    protected SpecialResult(String name, SpecialResultHandler source) {
        tag = name;
        handler = source;

        usedArgs = new ArrayList<String>();
    }

    /**
     * Get the tag name for this event.
     * 
     * @return The name.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Get the handler that provides this event.
     * 
     * @return The handler.
     */
    public SpecialResultHandler getHandler() {
        return handler;
    }

    /**
     * Mark an argument as having been parsed and used by the event. This does
     * several useful things:
     * <ul>
     * <li>Removes the argument from the list of remaining arguments.</li>
     * <li>Adds the argument to a list of used arguments, for creating a string
     * representation.</li>
     * <li>Ensures that OtherDrops will not warn of unused arguments, which it
     * normally does if it finds any remaining arguments when your function
     * returns.</li>
     * </ul>
     * It is recommended that you use this instead of the list's remove()
     * method.
     * 
     * @param arg
     *            The argument, unaltered exactly as it appeared in the list.
     */
    protected void used(String arg) {
        usedArgs.add(arg);
        arguments.remove(arg);
    }

    @Override
    public String toString() {
        StringBuilder event = new StringBuilder();
        event.append(tag);
        if (usedArgs.size() == 0)
            return event.toString();
        event.append("@");
        event.append(usedArgs.get(0));
        if (usedArgs.size() == 1)
            return event.toString();
        for (String arg : usedArgs.subList(1, usedArgs.size()))
            event.append("/" + arg);
        return event.toString();
    }

    /**
     * Called every time the event should be executed. As it may be called many
     * times, this should typically not change any member fields that affect
     * event behaviour.
     * 
     * @param event
     *            The actual drop that has triggered the event.
     */
    public abstract void executeAt(OccurredEvent event);

    /**
     * Called once on load to interpret the event's arguments, thus finalizing
     * its initialization. It's expected that, if the event takes arguments, it
     * will store them internally in some fashion.
     * 
     * @param args
     *            The list of arguments, already split and ready to go.
     */
    public abstract void interpretArguments(List<String> args);

    /**
     * Check whether it is possible for the drop to trigger this event. Some
     * events may depend on additional conditions, such as an event affecting
     * sheep requiring that the target is a sheep.
     * 
     * @param drop
     *            The configured drop.
     * @return True if this event can apply; false otherwise.
     */
    public abstract boolean canRunFor(SimpleDrop drop);

    /**
     * Check whether it is possible for the drop to trigger this event. Some
     * events may depend on additional conditions, such as an event affecting
     * sheep requiring that the target is a sheep.
     * 
     * @param drop
     *            The actual drop that has occurred and been determined to
     *            trigger this event.
     * @return True if this event can apply; false otherwise.
     */
    public abstract boolean canRunFor(OccurredEvent drop);

    public static List<SpecialResult> parseFrom(ConfigurationNode node) {
        List<String> events = OtherDropsConfig.getMaybeList(node, "event",
                "events");
        if (events == null)
            return null;
        // There's a good reason for using LinkedList; changing it could break
        // things
        List<SpecialResult> result = new LinkedList<SpecialResult>();
        for (String eventCall : events) {
            String[] split = eventCall.split("@", 2);
            String name = split[0].toUpperCase();
            SpecialResultHandler handler = SpecialResultLoader
                    .getHandlerFor(name);
            if (handler == null) {
                Log.logWarning("Unknown event type " + name + "; skipping...");
                continue;
            }
            SpecialResult event = handler.getNewEvent(name);
            if (split.length > 1) {
                // See the documentation of used() for why a custom list
                // implementation is used.
                event.arguments = new SpecialResultArgList(split[1].split("[/@]"));
                event.interpretArguments(event.arguments);
                if (!event.arguments.isEmpty()) {
                    Log.logWarning("While parsing arguments for event "
                            + event.getTag() + ", the "
                            + "following invalid arguments were ignored: "
                            + event.arguments.toString());
                }
                event.arguments = null;
            }
            result.add(event);
            Log.logInfo("Loading events... adding " + event.toString(), EXTREME);
        }
        if (result.isEmpty())
            return null;
        return result;
    }
}
