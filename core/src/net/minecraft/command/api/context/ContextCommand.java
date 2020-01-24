package net.minecraft.command.api.context;

import lombok.Getter;
import net.minecraft.command.legacy.CommandResultStats;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.command.api.Command;
import net.minecraft.command.api.SuitedExecutor;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
public class ContextCommand extends SuitedCommand {

	private final SuitedExecutor suitedExecutor;

	public ContextCommand(String address, String description, String ladder, int permissionLevel, SuitedExecutor suitedExecutor, Arg... args) {
		super(address, description, ladder, permissionLevel, args);
		this.suitedExecutor = suitedExecutor;
	}

	@Override
	protected void execute(Context ctx) {
		suitedExecutor.execute(ctx);
	}

}
