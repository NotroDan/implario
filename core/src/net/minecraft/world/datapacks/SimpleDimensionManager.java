package net.minecraft.world.datapacks;

import net.minecraft.world.WorldProvider;

import java.io.File;

public class SimpleDimensionManager extends WorldProvider {

	public SimpleDimensionManager(int dim) {
		this.dimensionId = dim;
	}

	@Override
	public String getDimensionName() {
		return "Dimension " + dimensionId;
	}

	@Override
	public String getInternalNameSuffix() {
		return dimensionId == 0 ? "" : "_" + dimensionId;
	}

	@Override
	public File getDimensionDir(File worldDir) {
		if (dimensionId == 0) return worldDir;
		File file = new File(worldDir, "DIM" + dimensionId);
		file.mkdir();
		return file;
	}

}
