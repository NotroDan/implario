package net.minecraft.client.gui;

import net.minecraft.Logger;
import net.minecraft.client.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.game.shader.Framebuffer;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class Preloader {
	
	public static final String[] states = {
			"Соединение с графическим ядром",
			"Загрузка настроек",
			"Загрузка стандартного ресурс-пака",
			"Запуск обработчика чанков",
			"Загрузка звукового ядра",
			"Загрузка звуков",
			"Проверка готовности систем",
			"Загрузка текстур блоков",
			"Запуск движка рендеринга",
			"Загрузка моделей блоков",
			"Обработка моделей блоков",
			"Рендер предметов",
			"Рендер мобов",
			"Рендер блоков",
			"Компоновка глобального рендера",
			"Загрузка достижений",
			"Можно играть!"
	};
	
	private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");

	private final ScaledResolution res;
	private final ResourceLocation logo;
	private final Minecraft mc = MC.i();
	private volatile int state = 3;
	private Framebuffer framebuffer;
	private final Tessellator t = new Tessellator(2097152);

	public Preloader(ScaledResolution res, DefaultResourcePack rp, TextureManager txtmgr) {
		this.res = res;
		ResourceLocation loc = null;
		InputStream inputstream = null;
		try {
			inputstream = rp.getInputStream(locationMojangPng);
			loc = txtmgr.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(inputstream)));
			txtmgr.bindTexture(loc);
		} catch (IOException ioexception) {
			Logger.getInstance().error("Unable to load logo: " + locationMojangPng, ioexception);
		} finally {
			IOUtils.closeQuietly(inputstream);
		}
		logo = loc;
		header();
	}
	
	public void drawLogo() {
		int i = res.getScaleFactor();
		
		G.disableAlpha();
//		Tessellator tessellator = Tessellator.getInstance();
//		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
//		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
//		worldrenderer.pos(0.0D, mc.displayHeight, 0.0D).tex(0.0D, 0.0D).color(32, 32, 32, 255).endVertex();
//		worldrenderer.pos(mc.displayWidth, mc.displayHeight, 0.0D).tex(0.0D, 0.0D).color(32, 32, 32, 255).endVertex();
//		worldrenderer.pos(mc.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(32, 32, 32, 255).endVertex();
//		worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(32, 32, 32, 255).endVertex();
//		tessellator.draw();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		int j = 256;
		int k = 256;
//		this.draw((res.getScaledWidth() - j) / 2, (res.getScaledHeight() - k) / 2, 0, 0, j, k, 255, 0, 255, 255);
		G.disableLighting();
		G.disableFog();
		framebuffer.unbindFramebuffer();
		framebuffer.framebufferRender(res.getScaledWidth() * i, res.getScaledHeight() * i);
		G.enableAlpha();
//		G.alphaFunc(516, 0F);

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);     // Clear Screen And Depth Buffer
		drawInfo();

		MC.frame();
	}
	
	public void header() {
		int i = res.getScaleFactor();
		framebuffer = new Framebuffer(res.getScaledWidth() * i, res.getScaledHeight() * i, true);
		framebuffer.bindFramebuffer(false);

		G.matrixMode(GL11.GL_PROJECTION);                        // Select The Projection Matrix
		G.loadIdentity();                                   // Reset The Projection Matrix
		G.ortho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, -1, 1);//1000.0D, 3000.0D);
		G.matrixMode(GL11.GL_MODELVIEW);                         // Select The Modelview Matrix
		G.loadIdentity(); // Reset The Modelview Matrix
		G.disableLighting();
		G.enableTexture2D();
		GL11.glDisable(GL11.GL_DITHER);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		G.enableNormalize();
		G.enableBlend();
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // selects blending method
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function

		BakedFont.CALIBRI_SMALL.getRenderer();
		BakedFont.CALIBRI.getRenderer();

		mc.getTextureManager().bindTexture(logo);
	}
	
	private void drawInfo() {

		G.enableTexture2D();
		G.color(0, 1, 1, 1);
		G.translate(300, 300, 0);
//		Utils.drawHexagonOutline(t, 20);
		G.translate(100, 0, 0);
//		Utils.drawCircle(t,100, 30, state * 30);
		G.enableBlend();
		G.translate(-400, -300, 0);
		for (int i = 0; i < states.length; i++) {
			String state;
			int offset = 0;
			int y = 10 + i * BakedFont.VERDANA.getRenderer().getFontHeight();
			if (i < this.state) {
				state = "§7 " + states[i];
				offset = BakedFont.ARIAL.getRenderer().renderString("§a\u2714 ", 5, y, true);
			}
			else {
				state = "§f   " + states[i];
			}
			if (i == this.state) state = "§e>  §o§n§m§l" + states[i] + "...";
			BakedFont.VERDANA.getRenderer().renderString(state, 5 + offset, y, true);

//			drawText(state, 5, 10 + i * BakedFont.VERDANA.getRenderer().getFontHeight(), 0);
		}
//		G.disableFog();
//		G.disableDepth();
		GlStateManager.pushMatrix();
		G.scale(16, 16, 16);
//		MC.getFontRenderer().drawString("§f§l§n§o" + System.currentTimeMillis() + "", 3, 3, 0xffff0000);
		GlStateManager.popMatrix();
//		drawText("Загрузка...", res.getScaledWidth() / 8 - 12, res.getScaledHeight() / 4 - 10, 0x36f746);
	}
	
	public void nextState() {
		state++;
	}
	
	private void draw(int x0, int y0, int texX, int texY, int width, int height, int r, int g, int b, int a) {
//		float f = 0.00390625F;
//		float f1 = 0.00390625F;
//		WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
//		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
//		worldrenderer.pos(x0, y0 + height, 0).tex((float) texX * f, (float) (texY + height) * f1).color(r, g, b, a).endVertex();
//		worldrenderer.pos(x0 + width, y0 + height, 0.0D).tex((float) (texX + width) * f, (float) (texY + height) * f1).color(r, g, b, a).endVertex();
//		worldrenderer.pos(x0 + width, y0, 0).tex((float) (texX + width) * f, (float) texY * f1).color(r, g, b, a).endVertex();
//		worldrenderer.pos(x0, y0, 0).tex((float) texX * f, (float) texY * f1).color(r, g, b, a).endVertex();
//		Tessellator.getInstance().draw();
	}

	public void dissolve() {
		state = 0;
	}

	public Tessellator getTesselator() {
		return t;
	}

}
