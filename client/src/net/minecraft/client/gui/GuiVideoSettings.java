package net.minecraft.client.gui;

import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.settings.Settings;
import optifine.Config;
import optifine.Lang;

import java.io.IOException;

public class GuiVideoSettings extends GuiScreen {

	protected String screenTitle = "Video Settings";
	private GuiScreen parentGuiScreen;


	public GuiVideoSettings(GuiScreen parentScreenIn) {
		this.parentGuiScreen = parentScreenIn;
	}

	public static int getButtonWidth(GuiButton p_getButtonWidth_0_) {
		return p_getButtonWidth_0_.width;
	}

	public static int getButtonHeight(GuiButton p_getButtonHeight_0_) {
		return p_getButtonHeight_0_.height;
	}

	public static void drawGradientRect(GuiScreen p_drawGradientRect_0_, int p_drawGradientRect_1_, int p_drawGradientRect_2_, int p_drawGradientRect_3_, int p_drawGradientRect_4_,
										int p_drawGradientRect_5_, int p_drawGradientRect_6_) {
		p_drawGradientRect_0_.drawGradientRect(p_drawGradientRect_1_, p_drawGradientRect_2_, p_drawGradientRect_3_, p_drawGradientRect_4_, p_drawGradientRect_5_, p_drawGradientRect_6_);
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
	 * window resizes, the buttonList is cleared beforehand.
	 */
	public void initGui() {

	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			int i = Settings.GUI_SCALE.i();

			//            if (button.id < 200 && button instanceof GuiOptionButton)
			//            {
			//                this.guiGameSettings.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
			//                button.displayString = this.guiGameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
			//            }

			if (button.id == 200) {
				Settings.saveOptions();
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}

			if (Settings.GUI_SCALE.i() != i) {
				ScaledResolution scaledresolution = new ScaledResolution(this.mc);
				int j = scaledresolution.getScaledWidth();
				int k = scaledresolution.getScaledHeight();
				this.setWorldAndResolution(this.mc, j, k);
			}

			if (button.id == 201) {
				Settings.saveOptions();
				//                GuiDetailSettingsOF guidetailsettingsof = new GuiDetailSettingsOF(this, this.guiGameSettings);
				//                this.mc.displayGuiScreen(guidetailsettingsof);
			}

			if (button.id == 202) {
				Settings.saveOptions();
				//                GuiQualitySettingsOF guiqualitysettingsof = new GuiQualitySettingsOF(this, this.guiGameSettings);
				//                this.mc.displayGuiScreen(guiqualitysettingsof);
			}

			if (button.id == 211) {
				Settings.saveOptions();
				//                GuiAnimationSettingsOF guianimationsettingsof = new GuiAnimationSettingsOF(this, this.guiGameSettings);
				//                this.mc.displayGuiScreen(guianimationsettingsof);
			}

			if (button.id == 212) {
				Settings.saveOptions();
				//                GuiPerformanceSettingsOF guiperformancesettingsof = new GuiPerformanceSettingsOF(this, this.guiGameSettings);
				//                this.mc.displayGuiScreen(guiperformancesettingsof);
			}

			if (button.id == 222) {
				Settings.saveOptions();
				//                GuiOtherSettingsOF guiothersettingsof = new GuiOtherSettingsOF(this, this.guiGameSettings);
				//                this.mc.displayGuiScreen(guiothersettingsof);
			}

			if (button.id == 231) {
				if (Config.isAntialiasing() || Config.isAntialiasingConfigured()) {
					Config.showGuiMessage(Lang.get("of.message.shaders.aa1"), Lang.get("of.message.shaders.aa2"));
					return;
				}

				if (Config.isAnisotropicFiltering()) {
					Config.showGuiMessage(Lang.get("of.message.shaders.af1"), Lang.get("of.message.shaders.af2"));
					return;
				}

				if (Config.isFastRender()) {
					Config.showGuiMessage(Lang.get("of.message.shaders.fr1"), Lang.get("of.message.shaders.fr2"));
					return;
				}

				Settings.saveOptions();
				//                GuiShaders guishaders = new GuiShaders(this, this.guiGameSettings);
				//                this.mc.displayGuiScreen(guishaders);
			}
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 16777215);
		String s = Config.getVersion();
		String s1 = "HD_U";

		if (s1.equals("HD")) {
			s = "OptiFine HD H8";
		}

		if (s1.equals("HD_U")) {
			s = "OptiFine HD H8 Ultra";
		}

		if (s1.equals("L")) {
			s = "OptiFine H8 Light";
		}

		this.drawString(this.fontRendererObj, s, 2, this.height - 10, 8421504);
		String s2 = "Minecraft 1.8.8";
		int i = this.fontRendererObj.getStringWidth(s2);
		this.drawString(this.fontRendererObj, s2, this.width - i - 2, this.height - 10, 8421504);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
