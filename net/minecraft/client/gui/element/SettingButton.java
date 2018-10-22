package net.minecraft.client.gui.element;

import net.minecraft.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.SelectorSetting;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;
import net.minecraft.client.settings.ToggleSetting;

import java.text.DecimalFormat;

public class SettingButton extends GuiButton {

	public Settings settings;
	public boolean updateGraphics = false;

	public SettingButton(Settings settings, int x, int y) {
		super(settings.ordinal(), x, y, getCaption(settings));
		this.settings = settings;
	}

	public SettingButton(Settings settings, int x, int y, int widthIn, int heightIn) {
		super(settings.ordinal(), x, y, widthIn, heightIn, getCaption(settings));
		this.settings = settings;
	}

	public SettingButton updateGraphics() {
		updateGraphics = true;
		return this;
	}

	public static String getCaption(Settings s) {
		if (s.getBase() instanceof ToggleSetting) return s.getBase().caption + ": " + Utils.bool(s.b());
		if (s.getBase() instanceof SliderSetting) {
			String value;
			if (((SliderSetting) s.getBase()).getMax() == 1) value = (int) (s.f() * 100) + "%";
			else if (((SliderSetting) s.getBase()).step == 1) value = (int) s.f() + "";
			else value = new DecimalFormat("#0.00").format(s.f());
			return s.getBase().caption + ": §e" + value;
		}
		if (s.getBase() instanceof SelectorSetting) {
			SelectorSetting ss = (SelectorSetting) s.getBase();
			return s.getBase().caption + ": §e" + ss.titles[ss.state];
		}
		return null;
	}

	public void click(GuiScreen screen) {
		if (settings.getBase() instanceof ToggleSetting) settings.toggle();
		else if (settings.getBase() instanceof SelectorSetting) ((SelectorSetting) settings.getBase()).next();
		displayString = getCaption(settings);
		if (updateGraphics) {
			ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
			drawRect(0, 0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0xd0202020);
			drawCenteredString(Minecraft.getMinecraft().fontRendererObj, "Применение настроек...",
					scaledresolution.getScaledWidth() / 2, scaledresolution.getScaledHeight() / 2, -1);
			Minecraft.getMinecraft().renderGlobal.loadRenderers();
		}
		settings.change();
	}
}
