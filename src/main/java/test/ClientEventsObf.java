package test;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;

import fr.wakfu.WakfuMod;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Map;

/**
 * Injecte les animations JSON juste après la pose vanilla,
 * en déréglant (reset) uniquement les bones que l'on remplace.
 */
@SideOnly(Side.CLIENT)
public class ClientEventsObf {

    @SubscribeEvent
    public void onSetupAngles(ModelPlayerEvent.SetupAngles.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        AnimationInstance inst = WakfuMod.animationManager.getInstance(player.getName());
        if (inst == null) return;

        // Récupère le modèle et ta structure d'animation
        ModelPlayer model = event.getModelPlayer();
        Animation anim = inst.getAnimation();
        float t      = inst.getTime();
        float len    = anim.getLength();
        boolean loop = anim.isLoop();

        // Parcours tous les os que ton JSON définit
        for (Map.Entry<String, BoneAnimation> entry : anim.getBones().entrySet()) {
            String boneKey = entry.getKey();
            BoneAnimation bone = entry.getValue();
            // Mappe la clé JSON en champ ModelRenderer
            ModelRenderer part = getPartForBone(model, boneKey);
            if (part == null || bone == null) continue;

            // 1) reset de la rotation vanilla pour CE bone uniquement
            part.rotateAngleX = 0f;
            part.rotateAngleY = 0f;
            part.rotateAngleZ = 0f;

            // 2) applique ensuite ta rotation custom
            float[] rot = bone.getRotationAt(t, len, loop);
            part.rotateAngleX += (float) Math.toRadians(rot[0]);
            part.rotateAngleY += (float) Math.toRadians(rot[1]);
            part.rotateAngleZ += (float) Math.toRadians(rot[2]);
        }

        // Pour un os de position (ex : translation d'item)
        BoneAnimation itemBone = anim.getBones().get("Item");
        if (itemBone != null) {
            float[] pos = itemBone.getPositionAt(t, len, loop);
            GlStateManager.pushMatrix();
            GlStateManager.translate(pos[0], pos[1], pos[2]);
            GlStateManager.popMatrix();
        }

        inst.tick();
    }

    /**
     * Retourne le bon ModelRenderer du ModelPlayer
     * selon la clé utilisée dans tes JSON (Head, Body, RightArm...)
     */
    private ModelRenderer getPartForBone(ModelPlayer model, String boneKey) {
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
