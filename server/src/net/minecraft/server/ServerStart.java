package net.minecraft.server;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.database.memory.MemoryStorage;
import net.minecraft.init.Bootstrap;
import net.minecraft.logging.Log;
import net.minecraft.resources.DatapackManager;
import net.minecraft.resources.load.DatapackLoadException;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.resources.load.JarDatapackLoader;
import net.minecraft.security.MinecraftSecurityManager;
import net.minecraft.security.Restart;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Util;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ServerStart {
	static{
		if(System.getSecurityManager() == null) System.setSecurityManager(new MinecraftSecurityManager());
	}

	public static void main(String[] args) throws DatapackLoadException {
		Restart.setArgs(args);

		OptionParser parser = new OptionParser(true);
		parser.allowsUnrecognizedOptions();
		parser.accepts("starter-kit");
		OptionSpec<Integer> spPort = parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
		OptionSpec<String> spOwner = parser.accepts("owner").withRequiredArg();
		OptionSpec<String> spWorkDir = parser.accepts("root").withRequiredArg().defaultsTo(".");
		OptionSpec<String> spWorld = parser.accepts("world").withRequiredArg();
		OptionSpec<String> spDatapack = parser.accepts("datapack").withRequiredArg();
		OptionSpec<String> spIgnored = parser.nonOptions();

		OptionSet options = parser.parse(args);
		List<String> ignored = options.valuesOf(spIgnored);
		if (!ignored.isEmpty()) System.out.println("Ignored arguments: " + ignored);


		String serverOwner = options.valueOf(spOwner);
		String workDir = options.valueOf(spWorkDir);
		String worldName = options.valueOf(spWorld);
		boolean starterKit = options.has("starter-kit");
		int port = options.valueOf(spPort);
		List<String> datapacks = options.valuesOf(spDatapack);

		MinecraftServer.storage = new MemoryStorage(new File(workDir), true);

//		if (!datapacks.isEmpty()) {
//			for (String datapackPath : datapacks) {
//				File file = new File(datapackPath);
//				if (!file.exists() || !file.isFile() || !datapackPath.endsWith(".jar")) {
//					System.out.println("Unable to find datapack '" + datapackPath + "'");
//					continue;
//				}
//				DatapackManager.prepare(new JarDatapackLoader(file));
//			}
//			DatapackManager.getTree().rebuild();
//		}


		List<DatapackLoader> datapackLoaders = DatapackManager.validateDir(new File("datapacks"));
		List<DatapackLoader> custom = Util.map(datapacks, DatapackManager::validateJar);
		datapackLoaders.addAll(custom);

		DatapackManager.prepareAndLoad(datapackLoaders);

//		DatapackManager.prepareAndLoadDir(new File("datapacks"));
		Bootstrap.register();
		for (DatapackLoader loader : DatapackManager.getTree().loadingOrder()) {
			Log.MAIN.info("Initializing " + loader.getProperties());
			loader.getInstance().init();
		}

		final DedicatedServer dedicatedserver = new DedicatedServer(new File(workDir));

		if (serverOwner != null) dedicatedserver.setServerOwner(serverOwner);

		if (worldName != null) dedicatedserver.setFolderName(worldName);

		if (port >= 0) dedicatedserver.setServerPort(port);

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
