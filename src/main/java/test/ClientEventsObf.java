package test;

import java.lang.reflect.Field;
import java.util.Map;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;

import fr.wakfu.WakfuMod;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventsObf {

    // 1) Animation appliquée au ModelPlayer (couches wear incluses)
    @SubscribeEvent
    public void onSetupAngles(ModelPlayerEvent.SetupAngles.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        AnimationInstance inst = WakfuMod.proxy
                                         .getAnimationManager()
                                         .getInstance(player.getName());
        if (inst == null) return;

        applyToBiped(event.getModelPlayer(), inst);
    }

    // 2) Animation appliquée aux couches d'armure et au model principal
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    // 3) Animation appliquée à l'item tenu
    @SubscribeEvent
    public void onRenderHeldItemPre(RenderItemEvent.Held.Pre event) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        EntityLivingBase entity = event.getEntity();
        if (!(entity instanceof EntityPlayer)) return;
        AnimationInstance inst = WakfuMod.proxy
                                         .getAnimationManager()
                                         .getInstance(((EntityPlayer) entity).getName());
        if (inst != null) {
            // clé de bone selon la main
            EnumHandSide side   = event.getHandSide();
            String      boneKey = (side == EnumHandSide.RIGHT)
                                  ? "Selected_Item"
                                  : "Item_Offhand";
            BoneAnimation bone  = inst.getAnimation().getBones().get(boneKey);
            if (bone != null) {
                // calcule transforms
                float t    = inst.getTime();
                float len  = inst.getAnimation().getLength();
                boolean lp = inst.getAnimation().isLoop();
                float[] rot = bone.getRotationAt(t, len, lp);
                float[] pos = bone.getPositionAt(t, len, lp);

                // conversion et application
                GlStateManager.translate(-pos[0]/16f, -pos[2]/16f, pos[1]/16f);
                GlStateManager.rotate(-rot[0], 1f, 0f, 0f);
                GlStateManager.rotate(-rot[2], 0f, 1f, 0f);
                GlStateManager.rotate(-rot[1], 0f, 0f, 1f);
            }
        }
    }

    @SubscribeEvent
    public void onRenderHeldItemPost(RenderItemEvent.Held.Post event) {
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    // 4) Tick logique (start/stop seulement)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        // gestion démarrage/arrêt des animations, sans inst.tick()
    }

    // 5) Tick rendu : on avance toutes les animations en cours
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        AnimationManager mgr = WakfuMod.proxy.getAnimationManager();
        try {
            // Accède au Map<String, AnimationInstance> instances
            Field instField = getField(AnimationManager.class, "instances");
            instField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, AnimationInstance> map =
                (Map<String, AnimationInstance>) instField.get(mgr);

            // Pour chaque instance, on « double-tick » pour garder la vitesse
            for (AnimationInstance inst : map.values()) {
                inst.tick();
                inst.tick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Applique rotation + translation à un ModelBiped. */
    private void applyToBiped(ModelBiped model, AnimationInstance inst) {
        Animation anim     = inst.getAnimation();
        float t            = inst.getTime();
        float len          = anim.getLength();
        boolean loop       = anim.isLoop();

        for (Map.Entry<String, BoneAnimation> entry : anim.getBones().entrySet()) {
            ModelRenderer part = getPart(model, entry.getKey());
            ModelRenderer wear = getPartWear(model, entry.getKey());
            if (part == null && wear == null) continue;

            float[] rot = entry.getValue().getRotationAt(t, len, loop);
            float[] pos = entry.getValue().getPositionAt(t, len, loop);
            float dx = pos[0]/16f, dy = -pos[1]/16f, dz = pos[2]/16f;

            for (ModelRenderer mr : new ModelRenderer[]{part, wear}) {
                if (mr == null) continue;
                mr.rotateAngleX = mr.rotateAngleY = mr.rotateAngleZ = 0;
                mr.offsetX = mr.offsetY = mr.offsetZ = 0;
                mr.rotateAngleX += (float) Math.toRadians(rot[0]);
                mr.rotateAngleY += (float) Math.toRadians(rot[1]);
                mr.rotateAngleZ += (float) Math.toRadians(rot[2]);
                mr.offsetX += dx;
                mr.offsetY += dy;
                mr.offsetZ += dz;
            }
        }
    }

    /** Recherche un champ par nom MCP, SRG ou OBF. */
    private Field getField(Class<?> cls, String... names) throws NoSuchFieldException {
        for (String n : names) {
            try { return cls.getDeclaredField(n); }
            catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException("Aucun champ trouvé dans " + cls.getName());
    }

    private ModelRenderer getPart(ModelBiped model, String key) {
        switch (key) {
            case "Head":     return model.bipedHead;
            case "Body":     return model.bipedBody;
            case "RightArm": return model.bipedRightArm;
            case "LeftArm":  return model.bipedLeftArm;
            case "RightLeg": return model.bipedRightLeg;
            case "LeftLeg":  return model.bipedLeftLeg;
            default:         return null;
        }
    }

    private ModelRenderer getPartWear(ModelBiped model, String key) {
        if (model instanceof ModelPlayer) {
            ModelPlayer pm = (ModelPlayer) model;
            switch (key) {
                case "Head":     return pm.bipedHeadwear;
                case "Body":     return pm.bipedBodyWear;
                case "RightArm": return pm.bipedRightArmwear;
                case "LeftArm":  return pm.bipedLeftArmwear;
                case "RightLeg": return pm.bipedRightLegwear;
                case "LeftLeg":  return pm.bipedLeftLegwear;
            }
        }
        return null;
    }
}
