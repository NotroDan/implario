package net.minecraft.command;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandEnchant extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "enchant";
	}

	/**
	 * Return the required permission level for this command.
	 */
	public int getRequiredPermissionLevel() {
		return 2;
	}

	/**
	 * Gets the usage string for the command.
	 */
	public String getCommandUsage(ICommandSender sender) {
		return "commands.enchant.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) throw new WrongUsageException("commands.enchant.usage");

		Player entityplayer = getPlayer(sender, args[0]);
		sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
		int i;

		try {
			i = parseInt(args[1], 0);
		} catch (NumberInvalidException numberinvalidexception) {
			Enchantment enchantment = Enchantment.getEnchantmentByLocation(args[1]);

			if (enchantment == null) throw numberinvalidexception;

			i = enchantment.effectId;
		}

		int lvl = 1;
		ItemStack itemstack = entityplayer.getCurrentEquippedItem();

		if (itemstack == null) throw new CommandException("commands.enchant.noItem");

		Enchantment ench = Enchantment.getEnchantmentById(i);

		if (ench == null) throw new NumberInvalidException("commands.enchant.notFound", i);
		if (!ench.canApply(itemstack)) throw new CommandException("commands.enchant.cantEnchant");

		boolean isIllegal = false;
		boolean requireAddition = true;
		if (args.length >= 3) {
			lvl = parseInt(args[2]);
			if (lvl < Short.MIN_VALUE) lvl = Short.MIN_VALUE;
			if (lvl > Short.MAX_VALUE) lvl = Short.MAX_VALUE;
			if (lvl < ench.getMinLevel() || lvl > ench.getMaxLevel()) isIllegal = true;
		}

		if (itemstack.hasTagCompound()) {
			NBTTagList nbt = itemstack.getEnchantmentTagList();

			if (nbt != null) {
				for (int k = 0; k < nbt.tagCount(); ++k) {
					int l = nbt.getCompoundTagAt(k).getShort("id");

					if (Enchantment.getEnchantmentById(l) != null) {
						Enchantment existing = Enchantment.getEnchantmentById(l);
						if (existing == ench) {
							nbt.getCompoundTagAt(k).setShort("lvl", (short) lvl);
							requireAddition = false;
						} else if (!existing.canApplyTogether(ench)) {
							throw new CommandException("commands.enchant.cantCombine",
									ench.getTranslatedName(lvl), existing.getTranslatedName(nbt.getCompoundTagAt(k).getShort("lvl")));
						}
					}
				}
			}
		}

		if (requireAddition) itemstack.addEnchantment(ench, lvl);
		notifyOperators(sender, this, "§aЗачарование §f" + ench.getTranslatedName(lvl) + "§a успешно наложено.");
		sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers()) : args.length == 2 ? getListOfStringsMatchingLastWord(args, Enchantment.func_181077_c()) : null;
	}

	protected String[] getListOfPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}

}
