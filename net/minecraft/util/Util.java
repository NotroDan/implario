package net.minecraft.util;

import net.minecraft.client.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Util {

	public static OS getOSType() {
		String s = System.getProperty("os.name").toLowerCase();
		return s.contains("win") ? OS.WINDOWS : s.contains("mac") ? OS.OSX : s.contains("solaris") ? OS.SOLARIS : s.contains("sunos") ? OS.SOLARIS : s.contains(
				"linux") ? OS.LINUX : s.contains("unix") ? OS.LINUX : OS.UNKNOWN;
	}

	public static <V> V schedule(FutureTask<V> task, Logger logger) {
		try {
			task.run();
			return task.get();
		} catch (ExecutionException | InterruptedException executionexception) {
			logger.fatal("Error executing task", executionexception);
			return null;
		}
	}

	public enum OS {
		LINUX,
		SOLARIS,
		WINDOWS,
		OSX,
		UNKNOWN
	}

}
