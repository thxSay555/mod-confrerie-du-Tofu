package test;

import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GeoPlayerRenderer extends GeoEntityRenderer<PlayerWrapper> {
    public GeoPlayerRenderer(RenderManager renderManager) {
        super(renderManager, new PlayerModel());
        this.shadowSize = 0.5F;
        addLayer(new RaceLayer(this));
    }

    @Override
    public void doRender(PlayerWrapper entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Synchronisation des états avec le joueur réel
        entity.setPosition(x, y, z);
        entity.rotationYaw = entityYaw;
        entity.rotationPitch = entity.originalPlayer.rotationPitch;
        entity.limbSwing = entity.originalPlayer.limbSwing;
        entity.limbSwingAmount = entity.originalPlayer.limbSwingAmount;
        
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}