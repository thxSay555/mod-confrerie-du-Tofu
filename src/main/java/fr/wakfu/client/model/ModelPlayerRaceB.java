package fr.wakfu.client.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelPlayerRaceB extends AnimatedGeoModel<AnimatablePlayer> {
    @Override public ResourceLocation getModelLocation(AnimatablePlayer player) {
        return new ResourceLocation("modid", "geo/player_race_b.geo.json");
    }
    @Override public ResourceLocation getTextureLocation(AnimatablePlayer player) {
        return new ResourceLocation("modid", "textures/entity/player_race_b.png");
    }
    @Override public ResourceLocation getAnimationFileLocation(AnimatablePlayer player) {
        return new ResourceLocation("modid", "animations/player_race_b.animation.json");
    }
}