package net.minecraft.client.gui.keymap;

import net.minecraft.client.MC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.G;

public class LetterButtonRender implements IButtonRender {

	private int color;
	private boolean small;

	public LetterButtonRender(boolean small, int color) {
		this.small = small;
		this.color = color;
	}

	public void setSmall(boolean small) {
		this.small = small;
	}

	public boolean isSmall() {
		return small;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void toggleSmall() {
		small = !small;
	}

	@Override
	public boolean render(Gui gui, KeyboardButton btn, MetaKey metaKey) {

		String text = btn.getKey().getCaption();
		int width = MC.FR.getStringWidth(text);

		if (small) {
			gui.drawShadowlessCenteredString(MC.FR, btn.getKey().getCaption(), btn.getWidth() / 2, btn.getRenderY() + 4, color);
			return true;
		}
		if (width * 2 >= btn.getWidth()) {
			text = btn.getKey().getShortAlternative();
			width = MC.FR.getStringWidth(text);
		}
		G.scale(2, 2, 2);
		if (width % 2 != 1) G.translate(0.5f,0, 0);
//		MC.FR.drawString(text, (float) (btn.getWidth() / 4 - width / 2),
//				btn.getRenderY() + 2, 0xeeeeee, false);
		gui.drawShadowlessCenteredString(MC.FR, text, btn.getWidth() / 4, btn.getRenderY() + 2, color);
		if (width % 2 == 1) G.translate(-0.5f,0, 0);
		G.scale(0.5, 0.5, 0.5);
		return true;
	}

}
