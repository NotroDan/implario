package net.minecraft.command.impl;

import net.minecraft.command.Arg;
import net.minecraft.command.Context;
import net.minecraft.command.Command;
import net.minecraft.command.args.ArgGameMode;
import net.minecraft.command.args.ArgPlayers;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.functional.StringUtils;
import net.minecraft.world.WorldSettings.GameType;

import java.util.Collection;
import java.util.Collections;

public class CommandGameMode extends Command {

	private static final ArgGameMode argMode =
			new ArgGameMode("Режим", "Режим, который нужно установить игроку");

	private static final Arg<Collection<MPlayer>> argPlayers =
			new ArgPlayers("Игрок", "Игрок, которому нужно установить режим")
					.defaults(parser -> Collections.singleton(parser.getInvoker()));

	public CommandGameMode() {
		super("gamemode", "Изменение игрового режима", "mod", 4,
				argMode, argPlayers);
	}

	@Override
	public void execute(Context ctx) {
		GameType gamemode = ctx.get(argMode);
		Collection<MPlayer> players = ctx.get(argPlayers);

		for (MPlayer player : players) {
			player.setGameType(gamemode);
			player.fallDistance = 0.0F;
		}

		int size = players.size();
		ctx.setAffectedEntities(size);
		if (size == 1)
			ctx.msg("§aИгровой режим игрока §f" + players.iterator().next().getName() + "§a изменён на §f" + gamemode.getTitle() + "§a.");
		else
			ctx.msg("§aИгровой режим установлен §f" + size + " игрок" + StringUtils.plurals(size, "у", "ам", "ам"));
	}

}
