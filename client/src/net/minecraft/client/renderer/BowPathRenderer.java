package net.minecraft.client.renderer;

import net.minecraft.Utils;
import net.minecraft.client.MC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.simulant.Simulant;
import net.minecraft.entity.projectile.simulant.SimulantArrow;
import net.minecraft.entity.projectile.simulant.SimulantSimpleProjectile;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Util;
import net.minecraft.util.Vec3i;
import org.lwjgl.opengl.GL11;

public class BowPathRenderer {

	private static volatile Simulant lastParsed;
	private static volatile double dst;

	public static void render(float partialTicks) {
		Player player = MC.getPlayer();
		if (player.getHeldItem() == null) {
			lastParsed = null;
			return;
		}

		Simulant g, s;
		float f = 1;

		if (player.getHeldItem().getItem() == Items.bow) {

			int ik = 72000 - player.itemInUseCount;
			f = (float) ik / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if ((double) f < 0.1D) {
				lastParsed = null;
				return;
			}

			if (f > 1.0F) f = 1.0F;

			g = new SimulantArrow(player.worldObj, player, f * 2, 0);
			s = new SimulantArrow(player.worldObj, player, f * 2, 1);
			g.power = EnchantmentHelper.getEnchantmentLevel(Enchantments.power.effectId, player.getHeldItem());

		} else if (player.getHeldItem().getItem() == Items.snowball ||
				player.getHeldItem().getItem() == Items.ender_pearl ||
				player.getHeldItem().getItem() == Items.egg) {
			g = new SimulantSimpleProjectile(player.worldObj, player, 0);
			s = new SimulantSimpleProjectile(player.worldObj, player, 1);
			f = 0.3F;
		} else {
			lastParsed = null;
			return;
		}

		RenderManager rm = MC.i().getRenderManager();
		rm.renderEngine.bindTexture(RenderArrow.arrowTextures);
		double x = player.posX;
		double y = player.posY;
		double z = player.posZ;

		G.pushMatrix();
		G.disableBlend();
		GL11.glLineWidth(1);


		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);

		G.disableLighting();
		G.disableTexture2D();

		double dx = 0, dy = 0, dz = 0;
		for (int i = 0; i < 300; i++) {
			double aX = g.posX, aY = g.posY, aZ = g.posZ;
			worldrenderer.pos(aX - x, aY - y, aZ - z).color((int) (200 * f), (int) (200 * (1 - f)), 0, 255).endVertex();
			g.onUpdate();
			s.onUpdate();
			if (g.destinated) break;
			if (!s.destinated) {
				dx = g.posX - s.posX;
				dy = g.posY - s.posY;
				dz = g.posZ - s.posZ;
			}
		}
		tessellator.draw();

		double inacc = Math.sqrt(dx * dx + dy * dy + dz * dz);
		g.inacc = (short) (inacc * 10);
		double sx = g.posX - x;
		double sy = g.posY - y;
		double sz = g.posZ - z;
		dst = Math.sqrt(sx * sx + sy * sy + sz * sz);
		G.enableTexture2D();
		renderArrow(g, 0, sx, sy, sz, inacc);
		G.disableTexture2D();
		Entity e = g.entityHit;
		if (e != null) {
			rm.renderDebugBoundingBox(e, e.posX - x, e.posY - y, e.posZ - z, e.rotationYaw, partialTicks);
		} else {
			//			x = g.posX - x;
			//			y = g.posY - y;
			//			z = g.posZ - z;
			//			float ss = 0.3F;
			//			worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
			//			worldrenderer.pos(x + ss, y + ss, z + ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x - ss, y + ss, z + ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x - ss, y + ss, z - ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x + ss, y + ss, z - ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x + ss, y + ss, z + ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x + ss, y - ss, z + ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x - ss, y - ss, z + ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x - ss, y + ss, z + ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x - ss, y - ss, z + ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x - ss, y - ss, z - ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x - ss, y + ss, z - ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x - ss, y - ss, z - ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x + ss, y - ss, z - ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x + ss, y + ss, z - ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x + ss, y - ss, z - ss).color(200, 0, 0, 255).endVertex();
			//			worldrenderer.pos(x + ss, y - ss, z + ss).color(200, 0, 0, 255).endVertex();
			//			tessellator.draw();
		}
		lastParsed = g;
		//		GlStateManager.enableLighting();
		G.enableTexture2D();
		G.enableBlend();
		G.popMatrix();
	}

	public static void renderOverlay(int x, int y) {
		if (lastParsed == null) return;
		if (MC.getPlayer() == null) return;
		AssetsFontRenderer f = MC.getFontRenderer();
		BlockPos l = MC.getPlayer().getPosition();
		if (f == null || l == null) return;
		G.scale(2, 2, 2);
		Gui.drawRect(x - 2, y - 1, x + 65, y + 35, 0x50202020);
		f.drawString("Расстояние: §a" + (double) (int) (dst * 10) / 10, x, y, 0xffffff);
		String target = lastParsed.entityHit == null ? lastParsed.inTile == null ? "§7-" : "§e" + lastParsed.inTile.getLocalizedName() : "§a" + lastParsed.entityHit.getName();
		G.scale(0.5, 0.5, 0.5);
		f.drawString("Цель: " + target, x * 2, y * 2 + 19, 0xffffff);
		f.drawString("Отклонение: §e" + (float) lastParsed.inacc / 10F, x * 2, y * 2 + 28, 0xffffff);
		f.drawString("Время полёта: §e" + (float) lastParsed.ticksInAir / 20F, x * 2, y * 2 + 37, 0xffffff);
		if (lastParsed.damage != 0) f.drawString("Базовый урон: §e" + lastParsed.damage, x * 2, y * 2 + 46, 0xffffff);
	}

	public static void renderArrow(Simulant entity, float partialTicks, double x, double y, double z, double inacc) {
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.pushMatrix();
		G.translate(x, y, z);
		G.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		G.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		int i = 0;
		float f = 0.0F;
		float f1 = 0.5F;
		float f2 = (float) (0 + i * 10) / 32.0F;
		float f3 = (float) (5 + i * 10) / 32.0F;
		float f4 = 0.0F;
		float f5 = 0.15625F;
		float f6 = (float) (5 + i * 10) / 32.0F;
		float f7 = (float) (10 + i * 10) / 32.0F;
		float f8 = 0.05625F;
		//		G.enableRescaleNormal();

		G.pushMatrix();
		G.disableTexture2D();
		G.disableDepth();
		G.disableCull();
		G.rotate(90, 0, 1, 0);
		G.color(0.5f, 0.5f, 1, 0.5f);
		Utils.drawHexagonOutline(tessellator, inacc);
		G.color(1f, 0.5f, 0.5f, 0.25f);
		Utils.drawHexagonOutline(tessellator, inacc * 2);
		G.color(1, 1, 1, 1);
		G.enableCull();
		G.enableDepth();
		G.enableTexture2D();
		G.popMatrix();
		G.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		G.scale(f8, f8, f8);
		G.translate(-4.0F, 0.0F, 0.0F);
		//		GL11.glNormal3f(f8, 0.0F, 0.0F);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(-7.0D, -2.0D, -2.0D).tex((double) f4, (double) f6).endVertex();
		worldrenderer.pos(-7.0D, -2.0D, 2.0D).tex((double) f5, (double) f6).endVertex();
		worldrenderer.pos(-7.0D, 2.0D, 2.0D).tex((double) f5, (double) f7).endVertex();
		worldrenderer.pos(-7.0D, 2.0D, -2.0D).tex((double) f4, (double) f7).endVertex();
		tessellator.draw();
		//		GL11.glNormal3f(-f8, 0.0F, 0.0F);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(-7.0D, 2.0D, -2.0D).tex((double) f4, (double) f6).endVertex();
		worldrenderer.pos(-7.0D, 2.0D, 2.0D).tex((double) f5, (double) f6).endVertex();
		worldrenderer.pos(-7.0D, -2.0D, 2.0D).tex((double) f5, (double) f7).endVertex();
		worldrenderer.pos(-7.0D, -2.0D, -2.0D).tex((double) f4, (double) f7).endVertex();
		tessellator.draw();

		for (int j = 0; j < 4; ++j) {
			G.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glNormal3f(0.0F, 0.0F, f8);
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldrenderer.pos(-8.0D, -2.0D, 0.0D).tex((double) f, (double) f2).endVertex();
			worldrenderer.pos(8.0D, -2.0D, 0.0D).tex((double) f1, (double) f2).endVertex();
			worldrenderer.pos(8.0D, 2.0D, 0.0D).tex((double) f1, (double) f3).endVertex();
			worldrenderer.pos(-8.0D, 2.0D, 0.0D).tex((double) f, (double) f3).endVertex();
			tessellator.draw();
		}
		//		G.disableRescaleNormal();
		G.popMatrix();
	}

}
