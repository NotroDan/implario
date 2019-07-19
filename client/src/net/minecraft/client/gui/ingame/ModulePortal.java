package net.minecraft.client.gui.ingame;

import net.minecraft.client.MC;
import net.minecraft.client.game.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.potion.Potion;

public class ModulePortal implements Module {

	@Override
	public void render(GuiIngame gui, float partialTicks, ScaledResolution res) {
		EntityPlayerSP p = MC.getPlayer();
		if (p.isPotionActive(Potion.confusion)) return;
		float time = p.prevTimeInPortal + (p.timeInPortal - p.prevTimeInPortal) * partialTicks;
		if (time <= 0) return;
		if (time <= 1.0F) {
			time *= time;
			time *= time;
			time = time * 0.8F + 0.2F;
		}

		G.disableAlpha();
		G.disableDepth();
		G.depthMask(false);
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		G.color(1.0F, 1.0F, 1.0F, time);
		MC.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		TextureAtlasSprite textureatlassprite = MC.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
		float f = textureatlassprite.getMinU();
		float f1 = textureatlassprite.getMinV();
		float f2 = textureatlassprite.getMaxU();
		float f3 = textureatlassprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(0.0D, (double) res.getScaledHeight(), -90.0D).tex((double) f, (double) f3).endVertex();
		worldrenderer.pos((double) res.getScaledWidth(), (double) res.getScaledHeight(), -90.0D).tex((double) f2, (double) f3).endVertex();
		worldrenderer.pos((double) res.getScaledWidth(), 0.0D, -90.0D).tex((double) f2, (double) f1).endVertex();
		worldrenderer.pos(0.0D, 0.0D, -90.0D).tex((double) f, (double) f1).endVertex();
		tessellator.draw();
		G.depthMask(true);
		G.enableDepth();
		G.enableAlpha();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
