package net.minecraft.util;

import net.minecraft.logging.Log;
import net.minecraft.network.ThreadQuickExitException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Util {
	public static OS getOSType() {
		String s = System.getProperty("os.name").toLowerCase();
		return
				s.contains("win") ? OS.WINDOWS :
						s.contains("mac") ? OS.OSX :
								s.contains("solaris") ? OS.SOLARIS :
										s.contains("sunos") ? OS.SOLARIS :
												s.contains("linux") ? OS.LINUX :
														s.contains("unix") ? OS.LINUX :
																OS.UNKNOWN;
	}

	public static <V> void schedule(FutureTask<V> task) {
		try {
			task.run();
			task.get();
		} catch (ThreadQuickExitException ignored) {
		} catch (ExecutionException | InterruptedException e) {
			if (e.getCause() == ThreadQuickExitException.EXIT_EXCEPTION) return;
			Log.MAIN.error("Ошибка при выполнении задачи");
			Log.MAIN.exception(e);
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
