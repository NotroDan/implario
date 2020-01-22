package net.minecraft.command.impl.core;

public class CommandException extends Exception {

	private final Object[] errorObjects;

	public CommandException(String message, Object... objects) {
		super(message);
		this.errorObjects = objects;
	}

	public Object[] getErrorObjects() {
		return this.errorObjects;
	}

}
