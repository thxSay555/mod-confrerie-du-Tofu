package fr.wakfu.network;

import fr.wakfu.client.model.GeoPlayerLayer;
import fr.wakfu.client.model.AnimatablePlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@Mod.EventBusSubscriber(Side.CLIENT)
public class PlayerRenderHandler {
    @SubscribeEvent
    public static void onAddLayers(RenderPlayerEvent.Post event) {
        RenderPlayer renderer = event.getRenderer();
        if (renderer instanceof IGeoRenderer<?>) {
            @SuppressWarnings("unchecked")
            IGeoRenderer<AnimatablePlayer> geoRend =
                (IGeoRenderer<AnimatablePlayer>) renderer;
            // Add layer on RenderPlayer
            renderer.addLayer(new GeoPlayerLayer(geoRend));
        }
    }
}
