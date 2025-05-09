package fr.wakfu.proxy;

import fr.wakfu.client.PlayerStatsScreen;
import fr.wakfu.client.WakfuHUDOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {
    @Override
    @SideOnly(Side.CLIENT)
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        // Déplacez l'initialisation ici
        ClientRegistry.registerKeyBinding(PlayerStatsScreen.KEY_STATS);
        MinecraftForge.EVENT_BUS.register(new WakfuHUDOverlay());
        MinecraftForge.EVENT_BUS.register(PlayerStatsScreen.class);
    }

    // Supprimez la méthode init() du proxy client
}