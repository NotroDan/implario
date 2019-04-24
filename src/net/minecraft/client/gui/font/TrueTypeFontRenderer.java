package net.minecraft.client.gui.font;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.TrueTypeFont;

import java.awt.*;

public class TrueTypeFontRenderer implements IFontRenderer {

	private final TrueTypeFont[] bases = new TrueTypeFont[4];
	private final String fontname;
	private final int size;

	public TrueTypeFontRenderer(String fontname, int size) {
		FontUtils.class.getCanonicalName();
		this.fontname = fontname;
		this.size = size;
		load(Font.PLAIN, 0);
		load(Font.BOLD, 1);
		load(Font.ITALIC, 2);
		load(Font.BOLD + Font.ITALIC, 3);
//		for (TrueTypeFont basis : bases) System.out.println(basis.getFont());


	}

	private void load(int c, int i) {
		bases[i] = new TrueTypeFont(new Font(fontname, c, size), true);
	}

	public int getSize() {
		return size;
	}

	public String getFontname() {
		return fontname;
	}

	@Override
	public int getFontHeight() {
		return bases[0].getHeight();
	}

	@Override
	public int drawString(String s, float x, float y, int color, boolean shadow) {
		GlStateManager.pushMatrix();
		boolean coloring = false;
		float strikeStart = -1, underStart = -1;
		byte style = 0;
		char[] charArray = s.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '§' && !coloring) {
				coloring = true;
				continue;
			}
			if (coloring) {
				coloring = false;
				int colorCode = "0123456789abcdef".indexOf(c);
				if (colorCode < 0) {
					switch (c) {
						case 'l': style ^= Font.BOLD; break;
						case 'o': style ^= Font.ITALIC; break;
						case 'r': style = 0; G.color(1, 1, 1); break;
					}
				} else {
					style = 0;
					color = FontUtils.colorCodes[colorCode];
					glColorNoAlpha(color);
				}
				continue;
			}

			TrueTypeFont font = bases[style];
			font.glHeader();
			int translate = font.drawChar(c, x, y);
			font.glFooter();
			G.translate(translate, 0, 0);

		}
		GlStateManager.popMatrix();
		return 0;
	}


	public static void glColor(int color) {

		float f3 = (float) (color >> 24 & 255) / 255.0F;
		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		G.color(f, f1, f2, f3);
	}
	public static void glColorNoAlpha(int color) {

		float f = (float) (color >> 16 & 255) / 255.0F;
		float f1 = (float) (color >> 8 & 255) / 255.0F;
		float f2 = (float) (color & 255) / 255.0F;

		G.color(f, f1, f2, 1);
	}

	public TrueTypeFont getPlainFont() {
		return bases[0];
	}

}
