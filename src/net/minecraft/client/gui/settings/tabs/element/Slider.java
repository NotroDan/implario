package net.minecraft.client.gui.settings.tabs.element;

import net.minecraft.Utils;
import net.minecraft.client.MC;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.gui.font.TrueTypeFontRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;

import static net.minecraft.client.gui.Gui.drawRect;
import static net.minecraft.client.gui.Gui.drawTriangle;
import static net.minecraft.client.gui.settings.GuiSettings.COLUMNWIDTH;
import static net.minecraft.util.Easing.ELAS_O;

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
		double setStep = s.step / (s.getMax() - s.getMin());
		while (setStep < 0.015625) setStep *= 2;
		splits = (float) (1 / setStep);
		step = length / splits;
		this.caption = caption;
//		this.captionW = BakedFont.CALIBRI.getRenderer().getStringWidth(caption) / 2;
		this.captionW = BakedFont.CALIBRI.getRenderer().getStringWidth(caption) / 2;
	}

	private long approachStart;
	private int approachFrom = -1;

	@Override
	public void render(float mx, float my, boolean hovered) {
		G.translate(3, 0, 0);
		for (int i = 0; i < splits + 1; i++) {
			int x = (int) (step * i);
			int r = i % 2;
			drawRect(x, r * 2, x + 1, -r * 2 + 12, r == 0 ? 0xffffffff : 0xff808080);
		}
		drawRect(0, 5, length, 7, -1);

		int pos = getSliderPosition();
		if (approachStart != 0) {
			int old = pos;
			pos = easeSliderPosition(pos, true);
			if (pos > length || pos < 0) {
				approachStart = 0;
				approachFrom = -1;
				pos = old;
			}
		}

		renderSlider(pos);


	}

	public void renderSlider(int pos) {

		int color = Utils.gradient(0xffff8888, 0xff88ff88, s.normalizeValue(s.value));
		drawRect(0, 4, pos, 8, color);


		int ts = 6;
		drawTriangle(pos + 1, 14, pos - ts, 14 + ts, pos + ts + 2, 14 + ts, 0xffbbbbbb);

		G.color(1, 1, 1, 1);
		String text = setting.getCaption();
		TrueTypeFontRenderer rb = BakedFont.CALIBRI.getRenderer();

		G.translate(pos, 0, 0);
		boolean b = false;
		if (getSliderPosition() + captionW + 40 > COLUMNWIDTH) b = true;
		rb.renderString(caption, b ? -26 - captionW : 15, 13, false);

		G.color(1F, 1F, 1F);

		G.scale(2, 2, 1);
		G.colorNoAlpha(color);
		MC.FR.drawString(text, -MC.FR.getStringWidth(text) / 2F + 1, 11, -2, false);
		G.scale(0.5, 0.5, 1);
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

	private float lastReportedValue = -1;
	private void set(int mx) {
		mx -= 3;
		float l = s.getMax() - s.getMin();
		float value = s.denormalizeValue((float) mx / (float) length);
		if (value != lastReportedValue) {
			int pos = getSliderPosition();
			approachFrom = approachStart != 0 ? easeSliderPosition(pos, false) : pos;
			approachStart = System.currentTimeMillis();
		}
		s.set(value);
		lastReportedValue = value;
	}

	private int easeSliderPosition(int pos, boolean check) {
		float time = (float) (System.currentTimeMillis() - approachStart) / 3000F;
		if (check && time >= 1) {
			approachFrom = -1;
			approachStart = 0;
			return pos;
		}
		time = (float) ELAS_O.ease(time);
		int value = (int) ((pos - approachFrom) * time) + approachFrom;
		if (value > length || value < 0) approachStart = System.currentTimeMillis();
		if (value > length) approachFrom = value = length;
		if (value < 0) approachFrom = value = 0;
		return value;
	}


	private int getSliderPosition() {
		return (int) (length * s.normalizeValue(s.value));
	}

}
