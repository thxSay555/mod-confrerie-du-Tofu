package fr.wakfu.client.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

/**
 * GeoPlayerLayer for Minecraft 1.12.2 + GeckoLib 3.0.31
 */
public class GeoPlayerLayer extends GeoLayerRenderer<AbstractClientPlayer> {
    private final ModelPlayerRaceA modelA = new ModelPlayerRaceA();
    private final ModelPlayerRaceB modelB = new ModelPlayerRaceB();

    public GeoPlayerLayer(RenderPlayer playerRenderer) {
        super(playerRenderer);
    }

    @Override
    public void doRenderLayer(
            AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            float scale
    ) {
        // Sélection du modèle selon le tag NBT "raceA"
        if (player.getEntityData().getBoolean("raceA")) {
            super.render(
                modelA,
                player,
                limbSwing,
                limbSwingAmount,
                partialTicks,
                ageInTicks,
                netHeadYaw,
                headPitch,
                scale
            );
        } else {
            super.render(
                modelB,
                player,
                limbSwing,
                limbSwingAmount,
                partialTicks,
                ageInTicks,
                netHeadYaw,
                headPitch,
                scale
            );
        }
    }
}
