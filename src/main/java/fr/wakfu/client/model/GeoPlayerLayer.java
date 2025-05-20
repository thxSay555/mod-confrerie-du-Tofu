package fr.wakfu.client.model;

import net.minecraft.client.renderer.entity.RenderPlayer;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;

/**
 * GeoPlayerLayer pour Forge 1.12.2 + GeckoLib 3.0.31.
 */
public class GeoPlayerLayer extends GeoLayerRenderer<AnimatablePlayer> {
    private final ModelPlayerRaceA modelA = new ModelPlayerRaceA();
    private final ModelPlayerRaceB modelB = new ModelPlayerRaceB();

    /**
     * Seul constructeur disponible en 1.12.2 / GeckoLib 3.0.31
     */
    public GeoPlayerLayer(RenderPlayer renderer) {
        super(renderer);
    }

    @Override
    public void doRenderLayer(
        AnimatablePlayer player,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch,
        float scale  // ce paramètre est ignoré ici : GeoLib ne le passe pas à render()
    ) {
        // Choix du modèle selon le tag NBT "raceA"
        AnimatedGeoModel<AnimatablePlayer> model =
            player.getEntityData().getBoolean("raceA") ? modelA : modelB;

        // Appel à la méthode protégée render() de GeoLayerRenderer
        super.render(
            model,
            player,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            netHeadYaw,
            headPitch
        );
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

	@Override
	public void render(AnimatablePlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch, Color renderColor) {
		// TODO Auto-generated method stub
		
	}
}
