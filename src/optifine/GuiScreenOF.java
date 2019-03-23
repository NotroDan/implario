package optifine;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.element.GuiButton;

import java.io.IOException;
import java.util.List;

public class GuiScreenOF extends GuiScreen {

	protected void actionPerformedRightClick(GuiButton p_actionPerformedRightClick_1_) throws IOException {
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (mouseButton != 1) return;
		GuiButton guibutton = getSelectedButton(this.buttonList, mouseX, mouseY);

		if (guibutton != null && guibutton.enabled) {
			guibutton.playPressSound(this.mc.getSoundHandler());
			this.actionPerformedRightClick(guibutton);
		}
	}

	@Override
	public void initGui() {}

	public static GuiButton getSelectedButton(List<GuiButton> buttons, int x, int y) {
		for (int i = 0; i < buttons.size(); ++i) {
			GuiButton guibutton = buttons.get(i);
			if (!guibutton.visible) continue;

			int j = GuiVideoSettings.getButtonWidth(guibutton);
			int k = GuiVideoSettings.getButtonHeight(guibutton);

			if (x >= guibutton.xPosition && y >= guibutton.yPosition && x < guibutton.xPosition + j && y < guibutton.yPosition + k)
				return guibutton;
		}

		return null;
	}

}
