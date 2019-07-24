package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.resources.Lang;

import java.util.ArrayList;
import java.util.List;

public class GuiLabel extends Gui {

	public int x;
	public int field_146174_h;
	public int field_175204_i;
	public boolean visible = true;
	protected int width;
	protected int field_146161_f;
	private List<String> lines;
	private boolean centered;
	private boolean labelBgEnabled;
	private int color;
	private int field_146169_o;
	private int field_146166_p;
	private int field_146165_q;
	private AssetsFontRenderer fontRenderer;
	private int field_146163_s;

	public GuiLabel(AssetsFontRenderer renderer, int p_i45540_2_, int x, int p_i45540_4_, int width, int p_i45540_6_, int color) {
		fontRenderer = renderer;
		field_175204_i = p_i45540_2_;
		this.x = x;
		field_146174_h = p_i45540_4_;
		this.width = width;
		field_146161_f = p_i45540_6_;
		lines = new ArrayList<>();
		centered = false;
		labelBgEnabled = false;
		this.color = color;
		field_146169_o = -1;
		field_146166_p = -1;
		field_146165_q = -1;
		field_146163_s = 0;
	}

	public void add(String line) {
		lines.add(Lang.format(line));
	}

	/**
	 * Sets the Label to be centered
	 */
	public GuiLabel setCentered() {
		centered = true;
		return this;
	}

	public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			G.enableBlend();
			G.tryBlendFuncSeparate(770, 771, 1, 0);
			drawLabelBackground(mc, mouseX, mouseY);
			int i = field_146174_h + field_146161_f / 2 + field_146163_s / 2;
			int j = i - lines.size() * 10 / 2;

			for (int k = 0; k < lines.size(); ++k) {
				if (centered) {
					drawCenteredString(fontRenderer, lines.get(k), x + width / 2, j + k * 10, color);
				} else {
					drawString(fontRenderer, lines.get(k), x, j + k * 10, color);
				}
			}
		}
	}

	protected void drawLabelBackground(Minecraft mc, int p_146160_2_, int p_146160_3_) {
		if (labelBgEnabled) {
			int i = width + field_146163_s * 2;
			int j = field_146161_f + field_146163_s * 2;
			int k = x - field_146163_s;
			int l = field_146174_h - field_146163_s;
			drawRect(k, l, k + i, l + j, field_146169_o);
			drawHorizontalLine(k, k + i, l, field_146166_p);
			drawHorizontalLine(k, k + i, l + j, field_146165_q);
			drawVerticalLine(k, l, l + j, field_146166_p);
			drawVerticalLine(k + i, l, l + j, field_146165_q);
		}
	}

}
