package net.minecraft.network;

public final class ThreadQuickExitException extends RuntimeException {

	public static final ThreadQuickExitException EXIT_EXCEPTION = new ThreadQuickExitException();

	private ThreadQuickExitException() {
		this.setStackTrace(new StackTraceElement[0]);
	}

	public synchronized Throwable fillInStackTrace() {
		this.setStackTrace(new StackTraceElement[0]);
		return this;
	}

}
