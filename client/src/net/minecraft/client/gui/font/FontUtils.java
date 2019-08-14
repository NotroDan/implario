package net.minecraft.client.gui.font;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import optifine.Config;
import optifine.CustomColors;

import static net.minecraft.client.Minecraft.getMinecraft;

public class FontUtils {

	// Массив со значениями цветовых кодов от 0 до f, а также теней этих кодов
	static int[] colorCodes = new int[32];

	public static int getColorCode(char c) {
		int code = "0123456789abcdef".indexOf(c);
		return code < 0 ? 0xffffff : Config.isCustomColors() ? CustomColors.getTextColor(code, colorCodes[code]) : colorCodes[code];
	}

	static {

		for (int i = 0; i < 32; ++i) {
			int j = (i / 8 & 1) * 0x55;
			int k = (i / 4 & 1) * 0xaa + j;
			int l = (i / 2 & 1) * 0xaa + j;
			int i1 = (i & 1) * 0xaa + j;

			if (i == 6) k += 85;

			if (i >= 16) {
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			colorCodes[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}
	}

	public static void underline(float charWidth, float charHeight, float factor) {
		rect(factor - 1, charWidth + factor, charHeight - 1, charHeight);
	}

	public static void strike(float charWidth, int height) {
		height /= 2f;
		rect(0, charWidth, height - 1, height);
	}

	public static void rect(float x1, float x2, float y1, float y2) {
		Tessellator t = getMinecraft().preloader == null ? Tessellator.getInstance() : getMinecraft().preloader.getTessellator();
		WorldRenderer r = t.getWorldRenderer();
		G.disableTexture2D();
		r.begin(7, DefaultVertexFormats.POSITION);
		r.pos(x1, y1, 0.0D).endVertex();
		r.pos(x1, y2, 0.0D).endVertex();
		r.pos(x2, y2, 0.0D).endVertex();
		r.pos(x2, y1, 0.0D).endVertex();
		t.draw();
		G.enableTexture2D();
	}

}
