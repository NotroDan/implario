package net.minecraft.command.api;

@FunctionalInterface
public interface SuitedExecutor {

	/**
	 * Действия, которые должны происходить при выполнении команды
	 */
	void execute(CommandContext ctx);

}
