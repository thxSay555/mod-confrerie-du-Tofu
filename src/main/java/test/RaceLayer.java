package test;

import fr.wakfu.WakfuMod;
import fr.wakfu.common.capabilities.RaceCapability;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

public class RaceLayer extends GeoLayerRenderer<PlayerWrapper> {
    private final GeoEntityRenderer<PlayerWrapper> geoRenderer;
    private final GeoModel model;
    
    @SuppressWarnings("unchecked")
	public RaceLayer(GeoEntityRenderer<PlayerWrapper> renderer) {
        super(renderer);
        this.geoRenderer = renderer;
        this.model = renderer.getGeoModelProvider().getModel(
            renderer.getGeoModelProvider().getModelLocation(null)
        );
    }

    @Override
    public void render(PlayerWrapper entity, float limbSwing, float limbSwingAmount, float partialTicks, 
                      float ageInTicks, float netHeadYaw, float headPitch, Color color) {
        if (entity.originalPlayer == null) return;
        
        RaceCapability.IRace race = entity.originalPlayer.getCapability(RaceCapability.RACE_CAPABILITY, null);
        if (race == null || race.getRace().isEmpty()) return;

        ResourceLocation raceTexture = getRaceTexture(race.getRace());
        if (raceTexture == null) return;

        // Configuration du rendu
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        // Application de la texture de race
        this.geoRenderer.bindTexture(raceTexture);
        this.geoRenderer.render(
            model,
            entity,
            partialTicks,
            1.0F, 1.0F, 1.0F, 1.0F  // RGBA values (white)
        );
        
        // Restauration des paramètres OpenGL
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    // Implémentation des méthodes requises par LayerRenderer
    @Override
    public void doRenderLayer(PlayerWrapper entity, float limbSwing, float limbSwingAmount, float partialTicks,
                             float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.render(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, Color.ofRGBA(1, 1, 1, 1));
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    private ResourceLocation getRaceTexture(String race) {
        switch(race.toLowerCase()) {
            case "eliatrope": return new ResourceLocation(WakfuMod.MODID,"textures/layers/race_eliatrope.png");
            case "sadida": return new ResourceLocation(WakfuMod.MODID,"textures/layers/race_sadida.png");
            case "cra": return new ResourceLocation(WakfuMod.MODID,"textures/layers/race_cra.png");
            case "iop": return new ResourceLocation(WakfuMod.MODID,"textures/layers/race_iop.png");
            case "huppermage": return new ResourceLocation(WakfuMod.MODID,"textures/layers/race_huppermage.png");
            case "steamer": return new ResourceLocation(WakfuMod.MODID,"textures/layers/race_steamer.png");
            default: return null;
        }
    }
}