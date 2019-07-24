package net.minecraft.client.gui.settings.tabs.element;

import net.minecraft.client.MC;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.SelectorSetting;
import net.minecraft.client.settings.Settings;

import static net.minecraft.client.gui.Gui.drawRect;
import static net.minecraft.client.gui.settings.GuiSettings.*;

public class Selector implements Element {

	private static final int width = 10;

	private final Settings setting;
	private final SelectorSetting s;
	private final int split;
	private final String caption;
	private final int captionW;


	public Selector(Settings setting, String caption) {
		this.setting = setting;
		this.s = (SelectorSetting) setting.getBase();
		split = (COLUMNWIDTH - width) / s.titles.length;
		this.caption = caption;
		this.captionW = COLUMNWIDTH / 2 - BakedFont.CALIBRI.getRenderer().getStringWidth(caption) / 4 - 4 - width / 2;
	}

	@Override
	public void render(float mx, float my, boolean hovered) {

		G.pushMatrix();
		for (int i = 0; i < s.titles.length; i++) {
//			drawRect(0, 0, 1, 20, 0xff545454);
//			drawRect(split - 1, 0, split, 20, 0xff545454);
			if (i == s.state) {
				drawRect(2, 19, split - 2, 20, -1);
				drawRect(2, 0, split - 2, 19, 0xff3355aa);

			} else {
				drawRect(2, 0, split - 2, 20, COLOR1);

			}
			G.scale(2, 2, 1);
			String option = s.titles[i];
			MC.FR.drawString(option, split / 4 - MC.FR.getStringWidth(option) / 2, 1, -1);
			G.scale(0.5, 0.5, 1);
			G.translate(split, 0, 0);
		}
		G.popMatrix();

		G.color(1,1,1,1);
		BakedFont.CALIBRI.getRenderer().renderString(caption, captionW, 22, false);

	}

	@Override
	public void mouseDown(int mx, int my, int button) {

	}

	@Override
	public void mouseUp(int mx, int my, int button) {

	}

	@Override
	public void mouseDrag(int mx, int my, int button, long timeSinceLastClick) {

	}

}
