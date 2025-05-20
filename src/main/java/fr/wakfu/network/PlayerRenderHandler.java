package fr.wakfu.network;

import fr.wakfu.client.model.GeoPlayerLayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side; 

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class PlayerRenderHandler {
    @SubscribeEvent
    public static void onAddLayers(RenderPlayerEvent.Post event) {
    	RenderPlayer renderer = event.getRenderer();
        // On ajoute notre GeoLayerRenderer devant le vanilla
        renderer.addLayer(new GeoPlayerLayer(renderer));
    }
}
