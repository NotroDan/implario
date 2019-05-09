package net.minecraft.command;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CommandFill extends CommandBase {

	/**
	 * Gets the name of the command
	 */
	public String getCommandName() {
		return "fill";
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
		return "commands.fill.usage";
	}

	/**
	 * Callback when the command is invoked
	 */
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 7) throw new WrongUsageException("commands.fill.usage");
		sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
		BlockPos blockpos = parseBlockPos(sender, args, 0, false);
		BlockPos blockpos1 = parseBlockPos(sender, args, 3, false);
		Block block = CommandBase.getBlockByText(sender, args[6]);
		int i = 0;

		if (args.length >= 8) i = parseInt(args[7], 0, 15);

		BlockPos pos1 = new BlockPos(Math.min(blockpos.getX(), blockpos1.getX()), Math.min(blockpos.getY(), blockpos1.getY()), Math.min(blockpos.getZ(), blockpos1.getZ()));
		BlockPos pos2 = new BlockPos(Math.max(blockpos.getX(), blockpos1.getX()), Math.max(blockpos.getY(), blockpos1.getY()), Math.max(blockpos.getZ(), blockpos1.getZ()));
		int j = (pos2.getX() - pos1.getX() + 1) * (pos2.getY() - pos1.getY() + 1) * (pos2.getZ() - pos1.getZ() + 1);

		if (j > 32768) throw new CommandException("commands.fill.tooManyBlocks", j, 32768);
		if (pos1.getY() < 0 || pos2.getY() >= 256) throw new CommandException("commands.fill.outOfWorld");
		World world = sender.getEntityWorld();

		for (int k = pos1.getZ(); k < pos2.getZ() + 16; k += 16) {
			for (int l = pos1.getX(); l < pos2.getX() + 16; l += 16) {
				if (!world.isBlockLoaded(new BlockPos(l, pos2.getY() - pos1.getY(), k))) {
					throw new CommandException("commands.fill.outOfWorld");
				}
			}
		}

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		boolean flag = false;

		if (args.length >= 10 && block.hasTileEntity()) {
			String s = getChatComponentFromNthArg(sender, args, 9).getUnformattedText();

			try {
				nbttagcompound = JsonToNBT.getTagFromJson(s);
				flag = true;
			} catch (NBTException nbtexception) {
				throw new CommandException("commands.fill.tagError", nbtexception.getMessage());
			}
		}

		List<BlockPos> list = Lists.newArrayList();
		j = 0;

		for (int z = pos1.getZ(); z <= pos2.getZ(); ++z) {
			for (int y = pos1.getY(); y <= pos2.getY(); ++y) {
				for (int x = pos1.getX(); x <= pos2.getX(); ++x) {
					BlockPos blockpos4 = new BlockPos(x, y, z);

					if (args.length >= 9) {
						if (!args[8].equals("outline") && !args[8].equals("hollow")) {
							if (args[8].equals("destroy")) {
								world.destroyBlock(blockpos4, true);
							} else if (args[8].equals("keep")) {
								if (!world.isAirBlock(blockpos4)) {
									continue;
								}
							} else if (args[8].equals("replace") && !block.hasTileEntity()) {
								if (args.length > 9) {
									Block block1 = CommandBase.getBlockByText(sender, args[9]);

									if (world.getBlockState(blockpos4).getBlock() != block1) {
										continue;
									}
								}

								if (args.length > 10) {
									int l1 = CommandBase.parseInt(args[10]);
									IBlockState iblockstate = world.getBlockState(blockpos4);

									if (iblockstate.getBlock().getMetaFromState(iblockstate) != l1) {
										continue;
									}
								}
							}
						} else if (x != pos1.getX() && x != pos2.getX() && y != pos1.getY() && y != pos2.getY() && z != pos1.getZ() && z != pos2.getZ()) {
							if (args[8].equals("hollow")) {
								world.setBlockState(blockpos4, Blocks.air.getDefaultState(), 2);
								list.add(blockpos4);
							}

							continue;
						}
					}

					TileEntity tileentity1 = world.getTileEntity(blockpos4);

					if (tileentity1 != null) {
						if (tileentity1 instanceof IInventory) {
							((IInventory) tileentity1).clear();
						}

						world.setBlockState(blockpos4, Blocks.barrier.getDefaultState(), block == Blocks.barrier ? 2 : 4);
					}

					IBlockState iblockstate1 = block.getStateFromMeta(i);

					if (world.setBlockState(blockpos4, iblockstate1, 2)) {
						list.add(blockpos4);
						++j;

						if (flag) {
							TileEntity tileentity = world.getTileEntity(blockpos4);

							if (tileentity != null) {
								nbttagcompound.setInteger("x", blockpos4.getX());
								nbttagcompound.setInteger("y", blockpos4.getY());
								nbttagcompound.setInteger("z", blockpos4.getZ());
								tileentity.readFromNBT(nbttagcompound);
							}
						}
					}
				}
			}
		}

		for (BlockPos blockpos5 : list) {
			Block block2 = world.getBlockState(blockpos5).getBlock();
			world.notifyNeighborsRespectDebug(blockpos5, block2);
		}

		if (j <= 0) {
			throw new CommandException("commands.fill.failed");
		}
		sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, j);
		notifyOperators(sender, this, "commands.fill.success", j);
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : args.length > 3 && args.length <= 6 ? func_175771_a(args, 3,
				pos) : args.length == 7 ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : args.length == 9 ? getListOfStringsMatchingLastWord(args,
				"replace", "destroy", "keep", "hollow", "outline") : args.length == 10 && "replace".equals(args[8]) ? getListOfStringsMatchingLastWord(args,
				Block.blockRegistry.getKeys()) : null;
	}

}
