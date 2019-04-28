package net.minecraft.client.gui.settings.tabs.element;

import net.minecraft.client.MC;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.gui.font.TrueTypeFontRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;

import static net.minecraft.client.gui.Gui.drawRect;
import static net.minecraft.client.gui.Gui.drawTriangle;
import static net.minecraft.client.gui.settings.GuiSettings.COLUMNWIDTH;

public class Slider implements Element {

	private final Settings setting;
	private final SliderSetting s;
	private final float splits;
	private final float step;
	private final int length;
	private final String caption;
	private final int captionW;

	public Slider(Settings setting, String caption) {
		this.setting = setting;
		s = (SliderSetting) setting.getBase();
		length = COLUMNWIDTH - 23;
		splits = (s.getMax() - s.getMin()) / s.step;
		step = length / splits;
		this.caption = caption;
		this.captionW = BakedFont.CALIBRI.getRenderer().getStringWidth(caption) / 2;
	}

	@Override
	public void render(float mx, float my) {
		for (int i = 0; i < splits + 1; i++) {
			int x = (int) (step * i);
			int r = i % 2;
			drawRect(x+ 3, r * 2, x + 4, -r * 2 + 12, r == 0 ? 0xffffffff : 0xff808080);
		}
		drawRect(3, 5, 3 + length, 7, -1);
		int pos = (int) (3 + length * s.getPercents());
		drawRect(3, 4, pos, 8, 0xff66ff66);


		int ts = 6;
		drawTriangle(pos + 1, 14, pos - ts, 14 + ts, pos + ts + 2, 14 + ts, 0xffbbbbbb);

		G.color(1, 1, 1, 1);
		String text = setting.getCaption();

		G.translate(pos, 0, 0);
		G.scale(2, 2, 1);
		MC.FR.drawString(text, -MC.FR.getStringWidth(text) / 2F + 1, 11, -1, false);
		G.scale(0.5, 0.5, 1);
		TrueTypeFontRenderer rb = BakedFont.CALIBRI.getRenderer();
		boolean b = false;
		if (pos + captionW + 40 > COLUMNWIDTH) b = true;
		rb.renderString(caption, b ? - 25 - captionW : 15, 13, false);
		G.translate(-pos, 0, 0);



	}

	@Override
	public void mouseDown(int mx, int my, int button) {
		set(mx);
	}

	@Override
	public void mouseUp(int mx, int my, int button) {

	}

	@Override
	public void mouseDrag(int mx, int my, int button, long timeSinceLastClick) {
		set(mx);
	}

	private void set(int mx) {
		mx -= 3;
		if (mx < 0) return;
		float l = s.getMax() - s.getMin();
		s.set(s.denormalizeValue((float) mx / (float) length));
	}

}
