package fr.wakfu.client.model;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class PlayerModel extends ModelPlayer {

    private final ModelRenderer debugCube;

    public PlayerModel(float modelSize, boolean smallArmsIn) {
        super(modelSize, smallArmsIn);

        // Ajouter un cube au-dessus de la tête (offset Y négatif = vers le haut)
        debugCube = new ModelRenderer(this, 32, 0);
        debugCube.addBox(-0.5F, -9.5F, -0.5F, 1, 1, 1);  // 1x1x1 cube

        // Le point de rotation est le même que pour la tête, pour suivre ses mouvements
        debugCube.setRotationPoint(this.bipedHead.rotationPointX, this.bipedHead.rotationPointY, this.bipedHead.rotationPointZ);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount,
                       float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        // Affiche le cube debug après la tête
        debugCube.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount,
                                  float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);

        // Synchroniser la rotation du cube avec celle de la tête
        debugCube.rotateAngleX = bipedHead.rotateAngleX;
        debugCube.rotateAngleY = bipedHead.rotateAngleY;
        debugCube.rotateAngleZ = bipedHead.rotateAngleZ;
    }
}
