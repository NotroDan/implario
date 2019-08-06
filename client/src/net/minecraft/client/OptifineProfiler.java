package net.minecraft.client;

import net.minecraft.client.renderer.G;
import net.minecraft.logging.Profiler;
import optifine.Config;
import optifine.Lagometer;

public class OptifineProfiler extends Profiler {

	private static final int HASH_SCHEDULED_EXECUTABLES = "scheduledExecutables".hashCode();
	private static final int HASH_TICK = "tick".hashCode();
	private static final int HASH_PRE_RENDER_ERRORS = "preRenderErrors".hashCode();
	private static final int HASH_RENDER = "render".hashCode();
	private static final int HASH_DISPLAY = "display".hashCode();

	public void startSection(String name) {
		if (Lagometer.isActive()) {
			int i = name.hashCode();

			if (i == HASH_SCHEDULED_EXECUTABLES && name.equals("scheduledExecutables")) {
				Lagometer.timerScheduledExecutables.start();
			} else if (i == HASH_TICK && name.equals("tick") && Config.isMinecraftThread()) {
				Lagometer.timerScheduledExecutables.end();
				Lagometer.timerTick.start();
			} else if (i == HASH_PRE_RENDER_ERRORS && name.equals("preRenderErrors")) {
				Lagometer.timerTick.end();
			}
		}

		if (Config.isFastRender()) {
			int j = name.hashCode();

			if (j == HASH_RENDER && name.equals("render")) G.clearEnabled = false;
			else if (j == HASH_DISPLAY && name.equals("display")) G.clearEnabled = true;
		}

		super.startSection(name);
	}

}
