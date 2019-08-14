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
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class Preloader {

	public static final String[] states = {
			"Соединение с графическим ядром",
			"Загрузка настроек",
			"Загрузка стандартного ресурс-пака",
			"Запуск обработчика чанков",
			"Загрузка звукового ядра",
			"Проверка готовности систем",
			"Запуск движка рендеринга",
			"Загрузка моделей блоков",
			"Загрузка вариантов моделей",
			"Загрузка проверки моделей",
			"Загрузка спрайтов",
			"Запекание моделей предметов",
			"Запекание моделей блоков",
			"Рендер предметов",
			"Рендер мобов",
			"Рендер блоков",
			"Компоновка глобального рендера",
			"Можно играть!"
	};

	private final ScaledResolution res;
	private final Minecraft mc = MC.i();
	private volatile int state = 3;
	private final Tessellator t = new Tessellator(2097152);

	public Preloader(ScaledResolution res, DefaultResourcePack rp, TextureManager txtmgr) {
		this.res = res;
		header();
	}

	public void drawLogo() {
		int i = res.getScaleFactor();

		G.disableAlpha();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		int j = 256;
		int k = 256;
		G.disableLighting();
		G.disableFog();
		G.enableAlpha();
		GL11.glClearColor(0x1p-4f, 0x1p-4f, 0x1p-4f, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		drawInfo();

		MC.frame();
	}

	public void header() {

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

	}

	private void drawInfo() {
		G.enableTexture2D();
		G.color(0, 1, 1, 1);
		G.translate(300, 300, 0);
		G.translate(100, 0, 0);
		G.enableBlend();
		G.translate(-400, -300, 0);
		for (int i = 0; i < states.length; i++) {
			String state;
			int offset = 0;
			int y = 10 + i * BakedFont.VERDANA.getRenderer().getFontHeight();
			if (i < this.state) {
				state = "§7 " + states[i];
				offset = BakedFont.ARIAL.getRenderer().renderString("§a\u2714 ", 5, y, true);
			} else {
				state = "§f   " + states[i];
			}
			if (i == this.state) state = "§e>  " + states[i] + "...";
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
