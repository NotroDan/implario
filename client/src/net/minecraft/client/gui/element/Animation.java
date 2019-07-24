package net.minecraft.client.gui.element;

import net.minecraft.Utils;
import net.minecraft.client.renderer.G;
import net.minecraft.util.Easing;

public class Animation {

	private final int startX, startY, startC, finalC;
	private final AnimationPlayer player;
	private final long time;

	private final int dX, dY;
	private final boolean color;
	private final Easing easing;

	public Animation(int startX, int startY, int startC,
					 int finalX, int finalY, int finalC,
					 long time, AnimationPlayer player) {
		this(startX, startY, startC, finalX, finalY, finalC, time, player, null);
	}

	public Animation(int startX, int startY, int startC,
					 int finalX, int finalY, int finalC,
					 long time, AnimationPlayer player, Easing easing) {
		this.startX = startX;
		this.startY = startY;
		this.startC = startC;
		color = startC != 0 && finalC != 0;
		this.finalC = finalC;
		this.player = player;
		this.time = time;
		this.easing = easing;

		dX = finalX - startX;
		dY = finalY - startY;
	}

	public long getTime() {
		return time;
	}

	float getPercentage(long start, long t) {
		float f = (float) (t - start) / (float) time;
		if (easing != null) return f >= 1 ? f : (float) easing.ease(f);
		return f;
	}


	void draw(float f) {
		int x = startX + (int) ((float) dX * f);
		int y = startY + (int) ((float) dY * f);
		G.translate(x, y, 0);
		if (color) Utils.glColor(Utils.gradient(finalC, startC, f));
		player.draw(f);
		G.translate(-x, -y, 0);
	}

	public void draw(long start, long t) {
		draw(getPercentage(start, t));
	}

	@FunctionalInterface
	public interface AnimationPlayer {

		void draw(float percentage);

	}


}
