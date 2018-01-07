package com.gmail.zariust.otherdrops;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.Warning.WarningState;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import com.avaje.ebean.config.ServerConfig;
import com.gmail.zariust.otherdrops.event.CustomDropTest;

public class BukkitMock {
    static {
        Bukkit.setServer(getServer());
    }

    // get a fake server - only real part is that .getLogger gets an actual
    // logger object
    // also: .getWorld("TestWorld") will return a mock world
    public static Server getServer() {
        return new Server() {

            @Override
            public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public Set<String> getListeningPluginChannels() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean useExactLoginLocation() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean unloadWorld(World arg0, boolean arg1) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean unloadWorld(String arg0, boolean arg1) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void unbanIP(String arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void shutdown() {
                // TODO Auto-generated method stub

            }

            @Override
            public void setWhitelist(boolean arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setSpawnRadius(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setDefaultGameMode(GameMode arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void savePlayers() {
                // TODO Auto-generated method stub

            }

            @Override
            public void resetRecipes() {
                // TODO Auto-generated method stub

            }

            @Override
            public void reloadWhitelist() {
                // TODO Auto-generated method stub

            }

            @Override
            public void reload() {
                // TODO Auto-generated method stub

            }

            @Override
            public Iterator<Recipe> recipeIterator() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<Player> matchPlayer(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean hasWhitelist() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public List<World> getWorlds() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public File getWorldContainer() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public World getWorld(UUID arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public World getWorld(String arg0) {

                if (arg0.equalsIgnoreCase("TestWorld"))
                    return CustomDropTest.testWorld;
                else if (arg0.equalsIgnoreCase("SecondWorld"))
                    return CustomDropTest.secondWorld;
                return null;
            }

            @Override
            public Set<OfflinePlayer> getWhitelistedPlayers() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getViewDistance() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getVersion() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public File getUpdateFolderFile() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getUpdateFolder() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getTicksPerMonsterSpawns() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getTicksPerAnimalSpawns() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getSpawnRadius() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public ServicesManager getServicesManager() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getServerName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getServerId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public BukkitScheduler getScheduler() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<Recipe> getRecipesFor(ItemStack arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getPort() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public PluginManager getPluginManager() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public PluginCommand getPluginCommand(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Player getPlayerExact(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Player getPlayer(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Set<OfflinePlayer> getOperators() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Player[] getOnlinePlayers() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean getOnlineMode() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public OfflinePlayer[] getOfflinePlayers() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public OfflinePlayer getOfflinePlayer(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Messenger getMessenger() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getMaxPlayers() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public MapView getMap(short arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Logger getLogger() {
                // TODO Auto-generated method stub
                return Logger.getLogger("zarTest");
            }

            @Override
            public String getIp() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Set<String> getIPBans() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public HelpMap getHelpMap() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public GameMode getDefaultGameMode() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ConsoleCommandSender getConsoleSender() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<String, String[]> getCommandAliases() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getBukkitVersion() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Set<OfflinePlayer> getBannedPlayers() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean getAllowNether() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean getAllowFlight() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean getAllowEnd() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean dispatchCommand(CommandSender arg0, String arg1)
                    throws CommandException {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public World createWorld(WorldCreator arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public MapView createMap(World arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Inventory createInventory(InventoryHolder arg0, int arg1,
                    String arg2) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Inventory createInventory(InventoryHolder arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Inventory createInventory(InventoryHolder arg0,
                    InventoryType arg1) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void configureDbConfig(ServerConfig arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void clearRecipes() {
                // TODO Auto-generated method stub

            }

            @Override
            public int broadcastMessage(String arg0) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int broadcast(String arg0, String arg1) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public void banIP(String arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean addRecipe(Recipe arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public int getAmbientSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getAnimalSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public long getConnectionThrottle() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public boolean getGenerateStructures() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public ItemFactory getItemFactory() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getMonsterSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getMotd() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getShutdownMessage() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public WarningState getWarningState() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getWaterAnimalSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getWorldType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isHardcore() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isPrimaryThread() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public ScoreboardManager getScoreboardManager() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    public static World getTestWorld_TestWorld() {
        // TODO Auto-generated method stub
        return new World() {
            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return "TestWorld";
            }
    
            @Override
            public boolean canGenerateStructures() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean createExplosion(Location arg0, float arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean createExplosion(Location arg0, float arg1,
                    boolean arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean createExplosion(double arg0, double arg1,
                    double arg2, float arg3) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean createExplosion(double arg0, double arg1,
                    double arg2, float arg3, boolean arg4) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public Item dropItem(Location arg0, ItemStack arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Item dropItemNaturally(Location arg0, ItemStack arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public boolean generateTree(Location arg0, TreeType arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean generateTree(Location arg0, TreeType arg1,
                    BlockChangeDelegate arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean getAllowAnimals() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean getAllowMonsters() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public Biome getBiome(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Block getBlockAt(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Block getBlockAt(int arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getBlockTypeIdAt(Location arg0) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getBlockTypeIdAt(int arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public Chunk getChunkAt(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Chunk getChunkAt(Block arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Chunk getChunkAt(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Difficulty getDifficulty() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public ChunkSnapshot getEmptyChunkSnapshot(int arg0, int arg1,
                    boolean arg2, boolean arg3) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public List<Entity> getEntities() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public <T extends Entity> Collection<T> getEntitiesByClass(
                    Class<T>... arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public <T extends Entity> Collection<T> getEntitiesByClass(
                    Class<T> arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Collection<Entity> getEntitiesByClasses(Class<?>... arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Environment getEnvironment() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public long getFullTime() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public ChunkGenerator getGenerator() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Block getHighestBlockAt(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Block getHighestBlockAt(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getHighestBlockYAt(Location arg0) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getHighestBlockYAt(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public double getHumidity(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public boolean getKeepSpawnInMemory() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public List<LivingEntity> getLivingEntities() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Chunk[] getLoadedChunks() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getMaxHeight() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public boolean getPVP() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public List<Player> getPlayers() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public List<BlockPopulator> getPopulators() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getSeaLevel() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public long getSeed() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public Location getSpawnLocation() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public double getTemperature(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getThunderDuration() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public long getTicksPerAnimalSpawns() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public long getTicksPerMonsterSpawns() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public long getTime() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public UUID getUID() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getWeatherDuration() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public File getWorldFolder() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public WorldType getWorldType() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public boolean hasStorm() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isAutoSave() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isChunkLoaded(Chunk arg0) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isChunkLoaded(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isThundering() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void loadChunk(Chunk arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void loadChunk(int arg0, int arg1) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean loadChunk(int arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void playEffect(Location arg0, Effect arg1, int arg2) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void playEffect(Location arg0, Effect arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public <T> void playEffect(Location arg0, Effect arg1, T arg2,
                    int arg3) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean refreshChunk(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean regenerateChunk(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void save() {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setAutoSave(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setDifficulty(Difficulty arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setFullTime(long arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setKeepSpawnInMemory(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setPVP(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setSpawnFlags(boolean arg0, boolean arg1) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean setSpawnLocation(int arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void setStorm(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setThunderDuration(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setThundering(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setTicksPerAnimalSpawns(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setTicksPerMonsterSpawns(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setTime(long arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setWeatherDuration(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public <T extends Entity> T spawn(Location arg0, Class<T> arg1)
                    throws IllegalArgumentException {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Arrow spawnArrow(Location arg0, Vector arg1, float arg2,
                    float arg3) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public LivingEntity spawnCreature(Location arg0, EntityType arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public LivingEntity spawnCreature(Location arg0, CreatureType arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public LightningStrike strikeLightning(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public LightningStrike strikeLightningEffect(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public boolean unloadChunk(Chunk arg0) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunk(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunk(int arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunk(int arg0, int arg1, boolean arg2,
                    boolean arg3) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunkRequest(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunkRequest(int arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public Set<String> getListeningPluginChannels() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public List<MetadataValue> getMetadata(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public boolean hasMetadata(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void removeMetadata(String arg0, Plugin arg1) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setMetadata(String arg0, MetadataValue arg1) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setBiome(int arg0, int arg1, Biome arg2) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean createExplosion(double arg0, double arg1,
                    double arg2, float arg3, boolean arg4, boolean arg5) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public int getAmbientSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getAnimalSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public String getGameRuleValue(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public String[] getGameRules() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getMonsterSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getWaterAnimalSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public boolean isChunkInUse(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isGameRule(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void playSound(Location arg0, Sound arg1, float arg2,
                    float arg3) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setAmbientSpawnLimit(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setAnimalSpawnLimit(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean setGameRuleValue(String arg0, String arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void setMonsterSpawnLimit(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setWaterAnimalSpawnLimit(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public Entity spawnEntity(Location arg0, EntityType arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public FallingBlock spawnFallingBlock(Location arg0, Material arg1,
                    byte arg2) throws IllegalArgumentException {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public FallingBlock spawnFallingBlock(Location arg0, int arg1,
                    byte arg2) throws IllegalArgumentException {
                // TODO Auto-generated method stub
                return null;
            }
    
        };
    }

    public static World getTestWorld_SecondWorld() {
        // TODO Auto-generated method stub
        return new World() {
            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return "SecondWorld";
            }
    
            @Override
            public boolean canGenerateStructures() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean createExplosion(Location arg0, float arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean createExplosion(Location arg0, float arg1,
                    boolean arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean createExplosion(double arg0, double arg1,
                    double arg2, float arg3) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean createExplosion(double arg0, double arg1,
                    double arg2, float arg3, boolean arg4) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public Item dropItem(Location arg0, ItemStack arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Item dropItemNaturally(Location arg0, ItemStack arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public boolean generateTree(Location arg0, TreeType arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean generateTree(Location arg0, TreeType arg1,
                    BlockChangeDelegate arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean getAllowAnimals() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean getAllowMonsters() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public Biome getBiome(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Block getBlockAt(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Block getBlockAt(int arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getBlockTypeIdAt(Location arg0) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getBlockTypeIdAt(int arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public Chunk getChunkAt(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Chunk getChunkAt(Block arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Chunk getChunkAt(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Difficulty getDifficulty() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public ChunkSnapshot getEmptyChunkSnapshot(int arg0, int arg1,
                    boolean arg2, boolean arg3) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public List<Entity> getEntities() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public <T extends Entity> Collection<T> getEntitiesByClass(
                    Class<T>... arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public <T extends Entity> Collection<T> getEntitiesByClass(
                    Class<T> arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Collection<Entity> getEntitiesByClasses(Class<?>... arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Environment getEnvironment() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public long getFullTime() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public ChunkGenerator getGenerator() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Block getHighestBlockAt(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Block getHighestBlockAt(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getHighestBlockYAt(Location arg0) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getHighestBlockYAt(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public double getHumidity(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public boolean getKeepSpawnInMemory() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public List<LivingEntity> getLivingEntities() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Chunk[] getLoadedChunks() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getMaxHeight() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public boolean getPVP() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public List<Player> getPlayers() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public List<BlockPopulator> getPopulators() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getSeaLevel() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public long getSeed() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public Location getSpawnLocation() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public double getTemperature(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getThunderDuration() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public long getTicksPerAnimalSpawns() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public long getTicksPerMonsterSpawns() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public long getTime() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public UUID getUID() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getWeatherDuration() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public File getWorldFolder() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public WorldType getWorldType() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public boolean hasStorm() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isAutoSave() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isChunkLoaded(Chunk arg0) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isChunkLoaded(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isThundering() {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void loadChunk(Chunk arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void loadChunk(int arg0, int arg1) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean loadChunk(int arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void playEffect(Location arg0, Effect arg1, int arg2) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void playEffect(Location arg0, Effect arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public <T> void playEffect(Location arg0, Effect arg1, T arg2,
                    int arg3) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean refreshChunk(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean regenerateChunk(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void save() {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setAutoSave(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setDifficulty(Difficulty arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setFullTime(long arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setKeepSpawnInMemory(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setPVP(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setSpawnFlags(boolean arg0, boolean arg1) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean setSpawnLocation(int arg0, int arg1, int arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void setStorm(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setThunderDuration(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setThundering(boolean arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setTicksPerAnimalSpawns(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setTicksPerMonsterSpawns(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setTime(long arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setWeatherDuration(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public <T extends Entity> T spawn(Location arg0, Class<T> arg1)
                    throws IllegalArgumentException {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public Arrow spawnArrow(Location arg0, Vector arg1, float arg2,
                    float arg3) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public LivingEntity spawnCreature(Location arg0, EntityType arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public LivingEntity spawnCreature(Location arg0, CreatureType arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public LightningStrike strikeLightning(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public LightningStrike strikeLightningEffect(Location arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public boolean unloadChunk(Chunk arg0) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunk(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunk(int arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunk(int arg0, int arg1, boolean arg2,
                    boolean arg3) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunkRequest(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean unloadChunkRequest(int arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public Set<String> getListeningPluginChannels() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public List<MetadataValue> getMetadata(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public boolean hasMetadata(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void removeMetadata(String arg0, Plugin arg1) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setMetadata(String arg0, MetadataValue arg1) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setBiome(int arg0, int arg1, Biome arg2) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean createExplosion(double arg0, double arg1,
                    double arg2, float arg3, boolean arg4, boolean arg5) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public int getAmbientSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getAnimalSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public String getGameRuleValue(String arg0) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public String[] getGameRules() {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public int getMonsterSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public int getWaterAnimalSpawnLimit() {
                // TODO Auto-generated method stub
                return 0;
            }
    
            @Override
            public boolean isChunkInUse(int arg0, int arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public boolean isGameRule(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void playSound(Location arg0, Sound arg1, float arg2,
                    float arg3) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setAmbientSpawnLimit(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setAnimalSpawnLimit(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public boolean setGameRuleValue(String arg0, String arg1) {
                // TODO Auto-generated method stub
                return false;
            }
    
            @Override
            public void setMonsterSpawnLimit(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public void setWaterAnimalSpawnLimit(int arg0) {
                // TODO Auto-generated method stub
    
            }
    
            @Override
            public Entity spawnEntity(Location arg0, EntityType arg1) {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public FallingBlock spawnFallingBlock(Location arg0, Material arg1,
                    byte arg2) throws IllegalArgumentException {
                // TODO Auto-generated method stub
                return null;
            }
    
            @Override
            public FallingBlock spawnFallingBlock(Location arg0, int arg1,
                    byte arg2) throws IllegalArgumentException {
                // TODO Auto-generated method stub
                return null;
            }
        };
    
    }

}

