package fr.wakfu.client.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelPlayerRaceA extends AnimatedGeoModel<AnimatablePlayer> {
    @Override public ResourceLocation getModelLocation(AnimatablePlayer player) {
        return new ResourceLocation("modid", "geo/player_race_a.geo.json");
    }
    @Override public ResourceLocation getTextureLocation(AnimatablePlayer player) {
        return new ResourceLocation("modid", "textures/entity/player_race_a.png");
    }
    @Override public ResourceLocation getAnimationFileLocation(AnimatablePlayer player) {
        return new ResourceLocation("modid", "animations/player_race_a.animation.json");
    }
}
