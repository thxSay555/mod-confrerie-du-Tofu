// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.7 - 1.12
// Paste this class into your mod and generate all required imports
package fr.wakfu.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class Eliatrope_hat extends ModelBase {
	private final ModelRenderer Head;
	private final ModelRenderer chapeau_eliatrope;
	private final ModelRenderer premier;
	private final ModelRenderer deuxieme;
	private final ModelRenderer troisieme;
	private final ModelRenderer cube_r1;
	private final ModelRenderer froufrou;
	private final ModelRenderer cube_r2;
	private final ModelRenderer oreilR;
	private final ModelRenderer cube_r3;
	private final ModelRenderer cube_r4;
	private final ModelRenderer oreilL;
	private final ModelRenderer cube_r5;
	private final ModelRenderer cube_r6;
	

	public Eliatrope_hat() {
		textureWidth = 64;
		textureHeight = 64;

		Head = new ModelRenderer(this);
		Head.setRotationPoint(-0.0013F, -0.0515F, -0.1708F);
		setRotationAngle(Head, 0.0F, 0.0087F, 0.0038F);
		Head.cubeList.add(new ModelBox(Head, 32, 0, -3.7369F, -7.975F, -4.2253F, 8, 8, 8, 0.5F, false));
		Head.cubeList.add(new ModelBox(Head, 0, 0, -3.7619F, -8.0F, -4.2003F, 8, 8, 8, 0.0F, false));

		chapeau_eliatrope = new ModelRenderer(this);
		chapeau_eliatrope.setRotationPoint(0.0F, -5.25F, 4.2F);
		Head.addChild(chapeau_eliatrope);
		

		premier = new ModelRenderer(this);
		premier.setRotationPoint(0.0283F, -2.3129F, 0.1378F);
		chapeau_eliatrope.addChild(premier);
		setRotationAngle(premier, -0.9163F, 0.0F, 0.0F);
		premier.cubeList.add(new ModelBox(premier, 0, 0, -3.3602F, -0.1542F, -1.4638F, 7, 7, 7, 0.0F, false));

		deuxieme = new ModelRenderer(this);
		deuxieme.setRotationPoint(0.0107F, 0.6867F, 6.4473F);
		premier.addChild(deuxieme);
		setRotationAngle(deuxieme, -0.5236F, 0.0F, 0.0F);
		deuxieme.cubeList.add(new ModelBox(deuxieme, 0, 19, -3.6191F, -0.4691F, -1.2809F, 7, 7, 7, 0.0F, false));

		troisieme = new ModelRenderer(this);
		troisieme.setRotationPoint(0.0064F, 3.7784F, 5.9976F);
		deuxieme.addChild(troisieme);
		setRotationAngle(troisieme, -0.0436F, 0.0F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(-0.0086F, -0.5502F, 1.9428F);
		troisieme.addChild(cube_r1);
		setRotationAngle(cube_r1, 0.2182F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 0, 42, -2.8558F, -2.8558F, -3.1442F, 6, 6, 6, 0.0F, false));

		froufrou = new ModelRenderer(this);
		froufrou.setRotationPoint(0.0F, -1.4677F, 4.9103F);
		troisieme.addChild(froufrou);
		setRotationAngle(froufrou, 0.2618F, 0.0F, 0.0F);
		

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0729F, 0.0566F, -0.5182F);
		froufrou.addChild(cube_r2);
		setRotationAngle(cube_r2, 1.5708F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 43, 33, -2.2658F, 0.1492F, -2.7342F, 5, 5, 5, 0.0F, false));

		oreilR = new ModelRenderer(this);
		oreilR.setRotationPoint(0.0447F, -7.8839F, 0.093F);
		Head.addChild(oreilR);
		setRotationAngle(oreilR, -1.524F, 0.4779F, -1.4816F);
		

		cube_r3 = new ModelRenderer(this);
		cube_r3.setRotationPoint(-0.4263F, -3.131F, 4.4334F);
		oreilR.addChild(cube_r3);
		setRotationAngle(cube_r3, -0.7719F, 0.6394F, -0.2069F);
		cube_r3.cubeList.add(new ModelBox(cube_r3, 55, 59, -0.8818F, -1.4068F, -1.0064F, 2, 2, 2, 0.0F, false));

		cube_r4 = new ModelRenderer(this);
		cube_r4.setRotationPoint(-0.6994F, -2.3049F, 3.8458F);
		oreilR.addChild(cube_r4);
		setRotationAngle(cube_r4, -0.7719F, 0.6394F, -0.2069F);
		cube_r4.cubeList.add(new ModelBox(cube_r4, 1, 2, -1.425F, -1.425F, -1.4175F, 3, 3, 3, 0.0F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 1, 2, -2.1572F, -0.7922F, -1.6585F, 4, 4, 3, 0.0F, false));
		cube_r4.cubeList.add(new ModelBox(cube_r4, 0, 58, -1.425F, -1.425F, -1.4175F, 3, 3, 3, 0.0F, false));

		oreilL = new ModelRenderer(this);
		oreilL.setRotationPoint(0.0447F, -7.8839F, 0.093F);
		Head.addChild(oreilL);
		setRotationAngle(oreilL, -1.5223F, -0.4778F, 1.4752F);
		

		cube_r5 = new ModelRenderer(this);
		cube_r5.setRotationPoint(0.4189F, -3.1306F, 4.5226F);
		oreilL.addChild(cube_r5);
		setRotationAngle(cube_r5, -0.7719F, -0.6394F, 0.2069F);
		cube_r5.cubeList.add(new ModelBox(cube_r5, 55, 59, -0.8817F, -1.4068F, -1.0064F, 2, 2, 2, 0.0F, false));

		cube_r6 = new ModelRenderer(this);
		cube_r6.setRotationPoint(0.6921F, -2.3044F, 3.9349F);
		oreilL.addChild(cube_r6);
		setRotationAngle(cube_r6, -0.7719F, -0.6394F, 0.2069F);
		cube_r6.cubeList.add(new ModelBox(cube_r6, 1, 2, -1.425F, -1.425F, -1.4175F, 3, 3, 3, 0.0F, false));
		cube_r6.cubeList.add(new ModelBox(cube_r6, 1, 2, -2.1572F, -0.7922F, -1.6585F, 4, 4, 3, 0.0F, false));
		cube_r6.cubeList.add(new ModelBox(cube_r6, 0, 58, -1.425F, -1.425F, -1.4175F, 3, 3, 3, 0.0F, false));
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		Head.render(f5);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}