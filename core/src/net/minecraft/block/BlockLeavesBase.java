package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.server.Todo;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import java.util.IdentityHashMap;
import java.util.Map;

public class BlockLeavesBase extends Block {

	private static Map mapOriginalOpacity = new IdentityHashMap();
	protected boolean fancyGraphics;

	protected BlockLeavesBase(Material materialIn, boolean fancyGraphics) {
		super(materialIn);
		this.fancyGraphics = fancyGraphics;
	}

	public static void setLightOpacity(Block b, int l) {
		if (!mapOriginalOpacity.containsKey(b)) mapOriginalOpacity.put(b, b.getLightOpacity());
		b.setLightOpacity(l);
	}

	public static void restoreLightOpacity(Block b) {
		if (!mapOriginalOpacity.containsKey(b)) return;
		int i = (Integer) mapOriginalOpacity.get(b);
		setLightOpacity(b, i);
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube() {
		return false;
	}

	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return (!Todo.instance.isCullFacesLeaves() || worldIn.getBlockState(pos).getBlock() != this) && super.shouldSideBeRendered(worldIn, pos, side);
	}

}
