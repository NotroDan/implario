package net.minecraft.client.keystrokes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;

public class KeyStroke {

	public final KeyBinding key;
	public int x, y;

	public KeyStroke(KeyBinding key, int x, int y) {
		this.key = key;
		this.x = x;
		this.y = y;
	}

	public void render(Gui screen) {
		Gui.drawRect(x, y, x + 15, y + 12, key.isKeyDown() ? 0xd0eeeeee : 0xc0202020);
		screen.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, key.getKeyDisplayString(), x + 7, y + 6, -1);
	}

	@Override
	public int hashCode() {
		return key.getKeyCode();
	}

}
