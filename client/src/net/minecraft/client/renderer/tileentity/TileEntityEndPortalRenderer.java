package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;

import java.nio.FloatBuffer;
import java.util.Random;

public class TileEntityEndPortalRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {

	private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
	public static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random field_147527_e = new Random(31100L);
	FloatBuffer field_147528_b = GLAllocation.createDirectFloatBuffer(16);
	static FloatBuffer f = GLAllocation.createDirectFloatBuffer(16);

	public void renderTileEntityAt(TileEntityEndPortal te, double x, double y, double z, float partialTicks, int destroyStage) {
		float f = (float) this.rendererDispatcher.entityX;
		float f1 = (float) this.rendererDispatcher.entityY;
		float f2 = (float) this.rendererDispatcher.entityZ;
		G.disableLighting();
		field_147527_e.setSeed(31100L);
		float f3 = 0.75F;

		for (int i = 0; i < 16; ++i) {
			G.pushMatrix();
			float f4 = (float) (16 - i);
			float f5 = 0.0625F;
			float f6 = 1.0F / (f4 + 1.0F);

			if (i == 0) {
				this.bindTexture(END_SKY_TEXTURE);
				f6 = 0.1F;
				f4 = 65.0F;
				f5 = 0.125F;
				G.enableBlend();
				G.blendFunc(770, 771);
			}

			if (i >= 1) {
				this.bindTexture(END_PORTAL_TEXTURE);
			}

			if (i == 1) {
				G.enableBlend();
				G.blendFunc(1, 1);
				f5 = 0.5F;
			}

			float f7 = (float) -(y + (double) f3);
			float f8 = f7 + (float) ActiveRenderInfo.getPosition().yCoord;
			float f9 = f7 + f4 + (float) ActiveRenderInfo.getPosition().yCoord;
			float f10 = f8 / f9;
			f10 = (float) (y + (double) f3) + f10;
			G.translate(f, f10, f2);
			G.texGen(G.TexGen.S, 9217);
			G.texGen(G.TexGen.T, 9217);
			G.texGen(G.TexGen.R, 9217);
			G.texGen(G.TexGen.Q, 9216);
			G.func_179105_a(G.TexGen.S, 9473, this.func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
			G.func_179105_a(G.TexGen.T, 9473, this.func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
			G.func_179105_a(G.TexGen.R, 9473, this.func_147525_a(0.0F, 0.0F, 0.0F, 1.0F));
			G.func_179105_a(G.TexGen.Q, 9474, this.func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
			G.enableTexGenCoord(G.TexGen.S);
			G.enableTexGenCoord(G.TexGen.T);
			G.enableTexGenCoord(G.TexGen.R);
			G.enableTexGenCoord(G.TexGen.Q);
			G.popMatrix();
			G.matrixMode(5890);
			G.pushMatrix();
			G.loadIdentity();
			G.translate(0.0F, (float) (Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
			G.scale(f5, f5, f5);
			G.translate(0.5F, 0.5F, 0.0F);
			G.rotate((float) (i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			G.translate(-0.5F, -0.5F, 0.0F);
			G.translate(-f, -f2, -f1);
			f8 = f7 + (float) ActiveRenderInfo.getPosition().yCoord;
			G.translate((float) ActiveRenderInfo.getPosition().xCoord * f4 / f8, (float) ActiveRenderInfo.getPosition().zCoord * f4 / f8, -f1);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			float f11 = (field_147527_e.nextFloat() * 0.5F + 0.1F) * f6;
			float f12 = (field_147527_e.nextFloat() * 0.5F + 0.4F) * f6;
			float f13 = (field_147527_e.nextFloat() * 0.5F + 0.5F) * f6;

			if (i == 0) {
				f11 = f12 = f13 = 1.0F * f6;
			}
			worldrenderer.pos(x, y + (double) f3, z).color(f11, f12, f13, 1.0F).endVertex();
			worldrenderer.pos(x, y + (double) f3, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
			worldrenderer.pos(x + 1.0D, y + (double) f3, z + 1.0D).color(f11, f12, f13, 1.0F).endVertex();
			worldrenderer.pos(x + 1.0D, y + (double) f3, z).color(f11, f12, f13, 1.0F).endVertex();
			tessellator.draw();
			G.popMatrix();
			G.matrixMode(5888);
			this.bindTexture(END_SKY_TEXTURE);
		}

		G.disableBlend();
		G.disableTexGenCoord(G.TexGen.S);
		G.disableTexGenCoord(G.TexGen.T);
		G.disableTexGenCoord(G.TexGen.R);
		G.disableTexGenCoord(G.TexGen.Q);
		G.enableLighting();
	}

	public static void portal(int w, int h) {
		//		GlStateManager.disableLighting();
		field_147527_e.setSeed(31100L);
		float f3 = 0.75F;

		for (int i = 0; i < 16; ++i) {
			G.pushMatrix();
			float f4 = (float) (16 - i);
			float f5 = 0.0625F;
			float f6 = 1.0F / (f4 + 1.0F);

			if (i == 0) {
				Minecraft.get().getTextureManager().bindTexture(END_SKY_TEXTURE);
				f6 = 0.1F;
				f4 = 65.0F;
				f5 = 0.125F;
				G.enableBlend();
				G.blendFunc(770, 771);
			}

			if (i >= 1) {
				Minecraft.get().getTextureManager().bindTexture(END_PORTAL_TEXTURE);
			}

			if (i == 1) {
				G.enableBlend();
				G.blendFunc(1, 1);
				f5 = 0.5F;
			}

			G.texGen(G.TexGen.S, 9217);
			G.texGen(G.TexGen.T, 9217);
			G.texGen(G.TexGen.R, 9217);
			G.texGen(G.TexGen.Q, 9216);
			G.func_179105_a(G.TexGen.S, 9473, f0(1.0F, 0.0F, 0.0F, 0.0F));
			G.func_179105_a(G.TexGen.T, 9473, f0(0.0F, 0.0F, 1.0F, 0.0F));
			G.func_179105_a(G.TexGen.R, 9473, f0(0.0F, 0.0F, 0.0F, 1.0F));
			G.func_179105_a(G.TexGen.Q, 9474, f0(0.0F, 1.0F, 0.0F, 0.0F));
			G.enableTexGenCoord(G.TexGen.S);
			G.enableTexGenCoord(G.TexGen.T);
			G.enableTexGenCoord(G.TexGen.R);
			G.enableTexGenCoord(G.TexGen.Q);
			G.popMatrix();
			G.matrixMode(5890);
			G.pushMatrix();
			G.loadIdentity();
			G.translate(0.0F, (float) (Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
			G.scale(f5, f5, f5);
			//			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			//			GlStateManager.rotate((float) (i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			//			GlStateManager.translate(-0.5F, -0.5F, 0.0F);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
			float f11 = (field_147527_e.nextFloat() * 0.5F + 0.1F) * f6;
			float f12 = (field_147527_e.nextFloat() * 0.5F + 0.4F) * f6;
			float f13 = (field_147527_e.nextFloat() * 0.5F + 0.5F) * f6;

			if (i == 0) {
				f11 = f12 = f13 = 1.0F * f6;
			}

			worldrenderer.pos(0, h, -1).color(f11, f12, f13, 1.0F).endVertex();
			worldrenderer.pos(w, h, 0).color(f11, f12, f13, 1.0F).endVertex();
			worldrenderer.pos(w, 0, 0).color(f11, f12, f13, 1.0F).endVertex();
			worldrenderer.pos(0, 0, -1).color(f11, f12, f13, 1.0F).endVertex();
			tessellator.draw();
			G.popMatrix();
			G.matrixMode(5888);
			Minecraft.get().getTextureManager().bindTexture(END_SKY_TEXTURE);
		}

		G.disableBlend();
		G.disableTexGenCoord(G.TexGen.S);
		G.disableTexGenCoord(G.TexGen.T);
		G.disableTexGenCoord(G.TexGen.R);
		G.disableTexGenCoord(G.TexGen.Q);
		//		GlStateManager.enableLighting();
	}

	private FloatBuffer func_147525_a(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
		this.field_147528_b.clear();
		this.field_147528_b.put(p_147525_1_).put(p_147525_2_).put(p_147525_3_).put(p_147525_4_);
		this.field_147528_b.flip();
		return this.field_147528_b;
	}

	private static FloatBuffer f0(float a, float b, float c, float d) {
		f.clear();
		f.put(a).put(b).put(c).put(d);
		f.flip();
		return f;
	}

}
