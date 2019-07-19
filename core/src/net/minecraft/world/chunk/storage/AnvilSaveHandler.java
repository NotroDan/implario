package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;

public class AnvilSaveHandler extends SaveHandler {

	public AnvilSaveHandler(File savesDirectory, String dir, boolean storePlayerdata) {
		super(savesDirectory, dir, storePlayerdata);
	}

	/**
	 * initializes and returns the chunk loader for the specified world provider
	 */
	public IChunkLoader getChunkLoader(WorldProvider provider) {
		File worldDir = this.getWorldDirectory();
		File dimensionDir = provider.getDimensionDir(worldDir);
		return new AnvilChunkLoader(dimensionDir);
	}

	/**
	 * Saves the given World Info with the given NBTTagCompound as the Player.
	 */
	public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
		worldInformation.setSaveVersion(19133);
		super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
	}

	/**
	 * Called to flush all changes to disk, waiting for them to complete.
	 */
	public void flush() {
		try {
			ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
		} catch (InterruptedException interruptedexception) {
			interruptedexception.printStackTrace();
		}

		RegionFileCache.clearRegionFileReferences();
	}

}
