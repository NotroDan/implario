package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.game.model.ModelChest;
import net.minecraft.client.game.model.ModelLargeChest;
import net.minecraft.client.renderer.G;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

import java.util.Calendar;

public class TileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest> {

	private static final ResourceLocation textureTrappedDouble = new ResourceLocation("textures/entity/chest/trapped_double.png");
	private static final ResourceLocation textureChristmasDouble = new ResourceLocation("textures/entity/chest/christmas_double.png");
	private static final ResourceLocation textureNormalDouble = new ResourceLocation("textures/entity/chest/normal_double.png");
	private static final ResourceLocation textureTrapped = new ResourceLocation("textures/entity/chest/trapped.png");
	private static final ResourceLocation textureChristmas = new ResourceLocation("textures/entity/chest/christmas.png");
	private static final ResourceLocation textureNormal = new ResourceLocation("textures/entity/chest/normal.png");
	private ModelChest simpleChest = new ModelChest();
	private ModelChest largeChest = new ModelLargeChest();
	private boolean isChristams;

	public TileEntityChestRenderer() {
		Calendar calendar = Calendar.getInstance();

		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.isChristams = true;
		}
	}

	public void renderTileEntityAt(TileEntityChest chest, double x, double y, double z, float partialTicks, int destroyStage) {
		G.enableDepth();
		G.depthFunc(515);
		G.depthMask(true);
		int i;

		if (!chest.hasWorldObj()) {
			i = 0;
		} else {
			Block block = chest.getBlockType();
			i = chest.getBlockMetadata();

			if (block instanceof BlockChest && i == 0) {
				((BlockChest) block).checkForSurroundingChests(chest.getWorld(), chest.getPos(), chest.getWorld().getBlockState(chest.getPos()));
				i = chest.getBlockMetadata();
			}

			chest.checkForAdjacentChests();
		}

		if (chest.adjacentChestZNeg == null && chest.adjacentChestXNeg == null) {
			ModelChest modelchest;

			if (chest.adjacentChestXPos == null && chest.adjacentChestZPos == null) {
				modelchest = this.simpleChest;

				if (destroyStage >= 0) {
					this.bindTexture(DESTROY_STAGES[destroyStage]);
					G.matrixMode(5890);
					G.pushMatrix();
					G.scale(4.0F, 4.0F, 1.0F);
					G.translate(0.0625F, 0.0625F, 0.0625F);
					G.matrixMode(5888);
				} else if (chest.getChestType() == 1) {
					this.bindTexture(textureTrapped);
				} else if (this.isChristams) {
					this.bindTexture(textureChristmas);
				} else {
					this.bindTexture(textureNormal);
				}
			} else {
				modelchest = this.largeChest;

				if (destroyStage >= 0) {
					this.bindTexture(DESTROY_STAGES[destroyStage]);
					G.matrixMode(5890);
					G.pushMatrix();
					G.scale(8.0F, 4.0F, 1.0F);
					G.translate(0.0625F, 0.0625F, 0.0625F);
					G.matrixMode(5888);
				} else if (chest.getChestType() == 1) {
					this.bindTexture(textureTrappedDouble);
				} else if (this.isChristams) {
					this.bindTexture(textureChristmasDouble);
				} else {
					this.bindTexture(textureNormalDouble);
				}
			}

			G.pushMatrix();
			G.enableRescaleNormal();

			if (destroyStage < 0) {
				G.color(1.0F, 1.0F, 1.0F, 1.0F);
			}

			G.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
			G.scale(1.0F, -1.0F, -1.0F);
			G.translate(0.5F, 0.5F, 0.5F);
			int j = 0;

			if (i == 2) {
				j = 180;
			}

			if (i == 3) {
				j = 0;
			}

			if (i == 4) {
				j = 90;
			}

			if (i == 5) {
				j = -90;
			}

			if (i == 2 && chest.adjacentChestXPos != null) {
				G.translate(1.0F, 0.0F, 0.0F);
			}

			if (i == 5 && chest.adjacentChestZPos != null) {
				G.translate(0.0F, 0.0F, -1.0F);
			}

			G.rotate((float) j, 0.0F, 1.0F, 0.0F);
			G.translate(-0.5F, -0.5F, -0.5F);
			float f = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;

			if (chest.adjacentChestZNeg != null) {
				float f1 = chest.adjacentChestZNeg.prevLidAngle + (chest.adjacentChestZNeg.lidAngle - chest.adjacentChestZNeg.prevLidAngle) * partialTicks;

				if (f1 > f) {
					f = f1;
				}
			}

			if (chest.adjacentChestXNeg != null) {
				float f2 = chest.adjacentChestXNeg.prevLidAngle + (chest.adjacentChestXNeg.lidAngle - chest.adjacentChestXNeg.prevLidAngle) * partialTicks;

				if (f2 > f) {
					f = f2;
				}
			}

			f = 1.0F - f;
			f = 1.0F - f * f * f;
			modelchest.chestLid.rotateAngleX = -(f * (float) Math.PI / 2.0F);
			modelchest.renderAll();
			G.disableRescaleNormal();
			G.popMatrix();
			G.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (destroyStage >= 0) {
				G.matrixMode(5890);
				G.popMatrix();
				G.matrixMode(5888);
			}
		}
	}

}
