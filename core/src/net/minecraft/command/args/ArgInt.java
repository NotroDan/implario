package net.minecraft.command.args;

import net.minecraft.command.ArgsParser;
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
