package net.minecraft.client.gui.keymap;

import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiKeymap extends GuiScreen {

	private final KeyboardRender keyboardRender = new KeyboardRender();

	@Override
	public void initGui() {

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		keyboardRender.render();

	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
	}

}
