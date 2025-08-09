// src/main/java/fr/wakfu/proxy/CommonProxy.java
package fr.wakfu.proxy;

import fr.wakfu.stats.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import test.AnimationManager;
import fr.wakfu.allies.AllyEventHandler;
import fr.wakfu.allies.AllyRegistry;
import fr.wakfu.commands.CommandWakfuLevel;

public class CommonProxy {
	protected AnimationManager animationManager;

    public void preInit(FMLPreInitializationEvent event) {
        // register capability once
    	 AllyRegistry.register();
    	 System.out.println("[WakfuMod] AllyRegistry.ALLY_CAPABILITY après register = " + AllyRegistry.ALLY_CAPABILITY);

         // REGISTER EVENT HANDLERS (attach capability)
         MinecraftForge.EVENT_BUS.register(new AllyEventHandler());
        CapabilityManager.INSTANCE.register(
            IPlayerStats.class,
            new StatsStorage(),
            PlayerStats::new
        );
    }

    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new StatsCapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new LevelHandler());
    }

    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandWakfuLevel());
    }


	public AnimationManager getAnimationManager() {
        return animationManager;
    }

    public void setAnimationManager(AnimationManager mgr) {
        this.animationManager = mgr;
    }
}