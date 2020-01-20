package net.minecraft.command.handling;

import lombok.Data;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.api.*;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

@Data
public class CommandContextImpl implements CommandContext {

	private final MinecraftServer server;
	private final SuitedCommand command;
	private final ICommandSender sender;
	private final Map<Arg, Object> argsMap;
	private int affectedEntities;


	@SuppressWarnings ("unchecked")
	@Override
	public <T> T get(Arg<T> arg) {
		return (T) argsMap.get(arg);
	}

}
