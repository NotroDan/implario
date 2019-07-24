package net.minecraft.client;

import net.minecraft.client.renderer.G;
import net.minecraft.server.Profiler;
import optifine.Config;
import optifine.Lagometer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientProfiler extends Profiler {

	private static final String SCHEDULED_EXECUTABLES = "scheduledExecutables";
	private static final String TICK = "tick";
	private static final String PRE_RENDER_ERRORS = "preRenderErrors";
	private static final String RENDER = "render";
	private static final String DISPLAY = "display";
	private static final int HASH_SCHEDULED_EXECUTABLES = "scheduledExecutables".hashCode();
	private static final int HASH_TICK = "tick".hashCode();
	private static final int HASH_PRE_RENDER_ERRORS = "preRenderErrors".hashCode();
	private static final int HASH_RENDER = "render".hashCode();
	private static final int HASH_DISPLAY = "display".hashCode();
	public boolean profilerGlobalEnabled = true;
	private boolean profilerLocalEnabled;

	public ClientProfiler() {
		this.profilerLocalEnabled = this.profilerGlobalEnabled;
	}

	/**
	 * Clear profiling.
	 */
	public void clearProfiling() {
		super.clearProfiling();
		this.profilerLocalEnabled = this.profilerGlobalEnabled;
	}

	/**
	 * Start section
	 */
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

			if (j == HASH_RENDER && name.equals("render")) {
				G.clearEnabled = false;
			} else if (j == HASH_DISPLAY && name.equals("display")) {
				G.clearEnabled = true;
			}
		}

		if (this.profilerLocalEnabled) super.startSection(name);
	}

	/**
	 * End section
	 */
	public void endSection() {
		if (profilerLocalEnabled) super.endSection();
	}

	/**
	 * Get profiling data
	 */
	public List getProfilingData(String str) {
		this.profilerLocalEnabled = this.profilerGlobalEnabled;

		if (!this.profilerLocalEnabled)
			return new ArrayList(Arrays.asList(new Result("root", 0.0D, 0.0D)));

		return super.getProfilingData(str);
	}

	/**
	 * End current section and start a new section
	 */
	public void endStartSection(String name) {
		if (this.profilerLocalEnabled) super.endStartSection(name);
	}

}
