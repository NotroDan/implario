package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.protocol.minecraft_47.play.server.S23PacketBlockChange;
import net.minecraft.network.protocol.minecraft_47.play.server.S38PacketPlayerListItem;
import net.minecraft.resources.event.ServerEvents;
import net.minecraft.resources.event.events.player.PlayerBlockBreakEvent;
import net.minecraft.resources.event.events.player.PlayerBlockInteractEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.LockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

public class ItemInWorldManager {

	/**
	 * The world object that this object is connected to.
	 */
	public World theWorld;

	/**
	 * The EntityPlayerMP object that this object is connected to.
	 */
	public MPlayer thisPlayerMP;
	private WorldSettings.GameType gameType = WorldSettings.GameType.NOT_SET;

	/**
	 * True if the player is destroying a block
	 */
	private boolean isDestroyingBlock;
	private int initialDamage;
	private BlockPos field_180240_f = BlockPos.ORIGIN;
	private int curblockDamage;

	/**
	 * Set to true when the "finished destroying block" packet is received but the block wasn't fully damaged yet. The
	 * block will not be destroyed while this is false.
	 */
	private boolean receivedFinishDiggingPacket;
	private BlockPos field_180241_i = BlockPos.ORIGIN;
	private int initialBlockDamage;
	private int durabilityRemainingOnBlock = -1;

	public ItemInWorldManager(World worldIn) {
		this.theWorld = worldIn;
	}

	public void setOptimizedGameType(WorldSettings.GameType type){
		this.gameType = type;
		this.thisPlayerMP.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_GAME_MODE, this.thisPlayerMP));
	}

	public void setGameType(WorldSettings.GameType type) {
		this.gameType = type;
		type.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
		this.thisPlayerMP.sendPlayerAbilities();
		this.thisPlayerMP.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_GAME_MODE, this.thisPlayerMP));
	}

	public WorldSettings.GameType getGameType() {
		return this.gameType;
	}

	public boolean survivalOrAdventure() {
		return this.gameType.isSurvivalOrAdventure();
	}

	/**
	 * Get if we are in creative game mode.
	 */
	public boolean isCreative() {
		return this.gameType.isCreative();
	}

	public void initializeOptimisedGameType(WorldSettings.GameType type){
		if (gameType == WorldSettings.GameType.NOT_SET)
			gameType = type;

		setOptimizedGameType(gameType);
	}

	public void initializeGameType(WorldSettings.GameType type) {
		if (this.gameType == WorldSettings.GameType.NOT_SET) {
			this.gameType = type;
		}

		this.setGameType(this.gameType);
	}

	public void updateBlockRemoving() {
		++this.curblockDamage;

		if (this.receivedFinishDiggingPacket) {
			int i = this.curblockDamage - this.initialBlockDamage;
			Block block = this.theWorld.getBlockState(this.field_180241_i).getBlock();

			if (block.getMaterial() == Material.air) {
				this.receivedFinishDiggingPacket = false;
			} else {
				float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.field_180241_i) * (float) (i + 1);
				int j = (int) (f * 10.0F);

				if (j != this.durabilityRemainingOnBlock) {
					this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180241_i, j);
					this.durabilityRemainingOnBlock = j;
				}

				if (f >= 1.0F) {
					this.receivedFinishDiggingPacket = false;
					this.tryHarvestBlock(this.field_180241_i);
				}
			}
		} else if (this.isDestroyingBlock) {
			Block block1 = this.theWorld.getBlockState(this.field_180240_f).getBlock();

			if (block1.getMaterial() == Material.air) {
				this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, -1);
				this.durabilityRemainingOnBlock = -1;
				this.isDestroyingBlock = false;
			} else {
				int k = this.curblockDamage - this.initialDamage;
				float f1 = block1.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.field_180241_i) * (float) (k + 1);
				int l = (int) (f1 * 10.0F);

				if (l != this.durabilityRemainingOnBlock) {
					this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, l);
					this.durabilityRemainingOnBlock = l;
				}
			}
		}
	}

	/**
	 * If not creative, it calls sendBlockBreakProgress until the block is broken first. tryHarvestBlock can also be the
	 * result of this call.
	 */
	public void onBlockClicked(BlockPos pos, EnumFacing side) {
		if (this.isCreative()) {
			if (!this.theWorld.extinguishFire((Player) null, pos, side)) {
				this.tryHarvestBlock(pos);
			}
		} else {
			Block block = this.theWorld.getBlockState(pos).getBlock();

			if (this.gameType.isAdventure()) {
				if (this.gameType == WorldSettings.GameType.SPECTATOR) {
					return;
				}

				if (!this.thisPlayerMP.isAllowEdit()) {
					ItemStack itemstack = thisPlayerMP.inventory.getCurrentItem();

					if (itemstack == null) {
						return;
					}

					if (!itemstack.canDestroy(block)) {
						return;
					}
				}
			}

			this.theWorld.extinguishFire((Player) null, pos, side);
			this.initialDamage = this.curblockDamage;
			float f = 1.0F;

			if (block.getMaterial() != Material.air) {
				block.onBlockClicked(this.theWorld, pos, this.thisPlayerMP);
				f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos);
			}

			if (block.getMaterial() != Material.air && f >= 1.0F) {
				this.tryHarvestBlock(pos);
			} else {
				this.isDestroyingBlock = true;
				this.field_180240_f = pos;
				int i = (int) (f * 10.0F);
				this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, i);
				this.durabilityRemainingOnBlock = i;
			}
		}
	}

	public void blockRemoving(BlockPos pos) {
		if (pos.equals(this.field_180240_f)) {
			int i = this.curblockDamage - this.initialDamage;
			Block block = this.theWorld.getBlockState(pos).getBlock();

			if (block.getMaterial() != Material.air) {
				float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos) * (float) (i + 1);

				if (f >= 0.7F) {
					this.isDestroyingBlock = false;
					this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, -1);
					this.tryHarvestBlock(pos);
				} else if (!this.receivedFinishDiggingPacket) {
					this.isDestroyingBlock = false;
					this.receivedFinishDiggingPacket = true;
					this.field_180241_i = pos;
					this.initialBlockDamage = this.initialDamage;
				}
			}
		}
	}

	/**
	 * Stops the block breaking process
	 */
	public void cancelDestroyingBlock() {
		this.isDestroyingBlock = false;
		this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.field_180240_f, -1);
	}

	/**
	 * Removes a block and triggers the appropriate events
	 */
	private boolean removeBlock(BlockPos pos) {
		IBlockState iblockstate = this.theWorld.getBlockState(pos);
		iblockstate.getBlock().onBlockHarvested(this.theWorld, pos, iblockstate, this.thisPlayerMP);
		boolean flag = this.theWorld.setBlockToAir(pos);

		if (flag) {
			iblockstate.getBlock().onBlockDestroyedByPlayer(this.theWorld, pos, iblockstate);
		}

		return flag;
	}

	/**
	 * Attempts to harvest a block
	 */
	public void tryHarvestBlock(BlockPos pos) {
        if(ServerEvents.playerBlockBreak.isUseful()) {
            if(ServerEvents.playerBlockBreak.call(new PlayerBlockBreakEvent(thisPlayerMP, pos)).isCanceled()){
                thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(theWorld, pos));
                return;
            }
        }
		if (this.gameType.isCreative() && this.thisPlayerMP.getHeldItem() != null && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemSword) {
			return;
		}
		IBlockState iblockstate = this.theWorld.getBlockState(pos);
		TileEntity tileentity = this.theWorld.getTileEntity(pos);

		if (this.gameType.isAdventure()) {
			if (this.gameType == WorldSettings.GameType.SPECTATOR) {
				return;
			}

			if (!this.thisPlayerMP.isAllowEdit()) {
				ItemStack itemstack = thisPlayerMP.inventory.getCurrentItem();

				if (itemstack == null) {
					return;
				}

				if (!itemstack.canDestroy(iblockstate.getBlock())) {
					return;
				}
			}
		}

		if(!iblockstate.getBlock().canBreakBlock(theWorld, pos, iblockstate, thisPlayerMP)){
			this.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(this.theWorld, pos));
			return;
		}

		this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, pos, Block.getStateId(iblockstate));
		boolean flag1 = this.removeBlock(pos);

		if (this.isCreative()) {
			this.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(this.theWorld, pos));
		} else {
			ItemStack itemstack1 = thisPlayerMP.inventory.getCurrentItem();
			boolean flag = this.thisPlayerMP.canHarvestBlock(iblockstate.getBlock());

			if (itemstack1 != null) {
				itemstack1.onBlockDestroyed(this.theWorld, iblockstate.getBlock(), pos, this.thisPlayerMP);

				if (itemstack1.stackSize == 0) {
					thisPlayerMP.inventory.clearCurrentSlot();
				}
			}

			if (flag1 && flag) {
				iblockstate.getBlock().harvestBlock(this.theWorld, this.thisPlayerMP, pos, iblockstate, tileentity);
			}
		}

		return;
	}

	/**
	 * Attempts to right-click use an item by the given EntityPlayer in the given World
	 */
	public void tryUseItem(Player player, World worldIn, ItemStack stack) {
		if (this.gameType == WorldSettings.GameType.SPECTATOR) return;
		int i = stack.stackSize;
		int j = stack.getMetadata();
		ItemStack itemstack = stack.useItemRightClick(worldIn, player);

		if (itemstack != stack || itemstack != null && (itemstack.stackSize != i || itemstack.getMaxItemUseDuration() > 0 || itemstack.getMetadata() != j)) {
			player.inventory.setCurrentItem(itemstack);

			if (this.isCreative()) {
				itemstack.stackSize = i;

				if (itemstack.isItemStackDamageable()) {
					itemstack.setItemDamage(j);
				}
			}

			if (itemstack.stackSize == 0) {
				player.inventory.clearCurrentSlot();
			}

			if (!player.isUsingItem()) {
				((MPlayer) player).sendContainerToPlayer(player.inventoryContainer);
			}
		}
	}

	/**
	 * Activate the clicked on block, otherwise use the held item.
	 */
	public void activateBlockOrUseItem(Player player, World worldIn, ItemStack stack, BlockPos pos, EnumFacing side, float offsetX, float offsetY, float offsetZ) {
		IBlockState b = worldIn.getBlockState(pos);
		if (ServerEvents.playerBlockInteract.isUseful()) {
			PlayerBlockInteractEvent event = new PlayerBlockInteractEvent(player, stack, pos, b, side, offsetX, offsetY, offsetZ);
			ServerEvents.playerBlockInteract.call(event);
			if (event.isCanceled()) return;
		}
		if (this.gameType == WorldSettings.GameType.SPECTATOR) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof LockableContainer) {
				Block block = worldIn.getBlockState(pos).getBlock();
				LockableContainer ilockablecontainer = (LockableContainer) tileentity;

				if (ilockablecontainer instanceof TileEntityChest && block instanceof BlockChest) {
					ilockablecontainer = ((BlockChest) block).getLockableContainer(worldIn, pos);
				}

				if (ilockablecontainer != null) {
					player.displayGUIChest(ilockablecontainer);
					return;
				}
			} else if (tileentity instanceof Inventory) {
				player.displayGUIChest((Inventory) tileentity);
				return;
			}

			return;
		}
		boolean preventBlockActivation = player.isSneaking() && player.getHeldItem() != null;
		if (!preventBlockActivation) {

			if (b.getBlock().onBlockActivated(worldIn, pos, b, player, side, offsetX, offsetY, offsetZ)) {
				return;
			}
		}

		if (stack == null) {
			return;
		}
		if (this.isCreative()) {
			int j = stack.getMetadata();
			int i = stack.stackSize;
			stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
			stack.setItemDamage(j);
			stack.stackSize = i;
			return;
		}
		stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
	}

	/**
	 * Sets the world instance.
	 */
	public void setWorld(WorldServer serverWorld) {
		this.theWorld = serverWorld;
	}

}
