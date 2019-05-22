package net.minecraft.client;

import net.minecraft.client.settings.Settings;
import net.minecraft.world.World;
import optifine.BlockPosM;
import optifine.Config;

public class TodoClient extends net.minecraft.server.Todo {

	@Override
	public boolean isSmoothWorld() {
		return Config.isSmoothWorld();
	}

	@Override
	public boolean isCullFacesLeaves() {
		return Config.isCullFacesLeaves();
	}

	@Override
	public boolean shouldUseOptifineOpaquenessChecking() {
		return true;
	}

	@Override
	public boolean isEntityInsideOpaqueBlockOptifineTupoeGovnoSdohniNahuyPozhaluysta(boolean noClip, double posX, double posY, double posZ, double width, double eyeHeight, World worldObj) {
		if (noClip) return false;

		BlockPosM blockposm = new BlockPosM(0, 0, 0);

		for (int i = 0; i < 8; ++i) {
			double d0 = posX + ((float) (i / 1 % 2) - 0.5F) * width * 0.8F;
			double d1 = posY + (double) (((float) (i / 2 % 2) - 0.5F) * 0.1F);
			double d2 = posZ + ((float) (i / 4 % 2) - 0.5F) * width * 0.8F;
			blockposm.setXyz(d0, d1 + eyeHeight, d2);

			if (worldObj.getBlockState(blockposm).getBlock().isVisuallyOpaque()) return true;
		}

		return false;
	}

	@Override
	public boolean shouldUseRomanianNotation(int level) {
		return Settings.ROMANIAN_NOTATION.i() == 0 ? level <= 100 : Settings.ROMANIAN_NOTATION.i() == 1;
	}

	@Override
	public boolean isServerSide() {
		return false;
	}

}
