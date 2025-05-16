package fr.wakfu.proxy;

import fr.wakfu.client.PlayerStatsScreen;
import fr.wakfu.client.WakfuHUDOverlay;
import fr.wakfu.client.model.ModelSwitcher;
import fr.wakfu.client.model.CustomPlayerLayer;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.List;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        System.out.println("ClientProxy init");
        super.init(event);

        // Init modèle/animation
        ModelSwitcher.init();

        // HUD + touches
        ClientRegistry.registerKeyBinding(PlayerStatsScreen.KEY_STATS);
        MinecraftForge.EVENT_BUS.register(new WakfuHUDOverlay());
        MinecraftForge.EVENT_BUS.register(PlayerStatsScreen.class);

        // Register this class to handle rendering events
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Utilise la réflexion pour accéder aux layers (car le champ est protected)
    @SuppressWarnings("unchecked")
    private static List<LayerRenderer<AbstractClientPlayer>> getLayerRenderers(RenderPlayer renderPlayer) {
        try {
            Field field = RenderPlayer.class.getSuperclass().getDeclaredField("layerRenderers"); // Dans RenderLivingBase
            field.setAccessible(true);
            return (List<LayerRenderer<AbstractClientPlayer>>) field.get(renderPlayer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Ajoute le CustomPlayerLayer si absent
    @SubscribeEvent
    public void onPlayerRenderInit(RenderPlayerEvent.Specials.Pre event) {
        RenderPlayer render = event.getRenderer();
        List<LayerRenderer<AbstractClientPlayer>> layers = getLayerRenderers(render);

        if (layers != null && layers.stream().noneMatch(layer -> layer instanceof CustomPlayerLayer)) {
            layers.add(new CustomPlayerLayer(render));
        }
    }
}
