package net.minecraft.client.gui;

import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.resources.Lang;

import java.io.IOException;

public class GuiErrorScreen extends GuiScreen {

	private String readable;
	private String exception;

	public GuiErrorScreen(String readable, String exception) {
		this.readable = readable;
		this.exception = exception;
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, width / 2 - 100, 140, Lang.format("gui.cancel")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawGradientRect(0, 0, width, height, -12574688, -11530224);
		drawCenteredString(fontRendererObj, readable, width / 2, 90, 16777215);
		drawCenteredString(fontRendererObj, exception, width / 2, 110, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		mc.displayGuiScreen(null);
	}
}
