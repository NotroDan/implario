package net.minecraft.client.gui.element;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Normalizer;

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

	float getPercentage(long start, long t) {
		float f = (float) (t - start) / (float) time;
		if (quadEase) return f >= 1 ? f : Normalizer.exp(f, 3);
		else return f;
	}
	
	public static float quadEase(float t) {
		if(t <= 0.5) return 2 * t * t;
		t -= 0.5;
		return 2 * t * (1 - t) + 0.5f;
	}
	
	boolean draw(float f) {
		if (f <= 0) f = 0;
		if (f >= 1) return true;
		int x = startX + (int) ((float) dX * f);
		int y = startY + (int) ((float) dY * f);
		GlStateManager.translate(x, y, 0);
		player.draw(f);
		GlStateManager.translate(-x, -y, 0);
		return false;
	}
	
	public boolean draw(long start, long t) {
		return draw(getPercentage(start, t));
	}
	
	@FunctionalInterface
	public interface AnimationPlayer {
		void draw(float percentage);
	}
	
	
}
