package test;

import fr.wakfu.WakfuMod;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Injecte les rotations/positions issues de tes JSON
 * juste après que Minecraft ait posé ses angles vanilla.
 */
@SideOnly(Side.CLIENT)
public class ClientEvents {

    public ClientEvents() {
        // Enregistre ce listener sur le bus Forge client
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Appelé juste avant le rendu de tout EntityLivingBase (incluant EntityPlayer),
     * après que ModelBiped.setRotationAngles(...) a été exécuté.
     */
    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
        // Vérifie qu'il s'agit bien d'un joueur
        EntityLivingBase entity = event.getEntity();
        if (!(entity instanceof EntityPlayer)) return;

        // Vérifie qu'on utilise bien le renderer du joueur
        RenderLivingBase<?> renderer = event.getRenderer();
        if (!(renderer instanceof RenderPlayer)) return;

        // Cast en player
        EntityPlayer player = (EntityPlayer) entity;
        String name = player.getName();

        // Récupère l'instance d'animation (côté client)
        AnimationInstance inst = WakfuMod.animationManager.getInstance(name);
        if (inst == null) {
            // Pas d'animation en cours → exit
            return;
        }

        // DEBUG : confirmation d'entrée
        System.out.println("[DEBUG] Applying animation '" +
            inst.getAnimation().getName() + "' for " + name +
            " at t=" + String.format("%.2f", inst.getTime()));

        Animation anim = inst.getAnimation();
        float time     = inst.getTime();
        float length   = anim.getLength();
        boolean loop   = anim.isLoop();

        // Récupère le ModelBiped (avec angles vanilla déjà appliqués)
        ModelBiped model = (ModelBiped) renderer.getMainModel();

        // Injection additive pour chaque bone
        applyBone(anim.getBones().get("Head"),     model.bipedHead,     time, length, loop);
        applyBone(anim.getBones().get("Body"),     model.bipedBody,     time, length, loop);
        applyBone(anim.getBones().get("RightArm"), model.bipedRightArm, time, length, loop);
        applyBone(anim.getBones().get("LeftArm"),  model.bipedLeftArm,  time, length, loop);
        applyBone(anim.getBones().get("RightLeg"), model.bipedRightLeg, time, length, loop);
        applyBone(anim.getBones().get("LeftLeg"),  model.bipedLeftLeg,  time, length, loop);

        // Translation pour l'item tenu si défini dans le JSON
        BoneAnimation itemBone = anim.getBones().get("Item");
        if (itemBone != null) {
            float[] pos = itemBone.getPositionAt(time, length, loop);
            GlStateManager.pushMatrix();
            GlStateManager.translate(pos[0], pos[1], pos[2]);
            GlStateManager.popMatrix();
        }

        // Avance la timeline D'UNE SEULE frame
        inst.tick();
    }

    /**
     * Applique la rotation interpolée d'un bone
     * sur le ModelRenderer en mode ADDITIF.
     */
    private void applyBone(BoneAnimation bone, ModelRenderer part,
                           float time, float totalLength, boolean loop) {
        if (bone == null || part == null) return;
        float[] rot = bone.getRotationAt(time, totalLength, loop);
        part.rotateAngleX += (float) Math.toRadians(rot[0]);
        part.rotateAngleY += (float) Math.toRadians(rot[1]);
        part.rotateAngleZ += (float) Math.toRadians(rot[2]);
    }
}
