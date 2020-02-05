package net.minecraft.resources.load;

public class DatapackLoadException extends Exception {
	public DatapackLoadException(Throwable cause, String message) {
		super(message, cause);
	}

	public DatapackLoadException(Throwable cause) {
		super(cause);
	}
}
