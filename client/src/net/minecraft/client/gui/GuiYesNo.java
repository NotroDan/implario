package net.minecraft.client.gui;

import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.resources.Lang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiYesNo extends GuiScreen {

	private final List<String> text = new ArrayList<>();
	protected GuiYesNoCallback parentScreen;
	protected String confirmButtonText;
	protected String cancelButtonText;
	protected int parentButtonClickedId;
	private String messageLine1;
	private String messageLine2;
	private int ticksUntilEnable;

	public GuiYesNo(GuiYesNoCallback parentScreen, String line1, String line2, int parentButtonClickedId) {
		this.parentScreen = parentScreen;
		this.messageLine1 = line1;
		this.messageLine2 = line2;
		this.parentButtonClickedId = parentButtonClickedId;
		this.confirmButtonText = Lang.format("gui.yes");
		this.cancelButtonText = Lang.format("gui.no");
	}

	public GuiYesNo(GuiYesNoCallback parentScreen, String line1, String line2, String confirmText, String cancelText, int parentButtonClickedId) {
		this.parentScreen = parentScreen;
		this.messageLine1 = line1;
		this.messageLine2 = line2;
		this.confirmButtonText = confirmText;
		this.cancelButtonText = cancelText;
		this.parentButtonClickedId = parentButtonClickedId;
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, (width >> 1) - 155, height / 6 + 96, confirmButtonText));
		buttonList.add(new GuiButton(1, (width >> 1) + 5, height / 6 + 96, cancelButtonText));
		text.clear();
		text.addAll(fontRendererObj.listFormattedStringToWidth(messageLine2, width - 50));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		parentScreen.confirmClicked(button.id == 0, parentButtonClickedId);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, messageLine1, width >> 1, 70, 16777215);
		int i = 90;

		for (String s : text) {
			drawCenteredString(fontRendererObj, s, this.width >> 1, i, 16777215);
			i += fontRendererObj.getFontHeight();
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void setButtonDelay(int buttonDelay) {
		this.ticksUntilEnable = buttonDelay;

		for (GuiButton guibutton : this.buttonList)
			guibutton.enabled = false;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (--this.ticksUntilEnable == 0)
			for (GuiButton guibutton : this.buttonList)
				guibutton.enabled = true;
	}

}
