package net.minecraft.client.gui.font;

import net.minecraft.Utils;
import net.minecraft.client.resources.TrueTypeFont;

import java.awt.Font;

public class SimpleTTFontRenderer implements IFontRenderer {

	private final TrueTypeFont font;

	public SimpleTTFontRenderer(String font, int size, int style) {
		this.font = new TrueTypeFont(new Font(font, style, size), true);
	}

	public SimpleTTFontRenderer(TrueTypeFont font) {
		this.font = font;
	}

	public TrueTypeFont getFont() {
		return font;
	}

	@Override
	public int getFontHeight() {
		return font.getHeight();
	}

	@Override
	public int drawString(String s, float x, float y, int color, boolean shadow) {
		Utils.glColor(color);
		return font.drawString(x, y, s);
	}

}
