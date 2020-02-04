package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.game.model.ModelBiped;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.Player;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItem implements LayerRenderer<EntityLivingBase> {

	private final RendererLivingEntity<?> livingEntityRenderer;

	public LayerHeldItem(RendererLivingEntity<?> livingEntityRendererIn) {
		this.livingEntityRenderer = livingEntityRendererIn;
	}

	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		ItemStack itemstack = entitylivingbaseIn.getHeldItem();

		if (itemstack != null) {
			G.pushMatrix();

			if (this.livingEntityRenderer.getMainModel().isChild) {
				float f = 0.5F;
				G.translate(0.0F, 0.625F, 0.0F);
				G.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
				G.scale(f, f, f);
			}

			((ModelBiped) this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F);
			G.translate(-0.0625F, 0.4375F, 0.0625F);

			if (entitylivingbaseIn instanceof Player && ((Player) entitylivingbaseIn).fishEntity != null) {
				itemstack = new ItemStack(Items.fishing_rod, 0);
			}

			Item item = itemstack.getItem();
			Minecraft minecraft = Minecraft.get();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item).getRenderType() == 2) {
				G.translate(0.0F, 0.1875F, -0.3125F);
				G.rotate(20.0F, 1.0F, 0.0F, 0.0F);
				G.rotate(45.0F, 0.0F, 1.0F, 0.0F);
				float f1 = 0.375F;
				G.scale(-f1, -f1, f1);
			}

			if (entitylivingbaseIn.isSneaking()) {
				G.translate(0.0F, 0.203125F, 0.0F);
			}

			minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON);
			G.popMatrix();
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}

}
