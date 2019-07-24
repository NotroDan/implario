package net.minecraft;

public class Logger {

	public static Logger instance = new Logger();


	public static Logger getInstance() {
		return instance;
	}

	public void print(Object... ob) {
		for (Object o : ob) {
			if (o instanceof Throwable) ((Throwable) o).printStackTrace();
			else System.out.println(o);
		}
	}

	public void info(Object... ob) {
		print(ob);
	}

	public void warn(Object... ob) {print(ob);}

	public void error(Object... ob) {print(ob);}

	public void fatal(Object... ob) {print(ob);}

	public void debug(Object... ob) {
		if (isDebugEnabled()) print(ob);
	}

	public boolean isDebugEnabled() {
		return false;
	}

}
