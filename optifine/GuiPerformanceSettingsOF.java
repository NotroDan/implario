package optifine;

import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.gui.element.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.Settings;

public class GuiPerformanceSettingsOF extends GuiScreen {

	private GuiScreen prevScreen;
	protected String title;

	private TooltipManager tooltipManager = new TooltipManager(this);

	public GuiPerformanceSettingsOF(GuiScreen p_i52_1_) {
		this.prevScreen = p_i52_1_;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {
		this.title = I18n.format("of.options.performanceTitle");
		this.buttonList.clear();

//		for (int i = 0; i < enumOptions.length; ++i) {
//			GameSettings.Options gamesettings$options = enumOptions[i];
//			int j = this.width / 2 - 155 + i % 2 * 160;
//			int k = this.height / 6 + 21 * (i / 2) - 12;
//
//			if (!gamesettings$options.getEnumFloat()) {
//				this.buttonList.add(new GuiOptionButtonOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options, this.settings.getKeyBinding(gamesettings$options)));
//			} else {
//				this.buttonList.add(new GuiOptionSliderOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
//			}
//		}
//
//		this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id < 200 && button instanceof GuiOptionButton) {
//				this.settings.setOptionValue(((GuiOptionButton) button).returnEnumOptions(), 1);
//				button.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
			}

			if (button.id == 200) {
				Settings.saveOptions();
				this.mc.displayGuiScreen(this.prevScreen);
			}
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
	}

}
