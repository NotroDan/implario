package net.minecraft.client.gui.ingame;

import net.minecraft.client.MC;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.border.WorldBorder;
import optifine.Config;

/**
 * Затемнение краёв экрана
 * Эффект виньетки
 */
public class ModuleVignette implements Module {

	private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");

	/**
	 * Яркость виньетки в предыдущем кадре
	 * Медленно меняется по 1% каждый кадр
	 */
	public float prevVignetteBrightness = 1.0F;

	@Override
	public void render(ScaledResolution res, float partialTicks) {
		if (!Config.isVignetteEnabled()) {
			G.enableDepth();
			G.tryBlendFuncSeparate(770, 771, 1, 0);
			return;
		}

		float brightness = MathHelper.clamp_float(1 - MC.getPlayer().getBrightness(partialTicks), 0.0F, 1.0F);
		WorldBorder worldborder = MC.getWorld().getWorldBorder();
		float f = (float) worldborder.getClosestDistance(MC.getPlayer());
		double d0 = Math.min(worldborder.getResizeSpeed() * (double) worldborder.getWarningTime() * 1000.0D, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter()));
		double d1 = Math.max((double) worldborder.getWarningDistance(), d0);

		if ((double) f < d1) f = 1.0F - (float) ((double) f / d1);
		else f = 0.0F;

		this.prevVignetteBrightness = (float) ((double) this.prevVignetteBrightness + (double) (brightness - this.prevVignetteBrightness) * 0.01D);
		G.disableDepth();
		G.depthMask(false);
		G.tryBlendFuncSeparate(0, 769, 1, 0);

		if (f > 0.0F) G.color(0.0F, f, f, 1.0F);
		else G.color(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1.0F);

		MC.getTextureManager().bindTexture(vignetteTexPath);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(0.0D, (double) res.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
		worldrenderer.pos((double) res.getScaledWidth(), (double) res.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
		worldrenderer.pos((double) res.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
		worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
		tessellator.draw();
		G.depthMask(true);
		G.enableDepth();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.tryBlendFuncSeparate(770, 771, 1, 0);
	}

}
