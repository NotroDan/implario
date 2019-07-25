package net.minecraft.client.gui.settings.tabs.element;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.element.Animation;
import net.minecraft.client.gui.element.Animator;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;

import static net.minecraft.client.gui.Gui.drawRect;
import static net.minecraft.client.gui.settings.GuiSettings.COLOR1;
import static net.minecraft.client.gui.settings.GuiSettings.COLORF;

public class Switch implements Element {

	private static final long TIME = 100;
	public final String caption;
	public final Settings setting;


	public Switch(String caption, Settings setting) {
		this.caption = caption;
		this.setting = setting;
	}

	private static final Animator animator = new Animator(0, 0,
			new Animation(0, 0, 0xFF_aaaaaa, 0, 0, 0xFF_aaffaa, TIME,
					p -> drawRect(3, 3, 47, 27, -2)),
			new Animation(0, 0, COLOR1, 0, 0, COLORF, TIME,
					p -> drawRect(4, 4, 46, 26, -2)),
			new Animation(6, 6, 0, 26, 6, 0, TIME,
					p -> drawRect(0, 0, 18, 18, 0xffeeeeee))
	);


	private Animator.Cycle anim;

	// Альтернативный метод рисования, пока выключил
	private static void drawButton(float p) {
		G.pushMatrix();
		G.translate(9, 0, 0);
		//		G.rotate(p * 90, 0, 0, 1);
		float s = p * 2F - 1;
		float k = (-s * s + 1) * 0.5f;
		G.scale(1 - k, 1 - k, 0);
		drawRect(-9, 0, 9, 18, 0xffeeeeee);
		G.popMatrix();
	}

	private boolean hovered;

	public void render(float mx, float my, boolean hovered) {

		this.hovered = hovered;
		int color = setting.b() ? 0xFF_aaffaa : 0xFF_aaaaaa;
		if (anim != null) {
			boolean b = anim.draw(System.currentTimeMillis());
			if (b) anim = null;
		}
		if (anim == null) {
			Gui.drawRect(3, 3, 47, 27, color);
			Gui.drawRect(4, 4, 46, 26, setting.b() ? COLORF : COLOR1);
			if (hovered) G.color(1, 1, 0.6F);
			int x = setting.b() ? 26 : 6;
			Gui.drawRect(x, 6, x + 18, 6 + 18, 0xffeeeeee);
		}

		if (hovered) G.color(1, 1, 0.6F);
		BakedFont.CALIBRI.getRenderer().renderString(caption, 57, 1, false);

	}

	@Override
	public void mouseUp(int mx, int my, int button) {
		setting.toggle();
		anim = animator.new Cycle(System.currentTimeMillis(), !setting.b());
	}

}
