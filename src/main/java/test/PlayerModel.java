package test;

import fr.wakfu.WakfuMod;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PlayerModel extends AnimatedGeoModel<PlayerWrapper> {
    private static final ResourceLocation MODEL = new ResourceLocation(WakfuMod.MODID,"geo/player.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(WakfuMod.MODID,"textures/entity/player_base.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(WakfuMod.MODID,"animations/player_animation.json");

    @Override
    public ResourceLocation getModelLocation(PlayerWrapper object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureLocation(PlayerWrapper object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationFileLocation(PlayerWrapper animatable) {
        return ANIMATIONS;
    }
}