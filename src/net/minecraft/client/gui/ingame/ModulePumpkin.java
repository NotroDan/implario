package net.minecraft.client.gui.ingame;

import net.minecraft.client.MC;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.Settings;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Когда игрок надевает на голову тыкву, часть взора перекрывается этим модулем
 */
public class ModulePumpkin implements Module {

	private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");

	@Override
	public void render(ScaledResolution res, float partialTicks) {

		ItemStack itemstack = MC.getPlayer().inventory.armorItemInSlot(3);
		if (Settings.getPerspective() != 0 || itemstack == null || itemstack.getItem() != Item.getItemFromBlock(Blocks.pumpkin)) return;

		G.disableDepth();
		G.depthMask(false);
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.disableAlpha();
		MC.getTextureManager().bindTexture(pumpkinBlurTexPath);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(0, res.getScaledHeight(), -90).tex(0, 1).endVertex();
		worldrenderer.pos(res.getScaledWidth(), res.getScaledHeight(), -90).tex(1, 1).endVertex();
		worldrenderer.pos(res.getScaledWidth(), 0, -90).tex(1, 0).endVertex();
		worldrenderer.pos(0, 0, -90).tex(0, 0).endVertex();
		tessellator.draw();
		G.depthMask(true);
		G.enableDepth();
		G.enableAlpha();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
