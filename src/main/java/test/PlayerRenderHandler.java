package test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderHandler {
    private final GeoPlayerRenderer playerRenderer;
    
    public PlayerRenderHandler() {
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        this.playerRenderer = new GeoPlayerRenderer(renderManager);
    }

    @SubscribeEvent
    public void onPlayerRenderPre(RenderPlayerEvent.Pre event) {
        // Annuler le rendu vanilla
        event.setCanceled(true);
        
        // Créer le wrapper et effectuer le rendu GeckoLib
        PlayerWrapper wrapper = new PlayerWrapper(event.getEntityPlayer());
        playerRenderer.doRender(
            wrapper,
            event.getEntityPlayer().posX,
            event.getEntityPlayer().posY,
            event.getEntityPlayer().posZ,
            event.getEntityPlayer().rotationYaw,
            event.getPartialRenderTick()
        );
    }
}