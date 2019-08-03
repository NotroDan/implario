package optifine;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMycelium;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BetterGrass {
	private static IBakedModel modelCubeMycelium = null;
	private static IBakedModel modelCubeGrassSnowy = null;
	private static IBakedModel modelCubeGrass = null;

	public static void update() {
		modelCubeGrass = BlockModelUtils.makeModelCube("minecraft:blocks/grass_top", 0);
		modelCubeGrassSnowy = BlockModelUtils.makeModelCube("minecraft:blocks/snow", -1);
		modelCubeMycelium = BlockModelUtils.makeModelCube("minecraft:blocks/mycelium_top", -1);
	}

	public static List<BakedQuad> getFaceQuads(IBlockAccess blockAccess, Block block, BlockPos blockPos, EnumFacing facing, List<BakedQuad> list) {
		if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
			if (block instanceof BlockMycelium)
				return Config.isBetterGrassFancy() ? getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.mycelium
						? modelCubeMycelium.getFaceQuads(facing) : list : modelCubeMycelium.getFaceQuads(facing);
			if (block instanceof BlockGrass) {
				Block blockUp = blockAccess.getBlockState(blockPos.up()).getBlock();
				boolean flag = blockUp == Blocks.snow || blockUp == Blocks.snow_layer;

				if (!Config.isBetterGrassFancy())
					if (flag)
						return modelCubeGrassSnowy.getFaceQuads(facing);
					else
						return modelCubeGrass.getFaceQuads(facing);

				if (flag)
					if (getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer)
						return modelCubeGrassSnowy.getFaceQuads(facing);
				else if (getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.grass)
					return modelCubeGrass.getFaceQuads(facing);
			}

			return list;
		}
		return list;
	}

	private static Block getBlockAt(BlockPos blockPos, EnumFacing facing, IBlockAccess blockAccess) {
		BlockPos blockpos = blockPos.offset(facing);
		return blockAccess.getBlockState(blockpos).getBlock();
	}
}
