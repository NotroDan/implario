package net.minecraft.client.keystrokes;

import net.minecraft.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.Settings;

public class KeyStroke {

	public final KeyBinding key;
	private float size;
	public int x, y;

	public KeyStroke(KeyBinding key, int x, int y, float size) {
		this.key = key;
		this.x = x;
		this.y = y;
		this.size = size;
	}

	public void render(Gui screen) {
		G.pushMatrix();
		G.scale(size, size, size);
		if (Settings.RAINBOW_SHIT.b()) {
			Gui.drawRect(x, y, x + 16, y + 16, Utils.rainbowGradient((int) (Minecraft.getSystemTime() % 1536)));
			Gui.drawRect(x + 1, y + 1, x + 15, y + 15, key.isKeyDown() ? 0xd0eeeeee : 0xc0202020);
		} else Gui.drawRect(x, y, x + 16, y + 16, key.isKeyDown() ? 0xd0eeeeee : 0xc0202020);
		screen.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, key.getKeyDisplayString(), x + 8, y + 5, -1);
		G.popMatrix();
	}

	@Override
	public int hashCode() {
		return key.getKeyCode();
	}

}
