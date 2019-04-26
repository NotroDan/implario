package net.minecraft.client.gui.font;

import net.minecraft.client.renderer.G;
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
	public float renderChar(char c, boolean bold, boolean italic) {

		TrueTypeFont font = bases[getType(bold, italic)];
		font.glHeader();
		int translate = font.drawChar(c, 0, 0);
		font.glFooter();

		return translate;

	}

	private static int getType(boolean bold, boolean italic) {
		int style = 0;
		if (bold) style += Font.BOLD;
		if (italic) style += Font.ITALIC;
		return style;
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

	@Override
	public int getShadowOffset() {
		return 2;
	}

}