package net.minecraft.command.impl.core;

public class SyntaxErrorException extends CommandException {

	public SyntaxErrorException() {
		this("commands.generic.snytax", new Object[0]);
	}

	public SyntaxErrorException(String message, Object... replacements) {
		super(message, replacements);
	}

}
