package net.minecraft.client.gui;

import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.resources.Lang;

import java.io.IOException;

public class GuiMemoryErrorScreen extends GuiScreen {

	@Override
	public void initGui() {
		buttonList.clear();
		buttonList.add(new GuiButton(0, (width >> 1) - 155, (height >> 2) + 132, Lang.format("gui.toTitle")));
		buttonList.add(new GuiButton(1, (width >> 1) + 5, (height >> 2) + 132, Lang.format("menu.quit")));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0)
			mc.displayGuiScreen(new GuiMainMenu());
		else if (button.id == 1)
			mc.shutdown();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		int width = (this.width >> 1) - 140, height = this.height >> 2;
		drawCenteredString(fontRendererObj, "Out of memory!", this.width >> 1, height - 40, 16777215);
		drawString(fontRendererObj, "Minecraft has run out of memory.",
				width, height, 10526880);
		drawString(fontRendererObj, "This could be caused by a bug in the game or by the",
				width, height + 18, 10526880);
		drawString(fontRendererObj, "Java Virtual Machine not being allocated enough",
				width, height + 27, 10526880);
		drawString(fontRendererObj, "memory.",
				width, height + 36, 10526880);
		drawString(fontRendererObj, "To prevent level corruption, the current game has quit.",
				width, height + 54, 10526880);
		drawString(fontRendererObj, "We\'ve tried to free up enough memory to let you go back to",
				width, height + 63, 10526880);
		drawString(fontRendererObj, "the main menu and back to playing, but this may not have worked.",
				width, height + 72, 10526880);
		drawString(fontRendererObj, "Please restart the game if you see this message again.",
				width, height + 81, 10526880);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
