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

import static com.gmail.zariust.common.CommonPlugin.enumValue;
import static com.gmail.zariust.common.CommonPlugin.getConfigVerbosity;
import static com.gmail.zariust.common.Verbosity.HIGH;
import static com.gmail.zariust.common.Verbosity.HIGHEST;
import static com.gmail.zariust.common.Verbosity.NORMAL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.yaml.snakeyaml.scanner.ScannerException;

import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.common.MaterialGroup;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.data.SimpleData;
import com.gmail.zariust.otherdrops.drop.CreatureDrop;
import com.gmail.zariust.otherdrops.drop.DropListExclusive;
import com.gmail.zariust.otherdrops.drop.DropListInclusive;
import com.gmail.zariust.otherdrops.drop.DropType;
import com.gmail.zariust.otherdrops.drop.ExperienceDrop;
import com.gmail.zariust.otherdrops.drop.MoneyDrop;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.DropsMap;
import com.gmail.zariust.otherdrops.event.GroupDropEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;
import com.gmail.zariust.otherdrops.metrics.Metrics;
import com.gmail.zariust.otherdrops.metrics.Metrics.Graph;
import com.gmail.zariust.otherdrops.options.Comparative;
import com.gmail.zariust.otherdrops.options.DoubleRange;
import com.gmail.zariust.otherdrops.options.Flag;
import com.gmail.zariust.otherdrops.options.IntRange;
import com.gmail.zariust.otherdrops.options.SoundEffect;
import com.gmail.zariust.otherdrops.options.Time;
import com.gmail.zariust.otherdrops.options.ToolDamage;
import com.gmail.zariust.otherdrops.options.Weather;
import com.gmail.zariust.otherdrops.parameters.Trigger;
import com.gmail.zariust.otherdrops.special.SpecialResult;
import com.gmail.zariust.otherdrops.special.SpecialResultHandler;
import com.gmail.zariust.otherdrops.special.SpecialResultLoader;
import com.gmail.zariust.otherdrops.subject.Agent;
import com.gmail.zariust.otherdrops.subject.AnySubject;
import com.gmail.zariust.otherdrops.subject.BlockTarget;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;
import com.gmail.zariust.otherdrops.subject.EnvironmentAgent;
import com.gmail.zariust.otherdrops.subject.ExplosionAgent;
import com.gmail.zariust.otherdrops.subject.GroupSubject;
import com.gmail.zariust.otherdrops.subject.LivingSubject;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.ProjectileAgent;
import com.gmail.zariust.otherdrops.subject.Subject.ItemCategory;
import com.gmail.zariust.otherdrops.subject.Target;
import com.gmail.zariust.otherdrops.subject.ToolAgent;
import com.gmail.zariust.otherdrops.subject.VehicleTarget;
import com.gmail.zariust.otherdrops.things.ODItem;

public class OtherDropsConfig {

    private final OtherDrops           parent;

    // Our main list of drops
    protected DropsMap                 blocksHash;

    // Name of drops file
    private String                     mainDropsName;

    // Track loaded files so we don't get into an infinite loop
    Set<String>                        loadedDropFiles                       = new HashSet<String>();

    // Action counts for Metrics
    private final Map<String, Integer> triggerCounts                         = new HashMap<String, Integer>();

    // Constants
    public static final String         CreatureDataSeparator                 = "!!";

    // A place for special events to stash options
    private ConfigurationNode          events;

    // Triggers configured - these enable the appropriate listeners
    // if a drop config is found using them.
    public static boolean              dropForBlocks;                                                         // target
                                                                                                               // type=BLOCK
                                                                                                               // or
                                                                                                               // ANY
    public static boolean              dropForCreatures;                                                      // target
                                                                                                               // type=CREATURE,
                                                                                                               // PLAYER
                                                                                                               // or
                                                                                                               // ANY
    public static boolean              dropForExplosions;                                                     // target
                                                                                                               // type=EXPLOSION
    public static boolean              dropForClick;                                                          // LEFT
                                                                                                               // or
                                                                                                               // RIGHTCLICK
    public static boolean              dropForFishing;                                                        // FISH_CAUGHT
                                                                                                               // or
                                                                                                               // FAILED
    public static boolean              dropForSpawned;                                                        // config
                                                                                                               // using
                                                                                                               // "spawned:"
    public static boolean              dropForSpawnTrigger;                                                   // config
                                                                                                               // using
                                                                                                               // "trigger: CREATURESPAWN"
    public static boolean              dropForRedstoneTrigger;                                                // POWERUP
                                                                                                               // or
                                                                                                               // POWERDOWN
    public static boolean              dropForPlayerJoin;                                                     // PLAYERJOIN
    public static boolean              dropForPlayerRespawn;                                                  // PLAYERRESPAWN
    public static boolean              dropForPlayerConsume;
    public static boolean              dropForPlayerMove;

    
    // Defaults
    protected Map<World, Boolean>      defaultWorlds;
    protected Map<String, Boolean>     defaultRegions;
    protected Map<Weather, Boolean>    defaultWeather;
    protected Map<Biome, Boolean>      defaultBiomes;
    protected Map<Time, Boolean>       defaultTime;
    protected Map<String, Boolean>     defaultPermissionGroups;
    protected Map<String, Boolean>     defaultPermissions;
    protected Comparative              defaultHeight;
    protected Comparative              defaultAttackRange;
    protected Comparative              defaultLightLevel;
    protected List<Trigger>            defaultTrigger;

    // Variables for settings from config.yml
    protected static Verbosity         verbosity                             = Verbosity.NORMAL;
    public boolean                     customDropsForExplosions;
    public boolean                     defaultDropSpread;                                                     // determines
                                                                                                               // if
                                                                                                               // dropspread
                                                                                                               // defaults
                                                                                                               // to
                                                                                                               // true
                                                                                                               // or
                                                                                                               // false
    public static boolean              enableBlockTo;
    protected boolean                  disableEntityDrops;
    public static boolean              disableXpOnNonDefault;                                                 // if
                                                                                                               // drops
                                                                                                               // are
                                                                                                               // configured
                                                                                                               // for
                                                                                                               // mobs
                                                                                                               // -
                                                                                                               // disable
                                                                                                               // the
                                                                                                               // xp
                                                                                                               // unless
                                                                                                               // there
                                                                                                               // is
                                                                                                               // a
                                                                                                               // default
                                                                                                               // drop
    public static int                  moneyPrecision;
    public static boolean              enchantmentsUseUnsafe;
    public static boolean              enchantmentsIgnoreLevel;
    public static boolean              enchantmentsRestrictMatching			 = true;
    public static boolean              spawnTriggerIgnoreOtherDropsSpawn     = true;
    private boolean                    globalLootOverridesDefault;
    private boolean                    globalMoneyOverridesDefault;
    private boolean                    globalXpOverridesDefault;
    private boolean                    moneyOverridesDefault;
    private boolean                    xpOverridesDefault;
    private boolean                    lootOverridesDefault;
    public static boolean              globalRedstonewireTriggersSurrounding = true;
    public static boolean              globalUpdateChecking					 = true;
    public static boolean              globalDisableMetrics                  = false;
    public static boolean              primedTNTEnabled		                 = false;

    public static boolean              globalOverrideExplosionCap            = false;

    public static int                  globalCustomSpawnLimit;

    public static String               gTimeFormat                           = "HH:mm:ss";

    public static String               gDateFormat                           = "yyyy/MM/dd";

    public static boolean              gColorLogMessages                     = true;

    public static double gActionRadius = 10;

    public static boolean dropForBlockGrow;
    public static boolean dropForProjectileHit;

    public static boolean gcustomBlockBreakToMcmmo;


    private boolean                    globalAllowAnyReplacementBlock;

    private int dropSections; // for summary after loading config
    private int dropTargets;  // for summary after loading config
    private int dropFailed;   // for summary after loading config

    public static boolean actionParameterFound; // for summary after loading config

    public static boolean dropForBlockPlace;

    public static boolean exportEnumLists;

    // mobs usually compared via ENUMs however some custom mods add multiple ENUMs with same name that fails this match,
    // config option match_mob_by_name_only allows for a string comparison to work around this issue.
    public static boolean matchMobByNameOnly;
    
    // option to turn off new "get keys deep" option (defaults to on) - getting keys deep allows mod names like
    // "MyMod.Mobname" to work (otherwise OtherDrops sees only "MyMod").
    private boolean configKeysGetDeep;

    public OtherDropsConfig(OtherDrops instance) {
        parent = instance;
        blocksHash = new DropsMap();

        dropForBlocks = false;
        dropForCreatures = false;
        dropForClick = false;
        dropForFishing = false;

        defaultDropSpread = true;
    }

    private void clearDefaults() {
        defaultTrigger = Collections.singletonList(Trigger.BREAK);
        defaultWorlds = null;
        defaultRegions = null;
        defaultWeather = null;
        defaultBiomes = null;
        defaultTime = null;
        defaultPermissionGroups = null;
        defaultPermissions = null;
        defaultHeight = null;
        defaultAttackRange = null;
        defaultLightLevel = null;
    }

    private void clearDropFor() {
        // reset "dropFor" variables before reading config
        dropForBlocks = false;
        dropForCreatures = false;
        dropForClick = false;
        dropForFishing = false;
        dropForExplosions = false;
        dropForSpawned = false;
        dropForSpawnTrigger = false;
        dropForRedstoneTrigger = false;
        dropForPlayerJoin = false;
        dropForPlayerRespawn = false;
        dropForPlayerConsume = false;
        dropForPlayerMove = false;
        dropForBlockGrow = false;
        dropForBlockPlace = false;
        actionParameterFound = false;
    }
    // load
    public void load(CommandSender sender) {
        List<String> result = new ArrayList<String>();
        
        try {
            // make sure all files exist, if not export from jar file
            firstRun();
            clearDropFor();
            // load initial config settings, verbosity, etc, this needs to be
            // before dependencies & drops files
            loadConfig();
            // intialise dependencies
            Dependencies.init();
            loadDropsFile(mainDropsName);
            blocksHash.applySorting();
            
            if (actionParameterFound)
                result.add("Note - 'action:' parameter is outdated (but still supported) - please use 'trigger:'");
            result.add("Config loaded - total targets: "+this.dropTargets +" sections: "+this.dropSections+ " failed: "+this.dropFailed);
            sendMessage(sender, result);
        } catch (ScannerException e) {
            if (verbosity.exceeds(HIGH)) e.printStackTrace();
            result.add("There was a syntax in your config file which has forced OtherDrops to abort loading!");
            result.add("The error was:\n" + e.toString());
            result.add("You can fix the error and reload with /odr.");
            sendMessage(sender, result);
        } catch (FileNotFoundException e) {
            if (verbosity.exceeds(HIGH)) e.printStackTrace();
            result.add("Config file not found!");
            result.add("The error was:\n" + e.toString());
            result.add("You can fix the error and reload with /odr.");
            sendMessage(sender, result);
        } catch (IOException e) {
            if (verbosity.exceeds(HIGH)) e.printStackTrace();
            result.add("There was an IO error which has forced OtherDrops to abort loading!");
            result.add("The error was:\n" + e.toString());
            result.add("You can fix the error and reload with /odr.");
            sendMessage(sender, result);
        } catch (InvalidConfigurationException e) {
            if (verbosity.exceeds(HIGH)) e.printStackTrace();
            result.add("Config is invalid!");
            result.add("The error was:\n" + e.toString());
            result.add("You can fix the error and reload with /odr.");
            sendMessage(sender, result);
        } catch (NullPointerException e) {
            result.add("Config load failed!");
            result.add("The error was:\n" + e.toString());
            if (verbosity.exceeds(Verbosity.NORMAL)) e.printStackTrace();
            result.add("Please try the latest version & report this issue to the developer if the problem remains.");
            sendMessage(sender, result);
        } catch (Exception e) {
            if (verbosity.exceeds(HIGH)) e.printStackTrace();
            result.add("Config load failed!  Something went wrong.");
            result.add("The error was:\n" + e.toString());
            result.add("If you can fix the error, reload with /odr.");
            sendMessage(sender, result);
        }
        OtherDrops.disableOtherDrops(); // deregister all listeners
        OtherDrops.enableOtherDrops(); // register only needed listeners

        plotConfigDataToMetrics();
    }

    private void sendMessage(CommandSender sender, List<String> result) {
        if (sender != null) {
            sender.sendMessage(result.toArray(new String[0]));
        }
        
        // Yes, we want to log to console even when /odr is issued by player
        Log.logInfo(result);
    }

    /**
     * Check for config files and other settings (events & includes), if not
     * found then export the resource from plugin jar file.
     * 
     * @throws Exception
     */
    private void firstRun() throws Exception {
        if (!checkIfAllowedToRefreshFiles())
            return;

        List<String> files = new ArrayList<String>();
        files.add("otherdrops-config.yml");
        files.add("otherdrops-drops.yml");

        files.add("includes/od-dyewool.yml");
        files.add("includes/od-fix_undroppables.yml");
        files.add("includes/od-goldtools-basic.yml");
        files.add("includes/od-goldtools-smelt.yml");
        files.add("includes/od-leaf_overhaul.yml");
        files.add("includes/od-ore_extraction.yml");
        files.add("includes/od-playerdeath_zombie.yml");
        files.add("includes/od-random_examples.yml");
        files.add("includes/od-stop_mob_farms.yml");
        files.add("includes/od-undead_chaos.yml");
        files.add("includes/od-unit_testing.yml");
        files.add("includes/overhaul-catballs.yml");
        files.add("includes/overhaul-zarius.yml");

        files.add("events/Explosions.jar");
        files.add("events/Sheep.jar");
        files.add("events/Trees.jar");
        files.add("events/Weather.jar");

        for (String filename : files) {
            File file = new File(parent.getDataFolder(), filename);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                copy(parent.getResource(filename), file);
            }
        }
    }

    private boolean checkIfAllowedToRefreshFiles()
            throws FileNotFoundException, IOException,
            InvalidConfigurationException {
        File file = new File(parent.getDataFolder(), "otherdrops-config.yml");
        if (file.exists()) {
            YamlConfiguration globalConfig = YamlConfiguration
                    .loadConfiguration(file);
            globalConfig.load(file);
            if (!globalConfig.getBoolean("restore_deleted_config_files", true))
                return false;
        }
        return true;
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set up any required custom graphs that count data from config loading.
     * Currently counts used triggers
     * 
     */
    private void plotConfigDataToMetrics() {
        if (!Dependencies.hasMetrics())
            return;

        Metrics metrics = Dependencies.getMetrics();

        // Construct a graph, which can be immediately used and considered as
        // valid
        Graph graph = metrics.createGraph("Triggers");
        String logMsg = "Custom Metrics, logging: ";

        for (final Entry<String, Integer> entry : triggerCounts.entrySet()) {
            logMsg += entry.getKey() + "+" + entry.getValue() + ", ";
            graph.addPlotter(new Metrics.Plotter(entry.getKey()) {

                @Override
                public int getValue() {
                    return entry.getValue();
                }

            });
        }

        triggerCounts.clear();

        Log.logInfo(logMsg.substring(0, logMsg.length() - 2), Verbosity.HIGH);
        metrics.start();
    }
    
    public void loadConfig() throws FileNotFoundException, IOException,
            InvalidConfigurationException {
        this.dropSections = 0; this.dropTargets = 0; this.dropFailed = 0; // initialise counts
        blocksHash.clear(); // clear here to avoid issues on /obr reloading
        loadedDropFiles.clear();
        clearDefaults();

        String filename = "otherdrops-config.yml";
        if (!(new File(parent.getDataFolder(), filename).exists()))
            filename = "otherblocks-globalconfig.yml"; // Compatibility with old
                                                       // filename
        if (!(new File(parent.getDataFolder(), filename).exists()))
            filename = "otherdrops-config.yml"; // If old file not found, go
                                                // back to new name

        File global = new File(parent.getDataFolder(), filename);
        YamlConfiguration globalConfig = YamlConfiguration
                .loadConfiguration(global);
        // Make sure config file exists (even for reloads - it's possible this
        // did not create successfully or was deleted before reload)
        if (!global.exists()) {
            try {
                global.createNewFile();
                Log.logInfo("Created an empty file " + parent.getDataFolder()
                        + "/" + filename + ", please edit it!");
                globalConfig.set("verbosity", "normal");
                globalConfig.set("priority", "high");
                globalConfig.save(global);
            } catch (IOException ex) {
                Log.logWarning(parent.getDescription().getName()
                        + ": could not generate " + filename
                        + ". Are the file permissions OK?");
            }
        }
        
        // Load in the values from the configuration file
        globalConfig.load(global);
        String configKeys = globalConfig.getKeys(false).toString();

        verbosity = getConfigVerbosity(globalConfig);
        enableBlockTo = globalConfig.getBoolean("enableblockto", false);
        moneyPrecision = globalConfig.getInt("money-precision", 2);
        customDropsForExplosions = globalConfig.getBoolean("customdropsforexplosions", false);
        defaultDropSpread = globalConfig.getBoolean("default_dropspread", true);
        disableXpOnNonDefault = globalConfig.getBoolean("disable_xp_on_non_default", true);
        enchantmentsIgnoreLevel = globalConfig.getBoolean("enchantments_ignore_level", false);
        enchantmentsUseUnsafe = globalConfig.getBoolean("enchantments_use_unsafe", false);
        enchantmentsRestrictMatching = globalConfig.getBoolean("enchantments_restrict_matching", true);

        configKeysGetDeep = globalConfig.getBoolean("config_keys_get_deep", true);

        spawnTriggerIgnoreOtherDropsSpawn = globalConfig.getBoolean("spawntrigger_ignores_otherdrops_spawn", true);

        globalLootOverridesDefault = globalConfig.getBoolean("loot_overrides_default", true);
        globalMoneyOverridesDefault = globalConfig.getBoolean("money_overrides_default", false);
        globalXpOverridesDefault = globalConfig.getBoolean("xp_overrides_default", false);

        matchMobByNameOnly = globalConfig.getBoolean("match_mob_by_name_only", true);

        exportEnumLists = globalConfig.getBoolean("export_enum_lists", true);
        globalAllowAnyReplacementBlock = globalConfig.getBoolean("allow_any_replacementblock", false);
        globalRedstonewireTriggersSurrounding = globalConfig.getBoolean("redstonewire_triggers_surrounding", true);
        globalUpdateChecking = globalConfig.getBoolean("update_checker", true);
        globalDisableMetrics = globalConfig.getBoolean("disable_metrics", false);
        primedTNTEnabled = globalConfig.getBoolean("primed_tnt", false);
        globalOverrideExplosionCap = globalConfig.getBoolean("override_explosion_cap", false);
        globalCustomSpawnLimit = globalConfig.getInt("custom_spawn_limit", 150);

        gTimeFormat = globalConfig.getString("time_format", "HH:mm:ss");
        gDateFormat = globalConfig.getString("date_format", "yyyy/MM/dd");

        gColorLogMessages = globalConfig.getBoolean("color_log_messages", true);
        gActionRadius = globalConfig.getInt("action_radius", 10);
        gcustomBlockBreakToMcmmo = globalConfig.getBoolean("send_customblockbreak_to_mcmmo", true);

        mainDropsName = globalConfig.getString("rootconfig",
                "otherdrops-drops.yml");
        if (!(new File(parent.getDataFolder(), mainDropsName).exists())
                && new File(parent.getDataFolder(),
                        "otherblocks-globalconfig.yml").exists())
            mainDropsName = "otherblocks-globalconfig.yml"; // Compatibility
                                                            // with old filename

        events = new ConfigurationNode(
                globalConfig.getConfigurationSection("events"));
        if (events == null) {
            globalConfig.set("events", new HashMap<String, Object>());
            events = new ConfigurationNode(new HashMap<String, Object>());
            if (events == null)
                Log.logWarning("EVENTS ARE NULL");
            else
                Log.logInfo("Events node created.", NORMAL);
        }

        // Warn if DAMAGE_WATER is enabled
        if (enableBlockTo)
            Log.logWarning("blockto/damage_water enabled - BE CAREFUL");

        try {
            SpecialResultLoader.loadEvents();
        } catch (Exception except) {
            Log.logWarning("Event files failed to load - this shouldn't happen, please inform developer.");
            if (verbosity.exceeds(HIGHEST))
                except.printStackTrace();
        }

        Log.logInfo("Loaded global config (" + global + "), keys found: "
                + configKeys + " (verbosity=" + verbosity + ")", Verbosity.HIGHEST);
    }

    private void loadDropsFile(String filename) throws Exception {
        // Check for infinite include loops
        if (loadedDropFiles.contains(filename)) {
            Log.logWarning("Infinite include loop detected at " + filename);
            return;
        } else
            loadedDropFiles.add(filename);

        Log.logInfo("Loading file: " + filename, NORMAL);

        File yml = new File(parent.getDataFolder(), filename);
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(yml);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (InvalidConfigurationException e) {
            //e.printStackTrace();
            throw e;
        }

        // Make sure config file exists (even for reloads - it's possible this
        // did not create successfully or was deleted before reload)
        if (!yml.exists()) {
            try {
                yml.createNewFile();
                Log.logInfo("Created an empty file " + parent.getDataFolder()
                        + "/" + filename + ", please edit it!");
                config.set("otherdrops", null);
                config.set("include-files", null);
                config.set("defaults", null);
                config.set("aliases", null);
                config.set("configversion", 3);
                config.save(yml);
            } catch (IOException ex) {
                Log.logWarning(parent.getDescription().getName()
                        + ": could not generate " + filename
                        + ". Are the file permissions OK?");
            }
            // Nothing to load in this case, so exit now
            return;
        }

        // Warn if wrong version
        int configVersion = config.getInt("configversion", 3);
        if (configVersion < 3)
            Log.logWarning("config file appears to be in older format; some things may not work");
        else if (configVersion > 3)
            Log.logWarning("config file appears to be in newer format; some things may not work");

        // Load defaults; each of these functions returns null if the value
        // isn't found
        Map<String, Object> map = new HashMap<String, Object>();
        ConfigurationNode defaultsNode = null;
        if (config.getConfigurationSection("defaults") == null) {
            if (config.getMapList("defaults") != null)
                if (config.getMapList("defaults").size() > 0)
                    defaultsNode = ConfigurationNode.parse(
                            config.getMapList("defaults")).get(0);
        } else {
            System.out.println("list: "
                    + config.getConfigurationSection("defaults").getKeys(true)
                            .toString());
            ConfigurationSection defaultsSection = config
                    .getConfigurationSection("defaults");
            for (String key : config.getConfigurationSection("defaults")
                    .getKeys(true)) {
                map.put(key, defaultsSection.get(key));
            }
            defaultsNode = new ConfigurationNode(map);
        }

        clearDefaults();
        loadModuleDefaults(defaultsNode);
        // else Log.logInfo("Loading defaults: none found.", Verbosity.HIGH);

        // Load the drops
        ConfigurationSection node = config
                .getConfigurationSection("otherdrops");
        Set<String> blocks = null;
        if (node != null)
            blocks = node.getKeys(configKeysGetDeep);

        if (node == null) { // Compatibility
            node = config.getConfigurationSection("otherblocks");
            if (node != null)
                blocks = node.getKeys(configKeysGetDeep);
        }
        if (node != null) {
            Log.logInfo("Loading keys: " + blocks, HIGHEST);

            for (Object blockNameObj : blocks.toArray()) {
                String blockName = "";
                blockName = blockNameObj.toString();

                if (blockNameObj instanceof Integer) {
                    Log.logWarning("Integer target: "
                            + blockName
                            + " (cannot process - please enclose in quotation marks eg. \""
                            + blockName + "\")");
                    this.dropFailed++;
                    continue;
                }

                Log.logInfo("Loading drop: " + blockName, HIGH);

                // convert spaces and dashes to underscore before parsing to
                // allow more flexible matching
                Target target = parseTarget(blockName.replaceAll("[ -]", "_"));
                if (target == null) {
                    Log.logWarning("Unrecognized target (skipping): "
                            + blockName, Verbosity.NORMAL);
                    this.dropFailed++;
                    continue;
                }
                switch (target.getType()) {
                case BLOCK:
                    dropForBlocks = true;
                    break;
                case PLAYER:
                    dropForCreatures = true;
                    break;
                case CREATURE:
                    dropForCreatures = true;
                    break;
                case EXPLOSION:
                    dropForExplosions = true;
                    break;
                case SPECIAL: // used for "ANY" drops - TODO: add specific
                              // categories for ANY_CREATURE and ANY_BLOCK
                    dropForBlocks = true;
                    dropForCreatures = true;
                    break;
                default:
                    // If you want to have other similar flags, add them above
                    // the default
                    // Possibilities are DAMAGE, PROJECTILE, SPECIAL (but
                    // special isn't used for anything)
                    // (The default is here so I don't get an
                    // "incomplete switch" warning.)
                }

                // List<Map<?, ?>> blockNode =
                // node.getMapList(blockName);//ConfigurationSection("GRASS.drop");
                // if (blockNode == null)
                // OtherDrops.logInfo("Blocknode is null!!");

                List<ConfigurationNode> drops = ConfigurationNode.parse(node
                        .getMapList(blockName));

                // Check if drop contains actual mappings or just a string
                Object nodeValue = node.get(blockName);
                if (drops.size() == 0 && nodeValue != null) {
                    // This section supports "TARGET: [drops]" short-format
                    // by grabbing the string/list/map and stashing it in a mapping to the drop parameter
                    String parameterName = "drop";
                    if (nodeValue instanceof String) {
                        String stringNodeVal = (String) nodeValue;
                        if (stringNodeVal.matches("[0-9~.-]+")) {
                            parameterName = "money";                            
                        }
                    } else if (nodeValue instanceof Integer || nodeValue instanceof Float || nodeValue instanceof Double) {
                        parameterName = "money";
                    }
                    
                    Map<String, Object> dropMap = new HashMap<String, Object>();
                    dropMap.put(parameterName, nodeValue);
                    drops = new ArrayList<ConfigurationNode>();
                    drops.add(new ConfigurationNode(dropMap));

                }

                loadBlockDrops(drops, blockName, target);

                this.dropTargets++;
                // Future modulized parameters parsing
                /*
                 * for (Map<String, Object> drop : blockNode) { for (String
                 * parameter : drop.keySet()) { String parameterKey =
                 * parameter.split(".")[0]; Parameters.parse(parameterKey); } }
                 */
                // OtherDrops.logInfo("Loading config... blocknode:"+blockNode.toString()
                // +" for blockname: "+originalBlockName);
                // Set<String> drops = null;
                // if (blockNode != null) drops = blockNode.getKeys(false);
                // loadBlockDrops(blockNode, blockName, target, node);
            }
        }

        // Load the include files
        List<String> includeFiles = config.getStringList("include-files");
        for (String include : includeFiles)
            loadDropsFile(include);
    }

    protected void loadModuleDefaults(ConfigurationNode defaults) {
        // Check for null - it's possible that the defaults key doesn't exist or
        // is empty
        defaultTrigger = Collections.singletonList(Trigger.BREAK);
        lootOverridesDefault = globalLootOverridesDefault;
        xpOverridesDefault = globalXpOverridesDefault;
        moneyOverridesDefault = globalMoneyOverridesDefault;

        if (defaults != null) {
            Log.logInfo("Loading defaults... nodemap=" + defaults.toString(),
                    HIGH);
            defaultWorlds = parseWorldsFrom(defaults, null);
            defaultRegions = parseRegionsFrom(defaults, null);
            defaultWeather = Weather.parseFrom(defaults, null);
            defaultBiomes = parseBiomesFrom(defaults, null);
            defaultTime = Time.parseFrom(defaults, null);
            defaultPermissionGroups = parseGroupsFrom(defaults, null);
            defaultPermissions = parsePermissionsFrom(defaults, null);
            defaultHeight = Comparative.parseFrom(defaults, "height", null);
            defaultAttackRange = Comparative.parseFrom(defaults, "attackrange",
                    null);
            defaultLightLevel = Comparative.parseFrom(defaults, "lightlevel",
                    null);
            defaultTrigger = Trigger.parseFrom(defaults, defaultTrigger);

            lootOverridesDefault = defaults.getBoolean(
                    "loot_overrides_default", globalLootOverridesDefault);
            moneyOverridesDefault = defaults.getBoolean(
                    "money_overrides_default", globalMoneyOverridesDefault);
            xpOverridesDefault = defaults.getBoolean("xp_overrides_default",
                    globalXpOverridesDefault);
        } else
            Log.logInfo("No defaults set.", HIGHEST);
    }

    private void loadBlockDrops(List<ConfigurationNode> drops,
            String blockName, Target target) {
        for (ConfigurationNode dropNode : drops) {
            boolean isGroup = dropNode.getKeys().contains("dropgroup");
            List<Trigger> triggers = new ArrayList<Trigger>();
            List<Trigger> leafdecayTrigger = new ArrayList<Trigger>();
            leafdecayTrigger.add(Trigger.LEAF_DECAY);
            if (blockName.equalsIgnoreCase("SPECIAL_LEAFDECAY")) {
                triggers = Trigger.parseFrom(dropNode, leafdecayTrigger);
            } else {
                triggers = Trigger.parseFrom(dropNode, defaultTrigger);
            }

            if (triggers.isEmpty()) {
                // FIXME: Find a way to say which trigger was invalid
                Log.logWarning("No recognized trigger for block " + blockName
                        + "; skipping (known triggers: "
                        + Trigger.getValidActions().toString() + ")", NORMAL);
                continue;
            }
            for (Trigger trigger : triggers) {
                if (trigger.equals(Trigger.HIT)
                        && target.getType() == ItemCategory.CREATURE)
                    incrementTriggerCounts("HIT_MOB");
                else if (trigger.equals(Trigger.HIT)
                        && target.getType() == ItemCategory.BLOCK)
                    incrementTriggerCounts("HIT_BLOCK");

                // show difference between mob death and block break for Metrics
                if (trigger.equals(Trigger.BREAK)
                        && target.getType() == ItemCategory.CREATURE)
                    incrementTriggerCounts("MOB_DEATH");
                else if (trigger.equals(Trigger.BREAK)
                        && target.getType() == ItemCategory.BLOCK)
                    incrementTriggerCounts("BLOCK_BREAK");
                else
                    incrementTriggerCounts(trigger.toString());

                // Register "dropForInteract"
                if (trigger.equals(Trigger.HIT)
                        || trigger.equals(Trigger.RIGHT_CLICK)) {
                    dropForClick = true;
                } else if (trigger.equals(Trigger.FISH_CAUGHT)
                        || trigger.equals(Trigger.FISH_FAILED)) {
                    dropForFishing = true;
                } else if (trigger.equals(Trigger.MOB_SPAWN)) {
                    dropForSpawned = true; // sets the spawnevent to be listened
                    dropForSpawnTrigger = true; // allows spawnevents to launch
                                                // a drop
                } else if (trigger.equals(Trigger.POWER_UP)
                        || trigger.equals(Trigger.POWER_DOWN)) {
                    dropForRedstoneTrigger = true; // allows redstone power
                                                   // events to launch a drop
                } else if (trigger.equals(Trigger.PLAYER_JOIN)) {
                    dropForPlayerJoin = true; // allows this event to launch a
                                              // drop
                } else if (trigger.equals(Trigger.PLAYER_RESPAWN)) {
                    dropForPlayerRespawn = true; // allows this event to launch
                                                 // a drop
                } else if (trigger.equals(Trigger.CONSUME_ITEM)) {
                    dropForPlayerConsume = true; // allows this event to launch
                                                 // a drop
                } else if (trigger.equals(Trigger.PLAYER_MOVE)) {
                    dropForPlayerMove = true;
                } else if (trigger.equals(Trigger.BLOCK_GROW)) {
                    dropForBlockGrow = true;
                } else if (trigger.equals(Trigger.PROJECTILE_HIT_BLOCK)) {
                    dropForProjectileHit = true;
                } else if (trigger.equals(Trigger.BLOCK_PLACE)) {
                    dropForBlockPlace = true;
                }
                // TODO: This reparses the same drop once for each listed
                // trigger; a way that involves parsing only once? Would require
                // having the drop class implement clone().
                CustomDrop drop = loadDrop(dropNode, target, trigger, isGroup);
                if (drop.getTool() == null || drop.getTool().isEmpty()) {
                    // FIXME: Should find a way to report the actual invalid
                    // tool as well
                    // FIXME: Also should find a way to report when some tools
                    // are valid and some are not
                    Log.logWarning("Unrecognized tool for block " + blockName
                            + "; skipping.", NORMAL);
                    continue;
                }
                blocksHash.addDrop(drop);
            }
        }
    }

    /**
     * Keeps a count of each individual trigger for the purpose of logging to
     * Metrics custom graph
     * 
     * @param triggerString
     */
    private void incrementTriggerCounts(String triggerString) {
        if (triggerCounts.get(triggerString) == null) {
            triggerCounts.put(triggerString, new Integer(1));
        } else {
            triggerCounts.put(triggerString,
                    triggerCounts.get(triggerString) + 1);
        }
    }

    private CustomDrop loadDrop(ConfigurationNode dropNode, Target target,
            Trigger trigger, boolean isGroup) {
        CustomDrop drop = isGroup ? new GroupDropEvent(target, trigger)
                : new SimpleDrop(target, trigger);
        loadConditions(dropNode, drop);
        if (isGroup)
            loadDropGroup(dropNode, (GroupDropEvent) drop, target, trigger);
        else
            loadSimpleDrop(dropNode, (SimpleDrop) drop);

        // Only allow PrimedTNT from mobs - very DANGEROUS for blocks (chain
        // reactions)
        // This has to be set here rather than in loadsimpledrop as we need to
        // know the target
        if (drop instanceof SimpleDrop) {
            if (((SimpleDrop) drop).getDropped() instanceof CreatureDrop) {
        		CreatureDrop cDrop = (CreatureDrop) ((SimpleDrop) drop).getDropped();
                if (cDrop.getCreature() == EntityType.PRIMED_TNT) {
                    if (!(target instanceof CreatureSubject) || !(primedTNTEnabled)) {
                        ((SimpleDrop) drop).setDropped(null);
                        Log.logWarning("DANGER: primedtnt not allowed to drop from blocks (a chain reaction can kill your server), drop removed. To enable these, check the config!", Verbosity.LOW);
                    }
                }
            }
        }
        return drop;
    }

    private void loadConditions(ConfigurationNode node, CustomDrop drop) {
        // drop.addActions(MessageAction.parse(node));
        // drop.addActions(PotionAction.parse(node));
        // drop.addActions(DamageAction.parse(node));
        drop.addActions(com.gmail.zariust.otherdrops.parameters.Action
                .parseNodes(node));
        drop.addConditions(com.gmail.zariust.otherdrops.parameters.Condition
                .parseNodes(node));

        // Read tool
        drop.setTool(parseAgentFrom(node));
        // Read faces
        drop.setBlockFace(parseFacesFrom(node));

        // Now read the stuff that might have a default; if null is returned,
        // use the default
        drop.setWorlds(parseWorldsFrom(node, defaultWorlds));
        drop.setRegions(parseRegionsFrom(node, defaultRegions));
        drop.setWeather(Weather.parseFrom(node, defaultWeather));
        drop.setBiome(parseBiomesFrom(node, defaultBiomes));
        drop.setTime(Time.parseFrom(node, defaultTime));
        drop.setGroups(parseGroupsFrom(node, defaultPermissionGroups));
        drop.setPermissions(parsePermissionsFrom(node, defaultPermissions));
        drop.setHeight(Comparative.parseFrom(node, "height", defaultHeight));
        drop.setAttackRange(Comparative.parseFrom(node, "attackrange",
                defaultAttackRange));
        drop.setLightLevel(Comparative.parseFrom(node, "lightlevel",
                defaultLightLevel));
        drop.setFlags(Flag.parseFrom(node));

        // Read chance, delay, etc
        drop.setChance(parseChanceFrom(node, "chance"));
        Object exclusive = node.get("exclusive");
        if (exclusive != null)
            drop.setExclusiveKey(exclusive.toString());

        // Note: playerrespawn requires minimum delay of 1
        if (drop.getTrigger() == Trigger.PLAYER_RESPAWN) {
            drop.setDelay(IntRange.parse(node.getString("delay", "1")));
        } else {
            drop.setDelay(IntRange.parse(node.getString("delay", "0")));
        }
    }

    public static double parseChanceFrom(ConfigurationNode node, String key) {
        String chanceString = node.getString(key, null);
        double chance = 100;
        if (chanceString == null) {
            chance = 100;
        } else {
            try {
                chance = Double.parseDouble(chanceString.replaceAll("%$", ""));
            } catch (NumberFormatException ex) {
                chance = 100;
            }
        }
        return chance;
    }

    private Location parseLocationFrom(ConfigurationNode node, String type,
            double d, double defY, double e) {
        String loc = getStringFrom(node, "loc-" + type, type + "loc");
        if (loc == null)
            return new Location(null, d, defY, e);
        double x = 0, y = 0, z = 0;
        String[] split = loc.split("/");
        if (split.length == 3) {
            try {
                x = Double.parseDouble(split[0]);
                y = Double.parseDouble(split[1]);
                z = Double.parseDouble(split[2]);
            } catch (NumberFormatException ex) {
                x = y = z = 0;
            }
        }
        return new Location(null, x, y, z);
    }

    private void loadSimpleDrop(ConfigurationNode node, SimpleDrop drop) {
        this.dropSections++;

        // Read drop
        @SuppressWarnings("unused")
		boolean deny = false;
        String dropStr = node.getString("drop", "UNSPECIFIED"); // default value
                                                                // should be
                                                                // NOTHING
                                                                // (DEFAULT will
                                                                // break some
                                                                // configs)
                                                                // FIXME: it
                                                                // should really
                                                                // be a third
                                                                // option -
                                                                // NOTAPPLICABLE,
                                                                // ie. doesn't
                                                                // change the
                                                                // drop
        dropStr = dropStr.replaceAll("[ -]", "_");
        if (dropStr.equalsIgnoreCase("DENY")) { // TODO: allow DENY to be
                                                // detected in a list (eg.
                                                // [DENY, SHEEP])
            drop.setDenied(true);
            // deny = true; // set to DENY (used later to set replacement block
            // to null)
            // drop.setDropped(new ItemDrop(Material.AIR)); // set the drop to
            // NOTHING
        } else
            drop.setDropped(DropType.parseFrom(node));

        setDefaultOverride(drop.getDropped());

        if (drop.getDropped() != null)
            Log.logInfo(drop.getTrigger() + " " + drop.getTarget() + " w/ "
                    + drop.getTool() + " -> " + drop.getDropped().toString(),
                    HIGH);
        else
            Log.logInfo(
                    "Loading drop (null: failed or default drop): "
                            + drop.getTrigger() + " with " + drop.getTool()
                            + " on " + drop.getTarget() + " -> \'" + dropStr
                            + "\"", HIGHEST);

        String quantityStr = node.getString("quantity");
        if (quantityStr == null)
            drop.setQuantity(1);
        else
            drop.setQuantity(DoubleRange.parse(quantityStr));
        // Damage
        drop.setToolDamage(ToolDamage.parseFrom(node));

        // to avoid replacement tools triggering immediately on right click....
        if (drop.getTrigger() == Trigger.RIGHT_CLICK) {
            if (drop.getToolDamage() != null
                    && drop.getToolDamage().isReplacement()) {
                if (drop.getDelay().getMax() == 0) {
                    drop.setDelay(1);
                    Log.logInfo(
                            "...replacetool & rightclick found, adding 'delay:1' to avoid triggering with replaced item.",
                            Verbosity.HIGHEST);
                }
            }
        }

        // Spread chance
        drop.setDropSpread(node, "dropspread", defaultDropSpread);
        // Replacement block
        drop.setReplacement(parseReplacement(node));
        // Random location multiplier
        drop.setRandomLocMult(parseLocationFrom(node, "randomise", 0, 0, 0));
        // Location offset
        if (drop.getDropped() instanceof CreatureDrop
                && drop.getTarget() instanceof BlockTarget)
            // Drop creature in the centre of the block, not on the corner
            drop.setLocationOffset(parseLocationFrom(node, "offset", 0.5, 1,
                    0.5));
        else
            drop.setLocationOffset(parseLocationFrom(node, "offset", 0, 0, 0));
        // Commands, messages, sound effects
        drop.setCommands(getMaybeList(node, "command", "commands"));
        drop.setMessages(getMaybeList(node, "message", "messages"));
        drop.setEffects(SoundEffect.parseFrom(node));
        // Events
        List<SpecialResult> dropEvents = SpecialResult.parseFrom(node);
        if (dropEvents == null)
            return; // We're done! Note, this means any new options must go
                    // above events!
        ListIterator<SpecialResult> iter = dropEvents.listIterator();
        while (iter.hasNext()) {
            SpecialResult event = iter.next();
            if (!event.canRunFor(drop))
                iter.remove();
        }
        drop.setEvents(dropEvents);
    }

    private void setDefaultOverride(DropType dropped) {
        if (dropped == null)
            return;

        if (dropped instanceof MoneyDrop) {
            dropped.overrideDefault = moneyOverridesDefault;
        } else if (dropped instanceof ExperienceDrop) {
            dropped.overrideDefault = xpOverridesDefault;
        } else if (dropped instanceof DropListExclusive) {
            for (DropType drop : ((DropListExclusive) dropped).getGroup()) {
                setDefaultOverride(drop);
            }
        } else if (dropped instanceof DropListInclusive) {
            for (DropType drop : ((DropListInclusive) dropped).getGroup()) {
                setDefaultOverride(drop);
            }
        } else {
            dropped.overrideDefault = lootOverridesDefault;
        }

    }

    private void loadDropGroup(ConfigurationNode node, GroupDropEvent group,
            Target target, Trigger trigger) {
        if (!node.getKeys().contains("drops")) {
            Log.logWarning("Empty drop group \"" + group.getName()
                    + "\"; will have no effect!");
            return;
        }
        Log.logInfo(
                "Loading drop group: " + group.getTrigger() + " with "
                        + group.getTool() + " on " + group.getTarget() + " -> "
                        + group.getName(), HIGHEST);
        group.setMessages(getMaybeList(node, "message", "messages"));

        List<ConfigurationNode> drops = node.getNodeList("drops", null);
        for (ConfigurationNode dropNode : drops) {
            boolean isGroup = dropNode.getKeys().contains("dropgroup");
            CustomDrop drop = loadDrop(dropNode, target, trigger, isGroup);
            group.add(drop);
        }
        group.sort();
    }

    public static List<String> getMaybeList(ConfigurationNode node,
            String... keys) {
        if (node == null)
            return new ArrayList<String>();
        Object prop = null;
        String key = null;
        for (int i = 0; i < keys.length; i++) {
            key = keys[i];
            prop = node.get(key);
            if (prop != null)
                break;
        }
        List<String> list;
        if (prop == null)
            return new ArrayList<String>();
        else if (prop instanceof List)
            list = node.getStringList(key);
        else
            list = Collections.singletonList(prop.toString());
        return list;
    }

    public static String getStringFrom(ConfigurationNode node, String... keys) {
        String prop = null;
        for (int i = 0; i < keys.length; i++) {
            prop = node.getString(keys[i]);
            if (prop != null)
                break;
        }
        return prop;
    }

    @SuppressWarnings("deprecation")
	private BlockTarget parseReplacement(ConfigurationNode node) {
        String blockName = getStringFrom(node, "replacementblock", "replaceblock", "replace");
        if (blockName == null)
            return null;
        String[] split = blockName.split("@");
        String name = split[0];
        String dataStr = split.length > 1 ? split[1] : "";
        Material mat = null;
        if(name.matches("[0-9]+")) {
            Log.logWarning("Error while parsing: " + name + ". Support for numerical IDs has been dropped! Locating item ID...");
        	Log.logWarning("Please replace the occurence of '" + name + "' with '" + Material.getMaterial(Integer.parseInt(name)).toString() + "'");
        }
        mat = Material.getMaterial(name.toUpperCase());
        if (mat == null) {
            return null;
        }
        if (!mat.isBlock() && !(this.globalAllowAnyReplacementBlock)) {
            Log.logWarning("Error in 'replacementblock' - " + mat.toString() + " is not a block-type.");
            return null;
        }

        if (dataStr.isEmpty())
            return new BlockTarget(mat);
        Data data = null;
        try {
            int intData = Integer.parseInt(dataStr);
            return new BlockTarget(mat, intData);
        } catch (NumberFormatException e) {
            try {
                data = SimpleData.parse(mat, dataStr);
            } catch (IllegalArgumentException ex) {
                Log.logWarning(ex.getMessage());
                return null;
            }
        }
        if (data == null)
            return new BlockTarget(mat);
        return new BlockTarget(mat, data);

    }

    private Map<World, Boolean> parseWorldsFrom(ConfigurationNode node,
            Map<World, Boolean> def) {
        List<String> worlds = getMaybeList(node, "world", "worlds");
        List<String> worldsExcept = getMaybeList(node, "worldexcept", "worldsexcept");
        if (worlds.isEmpty() && worldsExcept.isEmpty())
            return def;
        Map<World, Boolean> result = new HashMap<World, Boolean>();
        result.put(null, containsAll(worlds));
        for (String name : worlds) {
            World world = Bukkit.getServer().getWorld(name);
            if (world == null && name.startsWith("-")) {
                result.put(null, true);
                world = Bukkit.getServer().getWorld(name.substring(1));
                if (world == null) {
                    Log.logWarning("Invalid world " + name + "; skipping...");
                    continue;
                }
                result.put(world, false);
            } else if (world == null) {
                if (name.equalsIgnoreCase("ALL")
                        || name.equalsIgnoreCase("ANY")) {
                    result.put(null, true);
                } else {
                    Log.logWarning("Invalid world " + name + "; skipping...");
                    continue;
                }
            } else
                result.put(world, true);
        }
        for (String name : worldsExcept) {
            World world = Bukkit.getServer().getWorld(name);
            if (world == null) {
                Log.logWarning("Invalid world exception " + name + "; skipping...");
                continue;
            }
            result.put(null, true);
            result.put(world, false);
        }
        return result;
    }

    // TODO: refactor parseWorldsFrom, Regions & Biomes as they are all very
    // similar - (beware - fragile, breaks easy)
    private Map<String, Boolean> parseRegionsFrom(ConfigurationNode node,
            Map<String, Boolean> def) {
        List<String> regions = getMaybeList(node, "region", "regions");
        List<String> regionsExcept = getMaybeList(node, "regionexcept",
                "regionsexcept");
        if (regions.isEmpty() && regionsExcept.isEmpty())
            return def;
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        for (String name : regions) {
            if (name.startsWith("-")) {
                result.put(name, false); // deliberately including the "-" sign
            } else
                result.put(name, true);
        }
        for (String name : regionsExcept) {
            result.put(name, false);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    private Map<Biome, Boolean> parseBiomesFrom(ConfigurationNode node,
            Map<Biome, Boolean> def) {
        List<String> biomes = getMaybeList(node, "biome", "biomes");
        if (biomes.isEmpty())
            return def;
        HashMap<Biome, Boolean> result = new HashMap<Biome, Boolean>();
        result.put(null, containsAll(biomes));
        for (String name : biomes) {
            name = name.toUpperCase();
            boolean biomeNegated = false;
            boolean matched = false;

            if (name.startsWith("-")) {
                result.put(null, true);
                biomeNegated = true;
                name = name.substring(1);
            }
            // TODO: write some tests
            for (Biome biomeMatch : Biome.values()) {
                if (name.equalsIgnoreCase(biomeMatch.name())) {
                    result.put(biomeMatch, !biomeNegated);
                    matched = true;
                }
                Log.logInfo("Biome match: checking " + name + " against " + biomeMatch.name() + ", match = " + matched, HIGHEST);
            }
            if (!matched) {
                Log.logWarning("Invalid biome " + name + "; skipping...");
            }
        }
        return result;
    }

    private Map<String, Boolean> parseGroupsFrom(ConfigurationNode node,
            Map<String, Boolean> def) {
        List<String> groups = getMaybeList(node, "permissiongroup",
                "permissiongroups");
        List<String> groupsExcept = getMaybeList(node, "permissiongroupexcept",
                "permissiongroupsexcept");
        if (groups.isEmpty() && groupsExcept.isEmpty())
            return def;
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        for (String name : groups) {
            if (name.startsWith("-")) {
                result.put(name, false);
            } else
                result.put(name, true);
        }
        for (String name : groupsExcept) {
            result.put(name, false);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    private Map<String, Boolean> parsePermissionsFrom(ConfigurationNode node,
            Map<String, Boolean> def) {
        List<String> permissions = getMaybeList(node, "permission",
                "permissions");
        List<String> permissionsExcept = getMaybeList(node, "permissionexcept",
                "permissionsexcept");
        if (permissions.isEmpty() && permissionsExcept.isEmpty())
            return def;
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        for (String name : permissions) {
            if (name.startsWith("-")) {
                result.put(name, false);
            } else
                result.put(name, true);
        }
        for (String name : permissionsExcept) {
            result.put(name, false);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    private Map<BlockFace, Boolean> parseFacesFrom(ConfigurationNode node) {
        List<String> faces = getMaybeList(node, "face", "faces");
        if (faces.isEmpty())
            return null;
        HashMap<BlockFace, Boolean> result = new HashMap<BlockFace, Boolean>();
        result.put(null, containsAll(faces));
        for (String name : faces) {
            BlockFace face = enumValue(BlockFace.class, name.toUpperCase());
            if (face == null && name.startsWith("-")) {
                result.put(null, true);
                face = enumValue(BlockFace.class, name.substring(1)
                        .toUpperCase());
                if (face == null) {
                    Log.logWarning("Invalid block face " + name
                            + "; skipping...");
                    continue;
                }
                result.put(face, false);
            } else
                result.put(face, true);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    public static boolean containsAll(List<String> list) {
        for (String str : list) {
            if (str.equalsIgnoreCase("ALL") || str.equalsIgnoreCase("ANY"))
                return true;
        }
        return false;
    }

    public static Map<Agent, Boolean> parseAgentFrom(ConfigurationNode node) {
        List<String> tools = OtherDropsConfig.getMaybeList(node, "agent",
                "agents", "tool", "tools");
        List<String> toolsExcept = OtherDropsConfig.getMaybeList(node,
                "agentexcept", "agentsexcept", "toolexcept", "toolsexcept");
        Map<Agent, Boolean> toolMap = new HashMap<Agent, Boolean>();
        if (tools.isEmpty()) {
            toolMap.put(parseAgent("ALL"), true); // no tool defined - default
                                                  // to all
        } else
            for (String tool : tools) {
                Agent agent = null;
                boolean flag = true;
                if (tool.startsWith("-")) {
                    agent = parseAgent(tool.substring(1));
                    flag = false;
                } else
                    agent = parseAgent(tool);
                if (agent != null)
                    toolMap.put(agent, flag);
            }
        for (String tool : toolsExcept) {
            Agent agent = parseAgent(tool);
            if (agent != null)
                toolMap.put(agent, false);
        }
        return toolMap;
    }

    public static Agent parseAgent(String agent) {
        ODItem item = ODItem.parseItem(agent);
        
        /*
        String[] split = agent.split("@");
        // TODO: because data = "" then data becomes 0 in toolagent rather than
        // null - fixed in toolagent, need to check other agents
        String name = split[0].toUpperCase(), data = "", enchantment = "", lorename = "";
        if (split.length > 1) {
            data = split[1];
            String[] split2 = data.split("!", 2);
            if (split2.length > 0)
                data = split2[0];
            if (split2.length > 1) {
                enchantment = split2[1];

                String[] split3 = enchantment.split("~");
                enchantment = split3[0];
                if (split3.length > 1) {
                    lorename = split3[1];
                }
            }
        }
        */

        String name = item.name;
        String upperName = name.toUpperCase();
        String data = item.getDataString();
        
        // Agent can be one of the following
        // - A tool; ie, a Material constant
        // - One of the Material synonyms NOTHING and DYE
        // - A MaterialGroup constant
        // - One of the special wildcards ANY, ANY_CREATURE, ANY_DAMAGE
        // - A DamageCause constant prefixed by DAMAGE_
        // - DAMAGE_FIRE_TICK and DAMAGE_CUSTOM are valid but not allowed
        // - DAMAGE_WATER is invalid but allowed, and stored as CUSTOM
        // - A EntityType constant prefixed by CREATURE_
        // - A projectile; ie a Material constant prefixed by PROJECTILE_
        if (MaterialGroup.isValid(name) || upperName.startsWith("ANY")
                || upperName.equals("ALL"))
            return AnySubject.parseAgent(name);
        else if (upperName.equals("PLAYER"))
            return PlayerSubject.parse(data);
        else if (upperName.equals("PLAYERGROUP"))
            return new GroupSubject(data);
        else if (upperName.startsWith("DAMAGE_"))
            return EnvironmentAgent.parse(name, data);
        else {
            LivingSubject creatureSubject = CreatureSubject.parse(name, data, item.getDisplayName());

            if (creatureSubject != null)
                return creatureSubject;
            else if (upperName.startsWith("PROJECTILE"))
                return ProjectileAgent.parse(name, data);
            else if (upperName.startsWith("EXPLOSION"))
                return ExplosionAgent.parse(name, data);
            else
                return ToolAgent.parse(name, data, item.getEnchantments(), item.getDisplayName());

        }
    }

    public static Target parseTarget(String blockName) {
        blockName = CommonMaterial.substituteAlias(blockName);

/*        String[] split = blockName.split("@");
        if (blockName.matches("\\w+:.*")) {
            split = blockName.split(":", 2);
        }
        String name = split[0], data = "";
        String upperName = name.toUpperCase();
        if (split.length > 1)
            data = split[1];
            */
        
        ODItem item = ODItem.parseItem(blockName);
        String name = item.name;
        String upperName = item.name.toUpperCase();
        String data = item.getDataString();
        // Target name is one of the following:
        // - A Material constant that is a block, painting, or vehicle
        // - A EntityType constant prefixed by CREATURE_
        // - An integer representing a Material
        // - One of the keywords PLAYER or PLAYERGROUP
        // - Vehicle starting with VEHICLE (note: BOAT, MINECART, etc
        // can only be vehicles in a target so process accordingly)
        // - A MaterialGroup constant containing blocks
        if (upperName.equals("PLAYER"))
            return PlayerSubject.parse(data);
        else if (upperName.equals("PLAYERGROUP"))
            return new GroupSubject(data);
        else if (upperName.startsWith("ANY") || upperName.equals("ALL"))
            return AnySubject.parseTarget(upperName);
        else if (upperName.startsWith("VEHICLE")
                || upperName.matches("BOAT|MINECART|BOAT_SPRUCE|BOAT_JUNGLE|BOAT_BIRCH|BOAT_ACACIA|BOAT_DARK_OAk"))
            return VehicleTarget.parse(
                    Material.getMaterial(upperName.replaceAll("VEHICLE_", "")),
                    data);
        else {
            LivingSubject creatureSubject = CreatureSubject.parse(name, data, item.getDisplayName());

            if (creatureSubject != null)
                return creatureSubject;
            else if (upperName.equalsIgnoreCase("SPECIAL_LEAFDECAY"))
                return BlockTarget.parse("LEAVES", data, item.displayname); // for compatibility
            else
                return BlockTarget.parse(name, data, item.displayname);

        }
    }

    public ConfigurationNode getEventNode(SpecialResultHandler event) {
        String name = event.getName();
        if (events == null) {
            Log.logInfo("EventLoader (" + name
                    + ") failed to get config-node, events is null.", HIGH);
            return null;
        }
        ConfigurationNode node = events.getConfigurationNode(name);
        if (node == null) {
            events.set(name, new HashMap<String, Object>());
            node = events.getConfigurationNode(name);
        }

        return node;
    }

    public static Verbosity getVerbosity() {
        return verbosity;
    }

    public static void setVerbosity(Verbosity verbosity) {
        OtherDropsConfig.verbosity = verbosity;
    }
}