package net.minecraft.command.args;

import net.minecraft.command.ArgsParser;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.functional.StringUtils;
import net.minecraft.world.WorldSettings;

import java.util.Collection;

public class ArgGameMode extends AbstractArg<WorldSettings.GameType> {
	public ArgGameMode(String caption, String description) {
		super(caption, description);
	}

	@Override
	public WorldSettings.GameType get(ArgsParser parser) {
		String input = parser.next();
		char m = input.toLowerCase().charAt(0);
		WorldSettings.GameType gamemode;
		if (m >= '0' && m <= '3') gamemode = WorldSettings.GameType.getByID(m - '0');
		else if (m == 'c') gamemode = WorldSettings.GameType.CREATIVE;
		else if (m == 'v' || input.startsWith("sp")) gamemode = WorldSettings.GameType.SPECTATOR;
		else if (m == 's') gamemode = WorldSettings.GameType.SURVIVAL;
		else if (m == 'a') gamemode = WorldSettings.GameType.ADVENTURE;
		else {
			parser.error("§cИгровой режим §e" + input + "§c не найден.");
			gamemode = null;
		}
		return gamemode;
	}

	@Override
	public Collection<String> performTabCompletion(MPlayer player, BlockPos pos, String arg) {
		return StringUtils.filterCompletions(arg, "survival", "creative", "adventure", "spectator");
	}
}
