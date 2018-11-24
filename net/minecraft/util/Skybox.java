package net.minecraft.util;

import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class Skybox implements Background {

	private final Minecraft mc;
	private final ResourceLocation[] tiles;
	private final GuiScreen screen;
	private int panoramaTimer;
	private ResourceLocation background;
	private DynamicTexture viewportTexture;

	//	private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[] {
//			new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"),
//			new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"),
//			new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")
//	};


	public Skybox(ResourceLocation[] tiles, ResourceLocation background, DynamicTexture viewport, GuiScreen parent) {
		mc = MC.i();
		this.tiles = tiles;
		this.background = background;
		this.viewportTexture = viewport;
		this.screen = parent;
	}

	public void updateScreen() {
		panoramaTimer++;
	}

	public void update(ResourceLocation background, DynamicTexture viewport) {
		this.background = background;
		this.viewportTexture = viewport;
	}

	private void drawPanorama(float patrialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		int i = 8;

		for (int j = 0; j < i * i; ++j) {
			GlStateManager.pushMatrix();
			float f = ((float) (j % i) / (float) i - 0.5F) / 64.0F;
			float f1 = ((float) (j / i) / (float) i - 0.5F) / 64.0F;
			float f2 = 0.0F;
			GlStateManager.translate(f, f1, f2);
			GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + patrialTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-((float) this.panoramaTimer + patrialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int k = 0; k < 6; ++k) {
				GlStateManager.pushMatrix();

				if (k == 1) {
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 3) {
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (k == 4) {
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (k == 5) {
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				this.mc.getTextureManager().bindTexture(tiles[k]);
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				int l = 255 / (j + 1);
				float f3 = 0.0F;
				worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
				worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
				worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
				worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
	}

	/**
	 * Rotate and blurs the skybox view in the main menu
	 */
	private void rotateAndBlurSkybox() {
		this.mc.getTextureManager().bindTexture(background);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.disableAlpha();
		int i = 3;

		for (int j = 0; j < i; ++j) {
			float f = 1.0F / (float) (j + 1);
			int k = screen.width;
			int l = screen.height;
			float f1 = (float) (j - i / 2) / 256.0F;
			worldrenderer.pos((double) k, (double) l, (double) screen.zLevel).tex((double) (0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos((double) k, 0.0D, (double) screen.zLevel).tex((double) (1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos(0.0D, 0.0D, (double) screen.zLevel).tex((double) (1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos(0.0D, (double) l, (double) screen.zLevel).tex((double) (0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
		}

		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	@Override
	public void render(float partialTicks) {
		this.mc.getFramebuffer().unbindFramebuffer();
		GlStateManager.viewport(0, 0, 256, 256);
		this.drawPanorama(partialTicks);
		for (int i = 0; i < 7; i++) this.rotateAndBlurSkybox();
		this.mc.getFramebuffer().bindFramebuffer(true);
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		float f = screen.width > screen.height ? 120.0F / (float) screen.width : 120.0F / (float) screen.height;
		float f1 = (float) screen.height * f / 256.0F;
		float f2 = (float) screen.width * f / 256.0F;
		int i = screen.width;
		int j = screen.height;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(0.0D, (double) j, (double) screen.zLevel).tex((double) (0.5F - f1), (double) (0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos((double) i, (double) j, (double) screen.zLevel).tex((double) (0.5F - f1), (double) (0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos((double) i, 0.0D, (double) screen.zLevel).tex((double) (0.5F + f1), (double) (0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos(0.0D, 0.0D, (double) screen.zLevel).tex((double) (0.5F + f1), (double) (0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}

}
