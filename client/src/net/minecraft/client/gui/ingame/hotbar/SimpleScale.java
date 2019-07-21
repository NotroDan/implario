package net.minecraft.client.gui.ingame.hotbar;

import net.minecraft.client.MC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.element.Colors;
import net.minecraft.util.MathHelper;

public class SimpleScale extends Scale {

	// Является ли данная шкала утолщённой по высоте
	private final boolean wide;

	public SimpleScale(int x, int y, String name, boolean reverse, int fill, boolean wide) {
		super(x, y, name, reverse, fill);
		this.wide = wide;
	}

	@Override
	protected void render(int value, int max, int additional) {

		int hm = wide ? 2 : 1; // Множитель высоты

		int x2 = this.x;
		int y2 = this.y;
		int elements = max + additional;
		int fill = this.fill * 10;

		int height = ((elements - 1) / fill + 1) * 8;
		if (wide) height = height * 2 - 2;
		Gui.drawRect(x2, y2 - height, x2 + 84, y2 + 1, Colors.DARK);
		Gui.drawRect(x2 + 71, y2 - 8 * hm + 1, x2 + 83, y2, Colors.GRAY);
		for (int i = 0; i < elements; i++) {
			boolean odd = (i & 1) != 0;
			int color = i >= max ? Colors.YELLOW : i < value ? Colors.RED : Colors.GRAY;
			int heart = i % fill;
			int x = (heart >> 1) * 7;
			int y = (odd ? 1 + 3 * hm : 0) + i / fill * 8 * hm;
			Gui.drawRect(x2 + 1 + x, y2 - y - 3 * hm, x2 + 1 + x + 6, y2 - y, color);
		}
		String s = String.valueOf(value + additional);
		MC.FR.drawString(s, x2 + 77 - MC.FR.getStringWidth(s) / 2, y2 - (wide ? 14 : 8), additional == 0 ? Colors.RED : Colors.YELLOW);
		if (wide) MC.FR.drawString("HP", x2 + 74, y2 - 8, Colors.RED);
	}

}
