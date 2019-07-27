package net.minecraft.world.storage;

import net.minecraft.entity.player.Player;
import net.minecraft.nbt.NBTTagCompound;

public interface IPlayerFileData {

	/**
	 * Writes the player data to disk from the specified PlayerEntityMP.
	 */
	void writePlayerData(Player player);

	/**
	 * Reads the player data from disk into the specified PlayerEntityMP.
	 */
	NBTTagCompound readPlayerData(Player player);

	/**
	 * Returns an array of usernames for which player.dat exists for.
	 */
	String[] getAvailablePlayerDat();

}
