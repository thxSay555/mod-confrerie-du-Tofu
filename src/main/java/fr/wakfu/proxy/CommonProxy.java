// src/main/java/fr/wakfu/proxy/CommonProxy.java
package fr.wakfu.proxy;

import fr.wakfu.stats.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import fr.wakfu.commands.CommandWakfuLevel;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        // register capability once
        CapabilityManager.INSTANCE.register(
            IPlayerStats.class,
            new StatsStorage(),
            PlayerStats::new
        );
    }

    public void init() {
        MinecraftForge.EVENT_BUS.register(new StatsCapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new LevelHandler());
    }

    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandWakfuLevel());
    }
}