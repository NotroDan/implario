package net.minecraft.client.gui.element;

import net.minecraft.client.renderer.G;

import java.util.Arrays;
import java.util.List;

public class Animator {

	private final List<Animation> list;
	private int startX, startY;

	public Animator(int startX, int startY, List<Animation> list) {
		this.list = list;
		this.startX = startX;
		this.startY = startY;
	}

	public Animator(int startX, int startY, Animation... list) {
		this.startX = startX;
		this.startY = startY;
		this.list = Arrays.asList(list);
	}

	public void setOffsets(int x, int y) {
		this.startX = x;
		this.startY = y;
	}

	public class Cycle {

		private final long startTime;
		private final boolean reverse;

		public Cycle(long startTime) {
			this(startTime, false);
		}

		public Cycle(long startTime, boolean reverse) {
			this.startTime = startTime;
			this.reverse = reverse;
		}

		public boolean draw(long currentTime) {
			boolean e = false;
			for (Animation anim : list) {
				float f = anim.getPercentage(startTime, currentTime);
				if (reverse) f = 1 - f;
				float x = (1 - f) * startX;
				float y = (1 - f) * startY;
				G.translate(x, y, 0);
				float rawF = (float) (currentTime - startTime) / (float) anim.getTime();
				boolean expired = rawF >= 1;
				anim.draw(f);
				if (expired) e = true;
				G.translate(-x, -y, 0);
			}
			return e;
		}

	}

}
