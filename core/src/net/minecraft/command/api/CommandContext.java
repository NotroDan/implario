package net.minecraft.command.api;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface CommandContext {

	MinecraftServer getServer();

	ICommandSender getSender();

	SuitedCommand getCommand();

	<T> T get(Arg<T> arg);

	void setAffectedEntities(int amount);

	int getAffectedEntities();

}
