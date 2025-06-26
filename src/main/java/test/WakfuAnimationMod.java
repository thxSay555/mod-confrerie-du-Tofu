package test;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Main mod class
 */
@Mod(modid = "wakfuanimation", name = "Wakfu Animation", version = "1.0", clientSideOnly = true)
public class WakfuAnimationMod {
    public static AnimationManager animationManager;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        animationManager = new AnimationManager();
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAnimationTest());
    }
}