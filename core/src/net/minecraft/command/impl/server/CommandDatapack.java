package net.minecraft.command.impl.server;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.command.impl.core.WrongUsageException;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.DatapackManager;
import net.minecraft.resources.load.DatapackLoader;

public class CommandDatapack extends CommandBase {

	@Override
	public String getCommandName() {
		return "dp";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "dp reload [Jar]";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) throw new WrongUsageException("Использование: /dp reload [Jar]");

		String cmd = args[0];
		if(cmd.equals("reload")){
			for(DatapackLoader loader : DatapackManager.getLoaders()) {
				sender.sendMessage(loader.getProperties().getDomain());
				if (loader.getProperties().getDomain().equalsIgnoreCase(args[1])) {
					byte[] array = loader.getInstance().saveState();
					byte[] playerInfo = DatapackManager.removePlayerInfo(loader.getInstance());
					DatapackManager.shutdownBranch(loader);
					try {
						DatapackManager.loadBranch(loader);
						if (array != null) loader.getInstance().loadState(array);
						if (playerInfo != null) DatapackManager.loadPlayerInfo(loader.getInstance(), playerInfo);
						Datapack datapack = loader.getInstance();
						datapack.loadBlocks();
						Block.reloadBlockStates();
						Blocks.reload();

						BlockFire.init();

						datapack.loadItems();
						Items.reload();
						sender.sendMessage("все норм");
					} catch (Exception loadException) {
						loadException.printStackTrace();
						sender.sendMessage("ошыбка");
						sender.sendMessage(loadException.getMessage() + "");
						return;
					}
					return;
				}
			}
		}
	}
}
