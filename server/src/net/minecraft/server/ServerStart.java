package net.minecraft.server;

import net.minecraft.init.Bootstrap;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Datapacks;
import net.minecraft.server.dedicated.DedicatedServer;
import vanilla.Vanilla;

import java.io.File;

public class ServerStart {
	public static void main(String[] args) {
		Datapacks.loadSimple(new Vanilla());
		Bootstrap.register();
		for (Datapack datapack : Datapacks.getDatapacks()) {
			datapack.init();
			datapack.ready();
		}

		String s = null;
		String s1 = ".";
		String s2 = null;
		boolean flag2 = false;
		int i = -1;

		for (int j = 0; j < args.length; ++j) {
			String s3 = args[j];
			String s4 = j == args.length - 1 ? null : args[j + 1];
			boolean flag3 = false;

			if (s3.equals("--port") && s4 != null) {
				flag3 = true;

				try {
					i = Integer.parseInt(s4);
				} catch (NumberFormatException ignored) {}
			} else if (s3.equals("--singleplayer") && s4 != null) {
				flag3 = true;
				s = s4;
			} else if (s3.equals("--universe") && s4 != null) {
				flag3 = true;
				s1 = s4;
			} else if (s3.equals("--world") && s4 != null) {
				flag3 = true;
				s2 = s4;
			} else if (s3.equals("--bonusChest")) flag2 = true;

			if (flag3) ++j;
		}

		final DedicatedServer dedicatedserver = new DedicatedServer(new File(s1));

		if (s != null) dedicatedserver.setServerOwner(s);

		if (s2 != null) dedicatedserver.setFolderName(s2);

		if (i >= 0) dedicatedserver.setServerPort(i);

		if (flag2) dedicatedserver.canCreateBonusChest(true);

		dedicatedserver.startServerThread();
		Runtime.getRuntime().addShutdownHook(new Thread("Server Shutdown Thread") {
			public void run() {
				dedicatedserver.stopServer();
			}
		});
	}

}
