package net.minecraft.client.renderer;

import net.minecraft.client.MC;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityGiparrow;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class BowPathRenderer {

	private static volatile EntityGiparrow lastParsed;

	public static void render(float partialTicks) {
		EntityPlayer player = MC.getPlayer();
		if (player.getHeldItem() == null) {
			lastParsed = null;
			return;
		}
		if (player.getHeldItem().getItem() != Items.bow) {
			lastParsed = null;
			return;
		}

		double x = player.posX;
		double y = player.posY;
		// + player.getEyeHeight();
		double z = player.posZ;

		if (player == null) {
			lastParsed = null;
			return;
		}
		Vec3 vec3 = new Vec3(-0.36D, 0.03D, 0.35D);
		vec3 = vec3.rotatePitch(-(player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks) * (float) Math.PI / 180.0F);
		vec3 = vec3.rotateYaw(-(player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks) * (float) Math.PI / 180.0F);
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks + vec3.xCoord;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks + vec3.yCoord;
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks + vec3.zCoord;
		double d3 = (double) player.getEyeHeight();

		//            if (Settings.getPerspective() > 0 || entity.angler != Minecraft.getMinecraft().thePlayer)
		//            {
		//                float f9 = (entity.angler.prevRenderYawOffset + (entity.angler.renderYawOffset - entity.angler.prevRenderYawOffset) * partialTicks) * (float)Math.PI / 180.0F;
		//                double d4 = (double)MathHelper.sin(f9);
		//                double d6 = (double)MathHelper.cos(f9);
		//                double d8 = 0.35D;
		//                double d10 = 0.8D;
		//                d0 = entity.angler.prevPosX + (entity.angler.posX - entity.angler.prevPosX) * (double)partialTicks - d6 * 0.35D - d4 * 0.8D;
		//                d1 = entity.angler.prevPosY + d3 + (entity.angler.posY - entity.angler.prevPosY) * (double)partialTicks - 0.45D;
		//                d2 = entity.angler.prevPosZ + (entity.angler.posZ - entity.angler.prevPosZ) * (double)partialTicks - d4 * 0.35D + d6 * 0.8D;
		//                d3 = entity.angler.isSneaking() ? -0.1875D : 0.0D;
		//            }

		int ik = 72000 - player.itemInUseCount;
		float f = (float) ik / 20.0F;
		f = (f * f + f * 2.0F) / 3.0F;

		if ((double) f < 0.1D) {
			lastParsed = null;
			return;
		}

		if (f > 1.0F) f = 1.0F;

		EntityGiparrow g = new EntityGiparrow(player.worldObj, player, f * 2);

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);



		for (int i = 0; i < 300; i++) {
			double aX = g.posX, aY = g.posY, aZ = g.posZ;
			worldrenderer.pos(aX - x, aY - y, aZ - z).color((int) (200 * f), (int) (200 * (1 - f)), 0, 255).endVertex();
			g.onUpdate();
			if (g.destinated) break;
		}

		tessellator.draw();
		Entity e = g.entityHit;
		if (e != null) {
			MC.i().getRenderManager().renderDebugBoundingBox(e, e.posX - x, e.posY - y, e.posZ - z, e.rotationYaw, partialTicks);
		} else {
			x = g.posX - x; y = g.posY - y; z = g.posZ - z;
			float ss = 0.3F;
			worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
			worldrenderer.pos(x + ss, y + ss, z + ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x - ss, y + ss, z + ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x - ss, y + ss, z - ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x + ss, y + ss, z - ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x + ss, y + ss, z + ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x + ss, y - ss, z + ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x - ss, y - ss, z + ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x - ss, y + ss, z + ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x - ss, y - ss, z + ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x - ss, y - ss, z - ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x - ss, y + ss, z - ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x - ss, y - ss, z - ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x + ss, y - ss, z - ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x + ss, y + ss, z - ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x + ss, y - ss, z - ss).color(200, 0,0 , 255).endVertex();
			worldrenderer.pos(x + ss, y - ss, z + ss).color(200, 0,0 , 255).endVertex();
			tessellator.draw();
		}
		lastParsed = g;
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
	}

	public static void renderOverlay(int x, int y) {
		if (lastParsed == null) return;
		if (MC.getPlayer() == null) return;
		FontRenderer f = MC.getFontRenderer();
		BlockPos l = MC.getPlayer().getPosition();
		if (f == null || l == null) return;
		GlStateManager.scale(2, 2, 2);
		double sqrt = Math.sqrt(l.distanceSq(new Vec3i(lastParsed.posX, lastParsed.posY, lastParsed.posZ)));
		Gui.drawRect(x - 2, y - 1, x + 65, y + 15, 0x50202020);
		f.drawString("Расстояние: §a" + (double) (int) (sqrt * 10) / 10, x, y, 0xffffff);
		String target = lastParsed.entityHit == null ? lastParsed.inTile == null ? "§7-" : "§e" + lastParsed.inTile.getLocalizedName() : "§a" + lastParsed.entityHit.getName();
		GlStateManager.scale(0.5, 0.5, 0.5);
		f.drawString("Цель: " + target, x * 2, y * 2 + 19, 0xffffff);
	}

}
