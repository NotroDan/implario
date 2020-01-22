package net.minecraft.command.api;

import net.minecraft.command.api.context.Context;

@FunctionalInterface
public interface SuitedExecutor {

	/**
	 * Действия, которые должны происходить при выполнении команды
	 */
	void execute(Context ctx);

}
