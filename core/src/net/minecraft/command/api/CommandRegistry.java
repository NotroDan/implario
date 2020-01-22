package net.minecraft.command.api;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;

@UtilityClass
public class CommandRegistry {

	@Getter(AccessLevel.PROTECTED)
	private final Map<String, Command> commandMap = Maps.newHashMap();

	public Command registerCommand(Command cmd) {
		commandMap.put(cmd.getAddress(), cmd);
		return cmd;
	}

	public void unregisterCommand(Command command) {
		commandMap.remove(command.getAddress());
	}

	public Command getCommand(String command) {
		return commandMap.get(command);
	}

	public Collection<Command> getAllCommands() {
		return commandMap.values();
	}

}
