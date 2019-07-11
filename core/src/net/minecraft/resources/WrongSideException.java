package net.minecraft.resources;

public class WrongSideException extends RuntimeException {

	public WrongSideException(boolean serverSideRequired) {
		super("This method is only supported on " + (serverSideRequired ? "SERVER" : "CLIENT") + " side.");
	}

}
