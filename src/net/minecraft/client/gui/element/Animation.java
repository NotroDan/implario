package net.minecraft.client.gui.element;

import net.minecraft.client.renderer.G;
import net.minecraft.util.Easings;

public class Animation {
	
	private final int startX, startY, startC, finalX, finalY, finalC;
	private final AnimationPlayer player;
	private final long time;
	
	private final int dX, dY, dC;
	private final boolean quadEase;
	
	public Animation(int startX, int startY, int startC,
	                 int finalX, int finalY, int finalC,
	                 long time, AnimationPlayer player) {
		this(startX, startY, startC, finalX, finalY, finalC, time, player, false);
	}
	public Animation(int startX, int startY, int startC,
	                 int finalX, int finalY, int finalC,
	                 long time, AnimationPlayer player, boolean quadEase) {
		this.startX = startX;
		this.startY = startY;
		this.startC = startC;
		this.finalX = finalX;
		this.finalY = finalY;
		this.finalC = finalC;
		this.player = player;
		this.time = time;
		this.quadEase = quadEase;
		
		dX = finalX - startX;
		dY = finalY - startY;
		dC = finalC - startC;
	}

	public long getTime() {
		return time;
	}

	float getPercentage(long start, long t) {
		float f = (float) (t - start) / (float) time;
		if (quadEase) return f >= 1 ? f : (float) Easings.bothQuad(f);
		return f;
	}

	
	void draw(float f) {
		int x = startX + (int) ((float) dX * f);
		int y = startY + (int) ((float) dY * f);
		G.translate(x, y, 0);
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
