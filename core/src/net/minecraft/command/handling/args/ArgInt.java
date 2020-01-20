package net.minecraft.command.handling.args;

import net.minecraft.util.functional.StringUtils;

public class ArgInt extends AbstractArg<Integer> {

	public ArgInt(String caption, String description) {
		super(caption, description);
	}

	@Override
	public Integer get(ArgsParser parser) {

		String arg = parser.next();
		Integer value = StringUtils.parseBoxedInt(arg);
		if (value == null)
			parser.error("§f" + arg + "§c не является допустимым числом");
		return value;

	}




}
