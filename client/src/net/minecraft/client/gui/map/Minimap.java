package net.minecraft.client.gui.map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.MC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ingame.GuiIngame;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Minimap {

	private static DynamicTexture texture;
	private static BufferedImage image;
	private static final int size = 10 * 16;
	private static final int renderSize = 48;
	private static final int offsetRender = 5;


	static {
		texture = new DynamicTexture(size, size);
		image = new BufferedImage(size, size, Image.SCALE_DEFAULT);
	}

	public static void initChunk(Chunk chunk) {
		int chunkX = chunk.xPosition - MC.getPlayer().chunkCoordX, chunkY = chunk.zPosition - MC.getPlayer().chunkCoordZ;
		if (chunk.xPosition == 0 || chunk.zPosition == 0) return;
		Block[][] blocks = getBlocksFromChunk(chunk);
		writeImageFromChunk(blocks, chunkX, chunkY);
		texture.writeBufferedImage(image);
		texture.updateDynamicTexture();
	}

	public static void renderMinimap() {
		int x = ((int) MC.getPlayer().posX) & 15, z = ((int) MC.getPlayer().posX) & 15;
		G.bindTexture(texture.getGlTextureId());
		image.setRGB(size / 2, size / 2, Color.RED.getRGB());
		Gui.drawScaledCustomSizeModalRect(offsetRender, offsetRender,
				size / 2 - x - renderSize, size / 2 - z - renderSize,
				size / 2 + x + renderSize, size / 2 + z + renderSize,
				renderSize, renderSize, size, size);
	}

	private static void writeImageFromChunk(Block[][] map, int chunkX, int chunkZ) {
		int offsetX = size / 2 + chunkX * 16, offsetZ = size / 2 + chunkZ * 16;
		for (int x = 0; x < 16; x++)
			for (int y = 0; y < 16; y++) {
				IBlockState state = map[x][y].getDefaultState();
				image.setRGB(offsetX + x, offsetZ + y, state.getBlock().getMapColor(state).colorValue);
			}
	}

	private static Block[][] getBlocksFromChunk(Chunk chunk) {
		Block[][] blocks = new Block[16][16];
		for (int x = 0; x < 16; x++)
			for (int z = 0; z < 16; z++)
				for (int y = 255; y != -1; y--) {
					Block block = chunk.getBlock(x, y, z);
					blocks[x][z] = block;
					if (block.getMaterial() != Material.air) break;
				}
		return blocks;
	}

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
