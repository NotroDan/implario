package net.minecraft.command.handling.args;

public class ArgString extends AbstractArg<String> {


	public ArgString(String caption, String description) {
		super(caption, description);
	}

	@Override
	public String get(ArgsParser parser) {
		return parser.next();
	}


}
