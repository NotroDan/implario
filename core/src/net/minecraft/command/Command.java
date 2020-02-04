package net.minecraft.command;

import lombok.Getter;
import net.minecraft.command.legacy.CommandResultStats;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Getter
public abstract class Command implements ICommand {
	private final List<Arg> args;
	private final String description;
	private final String address;
	private final String ladder;
	private final int permissionLevel;

	private final int essentialArgsAmount;

	public Command(String address, String description, String ladder, int permissionLevel, Arg... args) {
		this.address = address;
		this.description = description;
		this.ladder = ladder;
		this.permissionLevel = permissionLevel;

		this.args = Arrays.asList(args);
		int requiredArgs = 0;
		for (Arg arg : args) if (arg.isEssential()) requiredArgs += arg.getEssentialPartsAmount();

		this.essentialArgsAmount = requiredArgs;
	}

	protected void args(Arg... args) {
		this.args.addAll(Arrays.asList(args));
	}

	@Override
	public int execute(Sender sender, String[] args) {
		ArgsParser parser = new ArgsParser(sender, MinecraftServer.getServer(), this, args);

		try {
			Context ctx = parser.parse();
			if (ctx == null) return 0;

			execute(ctx);
			int affected = ctx.getAffectedEntities();
			sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, affected);
			return affected;
		} catch (Throwable t) {
			t.printStackTrace();
			sender.sendMessage("§cПроизошла ошибка: " + t.getClass().getSimpleName() + ": " + t.getMessage());
			return 0;
		}
	}

	protected abstract void execute(Context ctx);

	@Override
	public Collection<String> tabComplete(MPlayer player, BlockPos pos, String[] args) {
		List<Arg> argTemplates = getArgs();
		if (argTemplates.size() < args.length) return null;
		Arg<?> arg = argTemplates.get(args.length - 1);
		return arg.performTabCompletion(player, pos, args[args.length - 1]);
	}
}
