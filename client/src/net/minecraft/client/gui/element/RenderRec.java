package net.minecraft.client.gui.element;

import net.minecraft.client.gui.Gui;

public class RenderRec implements RenderElement {

	private final int x1, y1, x2, y2, color;

	public RenderRec(int x, int y, int height, int width, int color) {
		this.x1 = x;
		this.y1 = y;
		this.x2 = x + height;
		this.y2 = y + width;
		this.color = color;
	}

	public static void render(int x, int y, int height, int width, int color) {
		Gui.drawRect(x, y, x + height, y + width, color);
	}

	@Override
	public void render() {
		Gui.drawRect(x1, y1, x2, y2, color);
	}

}
