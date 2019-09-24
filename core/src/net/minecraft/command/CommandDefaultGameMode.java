package net.minecraft.command;

import net.minecraft.entity.player.MPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;

public class CommandDefaultGameMode extends CommandGameMode {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "defaultgamemode";
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.defaultgamemode.usage";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length <= 0) {
			throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
		}
		WorldSettings.GameType worldsettings$gametype = this.getGameModeFromCommand(sender, args[0]);
		this.setGameType(worldsettings$gametype);
		notifyOperators(sender, this, "commands.defaultgamemode.success", new Object[] {new ChatComponentTranslation("gameMode." + worldsettings$gametype.getName(), new Object[0])});
	}

	protected void setGameType(WorldSettings.GameType p_71541_1_) {
		MinecraftServer minecraftserver = MinecraftServer.getServer();
		minecraftserver.setGameType(p_71541_1_);

		if (minecraftserver.getForceGamemode()) {
			for (MPlayer entityplayermp : MinecraftServer.getServer().getConfigurationManager().getPlayers()) {
				entityplayermp.setGameType(p_71541_1_);
				entityplayermp.fallDistance = 0.0F;
			}
		}
	}

}
