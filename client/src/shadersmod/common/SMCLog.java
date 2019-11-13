package shadersmod.common;

import net.minecraft.logging.Log;

//todo: ещё один логгер
public abstract class SMCLog {

	private static final Log LOGGER = Log.MAIN;

	public static void severe(String message) {
		LOGGER.error(message);
	}

	public static void warning(String message) {
		LOGGER.warn(message);
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warning(String format, Object... args) {
		String s = String.format(format, args);
		LOGGER.warn(s);
	}

	public static void info(String format, Object... args) {
		String s = String.format(format, args);
		LOGGER.info(s);
	}

}
