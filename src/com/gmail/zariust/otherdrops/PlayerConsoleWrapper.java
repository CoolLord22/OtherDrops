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

package com.gmail.zariust.otherdrops;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class PlayerConsoleWrapper implements ConsoleCommandSender {
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private boolean                           suppress;
    private Player                            caller;

    public PlayerConsoleWrapper(Player player, boolean suppressMessages) {
        caller = player;
        suppress = suppressMessages;
    }

    @Override
    public String getName() {
        return caller.getName();
    }

    @Override
    public void sendMessage(String message) {
        if (suppress)
            console.sendMessage(message);
        else
            caller.sendMessage(message);
    }

    @Override
    public Server getServer() {
        return console.getServer();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return console.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return console.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return console.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return console.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name,
            boolean value) {
        return console.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return console.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name,
            boolean value, int ticks) {
        return console.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return console.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        console.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        console.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return console.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return console.isOp();
    }

    @Override
    public void setOp(boolean value) {
        console.setOp(value);
    }

    @Override
    public void sendMessage(String[] arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void abandonConversation(Conversation arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void acceptConversationInput(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean beginConversation(Conversation arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConversing() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void sendRawMessage(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void abandonConversation(Conversation arg0,
            ConversationAbandonedEvent arg1) {
        // TODO Auto-generated method stub

    }
}
