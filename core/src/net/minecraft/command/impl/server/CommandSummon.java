package net.minecraft.command.impl.server;

import net.minecraft.command.impl.core.CommandBase;
import net.minecraft.command.impl.core.CommandException;
import net.minecraft.command.api.ICommandSender;
import net.minecraft.command.impl.core.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLightningBolt;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3d;
import net.minecraft.util.functional.StringUtils;
import net.minecraft.world.World;

import java.util.List;

public class CommandSummon extends CommandBase {
	@Override
	public String getCommandName() {
		return "summon";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.summon.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1)
			throw new WrongUsageException("commands.summon.usage");
		String s = args[0];
		BlockPos blockpos = sender.getPosition();
		Vec3d vec3D = sender.getPositionVector();
		double d0 = vec3D.xCoord;
		double d1 = vec3D.yCoord;
		double d2 = vec3D.zCoord;

		if (args.length >= 4) {
			d0 = parseDouble(d0, args[1], true);
			d1 = parseDouble(d1, args[2], false);
			d2 = parseDouble(d2, args[3], true);
			blockpos = new BlockPos(d0, d1, d2);
		}

		World world = sender.getEntityWorld();

		if (!world.isBlockLoaded(blockpos))
			throw new CommandException("commands.summon.outOfWorld");

		if ("LightningBolt".equals(s)) {
			world.addWeatherEffect(new EntityLightningBolt(world, d0, d1, d2));
			notifyOperators(sender, this, "commands.summon.success");
			return;
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		boolean customNbt = false;

		if (args.length >= 5) {
			IChatComponent ichatcomponent = getChatComponentFromNthArg(sender, args, 4);

			try {
				nbttagcompound = JsonToNBT.getTagFromJson(ichatcomponent.getUnformattedText());
				customNbt = true;
			} catch (NBTException nbtexception) {
				throw new CommandException("commands.summon.tagError", nbtexception.getMessage());
			}
		}

		nbttagcompound.setString("id", s);
		Entity entity2;

		try {
			entity2 = EntityList.createEntityFromNBT(nbttagcompound, world);
		} catch (RuntimeException var19) {
			throw new CommandException("commands.summon.failed");
		}

		if (entity2 == null) {
			throw new CommandException("commands.summon.failed");
		}
		entity2.setLocationAndAngles(d0, d1, d2, entity2.rotationYaw, entity2.rotationPitch);

		if (!customNbt)
			entity2.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(entity2)), null);

		world.spawnEntityInWorld(entity2);
		Entity entity = entity2;

		for (NBTTagCompound nbttagcompound1 = nbttagcompound; entity != null && nbttagcompound1.hasKey("Riding", 10); nbttagcompound1 = nbttagcompound1.getCompoundTag("Riding")) {
			Entity entity1 = EntityList.createEntityFromNBT(nbttagcompound1.getCompoundTag("Riding"), world);

			if (entity1 != null) {
				entity1.setLocationAndAngles(d0, d1, d2, entity1.rotationYaw, entity1.rotationPitch);
				world.spawnEntityInWorld(entity1);
				entity.mountEntity(entity1);
			}

			entity = entity1;
		}

		notifyOperators(sender, this, "commands.summon.success");
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length == 1 ? StringUtils.filterCompletions(args, EntityList.getEntityNameList()) : args.length > 1 && args.length <= 4 ? completePos(args, 1, pos) : null;
	}
}
