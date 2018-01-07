package com.gmail.zariust.otherdrops;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class CustomMobSupport {
    // Blank methods for non 1.5.2 support
    public static void spawnCustomMob(String args, Location loc) {
        Log.logInfo("Customspawn disabled (please use special build for v1.5.2).");
    }

    public static void spawnCustomMob(Integer args, Location loc) {
        Log.logInfo("Customspawn disabled (please use special build for v1.5.2).");
    }

    public static String getCustomMobName(LivingEntity le) {
        return "";
    }

    public static void exportCustomMobNames(File folder) {
    }

    public static void exportCustomBlockNames(File folder) {
    }
    
    // ////////////////
    // public static void spawnCustomMob(String args, Location loc) {
    // net.minecraft.server.v1_5_R3.World world = ((CraftWorld) loc.getWorld()).getHandle();
    // net.minecraft.server.v1_5_R3.Entity entity = net.minecraft.server.v1_5_R3.EntityTypes.createEntityByName(args, world);
    // spawnCustomMob(entity, loc, world);
    // }
    //
    // public static void spawnCustomMob(Integer args, Location loc) {
    // net.minecraft.server.v1_5_R3.World world = ((CraftWorld) loc.getWorld()).getHandle();
    // net.minecraft.server.v1_5_R3.Entity entity = net.minecraft.server.v1_5_R3.EntityTypes.a(Integer.valueOf(args), world);
    // spawnCustomMob(entity, loc, world);
    // }
    //
    // public static void spawnCustomMob(net.minecraft.server.v1_5_R3.Entity entity, Location loc, net.minecraft.server.v1_5_R3.World world) {
    // if (entity != null) {
    // entity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), world.random.nextFloat() * 360.0f, 0.0f);
    // world.addEntity(entity, SpawnReason.CUSTOM);
    // }
    // }
    //
    // /**
    // * @param folder
    // */
    // public static void exportCustomMobNames(File folder) {
    // BufferedWriter out;
    // try {
    // File configFile = new File(folder.getAbsolutePath() + File.separator + "known_lists" + File.separator + "CustomMobNames" + ".txt");
    // configFile.getParentFile().mkdirs();
    // configFile.createNewFile();
    // out = new BufferedWriter(new FileWriter(configFile));
    //
    // Field field = net.minecraft.server.v1_5_R3.EntityTypes.class.getDeclaredField("c");
    // field.setAccessible(true);
    // Map<String, String> map = (Map<String, String>) field.get(null);
    // List<String> sorted = new ArrayList<String>();
    //
    // for (Entry<String, String> key : map.entrySet()) {
    // sorted.add(key.getValue());
    // }
    // Collections.sort(sorted);
    // for (String key : sorted) {
    // out.write(key + "\n");
    // }
    //
    // out.close();
    // } catch (IOException exception) {
    // exception.printStackTrace();
    // } catch (SecurityException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (NoSuchFieldException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IllegalArgumentException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    //
    // /**
    // * @param folder
    // */
    // public static void exportCustomBlockNames(File folder) {
    // BufferedWriter out;
    // try {
    // File configFile = new File(folder.getAbsolutePath() + File.separator + "known_lists" + File.separator + "CustomBlockNames" + ".txt");
    // configFile.getParentFile().mkdirs();
    // configFile.createNewFile();
    // out = new BufferedWriter(new FileWriter(configFile));
    //
    // Field field = net.minecraft.server.v1_5_R3.EntityTypes.class.getDeclaredField("c");
    //
    // // net.minecraft.server.v1_5_R3.Block.
    // field.setAccessible(true);
    // Map<String, String> map = (Map<String, String>) field.get(null);
    // List<String> sorted = new ArrayList<String>();
    //
    // for (Entry<String, String> key : map.entrySet()) {
    // sorted.add(key.getValue());
    // }
    // Collections.sort(sorted);
    // for (String key : sorted) {
    // out.write(key + "\n");
    // }
    //
    // out.close();
    // } catch (IOException exception) {
    // exception.printStackTrace();
    // } catch (SecurityException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (NoSuchFieldException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IllegalArgumentException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    //
    // public static String getCustomMobName(LivingEntity le) {
    // net.minecraft.server.v1_5_R3.Entity ent = ((CraftEntity) le).getHandle();
    // String name = net.minecraft.server.v1_5_R3.EntityTypes.b(ent);
    // return name;
    // }
}
