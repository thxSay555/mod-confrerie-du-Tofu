package com.bobmowzie.mowziesmobs.client.render.entity.player;

import java.util.HashMap;
import java.util.Iterator;

import com.bobmowzie.mowziesmobs.client.model.entity.ModelGeckoPlayerFirstPerson;
import com.bobmowzie.mowziesmobs.client.model.tools.geckolib.MowzieGeoBone;
import com.bobmowzie.mowziesmobs.client.render.MowzieRenderUtils;
import com.bobmowzie.mowziesmobs.server.ability.Ability;
import com.bobmowzie.mowziesmobs.server.ability.AbilityHandler;
import com.bobmowzie.mowziesmobs.server.ability.AbilitySection;
import com.bobmowzie.mowziesmobs.server.capability.AbilityCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.RenderUtils;

@OnlyIn(Dist.CLIENT)
public class GeckoFirstPersonRenderer extends FirstPersonRenderer implements IGeoRenderer<GeckoPlayer> {
	public IRenderTypeBuffer rtb;
	public static GeckoPlayer.GeckoPlayerFirstPerson GECKO_PLAYER_FIRST_PERSON;

	private static HashMap<Class<? extends GeckoPlayer>, GeckoFirstPersonRenderer> modelsToLoad = new HashMap<>();
	private ModelGeckoPlayerFirstPerson modelProvider;

	boolean mirror;

	public GeckoFirstPersonRenderer(Minecraft mcIn, ModelGeckoPlayerFirstPerson modelProvider) {
		super(mcIn);
		this.modelProvider = modelProvider;
	}

	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof GeckoPlayer.GeckoPlayerFirstPerson) {
				GeckoFirstPersonRenderer render = modelsToLoad.get(object.getClass());
				return (IAnimatableModel<Object>) render.getGeoModelProvider();
			} else {
				return null;
			}
		});
	}

	public GeckoFirstPersonRenderer getModelProvider(Class<? extends GeckoPlayer> animatable) {
		return modelsToLoad.get(animatable);
	}

	public HashMap<Class<? extends GeckoPlayer>, GeckoFirstPersonRenderer> getModelsToLoad() {
		return modelsToLoad;
	}

	public void renderItemInFirstPerson(AbstractClientPlayerEntity player, float pitch, float partialTicks, Hand handIn,
			float swingProgress, ItemStack stack, float equippedProgress, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, GeckoPlayer geckoPlayer) {
		boolean flag = handIn == Hand.MAIN_HAND;
		HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
		mirror = player.getPrimaryHand() == HandSide.LEFT;

		if (flag) {
			this.modelProvider.setLivingAnimations(geckoPlayer, player.getUniqueID().hashCode());

			RenderType rendertype = RenderType.getItemEntityTranslucentCull(getTextureLocation(geckoPlayer));
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(rendertype);
			matrixStackIn.translate(0, -2, -1);
			render(getGeoModelProvider().getModel(getGeoModelProvider().getModelLocation(geckoPlayer)), geckoPlayer,
					partialTicks, rendertype, matrixStackIn, bufferIn, ivertexbuilder, combinedLightIn,
					OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		}

		Ability.HandDisplay handDisplay = Ability.HandDisplay.DEFAULT;
		float offHandEquipProgress = 0.0f;
		AbilityCapability.IAbilityCapability abilityCapability = AbilityHandler.INSTANCE.getAbilityCapability(player);
		if (abilityCapability != null && abilityCapability.getActiveAbility() != null) {
			Ability ability = abilityCapability.getActiveAbility();
			ItemStack stackOverride = flag ? ability.heldItemMainHandOverride() : ability.heldItemOffHandOverride();
			if (stackOverride != null)
				stack = stackOverride;

			handDisplay = flag ? ability.getFirstPersonMainHandDisplay() : ability.getFirstPersonOffHandDisplay();

			if (ability.getCurrentSection().sectionType == AbilitySection.AbilitySectionType.STARTUP)
				offHandEquipProgress = MathHelper.clamp(1f - (ability.getTicksInSection() + partialTicks) / 5f, 0f, 1f);
			else if (ability.getCurrentSection().sectionType == AbilitySection.AbilitySectionType.RECOVERY
					&& ability.getCurrentSection() instanceof AbilitySection.AbilitySectionDuration)
				offHandEquipProgress = MathHelper.clamp((ability.getTicksInSection() + partialTicks
						- ((AbilitySection.AbilitySectionDuration) ability.getCurrentSection()).duration + 5) / 5f, 0f,
						1f);
		}

		if (handDisplay != Ability.HandDisplay.DONT_RENDER && modelProvider.isInitialized()) {
			int sideMult = handside == HandSide.RIGHT ? -1 : 1;
			if (mirror)
				handside = handside.opposite();
			String sideName = handside == HandSide.RIGHT ? "Right" : "Left";
			String boneName = sideName + "Arm";
			MowzieGeoBone bone = this.modelProvider.getMowzieBone(boneName);

			MatrixStack newMatrixStack = new MatrixStack();

			float fixedPitchController = 1f - this.modelProvider.getControllerValue("FixedPitchController" + sideName);
			newMatrixStack.rotate(new Quaternion(Vector3f.XP, pitch * fixedPitchController, true));

			newMatrixStack.getLast().getNormal().mul(bone.getWorldSpaceNormal());
			newMatrixStack.getLast().getMatrix().mul(bone.getWorldSpaceXform());
			newMatrixStack.translate(sideMult * 0.547, 0.7655, 0.625);

			if (mirror)
				handside = handside.opposite();

			if (stack.isEmpty() && !flag && handDisplay == Ability.HandDisplay.FORCE_RENDER && !player.isInvisible()) {
				newMatrixStack.translate(0, -1 * offHandEquipProgress, 0);
				super.renderArmFirstPerson(newMatrixStack, bufferIn, combinedLightIn, 0.0f, 0.0f, handside);
			} else {
				super.renderItemInFirstPerson(player, partialTicks, pitch, handIn, 0.0f, stack, 0.0f, newMatrixStack,
						bufferIn, combinedLightIn);
			}
		}
	}

	public void setSmallArms() {
		this.modelProvider.setUseSmallArms(true);
	}

	@Override
	public GeoModelProvider<GeckoPlayer> getGeoModelProvider() {
		return this.modelProvider;
	}

	public ModelGeckoPlayerFirstPerson getAnimatedPlayerModel() {
		return this.modelProvider;
	}

	@Override
	public ResourceLocation getTextureLocation(GeckoPlayer geckoPlayer) {
		return ((AbstractClientPlayerEntity) geckoPlayer.getPlayer()).getLocationSkin();
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack matrixStack, IVertexBuilder bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		matrixStack.push();
		if (mirror) {
			MowzieRenderUtils.translateMirror(bone, matrixStack);
			MowzieRenderUtils.moveToPivotMirror(bone, matrixStack);
			MowzieRenderUtils.rotateMirror(bone, matrixStack);
			RenderUtils.scale(bone, matrixStack);
		} else {
			RenderUtils.translate(bone, matrixStack);
			RenderUtils.moveToPivot(bone, matrixStack);
			RenderUtils.rotate(bone, matrixStack);
			RenderUtils.scale(bone, matrixStack);
		}
		// Record xform matrices for relevant bones
		if (bone instanceof MowzieGeoBone) {
			MowzieGeoBone mowzieBone = (MowzieGeoBone) bone;
			if (mowzieBone.name.equals("LeftArm") || mowzieBone.name.equals("RightArm")) {
				matrixStack.push();
				MatrixStack.Entry entry = matrixStack.getLast();
				mowzieBone.setWorldSpaceNormal(entry.getNormal().copy());
				mowzieBone.setWorldSpaceXform(entry.getMatrix().copy());
				matrixStack.pop();
			}
		}
		if (mirror) {
			MowzieRenderUtils.moveBackFromPivotMirror(bone, matrixStack);
		} else {
			RenderUtils.moveBackFromPivot(bone, matrixStack);
		}
		if (!bone.isHidden) {
			Iterator var10 = bone.childCubes.iterator();

			while (var10.hasNext()) {
				GeoCube cube = (GeoCube) var10.next();
				matrixStack.push();
				this.renderCube(cube, matrixStack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				matrixStack.pop();
			}

			var10 = bone.childBones.iterator();

			while (var10.hasNext()) {
				GeoBone childBone = (GeoBone) var10.next();
				this.renderRecursively(childBone, matrixStack, bufferIn, packedLightIn, packedOverlayIn, red, green,
						blue, alpha);
			}
		}

		matrixStack.pop();
	}

	@Override
	public void renderEarly(GeckoPlayer animatable, MatrixStack stackIn, float partialTicks,
			IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		this.rtb = renderTypeBuffer;
		IGeoRenderer.super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void setCurrentRTB(IRenderTypeBuffer rtb) {
		this.rtb = rtb;
	}

	@Override
	public IRenderTypeBuffer getCurrentRTB() {
		return this.rtb;
	}
}
