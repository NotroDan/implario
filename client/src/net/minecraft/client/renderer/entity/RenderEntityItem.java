package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class RenderEntityItem extends Render<EntityItem> {

	private final RenderItem itemRenderer;
	private Random field_177079_e = new Random();

	public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
		super(renderManagerIn);
		this.itemRenderer = p_i46167_2_;
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	private int func_177077_a(EntityItem entity, double x, double y, double z, float partialTicks, IBakedModel model) {
		ItemStack itemstack = entity.getEntityItem();
		Item item = itemstack.getItem();

		if (item == null) return 0;
		boolean flag = true; //model.isGui3d();

		int i = this.func_177078_a(itemstack);
		float f = 0.25F;
		float f1 = MathHelper.sin(((float) entity.getAge() + partialTicks) / 10.0F + entity.hoverStart) * 0.1F + 0.1F;
		float f2 = model.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
		G.translate((float) x, (float) y + f1 + 0.25F * f2, (float) z);

		if (flag) {
			float f3 = (((float) entity.getAge() + partialTicks) / 20.0F + entity.hoverStart) * (180F / (float) Math.PI);
			G.rotate(f3, 0.0F, 1.0F, 0.0F);
		}

		if (!flag) {
			float f6 = -0.0F * (float) (i - 1) * 0.5F;
			float f4 = -0.0F * (float) (i - 1) * 0.5F;
			float f5 = -0.046875F * (float) (i - 1) * 0.5F;
			G.translate(f6, f4, f5);
		}

		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		return i;
	}

	private int func_177078_a(ItemStack stack) {
		int i = 1;

		if (stack.stackSize > 48) {
			i = 5;
		} else if (stack.stackSize > 32) {
			i = 4;
		} else if (stack.stackSize > 16) {
			i = 3;
		} else if (stack.stackSize > 1) {
			i = 2;
		}

		return i;
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doe
	 */
	public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
		ItemStack itemstack = entity.getEntityItem();
		this.field_177079_e.setSeed(187L);
		boolean flag = false;

		if (this.bindEntityTexture(entity)) {
			this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
			flag = true;
		}

		G.enableRescaleNormal();
		G.alphaFunc(516, 0.1F);
		G.enableBlend();
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		G.pushMatrix();
		IBakedModel ibakedmodel = this.itemRenderer.getItemModelMesher().getItemModel(itemstack);
		int i = this.func_177077_a(entity, x, y, z, partialTicks, ibakedmodel);

		for (int j = 0; j < i; ++j) {
			if (ibakedmodel.isGui3d()) {
				G.pushMatrix();

				if (j > 0) {
					float f = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f1 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
					float f2 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
					G.translate(f, f1, f2);
				}

				G.scale(0.5F, 0.5F, 0.5F);
				ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
				this.itemRenderer.renderItem(itemstack, ibakedmodel);
				G.popMatrix();
			} else {
				G.pushMatrix();
				ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
				this.itemRenderer.renderItem(itemstack, ibakedmodel);
				G.popMatrix();
				float f3 = ibakedmodel.getItemCameraTransforms().ground.scale.x;
				float f4 = ibakedmodel.getItemCameraTransforms().ground.scale.y;
				float f5 = ibakedmodel.getItemCameraTransforms().ground.scale.z;
				G.translate(0.0F * f3, 0.0F * f4, 0.046875F * f5);
			}
		}

		G.popMatrix();
		G.disableRescaleNormal();
		G.disableBlend();
		this.bindEntityTexture(entity);

		if (flag) {
			this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
		}

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityItem entity) {
		return TextureMap.locationBlocksTexture;
	}

}
