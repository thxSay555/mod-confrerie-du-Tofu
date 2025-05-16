package fr.wakfu.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class PlayerModel extends ModelPlayer {

	public PlayerModel(float modelSize, boolean smallArms) {
        super(modelSize, smallArms);

        // Remplace le corps par un modèle simple ici
        this.bipedHead = new ModelRenderer(this, 0, 0);
        this.bipedHead.addBox(-4F, -8F, -4F, 8, 8, 8);
        this.bipedHead.setRotationPoint(0F, 0F, 0F);

        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4F, 0F, -2F, 8, 12, 4);
        this.bipedBody.setRotationPoint(0F, 0F, 0F);

        this.bipedRightArm = new ModelRenderer(this, 40, 16);
        this.bipedRightArm.addBox(-3F, -2F, -2F, 4, 12, 4);
        this.bipedRightArm.setRotationPoint(-5F, 2F, 0F);

        this.bipedLeftArm = new ModelRenderer(this, 40, 16);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1F, -2F, -2F, 4, 12, 4);
        this.bipedLeftArm.setRotationPoint(5F, 2F, 0F);

        this.bipedRightLeg = new ModelRenderer(this, 0, 16);
        this.bipedRightLeg.addBox(-2F, 0F, -2F, 4, 12, 4);
        this.bipedRightLeg.setRotationPoint(-2F, 12F, 0F);

        this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-2F, 0F, -2F, 4, 12, 4);
        this.bipedLeftLeg.setRotationPoint(2F, 12F, 0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }
}
