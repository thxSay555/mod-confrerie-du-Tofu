package fr.wakfu.proxy;

import java.lang.reflect.Field;
import java.util.List;

import fr.wakfu.client.PlayerStatsScreen;
import fr.wakfu.client.WakfuHUDOverlay;
import fr.wakfu.common.event.RaceEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import software.bernie.geckolib3.GeckoLib;
import test.ClientEvents;

public class ClientProxy extends CommonProxy {
	@Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        GeckoLib.initialize(); // Initialisation de GeckoLib
        System.out.println("GeckoLib initialisé !");
    }

    @Override
    public void init(FMLInitializationEvent event) {
    	new ClientEvents();
        System.out.println("ClientProxy init");
        super.init(event);

        // Initialise le modèle personnalisé (GeckoLib, etc.)
        
     // Dans ClientProxy.init(FMLInitializationEvent event) :
        MinecraftForge.EVENT_BUS.register(RaceEventHandler.class);
        
       
        // [...] Autres couches
    
        // HUD et raccourcis
        ClientRegistry.registerKeyBinding(PlayerStatsScreen.KEY_STATS);
        MinecraftForge.EVENT_BUS.register(new WakfuHUDOverlay());
        MinecraftForge.EVENT_BUS.register(PlayerStatsScreen.class);
        RenderPlayer renderPlayerDefault = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default");
        RenderPlayer renderPlayerSlim = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().getSkinMap().get("slim");

    
    }

    /**
     * Accès réfléchi à la liste des LayerRenderer d’un RenderPlayer.
     */
    @SuppressWarnings("unchecked")
    private static List<LayerRenderer<AbstractClientPlayer>> getLayerRenderers(RenderPlayer renderPlayer) {
        try {
            Field field = renderPlayer.getClass().getSuperclass().getDeclaredField("layerRenderers");
            field.setAccessible(true);
            return (List<LayerRenderer<AbstractClientPlayer>>) field.get(renderPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Vérifie si un LayerRenderer du type donné est déjà présent.
     */
    private static boolean hasLayer(RenderPlayer renderer, Class<?> clazz) {
        List<LayerRenderer<AbstractClientPlayer>> layers = getLayerRenderers(renderer);
        if (layers == null) return false;
        return layers.stream().anyMatch(layer -> clazz.isAssignableFrom(layer.getClass()));
    }

    /**
     * Ajoute les LayerRenderer personnalisés lors du rendu du joueur.
     */
    
       
    
}
