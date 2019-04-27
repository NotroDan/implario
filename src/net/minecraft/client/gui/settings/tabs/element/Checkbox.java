package net.minecraft.client.gui.settings.tabs.element;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.settings.Settings;

import static net.minecraft.client.gui.settings.GuiSettings.COLOR1;

public class Checkbox implements Element {

	public final String caption;
	public final Settings setting;


	public Checkbox(String caption, Settings setting) {
		this.caption = caption;
		this.setting = setting;
	}

	public void render() {
		int color = setting.b() ? 0xFF_aaffaa : 0xFF_aaaaaa;
		Gui.drawRect(3, 3, 47, 27, color);
		Gui.drawRect(4, 4, 46, 26, COLOR1);
		Gui.drawRect(setting.b() ? 26 : 6, 6, setting.b() ? 44 : 25, 24, 0xffeeeeee);

		BakedFont.CALIBRI.getRenderer().renderString(caption, 57, 1, false);

	}

}
