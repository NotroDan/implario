package net.minecraft.command.api;

import lombok.Getter;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

@Getter
public class SimpleCommand implements Command {

	private final String description;
	private final String address;
	private final String ladder;
	private final int permissionLevel;
	private final SimpleExecutor executor;

	public SimpleCommand(String address, String description, String ladder, int permissionLevel, SimpleExecutor executor) {
		this.address = address;
		this.description = description;
		this.ladder = ladder;
		this.permissionLevel = permissionLevel;
		this.executor = executor;
	}

	public int execute(ICommandSender sender, String[] args) {
		return executor.execute(sender, args);
	}

}

