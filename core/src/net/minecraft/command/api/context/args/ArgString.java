package net.minecraft.command.api.context.args;

import net.minecraft.command.api.context.ArgsParser;

public class ArgString extends AbstractArg<String> {


	public ArgString(String caption, String description) {
		super(caption, description);
	}

	@Override
	public String get(ArgsParser parser) {
		return parser.next();
	}


}
