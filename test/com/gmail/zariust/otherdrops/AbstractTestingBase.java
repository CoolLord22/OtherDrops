package com.gmail.zariust.otherdrops;

import org.junit.BeforeClass;

import com.gmail.zariust.common.Verbosity;

/**
 * If you are getting: java.lang.ExceptionInInitializerError at
 * net.minecraft.server.StatisticList.<clinit>(SourceFile:58) at
 * net.minecraft.server.Item.<clinit>(SourceFile:252) at
 * net.minecraft.server.Block.<clinit>(Block.java:577)
 * 
 * extend this class to solve it.
 */
public abstract class AbstractTestingBase {

    @BeforeClass
    public static void setup() {
        // Initialization: Bukkit must have a server with a logger - also provides dummy worlds
        BukkitMock mock = new BukkitMock();
        // needs verbosity
        OtherDropsConfig.setVerbosity(Verbosity.EXTREME);
    }
}