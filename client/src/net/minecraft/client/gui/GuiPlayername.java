package net.minecraft.client.gui;

import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.gui.element.GuiTextField;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.settings.Settings;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiPlayername extends GuiScreen {

	private GuiScreen parentScreen;
	private GuiTextField inputField, token, uuid;

	public GuiPlayername(GuiScreen parentScreenIn) {
		this.parentScreen = parentScreenIn;
	}

	@Override
	public void updateScreen() {
		inputField.updateCursorCounter();
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, (width >> 1) - 100, (height >> 2) + 108, "Установить"));
		buttonList.add(new GuiButton(1, (width >> 1) - 100, (height >> 2) + 132, "Отмена"));
		String s = mc.getSession().username;
		inputField = new GuiTextField(2, fontRendererObj, (width >> 1) - 100, 60, 200, 20);
		token = new GuiTextField(3, fontRendererObj, (width >> 1) - 100, 90, 200, 20);
		uuid = new GuiTextField(4, fontRendererObj, (width >> 1) - 100, 120, 200, 20);
		inputField.setFocused(true);
		inputField.setText(s);
		token.setText(mc.getSession().token);
		uuid.setText(mc.getSession().playerID);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled)
			if (button.id == 1) mc.displayGuiScreen(parentScreen);
			else if (button.id == 0) {
				mc.getSession().username = inputField.getText().trim();
				mc.getSession().token = token.getText().trim();
				mc.getSession().playerID = uuid.getText().trim();
				Settings.saveOptions();
				mc.displayGuiScreen(parentScreen);
			}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (inputField.isFocused()) inputField.textboxKeyTyped(typedChar, keyCode);
		else if (token.isFocused()) token.textboxKeyTyped(typedChar, keyCode);
		else if (uuid.isFocused()) uuid.textboxKeyTyped(typedChar, keyCode);
		buttonList.get(0).enabled = inputField.getText().trim().length() > 0;
		if (keyCode == 28 || keyCode == 156)
			actionPerformed(buttonList.get(0));
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		inputField.mouseClicked(mouseX, mouseY, mouseButton);
		uuid.mouseClicked(mouseX, mouseY, mouseButton);
		token.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, Lang.format("Сменить никнейм"), width >> 1, 20, 16777215);
		drawString(fontRendererObj, Lang.format("Введите ник"), (width >> 1) - 100, 47, 10526880);
		inputField.drawTextBox();
		uuid.drawTextBox();
		token.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
