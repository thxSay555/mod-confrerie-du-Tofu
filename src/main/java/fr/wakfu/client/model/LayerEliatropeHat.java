package fr.wakfu.client.model;

import fr.wakfu.client.model.Eliatrope_hat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerEliatropeHat implements LayerRenderer<AbstractClientPlayer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("wakfu", "textures/entity/eliatrope_purple.png");
    private final RenderPlayer renderPlayer;
    private final Eliatrope_hat modelHat = new Eliatrope_hat();

    public LayerEliatropeHat(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks,
                              float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        if (ModelSwitcher.isCustomModelEnabled()) {
            GlStateManager.pushMatrix();

            // Positionne le modèle sur la tête du joueur
            renderPlayer.getMainModel().bipedHead.postRender(scale);
        	System.out.println("LayerEliatropeHat: Actif ? " + ModelSwitcher.isCustomModelEnabled());
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

            modelHat.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
