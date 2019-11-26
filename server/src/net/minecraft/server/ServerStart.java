package net.minecraft.server;

import net.minecraft.database.memory.MemoryStorage;
import net.minecraft.init.Bootstrap;
import net.minecraft.logging.Log;
import net.minecraft.resources.DatapackManager;
import net.minecraft.resources.load.DatapackLoadException;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.security.MinecraftSecurityManager;
import net.minecraft.security.Restart;
import net.minecraft.server.dedicated.DedicatedServer;

import java.io.File;

public class ServerStart {
	static{
		if(System.getSecurityManager() == null) System.setSecurityManager(new MinecraftSecurityManager());
	}

	public static void main(String[] args) throws DatapackLoadException {
		Restart.setArgs(args);
		String serverOwner = null;
		String workDir = ".";
		String worldName = null;
		boolean starterKit = false;
		int i = -1;

		for (int j = 0; j < args.length; ++j) {
			String s3 = args[j];
			String s4 = j == args.length - 1 ? null : args[j + 1];
			boolean wasArgumentUsed = false;

			if (s3.equals("--port") && s4 != null) {
				wasArgumentUsed = true;

				try {
					i = Integer.parseInt(s4);
				} catch (NumberFormatException ignored) {}
			} else if (s3.equals("--singleplayer") && s4 != null) {
				wasArgumentUsed = true;
				serverOwner = s4;
			} else if (s3.equals("--universe") && s4 != null) {
				wasArgumentUsed = true;
				workDir = s4;
			} else if (s3.equals("--world") && s4 != null) {
				wasArgumentUsed = true;
				worldName = s4;
			} else if (s3.equals("--bonusChest")) starterKit = true;

			if (wasArgumentUsed) ++j;
		}

		MinecraftServer.storage = new MemoryStorage(new File(workDir), true);

		DatapackManager.loadDir(new File("datapacks"));
		Bootstrap.register();
		for (DatapackLoader loader : DatapackManager.getTree().loadingOrder()) {
			Log.MAIN.info("Initializing " + loader.getProperties());
			loader.getInstance().init();
		}

		final DedicatedServer dedicatedserver = new DedicatedServer(new File(workDir));

		if (serverOwner != null) dedicatedserver.setServerOwner(serverOwner);

		if (worldName != null) dedicatedserver.setFolderName(worldName);

		if (i >= 0) dedicatedserver.setServerPort(i);

		if (starterKit) dedicatedserver.canCreateBonusChest(true);

		dedicatedserver.startServerThread();
		Runtime.getRuntime().addShutdownHook(new Thread("Server Shutdown Thread") {
			public void run() {
				if (!dedicatedserver.isServerStopped())
				dedicatedserver.stopServer();
			}
		});
	}

}
