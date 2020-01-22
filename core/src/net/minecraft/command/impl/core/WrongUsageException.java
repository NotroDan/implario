package net.minecraft.command.impl.core;

public class WrongUsageException extends SyntaxErrorException {

	public WrongUsageException(String message, Object... replacements) {
		super(message, replacements);
	}

}
