package net.minecraft.client.gui.keymap;

import net.minecraft.client.gui.Gui;

public class ActionButtonRender implements IButtonRender {

	@Override
	public boolean render(Gui gui, KeyboardButton button, MetaKey metaKey) {
		KeyBinding b = KeyBinding.KEYMAP.get(button.getKey());
		if (b == null) return false;
		gui.drawTexturedModalRect(button.getWidth() / 2 - 8, button.getHeight() / 2 - 8, b.ordinal() * 16, 0, 16, 16);
		return true;
	}

}
