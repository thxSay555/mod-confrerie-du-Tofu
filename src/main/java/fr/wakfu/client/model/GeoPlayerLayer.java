package fr.wakfu.client.model;

import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class GeoPlayerLayer extends GeoLayerRenderer<AnimatablePlayer> {
    private final AnimatedGeoModel<AnimatablePlayer> modelA = new ModelPlayerRaceA();
    private final AnimatedGeoModel<AnimatablePlayer> modelB = new ModelPlayerRaceB();

    // Correct constructor: takes IGeoRenderer<AnimatablePlayer>
    public GeoPlayerLayer(IGeoRenderer<AnimatablePlayer> geoRenderer) {
        super(geoRenderer);
    }

    // Implement doRenderLayer (LayerRenderer stub)
    @Override
    public void doRenderLayer(
        AnimatablePlayer player,
        float limbSwing, float limbSwingAmount,
        float partialTicks, float ageInTicks,
        float netHeadYaw, float headPitch,
        float scaleIn
    ) {
        // Delegate to abstract render(...)
        render(
            player,
            limbSwing, limbSwingAmount,
            partialTicks, ageInTicks,
            netHeadYaw, headPitch,
            Color.WHITE
        );
    }

    // Implement abstract render(...) from GeoLayerRenderer
    @Override
    public void render(
        AnimatablePlayer player,
        float limbSwing, float limbSwingAmount,
        float partialTicks, float ageInTicks,
        float netHeadYaw, float headPitch,
        Color renderColor
    ) {
        // Choose model based on NBT tag "raceA"
        AnimatedGeoModel<AnimatablePlayer> agm =
            player.getEntityData().getBoolean("raceA") ? modelA : modelB;

        // Retrieve baked GeoModel (bones + hierarchy)
        GeoModelProvider<AnimatablePlayer> provider = getEntityModel();
        GeoModel model = provider.getModel(agm.getModelLocation(player));

        // Call IGeoRenderer.render(model, animatable, partialTicks, r,g,b,a)
        getRenderer().render(
            model,
            player,
            partialTicks,
            renderColor.getRed(),
            renderColor.getGreen(),
            renderColor.getBlue(),
            renderColor.getAlpha()
        );
    }

    // Stub required by LayerRenderer<T>
    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
