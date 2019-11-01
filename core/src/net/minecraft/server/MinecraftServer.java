package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.Logger;
import net.minecraft.command.*;
import net.minecraft.crash.CrashReport;
import net.minecraft.database.Storage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.MPlayer;
import net.minecraft.logging.IProfiler;
import net.minecraft.logging.Profiler;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.resources.Provider;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.*;
import net.minecraft.util.chat.ChatComponentText;
import net.minecraft.world.*;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public abstract class MinecraftServer implements Runnable, ICommandSender, IThreadListener {

	public static final IProfiler profiler = new Profiler();
	public static final Provider<MinecraftServer, WorldService> WORLD_SERVICE_PROVIDER = new Provider<>(SimpleWorldService::new);
	private static final Logger logger = Logger.getInstance();
	public static final File USER_CACHE_FILE = new File(Todo.instance.isServerSide() ? "usercache.json" : "gamedata/usercache.json");
	public static MinecraftServer mcServer;
	public static Storage storage;

	private final File anvilFile;
	private final ISaveFormat anvilConverterForAnvilFile;
	protected final ICommandManager commandManager;
	private final NetworkSystem networkSystem;
	private final ServerStatusResponse statusResponse = new ServerStatusResponse();
	private final Random random = new Random();
	public long tickLength = 50L;
	private int serverPort = -1;
	public WorldService worldService;
	private ServerConfigurationManager serverConfigManager;
	private boolean serverRunning = true;
	private boolean serverStopped;
	private int tickCounter;
	protected final Proxy serverProxy;

	/**
	 * The task the server is currently working on(and will output on outputPercentRemaining).
	 */
	public String currentTask;

	/**
	 * The percentage of the current task finished so far.
	 */
	public int percentDone;

	private boolean onlineMode;
	private boolean canSpawnAnimals;
	private boolean canSpawnNPCs;
	private boolean pvpEnabled;
	private boolean allowFlight;
	private String motd;
	private int buildLimit;
	private int maxPlayerIdleMinutes = 0;
	public final long[] tickTimeArray = new long[100];

	/**
	 * Stats are [dimension][tick%100] system.nanoTime is stored.
	 */
	public long[][] timeOfLastDimensionTick;
	private KeyPair serverKeyPair;
	private String serverOwner;
	private String folderName;
	private String worldName;
	public boolean starterKit;
	protected String hostname;

	/**
	 * If true, there is no need to save chunks or stop the server, because that is already being done.
	 */
	private boolean worldIsBeingDeleted;

	/**
	 * The texture pack for the server
	 */
	private String resourcePackUrl = "";
	private String resourcePackHash = "";
	private boolean serverIsRunning;

	/**
	 * Set when warned for "Can't keep up", which triggers again after 15 seconds.
	 */
	private long timeOfLastWarning;
	private boolean startProfiling;
	private final YggdrasilAuthenticationService authService;
	private final MinecraftSessionService sessionService;
	private long nanoTimeSinceStatusRefresh = 0L;
	private final GameProfileRepository profileRepo;
	private final PlayerProfileCache profileCache;
	protected final Queue<FutureTask<?>> futureTaskQueue = new ArrayDeque<>();
	private Thread serverThread;
	private volatile long currentTime = getCurrentTimeMillis();

	public MinecraftServer(Proxy proxy, File workDir) {
		this.serverProxy = proxy;
		mcServer = this;
		this.anvilFile = null;
		this.networkSystem = null;
		this.profileCache = new PlayerProfileCache(this, workDir);
		this.commandManager = null;
		this.anvilConverterForAnvilFile = null;
		this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
		this.sessionService = this.authService.createMinecraftSessionService();
		this.profileRepo = this.authService.createProfileRepository();
	}

	public MinecraftServer(File workDir, Proxy proxy, File profileCacheDir) {
		this.serverProxy = proxy;
		mcServer = this;
		this.anvilFile = workDir;
		this.networkSystem = new NetworkSystem(this);
		this.profileCache = new PlayerProfileCache(this, profileCacheDir);
		this.commandManager = this.createNewCommandManager();
		this.anvilConverterForAnvilFile = new AnvilSaveConverter(workDir);
		this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
		this.sessionService = this.authService.createMinecraftSessionService();
		this.profileRepo = this.authService.createProfileRepository();
	}

	protected ServerCommandManager createNewCommandManager() {
		return new ServerCommandManager();
	}

	/**
	 * Initialises the server and starts it.
	 */
	protected abstract boolean startServer() throws IOException;

	protected void convertMapIfNeeded(String worldNameIn) {
		// Значит ситуация такая: Миры при создании каждый раз конвертируются этим методом, и вроде как он нужен, НО без него НИЧЕГО не меняется и всё продолжает работать.
		// Поэтому ToDo: разобраться, нахера эта конвертация инвокается постоянно
		if (true) return;
		if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn)) {
			logger.info("Converting map!");
			worldService.setUserMessage("menu.convertingLevel");
			this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate() {
				private long startTime = System.currentTimeMillis();

				public void displaySavingString(String message) {}
				public void resetProgressAndMessage(String message) {}

				public void setLoadingProgress(int progress) {
					if (System.currentTimeMillis() - this.startTime < 1000L) return;
					this.startTime = System.currentTimeMillis();
					MinecraftServer.logger.info("Converting... " + progress + "%");
				}

				public void setDoneWorking() {}

				public void displayLoadingString(String message) {}
			});
		}
	}

	public Storage getStorage(){
		return storage;
	}

	protected void loadAllWorlds(String name, String globalName, long seed, WorldType type, String p_71247_6_) {
		this.convertMapIfNeeded(name);
		worldService = WORLD_SERVICE_PROVIDER.provide(this);
		worldService.setUserMessage("menu.loadingLevel");
		this.timeOfLastDimensionTick = new long[worldService.getDimensionAmount()][100];
		ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(name, true);
		this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
		WorldInfo worldinfo = isavehandler.loadWorldInfo();
		WorldSettings worldsettings;

		if (worldinfo == null) {
			worldsettings = new WorldSettings(seed, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), type);
			worldsettings.setWorldName(p_71247_6_);
			if (this.starterKit) worldsettings.enableStarterKit();
			worldinfo = new WorldInfo(worldsettings, globalName);
		} else {
			worldinfo.setWorldName(globalName);
			worldsettings = new WorldSettings(worldinfo);
		}

		for (int i = 0; i < worldService.getDimensionAmount(); ++i) {
			WorldServer server = worldService.loadDim(i, globalName, worldinfo, worldsettings, isavehandler);
			server.addWorldAccess(new WorldManager(this, server));
			if (!this.isSinglePlayer()) server.getWorldInfo().setGameType(this.getGameType());
		}

		this.getConfigurationManager().setPlayerManager(worldService.getWorld(0));

		this.setDifficultyForAllWorlds(this.getDifficulty());
		this.initialWorldChunkLoad();
	}

	public void initialWorldChunkLoad() {
		int loaded = 0;
		worldService.setUserMessage("menu.generatingTerrain");
		logger.info("Подготовка чанков спавна для мира " + 0);
		WorldServer worldserver = worldService.getWorld(0);
		BlockPos pos = worldserver.getSpawnPoint();
		long lastMessageTime = getCurrentTimeMillis();

		int centerX = pos.getX() >> 2, centerZ = pos.getZ() >> 2;

		for (int x = -12; x <= 12; x++) {
			for (int z = -12; z <= 12; z++) {
				long time = getCurrentTimeMillis();
				if (time - lastMessageTime > 1000L) {
					this.outputPercentRemaining("Прогружаем центральные чанки", loaded, 625);
					lastMessageTime = time;
				}

				++loaded;
				worldserver.theChunkProviderServer.loadChunk(centerX + x, centerZ + z);
			}
		}

		this.clearCurrentTask();
	}

	public void setResourcePackFromWorld(String worldNameIn, ISaveHandler saveHandlerIn) {
		File resourcePackFile = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");
		if (resourcePackFile.isFile()) this.setResourcePack("level://" + worldNameIn + "/" + resourcePackFile.getName(), "");
	}

	public String getServerHostname() {
		return hostname;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public abstract boolean canStructuresSpawn();

	public abstract WorldSettings.GameType getGameType();

	/**
	 * Get the server's difficulty
	 */
	public abstract EnumDifficulty getDifficulty();

	/**
	 * Defaults to false.
	 */
	public abstract boolean isHardcore();

	public abstract int getOpPermissionLevel();

	public abstract boolean opsSeeConsole();

	/**
	 * Used to display a percent remaining given text and the percentage.
	 */
	protected void outputPercentRemaining(String message, int done, int total) {
		this.currentTask = message;
		int percent = done * 100 / total;
		this.percentDone = percent;
		logger.info(message + ": " + done + " из " + total + " (" + percent + "%)");
	}

	/**
	 * Set current task to null and set its percentage to 0.
	 */
	protected void clearCurrentTask() {
		this.currentTask = null;
		this.percentDone = 0;
	}

	public int getServerPort() {
		return serverPort;
	}

	public WorldServer[] getWorlds() {
		return worldService.getAll();
	}

	/**
	 * par1 indicates if a log message should be output.
	 */
	protected void saveAllWorlds(boolean dontLog) {
		if (!this.worldIsBeingDeleted) {
			for (WorldServer worldserver : getWorlds()) {
				if (worldserver != null) {
					if (!dontLog) {
						logger.info("Saving chunks for level \'" + worldserver.getWorldInfo().getWorldName() + "\'/" + worldserver.provider.getDimensionName());
					}

					try {
						worldserver.saveAllChunks(true, null);
					} catch (MinecraftException minecraftexception) {
						logger.warn(minecraftexception.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Saves all necessary data as preparation for stopping the server.
	 */
	public void stopServer() {
		if (!this.worldIsBeingDeleted) {
			logger.info("Stopping server");

			if (this.getNetworkSystem() != null) {
				this.getNetworkSystem().terminateEndpoints();
			}

			if (this.serverConfigManager != null) {
				logger.info("Saving players");
				this.serverConfigManager.saveAllPlayerData();
				this.serverConfigManager.removeAllPlayers();
			}

			if (this.worldService != null) {
				logger.info("Saving worlds");
				this.saveAllWorlds(false);

				for (WorldServer worldserver : getWorlds()) {
					if (worldserver != null) worldserver.flush();
				}
			}

		}
	}

	public boolean isServerRunning() {
		return this.serverRunning;
	}

	/**
	 * Sets the serverRunning variable to false, in order to get the server to shut down.
	 */
	public void initiateShutdown() {
		for (MPlayer player : getConfigurationManager().getPlayers().toArray(new MPlayer[]{})) {
			player.playerNetServerHandler.kickPlayerFromServer("§eСервер выключен.\nВозможно, он сейчас даже не включится.");
		}
		this.serverRunning = false;

	}

	protected void setInstance() {
		mcServer = this;
	}

	public void run() {
		try {
			if (this.startServer()) {
				this.currentTime = getCurrentTimeMillis();
				long i = 0L;
				this.statusResponse.setServerDescription(new ChatComponentText(this.motd));
				this.statusResponse.setProtocolVersionInfo(new ServerStatusResponse.MinecraftProtocolVersionIdentifier("1.8.8", 47));
				this.addFaviconToStatusResponse(this.statusResponse);

				while (this.serverRunning) {
					long k = getCurrentTimeMillis();
					long j = k - this.currentTime;

					long tickLength = this.tickLength;
					if (j > 40L * tickLength && this.currentTime - this.timeOfLastWarning >= 15000L) {
						logger.warn("Скип тиков (" + j + " т. или " + j / tickLength + "мс.)");
						j = 40L * tickLength;
						this.timeOfLastWarning = this.currentTime;
					}

					if (j < 0L) {
						logger.warn("Время пошло в обратную сторону?! Что ты бл* вообще сделал?!!!");
						j = 0L;
					}

					i += j;
					this.currentTime = k;
					if (worldService.getWorld(0).areAllPlayersAsleep()) {
						this.tick();
						i = 0L;
					} else {
						while (i > tickLength) {
							i -= tickLength;
							this.tick();
						}
					}

					Thread.sleep(Math.max(1L, tickLength - i));
					this.serverIsRunning = true;
				}
			} else {
				this.finalTick(null);
			}
		} catch (Throwable throwable1) {
			logger.error("Encountered an unexpected exception", throwable1);
			CrashReport crashreport;

			if (throwable1 instanceof ReportedException) {
				crashreport = this.addServerInfoToCrashReport(((ReportedException) throwable1).getCrashReport());
			} else {
				crashreport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", throwable1));
			}

			File file1 = new File(new File(this.getDataDirectory(), isDedicatedServer() ? "crash-reports" : "gamedata/logs/crash-reports"),
					"crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");

			if (crashreport.saveToFile(file1)) {
				logger.error("This crash report has been saved to: " + file1.getAbsolutePath());
			} else {
				logger.error("We were unable to save this crash report to disk.");
			}

			this.finalTick(crashreport);
		} finally {
			try {
				this.serverStopped = true;
				this.stopServer();
			} catch (Throwable throwable) {
				logger.error("Exception stopping the server", throwable);
			} finally {
				this.systemExitNow();
			}
		}
	}

	private void addFaviconToStatusResponse(ServerStatusResponse response) {
		File file1 = this.getFile("server-icon.png");

		if (file1.isFile()) {
			ByteBuf bytebuf = Unpooled.buffer();

			try {
				BufferedImage bufferedimage = ImageIO.read(file1);
				Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
				Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
				ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
				ByteBuf bytebuf1 = Base64.encode(bytebuf);
				response.setFavicon("data:image/png;base64," + bytebuf1.toString(Charsets.UTF_8));
			} catch (Exception exception) {
				logger.error("Couldn\'t load server icon", exception);
			} finally {
				bytebuf.release();
			}
		}
	}

	public File getDataDirectory() {
		return new File(".");
	}

	/**
	 * Called on exit from the main run() loop.
	 */
	protected void finalTick(CrashReport report) {
	}

	/**
	 * Directly calls System.exit(0), instantly killing the program.
	 */
	protected void systemExitNow() {
	}

	/**
	 * Main function called by run() every loop.
	 */
	public void tick() {
		long i = System.nanoTime();
		++this.tickCounter;

		if (this.startProfiling) {
			this.startProfiling = false;
			profiler.setEnabled(true);
			profiler.clearProfiling();
		}

		profiler.startSection("root");
		this.updateTimeLightAndEntities();

		if (i - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
			this.nanoTimeSinceStatusRefresh = i;
			this.statusResponse.setPlayerCountData(new ServerStatusResponse.PlayerCountData(this.getMaxPlayers(), this.getCurrentPlayerCount()));
			GameProfile[] agameprofile = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
			int j = MathHelper.getRandomIntegerInRange(this.random, 0, this.getCurrentPlayerCount() - agameprofile.length);

			for (int k = 0; k < agameprofile.length; ++k) {
				agameprofile[k] = this.serverConfigManager.getPlayers().get(j + k).getGameProfile();
			}

			Collections.shuffle(Arrays.asList(agameprofile));
			this.statusResponse.getPlayerCountData().setPlayers(agameprofile);
		}

		if (this.tickCounter % 900 == 0) {
			profiler.startSection("save");
			this.serverConfigManager.saveAllPlayerData();
			this.saveAllWorlds(true);
			profiler.endSection();
		}

		profiler.startSection("tallying");
		this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - i;
		profiler.endSection();
		profiler.endSection();
	}

	public void updateTimeLightAndEntities() {
		profiler.startSection("jobs");

		synchronized (this.futureTaskQueue) {
			while (!this.futureTaskQueue.isEmpty()) {
				Util.schedule(futureTaskQueue.poll());
			}
		}

		profiler.endStartSection("levels");

		for (int j = 0; j < worldService.getDimensionAmount(); ++j) {
			long i = System.nanoTime();

			if (j == 0 || this.getAllowNether()) {
				WorldServer worldserver = worldService.getWorld(j);
				profiler.startSection(worldserver.getWorldInfo().getWorldName());

				if (this.tickCounter % 20 == 0) {
					profiler.startSection("timeSync");
					this.serverConfigManager.sendPacketToAllPlayersInDimension(
							new S03PacketTimeUpdate(worldserver.getTotalWorldTime(), worldserver.getWorldTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")),
							worldserver.provider.getDimensionId());
					profiler.endSection();
				}

				profiler.startSection("tick");

				try {
					worldserver.tick();
				} catch (Throwable throwable1) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception ticking world");
					worldserver.addWorldInfoToCrashReport(crashreport);
					throw new ReportedException(crashreport);
				}

				try {
					worldserver.updateEntities();
				} catch (Throwable throwable) {
					CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Exception ticking world entities");
					worldserver.addWorldInfoToCrashReport(crashreport1);
					throw new ReportedException(crashreport1);
				}

				profiler.endSection();
				profiler.startSection("tracker");
				worldserver.getEntityTracker().updateTrackedEntities();
				profiler.endSection();
				profiler.endSection();
			}
			long time = System.nanoTime();
			this.timeOfLastDimensionTick[j][this.tickCounter % 100] = time - i;
		}

		profiler.endStartSection("connection");
		this.getNetworkSystem().networkTick();
		profiler.endStartSection("players");
		this.serverConfigManager.onTick();
		profiler.endSection();
	}

	public boolean getAllowNether() {
		return true;
	}

	public void startServerThread() {
		this.serverThread = new Thread(this, "Server thread");
		this.serverThread.start();
	}

	/**
	 * Returns a File object from the specified string.
	 */
	public File getFile(String fileName) {
		return new File(this.getDataDirectory(), fileName);
	}

	/**
	 * Logs the message with a level of WARN.
	 */
	public void logWarning(String msg) {
		logger.warn(msg);
	}

	/**
	 * Gets the worldServer by the given dimension.
	 */
	public WorldServer worldServerForDimension(int dimension) {
		return worldService.getWorld(dimension == -1 ? 1 : dimension == 1 ? 2 : 0);
	}

	/**
	 * Returns the server's Minecraft version as string.
	 */
	public String getMinecraftVersion() {
		return "1.8.8";
	}

	/**
	 * Returns the number of players currently on the server.
	 */
	public int getCurrentPlayerCount() {
		return this.serverConfigManager.getCurrentPlayerCount();
	}

	/**
	 * Returns the maximum number of players allowed on the server.
	 */
	public int getMaxPlayers() {
		return this.serverConfigManager.getMaxPlayers();
	}

	/**
	 * Returns an array of the usernames of all the connected players.
	 */
	public String[] getAllUsernames() {
		return this.serverConfigManager.getAllUsernames();
	}

	/**
	 * Returns an array of the GameProfiles of all the connected players
	 */
	public GameProfile[] getGameProfiles() {
		return this.serverConfigManager.getAllProfiles();
	}

	public String getServerModName() {
		return "vanilla";
	}

	/**
	 * Adds the server info, including from theWorldServer, to the crash report.
	 */
	public CrashReport addServerInfoToCrashReport(CrashReport report) {
		report.getCategory().addCrashSectionCallable("Profiler Position", () -> profiler.isEnabled() ? profiler.getNameOfLastSection() : "N/A (disabled)");

		if (this.serverConfigManager != null) {
			report.getCategory().addCrashSectionCallable("Player Count",
					() -> this.serverConfigManager.getCurrentPlayerCount() + " / " + this.serverConfigManager.getMaxPlayers() + "; " + this.serverConfigManager.getPlayers());
		}

		return report;
	}

	public List<String> getTabCompletions(ICommandSender sender, String input, BlockPos pos) {
		List<String> list = new ArrayList<>();

		if (input.startsWith("/")) {
			input = input.substring(1);
			boolean flag = !input.contains(" ");
			List<String> list1 = this.commandManager.getTabCompletionOptions(sender, input, pos);

			if (list1 != null) {
				for (String s2 : list1) {
					if (flag) {
						list.add("/" + s2);
					} else {
						list.add(s2);
					}
				}
			}

			return list;
		}
		String[] astring = input.split(" ", -1);
		String s = astring[astring.length - 1];

		for (String s1 : this.serverConfigManager.getAllUsernames()) {
			if (CommandBase.doesStringStartWith(s, s1)) {
				list.add(s1);
			}
		}

		return list;
	}

	/**
	 * Gets mcServer.
	 */
	public static MinecraftServer getServer() {
		return mcServer;
	}

	public boolean isAnvilFileSet() {
		return this.anvilFile != null;
	}

	/**
	 * Gets the name of this command sender (usually username, but possibly "Rcon")
	 */
	public String getName() {
		return "Server";
	}

	/**
	 * Send a chat message to the CommandSender
	 */
	public void sendMessage(IChatComponent component) {
		logger.info(component.getUnformattedText());
	}

	/**
	 * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
	 */
	public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
		return true;
	}

	public ICommandManager getCommandManager() {
		return this.commandManager;
	}

	/**
	 * Gets KeyPair instanced in MinecraftServer.
	 */
	public KeyPair getKeyPair() {
		return this.serverKeyPair;
	}

	/**
	 * Returns the username of the server owner (for integrated servers)
	 */
	public String getServerOwner() {
		return this.serverOwner;
	}

	/**
	 * Sets the username of the owner of this server (in the case of an integrated server)
	 */
	public void setServerOwner(String owner) {
		this.serverOwner = owner;
	}

	public boolean isSinglePlayer() {
		return this.serverOwner != null;
	}

	public String getFolderName() {
		return this.folderName;
	}

	public void setFolderName(String name) {
		this.folderName = name;
	}

	public void setWorldName(String p_71246_1_) {
		this.worldName = p_71246_1_;
	}

	public String getWorldName() {
		return this.worldName;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.serverKeyPair = keyPair;
	}

	public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
		for (WorldServer world : getWorlds()) {
			if (world != null) {
				if (world.getWorldInfo().isHardcoreModeEnabled()) {
					world.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
					world.setAllowedSpawnTypes(true, true);
				} else if (this.isSinglePlayer()) {
					world.getWorldInfo().setDifficulty(difficulty);
					world.setAllowedSpawnTypes(world.getDifficulty() != EnumDifficulty.PEACEFUL, true);
				} else {
					world.getWorldInfo().setDifficulty(difficulty);
					world.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
				}
			}
		}
	}

	protected boolean allowSpawnMonsters() {
		return true;
	}

	public void canCreateBonusChest(boolean enable) {
		this.starterKit = enable;
	}

	public ISaveFormat getActiveAnvilConverter() {
		return this.anvilConverterForAnvilFile;
	}

	/**
	 * WARNING : directly calls
	 * getActiveAnvilConverter().deleteWorldDirectory(theWorldServer[0].getSaveHandler().getWorldDirectoryName());
	 */
	public void deleteWorldAndStopServer() {
		this.worldIsBeingDeleted = true;
		this.getActiveAnvilConverter().flushCache();

		for (WorldServer worldserver : getWorlds()) {
			if (worldserver != null) {
				worldserver.flush();
			}
		}

		this.getActiveAnvilConverter().deleteWorldDirectory(worldService.getWorld(0).getSaveHandler().getWorldDirectoryName());
		this.initiateShutdown();
	}

	public String getResourcePackUrl() {
		return this.resourcePackUrl;
	}

	public String getResourcePackHash() {
		return this.resourcePackHash;
	}

	public void setResourcePack(String url, String hash) {
		this.resourcePackUrl = url;
		this.resourcePackHash = hash;
	}

	public abstract boolean isDedicatedServer();

	public boolean isServerInOnlineMode() {
		return this.onlineMode;
	}

	public void setOnlineMode(boolean online) {
		this.onlineMode = online;
	}

	public boolean getCanSpawnAnimals() {
		return this.canSpawnAnimals;
	}

	public void setCanSpawnAnimals(boolean spawnAnimals) {
		this.canSpawnAnimals = spawnAnimals;
	}

	public boolean getCanSpawnNPCs() {
		return this.canSpawnNPCs;
	}

	public abstract boolean useEpoll();

	public void setCanSpawnNPCs(boolean spawnNpcs) {
		this.canSpawnNPCs = spawnNpcs;
	}

	public boolean isPVPEnabled() {
		return this.pvpEnabled;
	}

	public void setAllowPvp(boolean allowPvp) {
		this.pvpEnabled = allowPvp;
	}

	public boolean isFlightAllowed() {
		return this.allowFlight;
	}

	public void setAllowFlight(boolean allow) {
		this.allowFlight = allow;
	}

	/**
	 * Return whether command blocks are enabled.
	 */
	public abstract boolean isCommandBlockEnabled();

	public String getMOTD() {
		return this.motd;
	}

	public void setMOTD(String motdIn) {
		this.motd = motdIn;
	}

	public int getBuildLimit() {
		return this.buildLimit;
	}

	public void setBuildLimit(int maxBuildHeight) {
		this.buildLimit = maxBuildHeight;
	}

	public boolean isServerStopped() {
		return this.serverStopped;
	}

	public ServerConfigurationManager getConfigurationManager() {
		return this.serverConfigManager;
	}

	public void setConfigManager(ServerConfigurationManager configManager) {
		this.serverConfigManager = configManager;
	}

	/**
	 * Sets the game type for all worlds.
	 */
	public void setGameType(WorldSettings.GameType gameMode) {
		for (WorldServer world : getServer().getWorlds())
			world.getWorldInfo().setGameType(gameMode);
	}

	public NetworkSystem getNetworkSystem() {
		return this.networkSystem;
	}

	public boolean serverIsInRunLoop() {
		return this.serverIsRunning;
	}

	public boolean getGuiEnabled() {
		return false;
	}

	/**
	 * On dedicated does nothing. On integrated, sets commandsAllowedForAll, gameType and allows external connections.
	 */
	public abstract String shareToLAN(WorldSettings.GameType type, boolean allowCheats);

	public int getTickCounter() {
		return this.tickCounter;
	}

	public void enableProfiling() {
		this.startProfiling = true;
	}

	/**
	 * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
	 * the coordinates 0, 0, 0
	 */
	public BlockPos getPosition() {
		return BlockPos.ORIGIN;
	}

	/**
	 * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return 0.0D,
	 * 0.0D, 0.0D
	 */
	public Vec3d getPositionVector() {
		return new Vec3d(0.0D, 0.0D, 0.0D);
	}

	/**
	 * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
	 * the overworld
	 */
	public World getEntityWorld() {
		return worldService.getWorld(0);
	}

	/**
	 * Returns the entity associated with the command sender. MAY BE NULL!
	 */
	public Entity getCommandSenderEntity() {
		return null;
	}

	/**
	 * Return the spawn protection area's size.
	 */
	public int getSpawnProtectionSize() {
		return 16;
	}

	public boolean isBlockProtected(World worldIn, BlockPos pos, Player playerIn) {
		return false;
	}

	public boolean getForceGamemode() {
		return false;
	}

	public Proxy getServerProxy() {
		return this.serverProxy;
	}

	public static long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}

	public int getMaxPlayerIdleMinutes() {
		return this.maxPlayerIdleMinutes;
	}

	public void setPlayerIdleTimeout(int idleTimeout) {
		this.maxPlayerIdleMinutes = idleTimeout;
	}

	/**
	 * Get the formatted ChatComponent that will be used for the sender's username in chat
	 */
	public IChatComponent getDisplayName() {
		return new ChatComponentText(this.getName());
	}

	public boolean isAnnouncingPlayerAchievements() {
		return true;
	}

	public MinecraftSessionService getMinecraftSessionService() {
		return this.sessionService;
	}

	public GameProfileRepository getGameProfileRepository() {
		return this.profileRepo;
	}

	public PlayerProfileCache getPlayerProfileCache() {
		return this.profileCache;
	}

	public ServerStatusResponse getServerStatusResponse() {
		return this.statusResponse;
	}

	public void refreshStatusNextTick() {
		this.nanoTimeSinceStatusRefresh = 0L;
	}

	public Entity getEntityFromUuid(UUID uuid) {
		for (WorldServer worldserver : this.getWorlds()) {
			if (worldserver == null) continue;
			Entity entity = worldserver.getEntityFromUuid(uuid);
			if (entity != null) return entity;
		}

		return null;
	}

	/**
	 * Returns true if the command sender should be sent feedback about executed commands
	 */
	public boolean sendCommandFeedback() {
		return getEntityWorld().getGameRules().getBoolean("sendCommandFeedback");
	}

	public void setCommandStat(CommandResultStats.Type type, int amount) {
	}

	public int getMaxWorldSize() {
		return 29999984;
	}

	public <V> ListenableFuture<V> callFromMainThread(Callable<V> callable) {
		Validate.notNull(callable);

		if (!this.isCallingFromMinecraftThread() && !this.isServerStopped()) {
			ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);

			synchronized (this.futureTaskQueue) {
				this.futureTaskQueue.add(listenablefuturetask);
				return listenablefuturetask;
			}
		}
		try {
			return Futures.immediateFuture(callable.call());
		} catch (Exception exception) {
			return Futures.immediateFailedCheckedFuture(exception);
		}
	}

	public <V> ListenableFuture<V> forcePushTask(Callable<V> callable) {
		ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);

		synchronized (this.futureTaskQueue) {
			this.futureTaskQueue.add(listenablefuturetask);
			return listenablefuturetask;
		}
	}

	public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
		Validate.notNull(runnableToSchedule);
		return this.callFromMainThread(Executors.callable(runnableToSchedule));
	}

	public boolean isCallingFromMinecraftThread() {
		return Thread.currentThread() == this.serverThread;
	}

	/**
	 * The compression treshold. If the packet is larger than the specified amount of bytes, it will be compressed
	 */
	public int getNetworkCompressionTreshold() {
		return 256;
	}

	public long getCurrentTime() {
		return this.currentTime;
	}

	public Thread getServerThread() {
		return this.serverThread;
	}


}
