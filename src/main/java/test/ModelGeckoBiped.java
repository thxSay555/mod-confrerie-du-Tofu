package test;



import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGeckoBiped extends MowzieAnimatedGeoModel<GeckoPlayer> {
    private ResourceLocation animFile, modelFile, textureFile;
    public boolean isSitting, isChild, isSneak;
    public float swingProgress, swimAnimation;
    public ModelBiped.ArmPose leftArmPose = ModelBiped.ArmPose.EMPTY;
    public ModelBiped.ArmPose rightArmPose = ModelBiped.ArmPose.EMPTY;
    protected boolean useSmallArms;

    @Override
    public ResourceLocation getAnimationFileLocation(GeckoPlayer animatable) {
        return animFile;
    }

    @Override
    public ResourceLocation getModelLocation(GeckoPlayer animatable) {
        return modelFile;
    }

    @Override
    public ResourceLocation getTextureLocation(GeckoPlayer animatable) {
        return textureFile;
    }

    @Override
    public boolean resourceForModelId(AbstractClientPlayer player) {
        animFile    = new ResourceLocation(MowziesMobs.MODID, "animations/animated_player.animation.json");
        modelFile   = new ResourceLocation(MowziesMobs.MODID, "geo/animated_player.geo.json");
        textureFile = player.getLocationSkin();
        return true;
    }

    public void setUseSmallArms(boolean flag) { useSmallArms = flag; }
    public boolean isUsingSmallArms() { return useSmallArms; }

    private MowzieGeoBone head   () { return getMowzieBone("Head");    }
    private MowzieGeoBone body   () { return getMowzieBone("Body");    }
    private MowzieGeoBone rightArm() { return getMowzieBone("RightArm");}
    private MowzieGeoBone leftArm () { return getMowzieBone("LeftArm"); }
    private MowzieGeoBone rightLeg() { return getMowzieBone("RightLeg");}
    private MowzieGeoBone leftLeg () { return getMowzieBone("LeftLeg"); }

    public void codeAnimations(GeckoPlayer entity, Integer uniqueID, software.bernie.geckolib3.core.event.predicate.AnimationEvent<?> event) {
        if (!isInitialized() || Minecraft.getMinecraft().isGamePaused()) return;

        // toggle slim vs classic arms
        MowzieGeoBone rc = getMowzieBone("RightArmClassic");
        MowzieGeoBone lc = getMowzieBone("LeftArmClassic");
        MowzieGeoBone rs = getMowzieBone("RightArmSlim");
        MowzieGeoBone ls = getMowzieBone("LeftArmSlim");
        rc.setHidden(useSmallArms);
        lc.setHidden(useSmallArms);
        rs.setHidden(!useSmallArms);
        ls.setHidden(!useSmallArms);

        EntityPlayer player = entity.getPlayer();
        swingProgress = player.swingProgress;
        swimAnimation = player.getSwimAnimation(player.isInWater() ? event.delta : 0);

        float headYaw   = MathHelper.clamp(event.netHeadYaw, -180, 180) * ((float)Math.PI/180F);
        float headPitch = MathHelper.clamp(event.headPitch, -90, 90)  * ((float)Math.PI/180F);
        head().setRotationY(-headYaw);
        head().setRotationX(-headPitch);

        // arm swing
        float swingCtrl     = getControllerValue("ArmSwingController");
        float swingRight    = 1.0F - getBone("ArmSwingController").getPositionY();
        float swingLeft     = 1.0F - getBone("ArmSwingController").getPositionZ();
        rightArm().addRotationX(swingCtrl * swingRight * MathHelper.cos(swingProgress*0.6662F + (float)Math.PI) * 2.0F * swingProgress * 0.5F);
        leftArm ().addRotationX(swingCtrl * swingLeft  * MathHelper.cos(swingProgress*0.6662F)             * 2.0F * swingProgress * 0.5F);

        // leg walk
        float legCtrl = getControllerValue("LegWalkController");
        rightLeg().addRotationX(legCtrl * MathHelper.cos(swingProgress*0.6662F) * 1.4F * swingProgress);
        leftLeg ().addRotationX(legCtrl * MathHelper.cos(swingProgress*0.6662F + (float)Math.PI) * 1.4F * swingProgress);

        // sitting pose
        if (isSitting) {
            float sitAngle = -(float)Math.PI/5F;
            rightArm().addRotationX(sitAngle);
            leftArm ().addRotationX(sitAngle);
            rightLeg().setRotation(-1.4137F,  Math.PI/10F,  0.0785F);
            leftLeg ().setRotation(-1.4137F, -Math.PI/10F, -0.0785F);
        }

        // arm item/block poses
        boolean rightMain = player.getPrimaryHand() == EnumHandSide.RIGHT;
        boolean useLeft   = rightMain ? leftArmPose.isTwoHanded()  : rightArmPose.isTwoHanded();
        if (rightMain != useLeft) applyBlockPose(player, false);
        else                         applyBlockPose(player, true);

        // sneak
        if (isSneak) {
            float sneakCtrl = getControllerValue("CrouchController");
            body().addRotationX(-0.5F * sneakCtrl);
            head().addPositionY(-1.0F * sneakCtrl);
            rightArm().addRotationX(-0.4F * sneakCtrl);
            leftArm ().addRotationX(-0.4F * sneakCtrl);
        }

        // ability-driven animations
        AbilityCapability.IAbilityCapability cap = AbilityHandler.INSTANCE.getAbilityCapability(player);
        if (cap != null && cap.getActiveAbility() != null) {
            cap.codeAnimations(this, event.delta);
        }
    }

    private void applyBlockPose(EntityPlayer player, boolean leftFirst) {
        float swingCtrl = getControllerValue("ArmSwingController");
        ModelBiped.ArmPose pose = leftFirst ? leftArmPose : rightArmPose;
        float angle = pose == ModelBiped.ArmPose.BLOCK
                    ? 0.9425F * swingCtrl
                    : pose == ModelBiped.ArmPose.ITEM
                    ? ((float)Math.PI/10F) * swingCtrl
                    : 0;
        if (leftFirst) leftArm().addRotationX(angle);
        else           rightArm().addRotationX(angle);
    }
}
