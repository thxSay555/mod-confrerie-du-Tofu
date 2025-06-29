package test;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import fr.wakfu.WakfuMod;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ClientEventsObf {

    @SubscribeEvent
    public void onSetupAngles(ModelPlayerEvent.SetupAngles.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        AnimationInstance inst = WakfuMod.proxy
                                         .getAnimationManager()
                                         .getInstance(player.getName());
        if (inst == null) {
            return;
        }

        ModelPlayer model = event.getModelPlayer();
        Animation anim     = inst.getAnimation();
        float t            = inst.getTime();
        float len          = anim.getLength();
        boolean loop       = anim.isLoop();

        // Pour chaque os animé...
        for (Map.Entry<String, BoneAnimation> entry : anim.getBones().entrySet()) {
            String boneKey  = entry.getKey();
            BoneAnimation bone = entry.getValue();
            ModelRenderer part  = getPart(model, boneKey);
            if (part == null) continue;

            // 1) Reset angles + offsets
            part.rotateAngleX = 0.0F;
            part.rotateAngleY = 0.0F;
            part.rotateAngleZ = 0.0F;
            part.offsetX     = 0.0F;
            part.offsetY     = 0.0F;
            part.offsetZ     = 0.0F;

            // 2) Appliquer la rotation
            float[] rot = bone.getRotationAt(t, len, loop);
            part.rotateAngleX += Math.toRadians(rot[0]);
            part.rotateAngleY += Math.toRadians(rot[1]);
            part.rotateAngleZ += Math.toRadians(rot[2]);

            // 3) Appliquer la translation directement sur le ModelRenderer
            //    Blockbench exporte en "pixels" → on convertit en blocs (1 bloc = 16 px)
            float[] pos = bone.getPositionAt(t, len, loop);
            part.offsetX = pos[0] / 16f;
            part.offsetY = pos[1] / 16f;
            part.offsetZ = pos[2] / 16f;
        }

        inst.tick();
    }

    private ModelRenderer getPart(ModelPlayer model, String boneKey) {
        switch (boneKey) {
            case "Head":     return model.bipedHead;
            case "Body":     return model.bipedBody;
            case "RightArm": return model.bipedRightArm;
            case "LeftArm":  return model.bipedLeftArm;
            case "RightLeg": return model.bipedRightLeg;
            case "LeftLeg":  return model.bipedLeftLeg;
            default:         return null;
        }
    }
}
