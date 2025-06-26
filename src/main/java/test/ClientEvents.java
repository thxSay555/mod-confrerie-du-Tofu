package test;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import fr.wakfu.WakfuMod;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Applique à chaque frame les rotations/positions selon l'AnimationInstance.
 */
public class ClientEvents {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        String name = event.getEntityPlayer().getName();
        AnimationInstance inst = WakfuMod.animationManager.getInstance(name);
        if (inst == null) return;

        Animation anim = inst.getAnimation();
        float t = inst.getTime();
        boolean loop = anim.isLoop();
        float length = anim.getLength();

        // Exemple pour la tête :
        BoneAnimation headBone = anim.getBones().get("Head");
        if (headBone != null) {
            float[] rot = headBone.getRotationAt(t, length, loop);
            event.getRenderer().getMainModel().bipedHead.rotateAngleX = (float) Math.toRadians(rot[0]);
            event.getRenderer().getMainModel().bipedHead.rotateAngleY = (float) Math.toRadians(rot[1]);
            event.getRenderer().getMainModel().bipedHead.rotateAngleZ = (float) Math.toRadians(rot[2]);
        }

        // À répéter pour bipedBody, bipedRightArm, bipedLeftArm, bipedRightLeg, bipedLeftLeg

        // Si tu veux aussi déplacer l'item tenu :
        GlStateManager.pushMatrix();
        // transform selon un os "Item" si présent :
        BoneAnimation itemBone = anim.getBones().get("Item");
        if (itemBone != null) {
            float[] pos = itemBone.getPositionAt(t, length, loop);
            GlStateManager.translate(pos[0], pos[1], pos[2]);
        }
        GlStateManager.popMatrix();

        // Faire avancer le temps après application
        WakfuMod.animationManager.tickAll();
    }
}