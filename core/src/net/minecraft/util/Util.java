package net.minecraft.util;

import net.minecraft.logging.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Util {

	public static OS getOSType() {
		String s = System.getProperty("os.name").toLowerCase();
		return s.contains("win") ? OS.WINDOWS : s.contains("mac") ? OS.OSX : s.contains("solaris") ? OS.SOLARIS : s.contains("sunos") ? OS.SOLARIS : s.contains(
				"linux") ? OS.LINUX : s.contains("unix") ? OS.LINUX : OS.UNKNOWN;
	}

	public static <V> V schedule(FutureTask<V> task, Log log) {
		try {
			task.run();
			return task.get();
		} catch (ExecutionException | InterruptedException e) {
			log.error("Ошибка при выполнении таска");
			log.exception(e);
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

	public static int firstEmpty(Object[] o) {
		for (int i = 0; i < o.length; i++) if (o[i] == null) return i;
		return -1;
	}

}
