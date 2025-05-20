package fr.wakfu.client.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ModelPlayerRaceA extends AnimatedGeoModel<AbstractClientPlayer> {
    @Override
    public ResourceLocation getModelLocation(AbstractClientPlayer player) {
        return new ResourceLocation("wakfu", "geo/player_race_a.geo.json");
    }
    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
        return new ResourceLocation("wakfu", "textures/entity/player_race_a.png");
    }
    @Override
    public ResourceLocation getAnimationFileLocation(AbstractClientPlayer player) {
        return new ResourceLocation("wakfu", "animations/player_race_a.animation.json");
    }
}
