package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;

import java.util.List;
import java.util.Random;

public class BlockStone extends Block {

	public static final PropertyEnum<BlockStone.EnumType> VARIANT = PropertyEnum.create("variant", BlockStone.EnumType.class);

	public BlockStone() {
		super(Material.rock);
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockStone.EnumType.STONE));
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	private boolean inited = false;

	public void postInit(){
		for(int i = 0; i < states.length; i++){
			states[i] = getDefaultState().withProperty(VARIANT, EnumType.values()[i]);
		}
		inited = true;
	}

	/**
	 * Gets the localized name of this block. Used for the statistics page.
	 */
	public String getLocalizedName() {
		return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockStone.EnumType.STONE.getUnlocalizedName() + ".name");
	}

	/**
	 * Get the MapColor for this Block and the given BlockState
	 */
	public MapColor getMapColor(IBlockState state) {
		return ((BlockStone.EnumType) state.getValue(VARIANT)).getMapColor();
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return state.getValue(VARIANT) == BlockStone.EnumType.STONE ? Item.getItemFromBlock(Blocks.cobblestone) : Item.getItemFromBlock(Blocks.stone);
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	public int damageDropped(IBlockState state) {
		return ((BlockStone.EnumType) state.getValue(VARIANT)).getMetadata();
	}

	/**
	 * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
	 */
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for (BlockStone.EnumType blockstone$enumtype : BlockStone.EnumType.values()) {
			list.add(new ItemStack(itemIn, 1, blockstone$enumtype.getMetadata()));
		}
	}

	private static IBlockState states[] = new IBlockState[7];

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		if(!inited)postInit();
		return states[meta];
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		if(!inited)postInit();
		for(int i = 0; i < 7; i++)
			if(states[i] == state)return i;
		throw new Error();
	}

	protected BlockState createBlockState() {
		return new BlockState(this, VARIANT);
	}

	public enum EnumType implements IStringSerializable {
		STONE(0, MapColor.stoneColor, "stone"),
		GRANITE(1, MapColor.dirtColor, "granite"),
		GRANITE_SMOOTH(2, MapColor.dirtColor, "smooth_granite", "graniteSmooth"),
		DIORITE(3, MapColor.quartzColor, "diorite"),
		DIORITE_SMOOTH(4, MapColor.quartzColor, "smooth_diorite", "dioriteSmooth"),
		ANDESITE(5, MapColor.stoneColor, "andesite"),
		ANDESITE_SMOOTH(6, MapColor.stoneColor, "smooth_andesite", "andesiteSmooth");

		private final int meta;
		private final String name;
		private final String unlocalizedName;
		private final MapColor mapColor;

		EnumType(int meta, MapColor mapColor, String name) {
			this(meta, mapColor, name, name);
		}

		EnumType(int meta, MapColor mapColor, String name, String unlocalizedName) {
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
			this.mapColor = mapColor;
		}

		public int getMetadata() {
			return this.meta;
		}

		public MapColor getMapColor() {
			return this.mapColor;
		}

		public String toString() {
			return this.name;
		}

		public static BlockStone.EnumType byMetadata(int meta) {
			if (meta < 0 || meta >= 7) meta = 0;
			return values()[meta];
		}

		public String getName() {
			return this.name;
		}

		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

	}

}
