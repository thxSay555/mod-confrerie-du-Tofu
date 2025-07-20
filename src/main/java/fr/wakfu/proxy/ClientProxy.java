package fr.wakfu.proxy;

import fr.wakfu.client.PlayerStatsScreen;
import fr.wakfu.client.WakfuHUDOverlay;
import fr.wakfu.common.event.RaceEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import software.bernie.geckolib3.GeckoLib;
import test.ClientEventsObf;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        GeckoLib.initialize();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
       // Enregistre le handler Obfuscate
        
        MinecraftForge.EVENT_BUS.register(new ClientEventsObf());

        // Tes autres listeners/couches HUD, etc.
        MinecraftForge.EVENT_BUS.register(RaceEventHandler.class);
        MinecraftForge.EVENT_BUS.register(new WakfuHUDOverlay());
        MinecraftForge.EVENT_BUS.register(PlayerStatsScreen.class);
        ClientRegistry.registerKeyBinding(PlayerStatsScreen.KEY_STATS);
    }
}
