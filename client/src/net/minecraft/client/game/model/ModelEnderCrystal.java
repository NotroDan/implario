package net.minecraft.client.game.model;

import net.minecraft.client.renderer.G;
import net.minecraft.entity.Entity;

public class ModelEnderCrystal extends ModelBase {

	/**
	 * The cube model for the Ender Crystal.
	 */
	private ModelRenderer cube;

	/**
	 * The glass model for the Ender Crystal.
	 */
	private ModelRenderer glass = new ModelRenderer(this, "glass");

	/**
	 * The base model for the Ender Crystal.
	 */
	private ModelRenderer base;

	public ModelEnderCrystal(float p_i1170_1_, boolean p_i1170_2_) {
		this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		this.cube = new ModelRenderer(this, "cube");
		this.cube.setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);

		if (p_i1170_2_) {
			this.base = new ModelRenderer(this, "base");
			this.base.setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
		}
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
		G.pushMatrix();
		G.scale(2.0F, 2.0F, 2.0F);
		G.translate(0.0F, -0.5F, 0.0F);

		if (this.base != null) {
			this.base.render(scale);
		}

		G.rotate(p_78088_3_, 0.0F, 1.0F, 0.0F);
		G.translate(0.0F, 0.8F + p_78088_4_, 0.0F);
		G.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		this.glass.render(scale);
		float f = 0.875F;
		G.scale(f, f, f);
		G.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		G.rotate(p_78088_3_, 0.0F, 1.0F, 0.0F);
		this.glass.render(scale);
		G.scale(f, f, f);
		G.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		G.rotate(p_78088_3_, 0.0F, 1.0F, 0.0F);
		this.cube.render(scale);
		G.popMatrix();
	}

}
