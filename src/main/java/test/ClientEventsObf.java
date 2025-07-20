package test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import com.mrcrayfish.obfuscate.client.event.RenderItemEvent;
import fr.wakfu.WakfuMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
        if (inst == null) {
            return;
        }

        applyToBiped(event.getModelPlayer(), inst);
        inst.tick();
    }

    // 2) Animation appliquée aux LayerArmorBase (armure vanilla)
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        AnimationInstance inst = WakfuMod.proxy
                                         .getAnimationManager()
                                         .getInstance(player.getName());
        if (inst == null) {
            return;
        }

        RenderLivingBase<?> renderer = (RenderPlayer) event.getRenderer();
        List<?> layers;

        // 1) Récupère la liste des layers (dev / SRG / OBF)
        try {
            Field layersField = getField(
                RenderLivingBase.class,
                "layerRenderers",     // nom MCP (dev)
                "field_177097_h",     // nom SRG
                "iLayerRenderers"     // nom obfusqué (à ajuster)
            );
            layersField.setAccessible(true);
       
            layers = (List<?>) layersField.get(renderer);
        } catch (Exception e) {
            e.printStackTrace();
            // Debug: afficher tous les champs disponibles

            for (Field f : RenderLivingBase.class.getDeclaredFields()) {
  
            }
            return;
        }

        // 2) Pour chaque LayerArmorBase, on applique l'animation
        for (Object layer : layers) {
            if (!(layer instanceof LayerArmorBase)) continue;
            LayerArmorBase<?> armorLayer = (LayerArmorBase<?>) layer;
            try {
                Field fArmor = getField(
                    LayerArmorBase.class,
                    "modelArmor",        // nom MCP
                    "field_188355_b",    // nom SRG
                    "field_78115_e"      // nom obfusqué (à ajuster)
                );
                Field fLegs = getField(
                    LayerArmorBase.class,
                    "modelLeggings",     // nom MCP
                    "field_188356_c",    // nom SRG
                    "field_78116_c"      // nom obfusqué (à ajuster)
                );
                fArmor.setAccessible(true);
                fLegs.setAccessible(true);
    
                ModelBiped armorModel = (ModelBiped) fArmor.get(armorLayer);
                ModelBiped legsModel  = (ModelBiped) fLegs.get(armorLayer);

                applyToBiped(armorModel, inst);
                applyToBiped(legsModel, inst);
            } catch (Exception e) {
                e.printStackTrace();
                // Debug: afficher tous les champs de LayerArmorBase

             
            }
        }
    }
 
        
    @SubscribeEvent
    public void onRenderHeldItemPre(RenderItemEvent.Held.Pre event) {
        EntityLivingBase entity = event.getEntity();
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        AnimationInstance inst = WakfuMod.proxy.getAnimationManager()
                                             .getInstance(player.getName());
        if (inst == null) {
            return;
        }

        // Détermine la clé de bone selon la main
        EnumHandSide side   = event.getHandSide();
        String      boneKey = (side == EnumHandSide.RIGHT)
                              ? "Selected_Item"
                              : "Item_Offhand";
        BoneAnimation bone  = inst.getAnimation().getBones().get(boneKey);
        if (bone == null) {
            return;
        }

        // Interpolation temps / keyframes
        float t    = inst.getTime();
        float len  = inst.getAnimation().getLength();
        boolean lp = inst.getAnimation().isLoop();
        float[] rot = bone.getRotationAt(t, len, lp);
        float[] pos = bone.getPositionAt(   t, len, lp);
 // si tu veux gérer le scale

        // Conversion JSON → repère Minecraft
        float rx = -rot[0];
        float ry = -rot[2];
        float rz = -rot[1];

        float px = -pos[0] / 16f;
        float py = -pos[2] / 16f;
        float pz = -pos[1] / 16f;

        // Appliquer transforms
        GlStateManager.pushMatrix();

        // Scale éventuel
   

        GlStateManager.translate(px, py, pz);
        GlStateManager.rotate(rx, 1f, 0f, 0f);
        GlStateManager.rotate(ry, 0f, 1f, 0f);
        GlStateManager.rotate(rz, 0f, 0f, 1f);
    }

    @SubscribeEvent
    public void onRenderHeldItemPost(RenderItemEvent.Held.Post event) {
        // Restaurer la matrice et avancer l’animation
        GlStateManager.popMatrix();
        EntityLivingBase entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            AnimationInstance inst = WakfuMod.proxy.getAnimationManager()
                                                 .getInstance(((EntityPlayer) entity).getName());
            if (inst != null) {
                inst.tick();
            }
        }
    }



    /** Applique rotation + translation à TOUTES les parties d’un ModelBiped (Player ou Armor). */
    private void applyToBiped(ModelBiped model, AnimationInstance inst) {
        Animation anim     = inst.getAnimation();
        float t            = inst.getTime();
        float len          = anim.getLength();
        boolean loop       = anim.isLoop();

        for (Map.Entry<String, BoneAnimation> entry : anim.getBones().entrySet()) {
            String boneKey      = entry.getKey();
            BoneAnimation bone  = entry.getValue();

            ModelRenderer part  = getPart(model, boneKey);
            ModelRenderer wear  = getPartWear(model, boneKey);
            if (part == null && wear == null) continue;

            float[] rot = bone.getRotationAt(t, len, loop);
            float[] pos = bone.getPositionAt(t, len, loop);
            float dx = pos[0] / 16f, dy = -pos[1] / 16f, dz = pos[2] / 16f;

            for (ModelRenderer mr : new ModelRenderer[]{part, wear}) {
                if (mr == null) continue;

                // Reset
                mr.rotateAngleX = 0;
                mr.rotateAngleY = 0;
                mr.rotateAngleZ = 0;
                mr.offsetX      = 0;
                mr.offsetY      = 0;
                mr.offsetZ      = 0;

                // Rotation
                mr.rotateAngleX += Math.toRadians(rot[0]);
                mr.rotateAngleY += Math.toRadians(rot[1]);
                mr.rotateAngleZ += Math.toRadians(rot[2]);

                // Translation
                mr.offsetX += dx;
                mr.offsetY += dy;
                mr.offsetZ += dz;
            }
        }
    }

    /** Tente successivement plusieurs noms de champ (MCP, SRG, OBF). */
    private Field getField(Class<?> cls, String... names) throws NoSuchFieldException {
        for (String name : names) {
            try {
                Field f = cls.getDeclaredField(name);
                return f;
            } catch (NoSuchFieldException ignored) {}
        }
        throw new NoSuchFieldException("Aucun champ trouvé dans " + cls.getName());
    }

    private ModelRenderer getPart(ModelBiped model, String boneKey) {
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

    private ModelRenderer getPartWear(ModelBiped model, String boneKey) {
        if (model instanceof ModelPlayer) {
            ModelPlayer playerModel = (ModelPlayer) model;
            switch (boneKey) {
                case "Head":     return playerModel.bipedHeadwear;
                case "Body":     return playerModel.bipedBodyWear;
                case "RightArm": return playerModel.bipedRightArmwear;
                case "LeftArm":  return playerModel.bipedLeftArmwear;
                case "RightLeg": return playerModel.bipedRightLegwear;
                case "LeftLeg":  return playerModel.bipedLeftLegwear;
            }
        }
        // ModelBiped d'armure vanilla n'ont pas de wear
        return null;
    }
}
