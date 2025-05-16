package fr.wakfu.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

public class CustomPlayerLayer implements LayerRenderer<AbstractClientPlayer> {

    private static final ResourceLocation CUSTOM_SKIN = new ResourceLocation("wakfu", "textures/entity/Saylie_Adult.png");
    private final RenderPlayer renderPlayer;
    private final ModelBase customModel = new PlayerModel(15, false);

    public CustomPlayerLayer(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                              float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        if (ModelSwitcher.isCustomModelEnabled()) {
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().getTextureManager().bindTexture(CUSTOM_SKIN);
            customModel.setLivingAnimations(player, limbSwing, limbSwingAmount, partialTicks);
            customModel.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, player);
            customModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
