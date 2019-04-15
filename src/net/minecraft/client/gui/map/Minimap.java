package net.minecraft.client.gui.map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.MC;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Minimap {

	public static IBlockState[][][] getMiniMap(int size) {
		IBlockState[][][] miniMap;
		int s = size * 2 + 1;
		miniMap = new IBlockState[s][s][];
		GuiIngame.mapblocks = new BlockPos[s][s][];
		int x = MC.getPlayer().getPosition().getX(), z = MC.getPlayer().getPosition().getZ();
		for (int a = x - size, u = 0; a < x + size; a++, u++)
			for (int b = z - size, f = 0; b < z + size; b++, f++) {

				List<IBlockState> list = new ArrayList<>();
				List<BlockPos> listpos = new ArrayList<>();

				for (int y = 0, v = 0; y < 256; y++, v++) {
					BlockPos pos = new BlockPos(a, y, b);
					IBlockState state = MC.getWorld().getBlockState(pos);
					if (state == null) continue;
					Block block = state.getBlock();
					if (block == Blocks.air || block.hasTileEntity()) continue;
					if (block.isOpaqueCube()) list.clear();
					listpos.add(pos);
					list.add(state);
				}
				miniMap[u][f] = list.toArray(new IBlockState[0]);
				GuiIngame.mapblocks[u][f] = listpos.toArray(new BlockPos[0]);
			}
		return miniMap;
	}


}
