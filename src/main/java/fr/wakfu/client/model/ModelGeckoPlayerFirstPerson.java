package fr.wakfu.client.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelGeckoPlayerFirstPerson extends AnimatedGeoModel<AnimatablePlayer> {
    @Override
    public ResourceLocation getModelLocation(AnimatablePlayer player) {
        return new ResourceLocation("wakfu", "geo/player_first_person.geo.json");
    }
    @Override
    public ResourceLocation getTextureLocation(AnimatablePlayer player) {
        return new ResourceLocation("wakfu", "textures/entity/player_first_person.png");
    }
    @Override
    public ResourceLocation getAnimationFileLocation(AnimatablePlayer player) {
        return new ResourceLocation("wakfu", "animations/player_first_person.animation.json");
    }
}
