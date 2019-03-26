package net.minecraft.client.gui.font;

import net.minecraft.Utils;
import net.minecraft.client.resources.TrueTypeFont;

import java.awt.*;

public class TrueTypeFontRenderer implements IFontRenderer {

	private final TrueTypeFont[] bases = new TrueTypeFont[4];
	private final String fontname;
	private final int size;
	private byte currentFont;

	public TrueTypeFontRenderer(String fontname, int size) {
		this.fontname = fontname;
		this.size = size;
		load(Font.PLAIN, 0);
		load(Font.BOLD, 1);
		load(Font.ITALIC, 2);
		load(Font.BOLD + Font.ITALIC, 3);

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
		currentFont = 0;
		boolean coloring = false;
		float strikeStart = -1, underStart = -1;
		byte style;
		for (char c : s.toCharArray()) {
			if (c == '§' && !coloring) {
				coloring = true;
				continue;
			}
			if (coloring) {
				coloring = false;
				int colorCode = "0123456789abcdef".indexOf(c);
				if (colorCode < 0) {

				} else {
					color = FontUtils.colorCodes[colorCode];
					Utils.glColorNoAlpha(color);
				}
				continue;
			}

		}
		return 0;
	}

}
