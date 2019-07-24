package net.minecraft.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public abstract class WorldService<T extends WorldServer> {

	protected final MinecraftServer server;
	protected String userMessage;
	protected int dimensionAmount;

	public WorldService(MinecraftServer server) {
		this.server = server;
	}

	public synchronized String getUserMessage() {
		return this.userMessage;
	}

	public synchronized void setUserMessage(String message) {
		this.userMessage = message;
	}

	public abstract T[] getAll();

	public abstract WorldServer loadDim(int dim, String worldName, WorldInfo info, WorldSettings settings, ISaveHandler isavehandler);

	public abstract T getWorld(int dim);

	public int getDimensionAmount() {
		return dimensionAmount;
	}

}
