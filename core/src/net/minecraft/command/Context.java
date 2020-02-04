package net.minecraft.command;

import lombok.Data;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

@Data
public class Context {

	private final MinecraftServer server;
	private final Command command;
	private final Sender sender;
	private final Map<Arg, Object> argsMap;
	private int affectedEntities;

	@SuppressWarnings ("unchecked")
	public <T> T get(Arg<T> arg) {
		return (T) argsMap.get(arg);
	}

	public void msg(String s) {
		sender.sendMessage(s);
	}

}
