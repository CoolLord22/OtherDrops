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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BlockIterator;

import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.drop.DropResult;
import com.gmail.zariust.otherdrops.drop.DropType;
import com.gmail.zariust.otherdrops.drop.DropType.DropFlags;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.DropsList;
import com.gmail.zariust.otherdrops.event.GroupDropEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;
import com.gmail.zariust.otherdrops.parameters.Trigger;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.Target;
import com.herocraftonline.heroes.characters.Hero;
import java.util.HashSet;
import org.bukkit.Material;

public class OtherDropsCommand implements CommandExecutor {
    private enum OBCommand {
        ID("id", "i", "otherdrops.admin.id"),
        WRITE("write", "w", "otherdrops.admin.id"),
        RELOAD("reload", "r", "otherdrops.admin.reloadconfig"),
        SHOW("show", "s", "otherdrops.admin.show"),
        CUSTOMSPAWN("customspawn", "c", "otherdrops.admin.customspawn"),
        SETTINGS("settings", "st", "otherdrops.admin.settings"),
        DISABLE("disable,disabled,off", "", "otherdrops.admin.enabledisable"),
        ENABLE("enable,enabled,on", "e", "otherdrops.admin.enabledisable"),
        HEROESTEST("heroestest", "ht", ""),
        RPGTEST("rpg", "", ""),
        DROP("drop", "d,o", "otherdrops.admin.drop"),
        TRIGGERS("triggers", "t", "otherdrops.admin.triggers");

        private String cmdName;
        private String cmdShort;
        private String perm;

        private OBCommand(String name, String abbr, String perm) {
            cmdName = name;
            cmdShort = abbr;
            this.perm = perm;
        }

        public static OBCommand match(String label, String firstArg) {
            boolean arg = false;
            if (label.equalsIgnoreCase("od"))
                arg = true;
            for (OBCommand cmd : values()) {
                if (arg) {
                    for (String item : cmd.cmdName.split(",")) {
                        if (firstArg.equalsIgnoreCase(item))
                            return cmd;
                    }
                } else if (label.equalsIgnoreCase("od" + cmd.cmdShort)
                        || label.equalsIgnoreCase("od" + cmd.cmdName))
                    return cmd;
                else {
                    for (String shortcut : cmd.cmdShort.split(",")) {
                        if (label.equalsIgnoreCase("od" + shortcut))
                            return cmd;

                        // special case for "o" as a shortcut by itself (eg.
                        // "/o")
                        if (shortcut.equalsIgnoreCase("o")
                                && label.equalsIgnoreCase(shortcut))
                            return cmd;

                    }
                }
            }
            return null;
        }

        public String[] trim(String[] args, StringBuffer name) {
            if (args.length == 0)
                return args;
            if (!args[0].equalsIgnoreCase(cmdName))
                return args;
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            if (name != null)
                name.append(" " + args[0]);
            return newArgs;
        }
    }

    private final OtherDrops otherdrops;

    public OtherDropsCommand(OtherDrops plugin) {
        otherdrops = plugin;
    }

    private String getName(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender)
            return "CONSOLE";
        else if (sender instanceof Player)
            return ((Player) sender).getName();
        else
            return "UNKNOWN";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        OBCommand cmd = OBCommand.match(label, args.length >= 1 ? args[0] : "");
        if (cmd == null)
            return false;
        StringBuffer cmdName = new StringBuffer(label);
        args = cmd.trim(args, cmdName);

        if (!checkCommandPermissions(sender, args, cmd))
            return true;

        switch (cmd) {
        case ID:
            cmdId(sender, args);
            break;
        case RELOAD:
            cmdReload(sender);
            break;
        case CUSTOMSPAWN:
            cmdCustomSpawn(sender, args, cmdName);
        case SHOW:
            cmdShow(sender, args, cmdName);
            break;
        case SETTINGS:
            cmdSettings(sender);
            break;
        case ENABLE:
            cmdEnable(sender);
            break;
        case DISABLE:
            cmdDisable(sender);
            break;
        case DROP:
            cmdDrop(sender, args);
            break;
        case HEROESTEST:
            cmdHeroesTest(sender);
            break;
        case RPGTEST:
            cmdRpgTest(sender, args);
            break;
        case WRITE:
        	cmdWriteFile(sender);
            break;
        case TRIGGERS:
            String triggers = "";
            for (Trigger value : Trigger.values()) {
                triggers += value.toString()+", ";
            }
            sender.sendMessage("Available OtherDrops triggers: "+triggers.substring(0, triggers.length()-2));
            break;
        default:
            break;

        }
        return true;
    }

    /**
     * @param sender
     * @param args
     */
    public void cmdRpgTest(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!Dependencies.hasRpgItems()) {
                player.sendMessage("Error: RPGITEMs not found.");
                return;
            }
            if (args.length > 0) {
                RPGItem blah2 = ItemManager.getItemByName(args[0]);
                Location loc = player.getTargetBlock(new HashSet<Material>(), 100)
                        .getLocation().add(0, 1, 0); // (???, max distance)
                ItemStack item2 = blah2.item;
                item2.setItemMeta(blah2.getLocaleMeta(Locale
                        .getPlayerLocale(player)));

                loc.getWorld().dropItemNaturally(loc, item2);
                player.sendMessage("RPG: Test finished - RPG item should have dropped where you are looking.");
            } else {
                player.sendMessage("RPG: Test failed.");
            }
        }
    }

    /**
     * @param sender
     * @param args
     * @param cmd
     */
    private boolean checkCommandPermissions(CommandSender sender, String[] args, OBCommand cmd) {
        boolean pass = false;
        if (cmd.perm.isEmpty())
            pass = true;
        else if (Dependencies.hasPermission(sender, cmd.perm))
            pass = true;

        if (!pass)
            sender.sendMessage("You don't have permission for this command.");
        return pass;
    }

    /**
     * @param sender
     */
    private void cmdHeroesTest(CommandSender sender) {
        if (sender instanceof Player) {
            Player playerSender = (Player) sender;
            if (Dependencies.hasHeroes()) {
                sender.sendMessage("Player is in class: "
                        + playerSender.getDisplayName()
                        + "->"
                        + Dependencies.getHeroes().getCharacterManager()
                                .getHero(playerSender).getClass());
                sender.sendMessage("Other players Heroes classes output to server.log");
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {

                    Hero heroPlayer = Dependencies.getHeroes()
                            .getCharacterManager().getHero(player);
                    Log.logInfo("Player:" + player.getDisplayName() + "->"
                            + heroPlayer.getHeroClass().toString() + "("
                            + heroPlayer.getLevel() + ")");
                }
            }
        }
    }

    class dropStringLoc {
        String dropString = null;
        Location loc = null;
        Player player = null;

        public dropStringLoc(String drop, Location location, Player thisPlayer) {
            dropString = drop;
            loc = location;
            player = thisPlayer;
        }
    }
    /**
     * @param sender
     * @param args
     */
    private void cmdDrop(CommandSender sender, String[] args) {
        if (args.length > 0) {

            dropStringLoc dsl = new dropStringLoc("", null, null);
            getLocationFromDropString(sender, args, dsl);

            sender.sendMessage("Dropped at: " + dsl.loc.toString());
            if (dsl.loc != null) {

                dsl.dropString = dsl.dropString.substring(0, dsl.dropString.length() - 1);
                DropType drop = DropType.parse(dsl.dropString, "");
                if (drop == null) {
                    sender.sendMessage("ODDrop - failed to parse drop.");
                    return;
                }

                DropFlags flags = DropType.flags(dsl.player, (dsl.player == null ? null : new PlayerSubject(
                        dsl.player)), true, false, OtherDrops.rng, "odd", "odd", "");
                DropResult dropResult = drop.drop(dsl.loc, (Target) null,
                        (Location) null, 1, flags);

                String dropped = "[NOTHING]";
                if (dropResult.droppedEntities != null)
                    dropped = dropResult.getDroppedString();
                sender.sendMessage("Dropped: " + dropResult.getQuantity() + "x"
                        + dropped);
            }
        } else {
            sender.sendMessage("Usage: /odd <item/mob>@<data> - drops the given item or mob");
        }
    }

    /**
     * @param sender
     * @param args
     * @param world
     * @param playerName
     * @param dsl
     */
    @SuppressWarnings("deprecation")
	public void getLocationFromDropString(CommandSender sender, String[] args, dropStringLoc dsl) {
        World world = null;
        String playerName = "unknown";
        if (sender instanceof Player) {
            dsl.player = (Player) sender;
            playerName = dsl.player.getDisplayName();
            // loc = player.getLocation();
            dsl.loc = dsl.player.getTargetBlock(new HashSet<Material>(), 100).getLocation()
                    .add(0, 1, 0); // (???, max distance)
            world = dsl.loc.getWorld();
        } else if (sender instanceof BlockCommandSender) {
            playerName = sender.getName();
            dsl.loc = ((BlockCommandSender) sender).getBlock().getLocation();
            world = dsl.loc.getWorld();
        }

        Float x = null, y = null, z = null;
        boolean notext = false;
        int xCounter = 0;
        for (int i = 0; i < args.length; i++) {// (String arg : args) {
            String arg = args[i];
            if (xCounter < 3 && !notext) {
                if (arg.matches("[0-9.-]+")) {
                    if (xCounter == 0)
                        x = Float.valueOf(arg);
                    if (xCounter == 1)
                        y = Float.valueOf(arg);
                    if (xCounter == 2)
                        z = Float.valueOf(arg);
                    xCounter++;
                    continue;
                } else if (arg.matches("p:.*")) {
                    if (arg.matches("p:"))
                        arg += args[1 + (i++)];
                    Log.dMsg("PLAYER FOUND" + arg);
                    try {
                        dsl.loc = Bukkit.getPlayer(arg.substring(2)).getLocation();
                    } catch (Exception ex) {
                        sender.sendMessage("Failed to locate player: " + arg.substring(2) + ", aborting.");
                        return;
                    }
                    // do not increment count
                    continue;
                } else if (arg.matches("w:.*")) {
                    if (arg.matches("w:"))
                        arg += args[1 + (i++)];
                    try {
                        world = Bukkit.getWorld(arg.substring(2));
                    } catch (Exception ex) {
                        sender.sendMessage("Failed to locate world: " + arg.substring(2) + ", aborting.");
                        return;
                    }
                    // do not increment count
                    continue;
                }
            }
            dsl.dropString += arg + " ";
            notext = true;
        }
        if (world != null && x != null && y != null & z != null) {
            dsl.loc = new Location(world, x, y, z);
        } else if (dsl.loc == null) {
            sender.sendMessage("No valid location given, aborting.");
            if (sender instanceof BlockCommandSender)
                Log.logInfo("/odd from commandblock (" + playerName + "): No valid location given, aborting.");
            return;
        }

        if (dsl.dropString.isEmpty()) {
            sender.sendMessage("No drop given, aborting.");
            if (sender instanceof BlockCommandSender)
                Log.logInfo("/odd from commandblock (" + playerName + "): No drop given, aborting.");
            return;
        }
    }

    /**
     * @param sender
     */
    private void cmdDisable(CommandSender sender) {
        if (otherdrops.enabled) {
            OtherDrops.disableOtherDrops();
            sender.sendMessage(ChatColor.RED + "OtherDrops disabled.");
        } else {
            sender.sendMessage(ChatColor.GRAY
                    + "OtherDrops is already disabled.");
        }
    }

    /**
     * @param sender
     */
    private void cmdEnable(CommandSender sender) {
        if (!otherdrops.enabled) {
            OtherDrops.enableOtherDrops();
            sender.sendMessage(ChatColor.GREEN + "OtherDrops enabled.");
        } else {
            sender.sendMessage(ChatColor.GRAY
                    + "OtherDrops is already enabled.");
        }
    }

    /**
     * @param sender
     */
    private void cmdSettings(CommandSender sender) {
        sender.sendMessage("OtherDrops settings:");
        sender.sendMessage((otherdrops.enabled ? ChatColor.GREEN
                + "OtherDrops enabled." : ChatColor.RED
                + "OtherDrops disabled."));
        sender.sendMessage("Verbosity: " + ChatColor.GRAY
                + OtherDropsConfig.getVerbosity());
        sender.sendMessage("Disable XP if no default drop: " + ChatColor.GRAY
                + OtherDropsConfig.disableXpOnNonDefault);
        sender.sendMessage("Money Precision (for messages): " + ChatColor.GRAY
                + OtherDropsConfig.moneyPrecision);
        sender.sendMessage("Use unsafe enchantments: " + ChatColor.GRAY
                + OtherDropsConfig.enchantmentsUseUnsafe);
        sender.sendMessage("Ignore enchantment start/maxlevel: "
                + ChatColor.GRAY + OtherDropsConfig.enchantmentsIgnoreLevel);
    }

    /**
     * @param sender
     * @param args
     * @param cmdName
     */
    private void cmdCustomSpawn(CommandSender sender, String[] args,
            StringBuffer cmdName) {

        if (args.length == 0) {
            sender.sendMessage("Error, no drop specified. Please use /" + cmdName
                    + " <custommob>, optionally /" + cmdName + " w:world x y z <custommob>/<quantity>");
            return;
        }


        dropStringLoc dsl = new dropStringLoc("", null, null);
        getLocationFromDropString(sender, args, dsl);

        String[] split = dsl.dropString.split("/");
        dsl.dropString = split[0];
        Integer quantity = 1;
        if (split.length > 1) {
            quantity = Integer.valueOf(split[1].trim());
        }
        Log.logInfo("Attempting to spawn: " + dsl.dropString + " (x" + quantity + ") at location " + dsl.loc.toString(), Verbosity.HIGHEST);
        for (int i = 0; i < quantity; i++) {// (String arg : args) {
            try {
                CustomMobSupport.spawnCustomMob(dsl.dropString.trim(), dsl.loc);
            } catch (Exception e) {
                Log.logInfo("Failed to spawn mob: " + e);
            }
        }
    }

    /**
     * @param sender
     * @param args
     * @param cmdName
     */
    private void cmdShow(CommandSender sender, String[] args,
            StringBuffer cmdName) {
        if (args.length == 0) {
            sender.sendMessage("Error, no block. Please use /" + cmdName
                    + " <block>");
            return;
        }
        Target target = OtherDropsConfig.parseTarget(args[0]);
        for (Trigger trigger : Trigger.values())
            showBlockInfo(sender, trigger, target);
    }

    /**
     * @param sender
     */
    private void cmdReload(CommandSender sender) {
        otherdrops.config.load(sender);
        sender.sendMessage("OtherDrops config reloaded.");
        Log.logInfo("Config reloaded by " + getName(sender) + ".");
    }

    /**
     * @param sender
     * @param args
     */
    private void cmdId(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack playerItem = player.getInventory().getItemInMainHand();

            if (args.length > 0 && args[0].toLowerCase().matches("(mob|creature)")) {
                Entity mob = getTarget(player);
                if (mob instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) mob;
                    // TODO: parse via CreatureDrop (need to create
                    // CreatureDrop.parse(entity)

                    List<MetadataValue> md = le.getMetadata("CreatureSpawnedBy");
                    String spawnReason = "not set";
                    if (md != null && md.size() > 0) spawnReason = (String) md.get(0).value();
                    sender.sendMessage("OdId: mob details: "
                            + mob.getType().toString() + "@"
                            + CreatureData.parse(mob).toString()
                            + " spawnedby: " + spawnReason
                            + CustomMobSupport.getCustomMobName(le));

                } else {
                    sender.sendMessage("No living entity found.");
                }
            } else {
                String itemMsg = playerItem.getType() + "@" + playerItem.getDurability() + " maxdura:" + playerItem.getType().getMaxDurability() +
                		" dura%:" + getDurabilityPercentage(playerItem) + " detail: " + playerItem.toString();
                if (playerItem.getItemMeta() != null && playerItem.getItemMeta().getDisplayName() != null)
                    itemMsg += " name: \"" + playerItem.getItemMeta().getDisplayName().replaceAll(" §", "&") + "\"";
               
                ((Player) sender).sendRawMessage(ChatColor.GREEN + "Item in hand: " + ChatColor.WHITE + itemMsg.replaceAll("§", "&"));
                sender.sendMessage("");
                
                Block block = player.getTargetBlock(new HashSet<Material>(), 100);
                ((Player) sender).sendRawMessage(ChatColor.GREEN + "Block looked at is " + ChatColor.WHITE + block.toString()
                        + " mat: " + block.getType().toString()
                        + " lightlevel: " + block.getLightLevel()
                        + " lightfromsky: " + block.getLightFromSky()
                        + " biome: " + block.getBiome().toString());
            }

            String itemFinalWriteData = "";
            
            itemFinalWriteData += playerItem.getType();
            itemFinalWriteData += "@" + playerItem.getDurability();
            if(!playerItem.getEnchantments().isEmpty()) {
            	itemFinalWriteData += "!";
            	for(Enchantment enchInMap : playerItem.getEnchantments().keySet()) {
            		itemFinalWriteData += enchInMap.getName() + "#" + playerItem.getEnchantmentLevel(enchInMap) + "!";
            	}
            }
            if(playerItem.getItemMeta() != null) {
            	if(playerItem.getItemMeta().getDisplayName() != null) {
                	itemFinalWriteData += "~" + playerItem.getItemMeta().getDisplayName();
            	}
            	if(playerItem.getItemMeta().getLore() != null) {
            		List<String> loreList = playerItem.getItemMeta().getLore();
            		for(String loreLine : loreList) {
                		itemFinalWriteData += ";" + loreLine;
            		}
            	}
            }
            sender.sendMessage("");
            ((Player) sender).sendRawMessage(ChatColor.GREEN + "The item config is:§r " + ChatColor.WHITE + itemFinalWriteData.replaceAll("§", "&"));
        }
    }
    
    private void cmdWriteFile(CommandSender sender) {
    	if (sender instanceof Player) {
            File folder = new File("plugins" + File.separator + "OtherDrops");
            BufferedWriter out = null;
            
            Player player = (Player) sender;
            ItemStack playerItem = player.getInventory().getItemInMainHand();

            String itemFinalWriteData = "";
            
            itemFinalWriteData += playerItem.getType();
            itemFinalWriteData += "@" + playerItem.getDurability();
            if(!playerItem.getEnchantments().isEmpty()) {
            	itemFinalWriteData += "!";
            	for(Enchantment enchInMap : playerItem.getEnchantments().keySet()) {
            		itemFinalWriteData += enchInMap.getName() + "#" + playerItem.getEnchantmentLevel(enchInMap) + "!";
            	}
            }
            
            if(playerItem.getItemMeta() != null) {
            	if(playerItem.getItemMeta().getDisplayName() != null) {
                	itemFinalWriteData += "~" + playerItem.getItemMeta().getDisplayName();
            	}
            	if(playerItem.getItemMeta().getLore() != null) {
            		List<String> loreList = playerItem.getItemMeta().getLore();
            		for(String loreLine : loreList) {
                		itemFinalWriteData += ";" + loreLine;
            		}
            	}
            }

            try {
                File configFile = new File(folder.getAbsolutePath() + File.separator + "ItemOutput" + ".txt");
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                out = new BufferedWriter(new FileWriter(configFile));
                out.write(itemFinalWriteData + "\n");
                out.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            
            ((Player) sender).sendRawMessage(ChatColor.GREEN + "The item config is:§r " + ChatColor.WHITE + itemFinalWriteData.replaceAll("§", "&"));
        }
    }
    /*
     * "/od show" command - shows conditions and triggers for the specified
     * block
     * 
     * @param sender The sender requesting the info
     * 
     * @param trigger The trigger to show info for
     * 
     * @param block The requested target
     */
    public void showBlockInfo(CommandSender sender, Trigger trigger,
            Target block) {
        StringBuilder message = new StringBuilder();
        message.append("Block §a" + block + "§f (" + trigger + "):");

        DropsList dropGroups = otherdrops.config.blocksHash.getList(trigger,
                block);
        int i = 1;

        if (dropGroups != null) {
            for (CustomDrop drop : dropGroups) {
                if (drop != null) {
                    message.append("\n §fDrop Number: §a" + i++);
                    if (drop instanceof GroupDropEvent)
                        addDropInfo(message, (GroupDropEvent) drop);
                    else
                        addDropInfo(message, (SimpleDrop) drop);
                }
                message.append("\n ");
            }
            sender.sendMessage(message.toString());
        } else
            return; // sender.sendMessage(message+"No info found.");
    }

    private void addDropConditions(StringBuilder message, CustomDrop drop) {
        Map<String, String> messageMap = new HashMap<String, String>();

        // Conditions
        messageMap.put("Agent", drop.getToolString()); // make null if "any"
        messageMap.put("Worlds", drop.getWorldsString());
        messageMap.put("Regions", drop.getRegionsString());
        messageMap.put("Weather", drop.getWeatherString());
        messageMap.put("Block faces", drop.getBlockFacesString());
        messageMap.put("Biomes", drop.getBiomeString());
        messageMap.put("Times", drop.getTimeString());
        messageMap.put("Groups", drop.getGroupsString());
        messageMap.put("Permissions", drop.getPermissionsString());
        messageMap.put("Height", (drop.getHeight() == null) ? null : drop
                .getHeight().toString());
        messageMap.put("Attack range", (drop.getAttackRange() == null) ? null
                : drop.getAttackRange().toString());
        messageMap.put("Light level ", (drop.getLightLevel() == null) ? null
                : drop.getLightLevel().toString());
        // Chance and delay
        messageMap.put("Chance", String.valueOf(drop.getChance())); // make null
                                                                    // if = 100
        messageMap.put("Exclusive key", drop.getExclusiveKey());
        messageMap.put("Delay", drop.getDelayRange()); // make null if 0

        for (Entry<String, String> entry : messageMap.entrySet()) {
            if (entry.getValue() != null) {
                message.append("\n  §7" + entry.getKey() + ": §f" + entry.getValue());
            }
        }
    }

    private void addDropInfo(StringBuilder message, SimpleDrop drop) {
        addDropConditions(message, drop);
        Map<String, String> messageMap = new HashMap<String, String>();

        messageMap.put("Drop", stringHelper(drop.getDropped())); // TODO: this
                                                                 // returns the
                                                                 // object, not
                                                                 // a string?
        messageMap.put("Quantity", stringHelper(drop.getQuantityRange())); // make
                                                                           // null
                                                                           // if
                                                                           // 1
                                                                           // (Default)
        messageMap.put("Attacker damage", stringHelper(drop.getAttackerDamageRange()));
        messageMap.put("Tool damage", stringHelper(drop.getToolDamage()));
        messageMap.put("Drop spread", drop.getDropSpreadChance() + "% chance");
        messageMap.put("Replacement block", stringHelper(drop.getReplacement()));
        messageMap.put("Commands", stringHelper(drop.getCommands())); // make
                                                                      // null if
                                                                      // empty
                                                                      // list
        messageMap.put("Messages", drop.getMessagesString());
        messageMap.put("Sound effects", drop.getEffectsString());
        messageMap.put("Events", stringHelper(drop.getEvents()));

        for (Entry<String, String> entry : messageMap.entrySet()) {
            if (entry.getValue() != null) {
                message.append("\n  §7" + entry.getKey() + ":§f " + entry.getValue());
            }
        }
    }

    private String stringHelper(Object object) {
        if (object == null)
            return null;
        else
            return object.toString();
    }

    private void addDropInfo(StringBuilder message, GroupDropEvent group) {
        addDropConditions(message, group);
        message.append(" Drop group: " + group.getName());
        char j = 'A';
        for (CustomDrop subDrop : group.getDrops()) {
            message.append("\n §fNumber: §a" + j++);
            if (j > 'Z')
                j = 'a';
            if (subDrop instanceof GroupDropEvent)
                addDropInfo(message, (GroupDropEvent) subDrop);
            else
                addDropInfo(message, (SimpleDrop) subDrop);
        }
    }

    // returns null if not durability is not valid (ie. Has no maxdurability)
    private Double getDurabilityPercentage(ItemStack item) {
        Double maxDura = Double.valueOf(item.getType().getMaxDurability());
        Double dura = Double.valueOf(item.getDurability());

        if (maxDura < 1)
            return null;
        return (double) (Math.round((float) (1 - (dura / maxDura)) * 10000) / 100);
    }

    public static Entity getTarget(final Player player) {

        BlockIterator iterator = new BlockIterator(player.getWorld(), player
                .getLocation().toVector(), player.getEyeLocation()
                .getDirection(), 0, 100);
        Entity target = null;
        while (iterator.hasNext()) {
            Block item = iterator.next();
            for (Entity entity : player.getNearbyEntities(100, 100, 100)) {
                int acc = 2;
                for (int x = -acc; x < acc; x++)
                    for (int z = -acc; z < acc; z++)
                        for (int y = -acc; y < acc; y++)
                            if (entity.getLocation().getBlock()
                                    .getRelative(x, y, z).equals(item)) {
                                return target = entity;
                            }
            }
        }
        return target;
    }
}
