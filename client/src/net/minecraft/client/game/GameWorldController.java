package net.minecraft.client.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.game.entity.CPlayer;
import net.minecraft.client.game.input.MovementInputFromOptions;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.Lang;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.protocol.Protocols;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.FileUtil;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldService;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import java.net.SocketAddress;

public class GameWorldController {

	private final Minecraft mc;
	public Entity renderViewEntity;
	public final AnvilSaveConverter saveLoader;

	public GameWorldController(Minecraft minecraft) {
		mc = minecraft;
		saveLoader = new AnvilSaveConverter(FileUtil.getFile("saves"));
	}

	public void setDimensionAndSpawnPlayer(int dimension, Minecraft mc) {
		mc.theWorld.setInitialSpawnLocation();
		mc.theWorld.removeAllEntities();
		int i = 0;
		String s = null;

		if (mc.thePlayer != null) {
			i = mc.thePlayer.getEntityId();
			mc.theWorld.removeEntity(mc.thePlayer);
			s = mc.thePlayer.getClientBrand();
		}

		renderViewEntity = null;
		CPlayer entityplayersp = mc.thePlayer;
		mc.thePlayer = mc.playerController.func_178892_a(mc.theWorld, mc.thePlayer == null ? new StatFileWriter() : mc.thePlayer.getStatFileWriter());
		mc.thePlayer.getDataWatcher().updateWatchedObjectsFromList(entityplayersp.getDataWatcher().getAllWatched());
		mc.thePlayer.dimension = dimension;
		renderViewEntity = mc.thePlayer;
		mc.thePlayer.preparePlayerToSpawn();
		mc.thePlayer.setClientBrand(s);
		mc.theWorld.spawnEntityInWorld(mc.thePlayer);
		mc.playerController.flipPlayer(mc.thePlayer);
		mc.thePlayer.movementInput = new MovementInputFromOptions();
		mc.thePlayer.setEntityId(i);
		mc.playerController.setPlayerCapabilities(mc.thePlayer);
		mc.thePlayer.setReducedDebug(entityplayersp.hasReducedDebug());

		if (mc.currentScreen instanceof GuiGameOver) {
			mc.displayGuiScreen(null);
		}
	}

	/**
	 * par2Str is displayed on the loading screen to the user unloads the current world first
	 */
	public void loadWorld(WorldClient worldClientIn, String loadingMessage, Minecraft mc) {
		if (worldClientIn == null) {
			NetHandlerPlayClient nethandlerplayclient = mc.getNetHandler();

			if (nethandlerplayclient != null) {
				nethandlerplayclient.cleanup();
			}

			if (mc.theIntegratedServer != null && mc.theIntegratedServer.isAnvilFileSet()) {
				mc.theIntegratedServer.initiateShutdown();
				mc.theIntegratedServer.setStaticInstance();
			}

			mc.theIntegratedServer = null;
			mc.guiAchievement.clearAchievements();
			mc.entityRenderer.getMapItemRenderer().clearLoadedMaps();
		}

		renderViewEntity = null;
		mc.myNetworkManager = null;

		if (mc.loadingScreen != null) {
			mc.loadingScreen.resetProgressAndMessage(loadingMessage);
			mc.loadingScreen.displayLoadingString("");
		}

		if (worldClientIn == null && mc.theWorld != null) {
			mc.getResourcePackRepository().func_148529_f();
			mc.ingameGUI.func_181029_i();
			mc.setServerData(null);
			mc.integratedServerIsRunning = false;
		}

		mc.getSoundHandler().stopSounds();
		mc.theWorld = worldClientIn;

		if (worldClientIn != null) {
			if (mc.renderGlobal != null) {
				mc.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
			}

			if (mc.effectRenderer != null) {
				mc.effectRenderer.clearEffects(worldClientIn);
			}

			if (mc.thePlayer == null) {
				mc.thePlayer = mc.playerController.func_178892_a(worldClientIn, new StatFileWriter());
				mc.playerController.flipPlayer(mc.thePlayer);
			}

			mc.thePlayer.preparePlayerToSpawn();
			worldClientIn.spawnEntityInWorld(mc.thePlayer);
			mc.thePlayer.movementInput = new MovementInputFromOptions();
			mc.playerController.setPlayerCapabilities(mc.thePlayer);
			renderViewEntity = mc.thePlayer;
		} else {
			saveLoader.flushCache();
			mc.thePlayer = null;
		}

		System.gc();
		mc.systemTime = 0L;
	}

	/**
	 * unloads the current world first
	 */
	public void loadWorld(WorldClient worldClientIn, Minecraft mc) {
		loadWorld(worldClientIn, "", mc);
	}

	/**
	 * Arguments: World foldername,  World ingame name, WorldSettings
	 */
	public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn, Minecraft mc) {
		loadWorld(null, mc);
		System.gc();
		ISaveHandler isavehandler = saveLoader.getSaveLoader(folderName, false);
		WorldInfo worldinfo = isavehandler.loadWorldInfo();

		if (worldinfo == null && worldSettingsIn != null) {
			worldinfo = new WorldInfo(worldSettingsIn, folderName);
			isavehandler.saveWorldInfo(worldinfo);
		}

		if (worldSettingsIn == null) {
			worldSettingsIn = new WorldSettings(worldinfo);
		}

		try {
			mc.theIntegratedServer = new IntegratedServer(mc, folderName, worldName, worldSettingsIn);
			mc.theIntegratedServer.startServerThread();
			mc.integratedServerIsRunning = true;
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
			crashreportcategory.addCrashSection("Level ID", folderName);
			crashreportcategory.addCrashSection("Level Name", worldName);
			throw new ReportedException(crashreport);
		}

		mc.loadingScreen.displaySavingString(Lang.format("menu.loadingLevel"));

		while (!mc.theIntegratedServer.serverIsInRunLoop()) {
			WorldService s = mc.theIntegratedServer.worldService;

			if (s != null) {
				mc.loadingScreen.displayLoadingString(Lang.format(s.getUserMessage()));
			} else {
				mc.loadingScreen.displayLoadingString("");
			}

			try {
				Thread.sleep(200L);
			} catch (InterruptedException ignored) {}
		}

		mc.displayGuiScreen(null);
		SocketAddress socketaddress = mc.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
		NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
		networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, mc, null));
		networkmanager.sendPacket(new C00Handshake(47, socketaddress.toString(), 0, Protocols.LOGIN));
		networkmanager.sendPacket(new C00PacketLoginStart(mc.getSession().getProfile()));
		mc.myNetworkManager = networkmanager;
	}

	/**
	 * Returns the save loader that is currently being used
	 */
	public ISaveFormat getSaveLoader() {
		return saveLoader;
	}

}
