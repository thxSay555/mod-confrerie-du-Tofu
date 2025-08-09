package fr.wakfu.proxy;

import fr.skill.capability.CapabilityPlayerSkills;
import fr.skill.handlers.SkillSyncHandler;
import fr.wakfu.IntensityControls;
import fr.wakfu.client.PlayerStatsScreen;
import fr.wakfu.client.WakfuHUD;
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
        MinecraftForge.EVENT_BUS.register(new fr.skill.input.SkillKeyInputHandler());
        CapabilityPlayerSkills.register();
        MinecraftForge.EVENT_BUS.register(new fr.skill.handlers.SkillSyncHandler());
        MinecraftForge.EVENT_BUS.register(new fr.skill.capability.AttachSkillCapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new SkillSyncHandler()); 
        fr.skill.SkillDefinitions.registerAll();
        MinecraftForge.EVENT_BUS.register(new fr.skill.client.RadialClient());
        MinecraftForge.EVENT_BUS.register(new fr.wakfu.IntensityControls());
        GeckoLib.initialize();
      
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
       // Enregistre le handler Obfuscate
        MinecraftForge.EVENT_BUS.register(new ClientEventsObf());
        MinecraftForge.EVENT_BUS.register(RaceEventHandler.class);
        MinecraftForge.EVENT_BUS.register(new WakfuHUD());
        MinecraftForge.EVENT_BUS.register(PlayerStatsScreen.class);
        MinecraftForge.EVENT_BUS.register(new IntensityControls());
    }
}
