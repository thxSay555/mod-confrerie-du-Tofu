package test;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;

import fr.wakfu.WakfuMod;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventsObf {

    @SubscribeEvent
    public void onSetupAngles(ModelPlayerEvent.SetupAngles.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        AnimationInstance inst = WakfuMod.animationManager.getInstance(player.getName());
        if (inst == null) return;

        ModelPlayer model = event.getModelPlayer();
        Animation anim = inst.getAnimation();
        float t      = inst.getTime();
        float len    = anim.getLength();
        boolean loop = anim.isLoop();

        // Rotation additive pour chaque bone
        applyBone(anim.getBones().get("Head"),     model.bipedHead,     t, len, loop);
        applyBone(anim.getBones().get("Body"),     model.bipedBody,     t, len, loop);
        applyBone(anim.getBones().get("RightArm"), model.bipedRightArm, t, len, loop);
        applyBone(anim.getBones().get("LeftArm"),  model.bipedLeftArm,  t, len, loop);
        applyBone(anim.getBones().get("RightLeg"), model.bipedRightLeg, t, len, loop);
        applyBone(anim.getBones().get("LeftLeg"),  model.bipedLeftLeg,  t, len, loop);

        // Translation pour un os "Item", si présent
        BoneAnimation itemBone = anim.getBones().get("Item");
        if (itemBone != null) {
            float[] pos = itemBone.getPositionAt(t, len, loop);
            GlStateManager.pushMatrix();
            GlStateManager.translate(pos[0], pos[1], pos[2]);
            GlStateManager.popMatrix();
        }

        // Incrémente le temps
        inst.tick();
    }

    private void applyBone(BoneAnimation bone, ModelRenderer part,
                           float time, float totalLength, boolean loop) {
        if (bone == null || part == null) return;
        float[] rot = bone.getRotationAt(time, totalLength, loop);
        part.rotateAngleX += (float) Math.toRadians(rot[0]);
        part.rotateAngleY += (float) Math.toRadians(rot[1]);
        part.rotateAngleZ += (float) Math.toRadians(rot[2]);
    }
}
