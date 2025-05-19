package fr.wakfu.proxy;

import java.lang.reflect.Field;
import java.util.List;

import fr.wakfu.client.PlayerStatsScreen;
import fr.wakfu.client.WakfuHUDOverlay;
import fr.wakfu.client.model.CustomRenderPlayer;
import fr.wakfu.client.model.LayerEliatropeHat;
import fr.wakfu.client.model.ModelSwitcher;
import fr.wakfu.common.event.RaceEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        System.out.println("ClientProxy init");
        super.init(event);

        // Initialise le modèle personnalisé (GeckoLib, etc.)
        ModelSwitcher.init();
     // Dans ClientProxy.init(FMLInitializationEvent event) :
        MinecraftForge.EVENT_BUS.register(RaceEventHandler.class);

        // HUD et raccourcis
        ClientRegistry.registerKeyBinding(PlayerStatsScreen.KEY_STATS);
        MinecraftForge.EVENT_BUS.register(new WakfuHUDOverlay());
        MinecraftForge.EVENT_BUS.register(PlayerStatsScreen.class);
        RenderingRegistry.registerEntityRenderingHandler(AbstractClientPlayer.class, manager -> new CustomRenderPlayer(manager, false));
        RenderPlayer renderPlayerDefault = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().getSkinMap().get("default");
        RenderPlayer renderPlayerSlim = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().getSkinMap().get("slim");

        if (!hasLayer(renderPlayerDefault, LayerEliatropeHat.class)) {
            renderPlayerDefault.addLayer(new LayerEliatropeHat(renderPlayerDefault));
        }
        if (!hasLayer(renderPlayerSlim, LayerEliatropeHat.class)) {
            renderPlayerSlim.addLayer(new LayerEliatropeHat(renderPlayerSlim));
        }

   

        // Enregistre les événements liés au rendu du joueur
        MinecraftForge.EVENT_BUS.register(this);
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
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Specials.Pre event) {
        RenderPlayer renderer = event.getRenderer();
        List<LayerRenderer<AbstractClientPlayer>> layers = getLayerRenderers(renderer);
        if (layers == null) return;

        // Chapeau Éliatrope
        if (!hasLayer(renderer, LayerEliatropeHat.class)) {
            layers.add(new LayerEliatropeHat(renderer));
        }

        
        // Tu peux ajouter d'autres layers ici
        // if (!hasLayer(renderer, LayerAutre.class)) {
        //     layers.add(new LayerAutre(renderer));
        // }
    }
}
