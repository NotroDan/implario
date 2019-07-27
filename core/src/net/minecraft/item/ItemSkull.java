package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.Player;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.creativetab.CreativeTabs;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ItemSkull extends Item {

	private static final String[] skullTypes = new String[] {"skeleton", "wither", "zombie", "char", "creeper"};

	public ItemSkull() {
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public boolean onItemUse(ItemStack stack, Player playerIn, World w, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (side == EnumFacing.DOWN) return false;
		IBlockState iblockstate = w.getBlockState(pos);
		Block block = iblockstate.getBlock();
		boolean flag = block.isReplaceable(w, pos);

		if (!flag) {
			if (!w.getBlockState(pos).getBlock().getMaterial().isSolid()) {
				return false;
			}

			pos = pos.offset(side);
		}

		if (!playerIn.canPlayerEdit(pos, side, stack)) return false;
		if (!Blocks.skull.canPlaceBlockAt(w, pos)) return false;
		if (w.isClientSide) return true;

		w.setBlockState(pos, Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, side), 3);
		int facing = 0;

		if (side == EnumFacing.UP)
			facing = MathHelper.floor_double((double) (playerIn.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;

		TileEntity tile = w.getTileEntity(pos);

		if (tile instanceof TileEntitySkull) {
			TileEntitySkull skull = (TileEntitySkull) tile;

			if (stack.getMetadata() == 3) {
				GameProfile gameprofile = null;

				if (stack.hasTagCompound()) {
					NBTTagCompound nbttagcompound = stack.getTagCompound();

					if (nbttagcompound.hasKey("SkullOwner", 10)) {
						gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
					} else if (nbttagcompound.hasKey("SkullOwner", 8) && nbttagcompound.getString("SkullOwner").length() > 0) {
						gameprofile = new GameProfile(null, nbttagcompound.getString("SkullOwner"));
					}
				}

				skull.setPlayerProfile(gameprofile);
			} else {
				skull.setType(stack.getMetadata());
			}

			skull.setSkullRotation(facing);
			onSkullPlaced(w, pos, skull);
		}

		--stack.stackSize;

		return true;
	}

	protected void onSkullPlaced(World w, BlockPos pos, TileEntitySkull skull) {

	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int i = 0; i < skullTypes.length; ++i) {
			subItems.add(new ItemStack(itemIn, 1, i));
		}
	}

	/**
	 * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
	 * placed as a Block (mostly used with ItemBlocks).
	 */
	public int getMetadata(int damage) {
		return damage;
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
	 * different names based on their damage or NBT.
	 */
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getMetadata();

		if (i < 0 || i >= skullTypes.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + skullTypes[i];
	}

	public String getItemStackDisplayName(ItemStack stack) {
		if (stack.getMetadata() == 3 && stack.hasTagCompound()) {
			if (stack.getTagCompound().hasKey("SkullOwner", 8)) {
				return StatCollector.translateToLocalFormatted("item.skull.player.name", new Object[] {stack.getTagCompound().getString("SkullOwner")});
			}

			if (stack.getTagCompound().hasKey("SkullOwner", 10)) {
				NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("SkullOwner");

				if (nbttagcompound.hasKey("Name", 8)) {
					return StatCollector.translateToLocalFormatted("item.skull.player.name", new Object[] {nbttagcompound.getString("Name")});
				}
			}
		}

		return super.getItemStackDisplayName(stack);
	}

	/**
	 * Called when an ItemStack with NBT data is read to potentially that ItemStack's NBT data
	 */
	public boolean updateItemStackNBT(NBTTagCompound nbt) {
		super.updateItemStackNBT(nbt);

		if (nbt.hasKey("SkullOwner", 8) && nbt.getString("SkullOwner").length() > 0) {
			GameProfile gameprofile = new GameProfile((UUID) null, nbt.getString("SkullOwner"));
			gameprofile = TileEntitySkull.updateGameprofile(gameprofile);
			nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
			return true;
		}
		return false;
	}

}
