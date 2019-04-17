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

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen()
	{
		inputField.updateCursorCounter();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Установить"));
		buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, "Отмена"));
		String s = mc.getSession().username;
		inputField = new GuiTextField(2, fontRendererObj, width / 2 - 100, 60, 200, 20);
		token = new GuiTextField(3, fontRendererObj, width / 2 - 100, 90, 200, 20);
		uuid = new GuiTextField(4, fontRendererObj, width / 2 - 100, 120, 200, 20);
		inputField.setFocused(true);
		inputField.setText(s);
		token.setText(mc.getSession().token);
		uuid.setText(mc.getSession().playerID);
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id == 1) this.mc.displayGuiScreen(this.parentScreen);
			else if (button.id == 0) {
				mc.getSession().username = inputField.getText().trim();
				mc.getSession().token = token.getText().trim();
				mc.getSession().playerID = uuid.getText().trim();
				Settings.saveOptions();
				this.mc.displayGuiScreen(this.parentScreen);
			}
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (inputField.isFocused()) this.inputField.textboxKeyTyped(typedChar, keyCode);
		if (token.isFocused()) this.token.textboxKeyTyped(typedChar, keyCode);
		if (uuid.isFocused()) this.uuid.textboxKeyTyped(typedChar, keyCode);
		this.buttonList.get(0).enabled = this.inputField.getText().trim().length() > 0;
		if (keyCode == 28 || keyCode == 156) {
			this.actionPerformed(this.buttonList.get(0));
		}
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
		this.uuid.mouseClicked(mouseX, mouseY, mouseButton);
		this.token.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, Lang.format("Сменить никнейм"), this.width / 2, 20, 16777215);
		this.drawString(this.fontRendererObj, Lang.format("Введите ник"), this.width / 2 - 100, 47, 10526880);
		this.inputField.drawTextBox();
		this.uuid.drawTextBox();
		this.token.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
