package net.minecraft.command;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;

@UtilityClass
public class CommandRegistry {

	@Getter(AccessLevel.PROTECTED)
	private final Map<String, ICommand> commandMap = Maps.newHashMap();

	public ICommand registerCommand(ICommand cmd) {
		commandMap.put(cmd.getName(), cmd);
		return cmd;
	}

	public void unregisterCommand(ICommand command) {
		commandMap.remove(command.getName());
	}

	public ICommand getCommand(String command) {
		return commandMap.get(command);
	}

	public Collection<ICommand> getAllCommands() {
		return commandMap.values();
	}

}
