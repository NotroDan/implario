package net.minecraft.client.game.worldedit;

import net.minecraft.Utils;
import net.minecraft.client.MC;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

public class WorldEditUI {


	public static void render(float partialTicks) {
		BlockPos pos1 = WorldEdit.getPos1();
		BlockPos pos2 = WorldEdit.getPos2();
		if (pos1 != null) drawCube(pos1, 0xffff5555, partialTicks);
		if (pos2 != null) drawCube(pos2, 0xff55ff55, partialTicks);
	}

	private static void drawCube(BlockPos pos, int color, float partialTicks) {
		Utils.glColor(color);
		EntityPlayer p = MC.getPlayer();
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		Tessellator t = Tessellator.getInstance();
		WorldRenderer r = t.getWorldRenderer();

		Entity renderViewEntity = MC.i().getRenderViewEntity();

		x -= renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double) partialTicks;
		y -= renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double) partialTicks;
		z -= renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double) partialTicks;

		G.pushMatrix();
		//		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);
		//		G.depthFunc(GL11.GL_ALWAYS);
		G.disableDepth();
		GL11.glLineWidth(3);
		float ss = 0.51F;
		r.begin(3, DefaultVertexFormats.POSITION);
		r.pos(x + ss, y + ss, z + ss).endVertex();
		r.pos(x - ss, y + ss, z + ss).endVertex();
		r.pos(x - ss, y + ss, z - ss).endVertex();
		r.pos(x + ss, y + ss, z - ss).endVertex();
		r.pos(x + ss, y + ss, z + ss).endVertex();
		r.pos(x + ss, y - ss, z + ss).endVertex();
		r.pos(x - ss, y - ss, z + ss).endVertex();
		r.pos(x - ss, y + ss, z + ss).endVertex();
		r.pos(x - ss, y - ss, z + ss).endVertex();
		r.pos(x - ss, y - ss, z - ss).endVertex();
		r.pos(x - ss, y + ss, z - ss).endVertex();
		r.pos(x - ss, y - ss, z - ss).endVertex();
		r.pos(x + ss, y - ss, z - ss).endVertex();
		r.pos(x + ss, y + ss, z - ss).endVertex();
		r.pos(x + ss, y - ss, z - ss).endVertex();
		r.pos(x + ss, y - ss, z + ss).endVertex();
		t.draw();
		G.enableDepth();
		G.popMatrix();
		//		GL11.glPopAttrib();

	}

}
