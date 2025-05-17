package fr.wakfu.client.model;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class CustomRenderPlayer extends RenderPlayer {
    private static final ResourceLocation CUSTOM_SKIN = new ResourceLocation("wakfu", "textures/entity/Saylie_Adult.png");

    public CustomRenderPlayer(RenderManager renderManager, boolean useSmallArms) {
        super(renderManager, useSmallArms);
    }

    @Override
	public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        if (ModelSwitcher.isCustomModelEnabled()) {
            return CUSTOM_SKIN;
        } else {
            return super.getEntityTexture(entity);
        }
    }
}
