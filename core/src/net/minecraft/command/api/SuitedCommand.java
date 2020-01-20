package net.minecraft.command.api;

import lombok.Getter;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.handling.args.ArgsParser;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
public class SuitedCommand implements Command {

	private final List<Arg> args;
	private final String description;
	private final String address;
	private final String ladder;
	private final int permissionLevel;
	private final SuitedExecutor suitedExecutor;

	private final int essentialArgsAmount;

	public SuitedCommand(String address, String description, String ladder, int permissionLevel, SuitedExecutor suitedExecutor, Arg... args) {
		this.address = address;
		this.description = description;
		this.ladder = ladder;
		this.permissionLevel = permissionLevel;
		this.suitedExecutor = suitedExecutor;

		this.args = Arrays.asList(args);
		int requiredArgs = 0;
		for (Arg arg : args) if (arg.isEssential()) requiredArgs += arg.getEssentialPartsAmount();

		this.essentialArgsAmount = requiredArgs;
	}

	protected void args(Arg... args) {
		this.args.addAll(Arrays.asList(args));
	}

	@Override
	public int execute(ICommandSender sender, String[] args) {
		ArgsParser parser = new ArgsParser(sender, MinecraftServer.getServer(), this, args);

		try {
			CommandContext ctx = parser.parse();
			if (ctx == null) return 0;

			suitedExecutor.execute(ctx);
			int affected = ctx.getAffectedEntities();
			sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, affected);
			return affected;
		} catch (Throwable t) {
			t.printStackTrace();
			sender.sendMessage("§cПроизошла ошибка: " + t.getClass().getSimpleName() + ": " + t.getMessage());
			return 0;
		}
	}

	@Override
	public Collection<String> tabComplete(MPlayer player, BlockPos pos, String[] args) {
		List<Arg> argTemplates = getArgs();
		if (argTemplates.size() < args.length) return null;
		Arg<?> arg = argTemplates.get(args.length - 1);
		return arg.performTabCompletion(player, pos);
	}

}
