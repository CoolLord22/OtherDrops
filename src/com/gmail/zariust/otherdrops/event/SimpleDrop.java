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
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.options.DoubleRange;
import com.gmail.zariust.otherdrops.options.IntRange;
import com.gmail.zariust.otherdrops.options.SoundEffect;
import com.gmail.zariust.otherdrops.options.ToolDamage;
import com.gmail.zariust.otherdrops.parameters.Trigger;
import com.gmail.zariust.otherdrops.subject.BlockTarget;
import com.gmail.zariust.otherdrops.subject.Target;
import com.gmail.zariust.otherdrops.drop.DropType;
import com.gmail.zariust.otherdrops.drop.ItemDrop;
import com.gmail.zariust.otherdrops.special.SpecialResult;

public class SimpleDrop extends CustomDrop {
    // Actions
    private DropType            dropped;
    DoubleRange                 quantity;
    private IntRange            attackerDamage;
    private ToolDamage          toolDamage;
    private double              dropSpread;
    private BlockTarget         replacementBlock;
    private List<SpecialResult> events;
    private List<String>        commands;
    private Set<SoundEffect>    effects;
    private boolean             denied = false;

    Location                    randomize;

    private Location            offset;

    // Constructors
    // TODO: Expand!? Probably not necessary though...
    public SimpleDrop(Target targ, Trigger trigger) {
        super(targ, trigger);
    }

    public void setRandomLocMult(Location loc) {
        randomize = loc;
    }

    public void setLocationOffset(Location loc) {
        setOffset(loc);
    }

    // Tool Damage
    public ToolDamage getToolDamage() {
        return toolDamage;
    }

    public void setToolDamage(ToolDamage val) {
        toolDamage = val;
    }

    // Quantity getters and setters
    public DoubleRange getQuantityRange() {
        return quantity;
    }

    public void setQuantity(double val) {
        quantity = new DoubleRange(val, val);
    }

    public void setQuantity(DoubleRange val) {
        quantity = val;
    }

    public void setQuantity(double low, double high) {
        quantity = new DoubleRange(low, high);
    }

    // The drop
    public void setDropped(DropType drop) {
        this.dropped = drop;
    }

    public DropType getDropped() {
        return dropped;
    }

    @Override
    public boolean isDefault() {
        return (dropped instanceof ItemDrop && ((ItemDrop) dropped)
                .getMaterial() == null);
    }

    @Override
    public String getDropName() {
        if (dropped == null)
            return "NULL";
        else if (dropped instanceof ItemDrop
                && ((ItemDrop) dropped).getMaterial() == null)
            return "DEFAULT";
        else if (dropped instanceof ItemDrop
                && ((ItemDrop) dropped).getMaterial() == Material.AIR
                && (getReplacementBlock() == null || getReplacementBlock()
                        .getMaterial() == null))
            return "DENY";
        return dropped.toString();
    }

    // The drop spread chance
    public void setDropSpread(double spread) {
        this.dropSpread = spread;
    }

    public void setDropSpread(ConfigurationNode node, String parameterName,
            boolean def) {
        Object spread = node.get(parameterName);
        if (spread instanceof Boolean)
            this.dropSpread = (Boolean) spread ? 100.0 : 0.0;
        else if (spread instanceof Number)
            this.dropSpread = OtherDropsConfig.parseChanceFrom(node,
                    parameterName);
        else
            this.dropSpread = def ? 100.0 : 0.0;
    }

    public double getDropSpreadChance() {
        return dropSpread;
    }

    public boolean getDropSpread() {
        if (dropSpread >= 100.0)
            return true;
        else if (dropSpread <= 0.0)
            return false;
        return rng.nextDouble() > dropSpread / 100.0;
    }

    // Attacker Damage
    public IntRange getAttackerDamageRange() {
        return getAttackerDamage();
    }

    public void setAttackerDamage(int val) {
        attackerDamage = new IntRange(val, val);
    }

    public void setAttackerDamage(IntRange val) {
        attackerDamage = val;
    }

    public void setAttackerDamage(int low, int high) {
        attackerDamage = new IntRange(low, high);
    }

    // Replacement
    public BlockTarget getReplacement() {
        return getReplacementBlock();
    }

    public void setReplacement(BlockTarget block) {
        setReplacementBlock(block);
    }

    // Events
    public void setEvents(List<SpecialResult> evt) {
        this.events = evt;
    }

    public List<SpecialResult> getEvents() {
        return events;
    }

    // Commands
    public void setCommands(List<String> cmd) {
        this.commands = cmd;
    }

    public List<String> getCommands() {
        return commands;
    }

    // Messages
    @Override
    public void setMessages(List<String> msg) {
        this.messages = msg;
    }

    @Override
    public List<String> getMessages() {
        return messages;
    }

    public String getMessagesString() {
        if (messages.size() == 0)
            return "(none)";
        else if (messages.size() == 1)
            return quoted(messages.get(0));
        List<String> msg = new ArrayList<String>();
        for (String message : messages)
            msg.add(quoted(message));
        return msg.toString();
    }

    private String quoted(String string) {
        if (!string.contains("\""))
            return '"' + string + '"';
        else if (!string.contains("'"))
            return "'" + string + "'";
        return '"' + string.replace("\"", "\\\"") + '"';
    }

    // Effects
    public void setEffects(Set<SoundEffect> set) {
        this.effects = set;
    }

    public Set<SoundEffect> getEffects() {
        return effects;
    }

    public String getEffectsString() {
        if (effects == null)
            return null;
        if (effects.size() > 1)
            return effects.toString();
        if (effects.isEmpty())
            return "(none)";
        List<Object> list = new ArrayList<Object>();
        list.addAll(effects);
        return list.get(0).toString();
    }

    public Location getRandomisedLocation(Location loc) {
        return randomiseLocation(loc, randomize);
    }

    Location randomiseLocation(Location location, Location maxOffset) {
        double x = maxOffset.getX();
        double y = maxOffset.getY();
        double z = maxOffset.getZ();
        return location.add(
                OtherDrops.rng.nextDouble() * x
                        * (OtherDrops.rng.nextInt() > 0.5 ? 1 : -1),
                OtherDrops.rng.nextDouble() * y
                        * (OtherDrops.rng.nextInt() > 0.5 ? 1 : -1),
                OtherDrops.rng.nextDouble() * z
                        * (OtherDrops.rng.nextInt() > 0.5 ? 1 : -1));
    }

    @Override
    public String getLogMessage() {
        StringBuilder log = new StringBuilder();
        log.append(quantity);
        log.append("x " + dropped);
        if (getReplacementBlock() != null)
            log.append(", leaving " + getReplacementBlock().getMaterial() + ",");
        return super.getLogMessage().replace("%d", log.toString());
    }

    public void setReplacementBlock(BlockTarget replacementBlock) {
        this.replacementBlock = replacementBlock;
    }

    public BlockTarget getReplacementBlock() {
        return replacementBlock;
    }

    public IntRange getAttackerDamage() {
        return attackerDamage;
    }

    public void setOffset(Location offset) {
        this.offset = offset;
    }

    public Location getOffset() {
        return offset;
    }

    @Override
    public void run() {
    }

    public void setDenied(boolean denied) {
        this.denied = denied;
    }

    public boolean isDenied() {
        return denied;
    }

}
