package net.minecraft.command.api.context;

import net.minecraft.command.api.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface Context {

	MinecraftServer getServer();

	ICommandSender getSender();

	ContextCommand getCommand();

	<T> T get(Arg<T> arg);

	void setAffectedEntities(int amount);

	int getAffectedEntities();

}
