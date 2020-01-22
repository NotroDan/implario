package net.minecraft.command.api.context;

import lombok.Data;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

@Data
class ContextImpl implements Context {

	private final MinecraftServer server;
	private final ContextCommand command;
	private final ICommandSender sender;
	private final Map<Arg, Object> argsMap;
	private int affectedEntities;


	@SuppressWarnings ("unchecked")
	@Override
	public <T> T get(Arg<T> arg) {
		return (T) argsMap.get(arg);
	}

}
