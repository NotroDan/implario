package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.settings.KeyBinding;

import java.io.IOException;

public class GuiControls extends GuiScreen {
	//    private static final GameSettings.Options[] optionsArr = new GameSettings.Options[] {GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN};

	public KeyBinding editing = null;
	public long time;
	protected String screenTitle = "Управление";
	/**
	 * A reference to the screen object that created this. Used for navigating between screens.
	 */
	private GuiScreen parentScreen;
	private GuiKeyBindingList keyBindingList;
	private GuiButton buttonReset;

	public GuiControls(GuiScreen screen) {
		this.parentScreen = screen;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		this.keyBindingList = new GuiKeyBindingList(this, this.mc);
		this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, Lang.format("gui.done")));
		this.buttonList.add(this.buttonReset = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, Lang.format("controls.resetAll")));
		this.screenTitle = Lang.format("controls.title");
		int i = 0;
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.keyBindingList.handleMouseInput();
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 200) this.mc.displayGuiScreen(this.parentScreen);
		else if (button.id == 201) {
			for (KeyBinding keybinding : KeyBinding.values()) keybinding.setKeyCode(keybinding.getKeyCodeDefault());
			KeyBinding.resetKeyBindingArrayAndHash();
		}
		//        else if (button.id < 100 && button instanceof GuiOptionButton) {
		//            this.options.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
		//            button.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
		//        }
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (this.editing != null) {
			editing.setKeyCode(mouseButton - 100);
			this.editing = null;
			KeyBinding.resetKeyBindingArrayAndHash();
		} else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	/**
	 * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
	 */
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
			super.mouseReleased(mouseX, mouseY, state);
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.editing != null) {
			if (keyCode == 1) editing.setKeyCode(0);
			else if (keyCode != 0) editing.setKeyCode(keyCode);
			else if (typedChar > 0) editing.setKeyCode(typedChar + 256);

			this.editing = null;
			this.time = Minecraft.getSystemTime();
			KeyBinding.resetKeyBindingArrayAndHash();
		} else super.keyTyped(typedChar, keyCode);
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 8, 16777215);
		boolean flag = true;

		for (KeyBinding keybinding : KeyBinding.values()) {
			if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault()) {
				flag = false;
				break;
			}
		}

		this.buttonReset.enabled = !flag;
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
