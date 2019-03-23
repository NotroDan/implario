package net.minecraft.client;

import net.minecraft.Logger;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

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
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(0.0D, mc.displayHeight, 0.0D).tex(0.0D, 0.0D).color(32, 32, 32, 255).endVertex();
		worldrenderer.pos(mc.displayWidth, mc.displayHeight, 0.0D).tex(0.0D, 0.0D).color(32, 32, 32, 255).endVertex();
		worldrenderer.pos(mc.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(32, 32, 32, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(32, 32, 32, 255).endVertex();
		tessellator.draw();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		int j = 256;
		int k = 256;
//		this.draw((res.getScaledWidth() - j) / 2, (res.getScaledHeight() - k) / 2, 0, 0, j, k, 255, 0, 255, 255);
		G.disableLighting();
		G.disableFog();
		framebuffer.unbindFramebuffer();
		framebuffer.framebufferRender(res.getScaledWidth() * i, res.getScaledHeight() * i);
		G.enableAlpha();
		G.alphaFunc(516, 0.1F);
		drawInfo();
		MC.frame();
	}
	
	public void header() {
		int i = res.getScaleFactor();
		framebuffer = new Framebuffer(res.getScaledWidth() * i, res.getScaledHeight() * i, true);
		framebuffer.bindFramebuffer(false);
		G.matrixMode(5889);
		G.loadIdentity();
		G.ortho(0.0D, res.getScaledWidth(), res.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		G.matrixMode(5888);
		G.loadIdentity();
		G.translate(0.0F, 0.0F, -2000.0F);
		G.disableLighting();
		G.disableFog();
		G.disableDepth();
		G.enableTexture2D();
		
		mc.getTextureManager().bindTexture(logo);
	}
	
	private void drawInfo() {
		for (int i = 0; i < states.length; i++) {
			String state = i < this.state ? "§a§l\u2714§7 " + states[i] : "§f   " + states[i];
			if (i == this.state) state = "§e>  " + states[i] + "...";
			drawText(state, 5, 10 + i * 9, 0);
		}
		G.scale(4, 4, 4);
		drawText("Загрузка...", res.getScaledWidth() / 8 - 12, res.getScaledHeight() / 4 - 10, 0x36f746);
		G.scale(0.25, 0.25, 0.25);
	}
	
	public void nextState() {
		state++;
		drawLogo();
	}
	
	private void draw(int x0, int y0, int texX, int texY, int width, int height, int r, int g, int b, int a) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(x0, y0 + height, 0).tex((float) texX * f, (float) (texY + height) * f1).color(r, g, b, a).endVertex();
		worldrenderer.pos(x0 + width, y0 + height, 0.0D).tex((float) (texX + width) * f, (float) (texY + height) * f1).color(r, g, b, a).endVertex();
		worldrenderer.pos(x0 + width, y0, 0).tex((float) (texX + width) * f, (float) texY * f1).color(r, g, b, a).endVertex();
		worldrenderer.pos(x0, y0, 0).tex((float) texX * f, (float) texY * f1).color(r, g, b, a).endVertex();
		Tessellator.getInstance().draw();
	}
	
	void drawText(String s, int x, int y, int color) {
		MC.getFontRenderer().drawString(s, x, y, color);
	}
}
