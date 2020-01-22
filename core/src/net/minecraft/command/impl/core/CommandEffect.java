package net.minecraft.command.impl.core;

import java.util.List;

import net.minecraft.command.api.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.potion.Potion;
import net.minecraft.item.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.functional.StringUtils;

public class CommandEffect extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "effect";
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
		return "commands.effect.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			throw new WrongUsageException("commands.effect.usage");
		}
		EntityLivingBase entitylivingbase = getEntity(sender, args[0], EntityLivingBase.class);

		if (args[1].equals("clear")) {
			if (entitylivingbase.getActivePotionEffects().isEmpty()) {
				throw new CommandException("commands.effect.failure.notActive.all", entitylivingbase.getName());
			}
			entitylivingbase.clearActivePotions();
			notifyOperators(sender, this, "commands.effect.success.removed.all", entitylivingbase.getName());
		} else {
			int i;

			try {
				i = parseInt(args[1], 1);
			} catch (NumberInvalidException numberinvalidexception) {
				Potion potion = Potion.getPotionFromResourceLocation(args[1]);

				if (potion == null) {
					throw numberinvalidexception;
				}

				i = potion.id;
			}

			int j = 600;
			int l = 30;
			int k = 0;

			if (i >= 0 && i < Potion.potionTypes.length && Potion.potionTypes[i] != null) {
				Potion potion1 = Potion.potionTypes[i];

				if (args.length >= 3) {
					l = parseInt(args[2], 0, 1000000);

					if (potion1.isInstant()) {
						j = l;
					} else {
						j = l * 20;
					}
				} else if (potion1.isInstant()) {
					j = 1;
				}

				if (args.length >= 4) {
					k = parseInt(args[3], 0, 255);
				}

				boolean flag = true;

				if (args.length >= 5 && "true".equalsIgnoreCase(args[4])) {
					flag = false;
				}

				if (l > 0) {
					PotionEffect potioneffect = new PotionEffect(i, j, k, false, flag);
					entitylivingbase.addPotionEffect(potioneffect);
					notifyOperators(sender, this, "commands.effect.success", new ChatComponentTranslation(potioneffect.getEffectName()), i, k, entitylivingbase.getName(),
							l);
				} else if (entitylivingbase.isPotionActive(i)) {
					entitylivingbase.removePotionEffect(i);
					notifyOperators(sender, this, "commands.effect.success.removed", new ChatComponentTranslation(potion1.getName()), entitylivingbase.getName());
				} else {
					throw new CommandException("commands.effect.failure.notActive", new ChatComponentTranslation(potion1.getName()), entitylivingbase.getName());
				}
			} else {
				throw new NumberInvalidException("commands.effect.notFound", i);
			}
		}
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? StringUtils.filterCompletions(args, this.getAllUsernames()) : args.length == 2 ? StringUtils.filterCompletions(args,
				Potion.func_181168_c()) : args.length == 5 ? StringUtils.filterCompletions(args, "true", "false") : null;
	}

	protected String[] getAllUsernames() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}

}
