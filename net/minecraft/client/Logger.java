package net.minecraft.client;

public class Logger {

	public static Logger instance = new Logger();

	public static Logger getInstance() {
		return instance;
	}

	public void print(String prefix, Object... ob) {
		for (Object o : ob) {
			if (o instanceof Throwable) ((Throwable) o).printStackTrace();
			else System.out.println(prefix + o);
		}
	}

	public void info(Object... ob) {print("", ob);}
	public void warn(Object... ob) {print("[Warn]", ob);}
	public void error(Object... ob) {print("[Error]", ob);}
	public void fatal(Object... ob) {print("[FATAL]", ob);}
	public void debug(Object... ob) {
		if (isDebugEnabled()) print("[Debug]", ob);}

	public boolean isDebugEnabled() {
		return false;
	}

}
