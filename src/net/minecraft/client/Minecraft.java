package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.Utils;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.game.InputHandler;
import net.minecraft.client.game.entity.EntityPlayerSP;
import net.minecraft.client.game.particle.EffectRenderer;
import net.minecraft.client.game.shader.Framebuffer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.init.Bootstrap;
import net.minecraft.logging.Log;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.server.Profiler;
import net.minecraft.server.Todo;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.*;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static net.minecraft.client.ClientProfiler.in;
import static net.minecraft.logging.Log.MAIN;
import static net.minecraft.util.Util.OS.OSX;

public class Minecraft implements IThreadListener {

	public static final boolean isRunningOnMac = Util.getOSType() == OSX;

	private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
	private final File fileResourcepacks;
	private final PropertyMap propertyMap;
	private ServerData currentServerData;

	private TextureManager renderEngine;

	protected static Minecraft theMinecraft;
	public PlayerControllerMP playerController;
	private boolean fullscreen;
	private boolean hasCrashed;

	private CrashReport crashReporter;
	public int displayWidth;
	public int displayHeight;
	private boolean field_181541_X = false;
	private Timer timer = new Timer(20.0F);

	public WorldClient theWorld;
	public RenderGlobal renderGlobal;
	private RenderManager renderManager;
	private RenderItem renderItem;
	private ItemRenderer itemRenderer;
	public EntityPlayerSP thePlayer;
	private Entity renderViewEntity;
	public Entity pointedEntity;
	public EffectRenderer effectRenderer;
	private final Session session;
	private boolean isGamePaused;
	public AssetsFontRenderer fontRendererObj;
	public AssetsFontRenderer standardGalacticFontRenderer;
	public GuiScreen currentScreen;
	public LoadingScreenRenderer loadingScreen;
	public EntityRenderer entityRenderer;


	private int tempDisplayWidth;
	private int tempDisplayHeight;

	private IntegratedServer theIntegratedServer;

	public GuiAchievement guiAchievement;
	public GuiIngame ingameGUI;

	public boolean skipRenderWorld;

	// The ray trace hit that the mouse is over.
	public MovingObjectPosition objectMouseOver;

	public MouseHelper mouseHelper;
	public final File mcDataDir;
	private final String launchedVersion;
	private final Proxy proxy;
	private ISaveFormat saveLoader;

	/**
	 * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part of
	 * the usage snooping.
	 */
	private static int debugFPS;

	/**
	 * When you place a block, it's set to 6, decremented once per tick, when it's 0, you can place another block.
	 */
	private String serverName;
	private int serverPort;

	/**
	 * Does the actual gameplay have focus. If so then mouse and keys will effect the player instead of menus.
	 */
	public boolean inGameHasFocus;
	public long systemTime = getSystemTime();

	/**
	 * Join player counter
	 */
	private int joinPlayerCounter;
	public final FrameTimer field_181542_y = new FrameTimer();
	long field_181543_z = System.nanoTime();
	private final boolean jvm64bit;
	private NetworkManager myNetworkManager;
	private boolean integratedServerIsRunning;

	private IReloadableResourceManager mcResourceManager;
	private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
	private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
	private final DefaultResourcePack mcDefaultResourcePack;
	private ResourcePackRepository mcResourcePackRepository;
	public LanguageManager mcLanguageManager;
	private Framebuffer framebufferMc;
	private TextureMap textureMapBlocks;
	private SoundHandler mcSoundHandler;
	private MusicTicker mcMusicTicker;
	private final MinecraftSessionService sessionService;
	private SkinManager skinManager;
	private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
	private final Thread mcThread = Thread.currentThread();
	private ModelManager modelManager;
	private BlockRendererDispatcher blockRenderDispatcher;

	/**
	 * Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly.
	 */
	volatile boolean running = true;

	/**
	 * String that shows the debug information
	 */
	public String debug = "";
	public boolean renderChunksMany = true;

	/**
	 * Approximate time (in ms) of last update to debug string
	 */
	long debugUpdateTime = getSystemTime();

	/**
	 * holds the current fps
	 */
	int fpsCounter;
	long prevFrameTime = -1L;

	public InputHandler inputHandler;

	public Minecraft(GameConfiguration gameConfig) {
		theMinecraft = this;
		Profiler.in = new ClientProfiler();
		Todo.instance = new TodoClient();
		this.mcDataDir = gameConfig.folderInfo.mcDataDir;
		this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
		this.launchedVersion = gameConfig.gameInfo.version;
		this.propertyMap = gameConfig.userInfo.field_181172_c;
		this.mcDefaultResourcePack = new DefaultResourcePack();
		this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
		this.sessionService = new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.session = gameConfig.userInfo.session;
		this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
		this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
		this.tempDisplayWidth = gameConfig.displayInfo.width;
		this.tempDisplayHeight = gameConfig.displayInfo.height;
		this.fullscreen = gameConfig.displayInfo.fullscreen;
		this.jvm64bit = isJvm64bit();
		this.theIntegratedServer = new IntegratedServer(this);
		Textifier.class.getCanonicalName();

		if (gameConfig.serverInfo.serverName != null) {
			this.serverName = gameConfig.serverInfo.serverName;
			this.serverPort = gameConfig.serverInfo.serverPort;
		}

		ImageIO.setUseCache(false);
		Bootstrap.register();
	}

	public void run() {
		this.running = true;

		try {
			this.startGame();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
			crashreport.makeCategory("Initialization");
			this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
			return;
		}

		try {
			while (this.running) {
				if (this.hasCrashed && this.crashReporter != null) this.displayCrashReport(this.crashReporter);
				else try {
					this.runGameLoop();
				} catch (OutOfMemoryError var10) {
					this.freeMemory();
					this.displayGuiScreen(new GuiMemoryErrorScreen());
					System.gc();
				}
			}
		} catch (MinecraftError ignored) {
		} catch (ReportedException e) {
			this.addGraphicsAndWorldToCrashReport(e.getCrashReport());
			this.freeMemory();
			MAIN.error("Почему бы мне не крашнуться?");
			MAIN.exception(e);
			this.displayCrashReport(e.getCrashReport());
		} catch (Throwable t) {
			CrashReport report = this.addGraphicsAndWorldToCrashReport(new CrashReport("Неожиданная ошибка", t));
			this.freeMemory();
			MAIN.error("Произошла непонятная фигня! Спасайтесь!");
			MAIN.exception(t);
			this.displayCrashReport(report);
		} finally {
			this.shutdownMinecraftApplet();
		}
	}

	/**
	 * Starts the game: initializes the canvas, the title, the settings, etcetera.
	 */
	private void startGame() throws LWJGLException {
		long start = System.currentTimeMillis();
		Log.init();
		Settings.init();
		this.inputHandler = new InputHandler(this);
		MAIN.info("Установлено имя " + this.session.getUsername() + ", ID сессии: " + session.getSessionID());

		this.defaultResourcePacks.add(this.mcDefaultResourcePack);
		this.startTimerHackThread();

		MAIN.info("Используемая версия LWJGL: " + Sys.getVersion() + " (Вау, почти как новая)");
		this.setWindowIcon();
		this.setInitialDisplayMode();

		this.createDisplay();
		OpenGlHelper.initializeTextures();
		this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
		this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.registerMetadataSerializers();
		this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_);
		this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
		this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, Settings.language);
		this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
		this.refreshResources();
		this.renderEngine = new TextureManager(this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.renderEngine);
		this.skinManager = new SkinManager(this.renderEngine, new File("gamedata/defaultresourcepack/skins"), this.sessionService);
		this.fontRendererObj = new AssetsFontRenderer(new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
		if (Settings.language != null) this.fontRendererObj.setUnicodeFlag(this.isUnicode());

		Preloader preloader = new Preloader(new ScaledResolution(this), mcDefaultResourcePack, renderEngine);
		preloader.drawLogo();
		
		this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
		preloader.nextState();
		
		this.mcSoundHandler = new SoundHandler(this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
		preloader.nextState();
		
		this.mcMusicTicker = new MusicTicker(this);
		preloader.nextState();

		long end = System.currentTimeMillis();
		System.out.println("# Преинициализация завершена за " + (end - start) + " мс.");

		this.standardGalacticFontRenderer = new AssetsFontRenderer(new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
		this.mcResourceManager.registerReloadListener(this.fontRendererObj);
		this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
		this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
		this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
		AchievementList.openInventory.setStatStringFormatter(s -> String.format(s, "E"));
		this.mouseHelper = new MouseHelper();
		preloader.nextState();
		this.checkGLError("Pre startup");
		this.checkGLError("Startup");
		this.textureMapBlocks = new TextureMap("textures");
		this.textureMapBlocks.setMipmapLevels((int) Settings.MIPMAP_LEVELS.f());
		preloader.nextState();
		this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
		this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		this.textureMapBlocks.setBlurMipmapDirect(false, Settings.MIPMAP_LEVELS.i() > 0);
		preloader.nextState();
		this.modelManager = new ModelManager(this.textureMapBlocks);
		preloader.nextState();
		this.mcResourceManager.registerReloadListener(this.modelManager);
		preloader.nextState();
		this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
		this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
		this.itemRenderer = new ItemRenderer(this);
		this.mcResourceManager.registerReloadListener(this.renderItem);
		preloader.nextState();
		this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.entityRenderer);
		preloader.nextState();
		this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes());
		this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
		preloader.nextState();
		this.renderGlobal = new RenderGlobal(this);
		this.mcResourceManager.registerReloadListener(this.renderGlobal);
		preloader.nextState();
		this.guiAchievement = new GuiAchievement(this);
		preloader.nextState();
		preloader.nextState();
		G.enableTexture2D();
		G.shadeModel(7425);
		G.clearDepth(1.0D);
		G.enableDepth();
		G.depthFunc(515);
		G.enableAlpha();
		G.alphaFunc(516, 0.1F);
		G.cullFace(1029);
		G.matrixMode(5889);
		G.loadIdentity();
		G.matrixMode(5888);
		G.viewport(0, 0, this.displayWidth, this.displayHeight);
		this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
		this.checkGLError("Post startup");
		this.ingameGUI = new GuiIngame(this);

		if (this.serverName != null) this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
		else this.displayGuiScreen(new GuiMainMenu());

		this.loadingScreen = new LoadingScreenRenderer(this);

		if (Settings.USE_FULLSCREEN.b() && !this.fullscreen) this.toggleFullscreen();

		try {
			Display.setVSyncEnabled(Settings.ENABLE_VSYNC.b());
		} catch (OpenGLException var2) {
			Settings.ENABLE_VSYNC.set(false);
			Settings.saveOptions();
		}

		this.renderGlobal.makeEntityOutlineShader();
	}

	private void registerMetadataSerializers() {
		this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
	}

	private void createDisplay() throws LWJGLException {
		Display.setResizable(true);
		Display.setTitle("Implario GeoMaster");

		try {
			Display.create(new PixelFormat().withDepthBits(24));
		} catch (LWJGLException lwjglexception) {
			MAIN.error("Не удалось создать окно для игры с глубоким разрешением");
			MAIN.exception(lwjglexception);

			Utils.sleep(1000);

			if (this.fullscreen) this.updateDisplayMode();
			Display.create();
		}
	}

	private void setInitialDisplayMode() throws LWJGLException {
		if (this.fullscreen) {
			Display.setFullscreen(true);
			DisplayMode displaymode = Display.getDisplayMode();
			this.displayWidth = Math.max(1, displaymode.getWidth());
			this.displayHeight = Math.max(1, displaymode.getHeight());
		} else {
			Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
		}
	}

	private void setWindowIcon() {
		if (Util.getOSType() == OSX) return;

		InputStream icon16 = null;
		InputStream icon32 = null;

		try {
			icon16 = Minecraft.class.getResourceAsStream("/icon16.png");
			icon32 = Minecraft.class.getResourceAsStream("/icon32.png");

			if (icon16 != null && icon32 != null) {
				Display.setIcon(new ByteBuffer[] {this.readImageToBuffer(icon16), this.readImageToBuffer(icon32)});
			}
		} catch (IOException e) {
			MAIN.error("Не удалось установить иконку окна");
			MAIN.exception(e);
		} finally {
			IOUtils.closeQuietly(icon16);
			IOUtils.closeQuietly(icon32);
		}
	}

	private static boolean isJvm64bit() {
		String[] astring = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

		for (String s : astring) {
			String s1 = System.getProperty(s);
			if (s1 != null && s1.contains("64")) return true;
		}

		return false;
	}

	public Framebuffer getFramebuffer() {
		return this.framebufferMc;
	}

	public String getVersion() {
		return this.launchedVersion;
	}

	private void startTimerHackThread() {
		Thread thread = new Thread("Timer hack thread") {
			public void run() {
				while (Minecraft.this.running) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException ignored) {}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	public void crashed(CrashReport crash) {
		this.hasCrashed = true;
		this.crashReporter = crash;
	}

	/**
	 * Wrapper around displayCrashReportInternal
	 */
	public void displayCrashReport(CrashReport crashReportIn) {
		File file1 = new File(getMinecraft().mcDataDir, "gamedata/logs/crash-reports");
		File file2 = new File(file1, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
		Bootstrap.print(crashReportIn.getCompleteReport());

		if (crashReportIn.getFile() != null) {
			Bootstrap.print("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
			System.exit(-1);
		}
		if (crashReportIn.saveToFile(file2)) {
			Bootstrap.print("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
			System.exit(-1);
		}
		Bootstrap.print("#@?@# Game crashed! Crash report could not be saved. #@?@#");
		System.exit(-2);
	}

	public boolean isUnicode() {
		return this.mcLanguageManager.isCurrentLocaleUnicode() || Settings.FORCE_UNICODE_FONT.b();
	}

	public void refreshResources() {
		List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);

		for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
			list.add(resourcepackrepository$entry.getResourcePack());
		}

		if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
			list.add(this.mcResourcePackRepository.getResourcePackInstance());
		}

		try {
			this.mcResourceManager.reloadResources(list);
		} catch (RuntimeException runtimeexception) {
			MAIN.warn("Во время перезагрузки ресурсов произошла ошибка. Выключаем все ресурс-паки.");
			MAIN.exception(runtimeexception);
			list.clear();
			list.addAll(this.defaultResourcePacks);
			this.mcResourcePackRepository.setRepositories(Collections.emptyList());
			this.mcResourceManager.reloadResources(list);
			Settings.resourcePacks.clear();
			Settings.incompatibleResourcePacks.clear();
			Settings.saveOptions();
		}

		this.mcLanguageManager.parseLanguageMetadata(list);
		if (this.renderGlobal != null) this.renderGlobal.loadRenderers();
	}

	private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
		BufferedImage bufferedimage = ImageIO.read(imageStream);
		int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
		ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

		for (int i : aint) {
			bytebuffer.putInt(i << 8 | i >> 24 & 255);
		}

		bytebuffer.flip();
		return bytebuffer;
	}

	private void updateDisplayMode() throws LWJGLException {
		Set<DisplayMode> set = Sets.newHashSet();
		Collections.addAll(set, Display.getAvailableDisplayModes());
		DisplayMode displaymode = Display.getDesktopDisplayMode();

		if (!set.contains(displaymode) && Util.getOSType() == OSX) {
label53:

			for (DisplayMode displaymode1 : macDisplayModes) {
				boolean flag = true;

				for (DisplayMode displaymode2 : set) {
					if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() && displaymode2.getHeight() == displaymode1.getHeight()) {
						flag = false;
						break;
					}
				}

				if (!flag) {

					Iterator iterator = set.iterator();
					DisplayMode displaymode3;

					do {
						if (!iterator.hasNext()) continue label53;
						displaymode3 = (DisplayMode) iterator.next();

					} while (displaymode3.getBitsPerPixel() != 32 || displaymode3.getWidth() != displaymode1.getWidth() / 2 || displaymode3.getHeight() != displaymode1.getHeight() / 2);

					displaymode = displaymode3;
				}
			}
		}

		Display.setDisplayMode(displaymode);
		this.displayWidth = displaymode.getWidth();
		this.displayHeight = displaymode.getHeight();
	}

	/**
	 * Returns the save loader that is currently being used
	 */
	public ISaveFormat getSaveLoader() {
		return this.saveLoader;
	}

	/**
	 * Sets the argument GuiScreen as the main (topmost visible) screen.
	 */
	public void displayGuiScreen(GuiScreen guiScreenIn) {
		if (this.currentScreen != null) this.currentScreen.onGuiClosed();

		if (guiScreenIn == null && this.theWorld == null) guiScreenIn = new GuiMainMenu();
		else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) guiScreenIn = new GuiGameOver();

		if (guiScreenIn instanceof GuiMainMenu) {
			Settings.SHOW_DEBUG.set(false);
			this.ingameGUI.getChatGUI().clearChatMessages();
		}

		this.currentScreen = guiScreenIn;

		if (guiScreenIn != null) {
			this.inputHandler.setIngameNotInFocus();
			ScaledResolution scaledresolution = new ScaledResolution(this);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
			guiScreenIn.setWorldAndResolution(this, i, j);
			this.skipRenderWorld = false;
		} else {
			this.mcSoundHandler.resumeSounds();
			this.inputHandler.setIngameFocus();
		}
	}

	/**
	 * Checks for an OpenGL error. If there is one, prints the error ID and error string.
	 */
	private void checkGLError(String message) {
		int i = GL11.glGetError();
		if (i == 0) return;

		String s = GLU.gluErrorString(i);
		MAIN.error("########## ОШИБКА OpenGL ##########");
		MAIN.error("Процесс: " + message);
		MAIN.error(i + ": " + s);
	}

	/**
	 * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
	 * application (or web page) is exited.
	 */
	public void shutdownMinecraftApplet() {
		try {
			MAIN.info("Завершаем работу Minecraft...");
			try {
				this.loadWorld(null);
			} catch (Throwable ignored) {}

			this.mcSoundHandler.unloadSounds();
		} finally {
			Display.destroy();
			Date date = new Date();
			String end = "Конец сессии " + Log.DAY.format(date) + " в " + Log.TIME.format(date) + "\n";
			Log.SOUND.comment(end);
			MAIN.comment(end);
			Log.CHAT.comment(end);
			Log.SOUND.close();
			Log.CHAT.close();
			MAIN.close();
			if (!this.hasCrashed) System.exit(0);
		}

		System.gc();
	}

	/**
	 * Called repeatedly from run()
	 */
	private void runGameLoop() throws IOException {
		long i = System.nanoTime();
		in.startSection("root");

		if (Display.isCreated() && Display.isCloseRequested()) this.shutdown();

		if (!this.isGamePaused || this.theWorld == null) this.timer.updateTimer();
		else {
			float f = this.timer.renderPartialTicks;
			this.timer.updateTimer();
			this.timer.renderPartialTicks = f;
		}

		in.startSection("scheduledExecutables");

		synchronized (this.scheduledTasks) {
			while (!this.scheduledTasks.isEmpty()) Util.schedule((FutureTask) this.scheduledTasks.poll(), MAIN);
		}

		in.endSection();
		long l = System.nanoTime();
		in.startSection("tick");

		for (int j = 0; j < this.timer.elapsedTicks; ++j) this.runTick();

		in.endStartSection("preRenderErrors");
		long i1 = System.nanoTime() - l;
		this.checkGLError("Pre render");
		in.endStartSection("sound");
		this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
		in.endSection();
		in.startSection("render");
		net.minecraft.client.renderer.G.pushMatrix();
		net.minecraft.client.renderer.G.clear(16640);
		this.framebufferMc.bindFramebuffer(true);
		in.startSection("display");
		net.minecraft.client.renderer.G.enableTexture2D();

		if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) Settings.PERSPECTIVE.set(0);

		in.endSection();

		if (!this.skipRenderWorld) {
			in.endStartSection("gameRenderer");
			this.entityRenderer.func_181560_a(this.timer.renderPartialTicks, i);
			in.endSection();
		}

		in.endSection();

		if (Settings.SHOW_DEBUG.b() && Settings.PROFILER.b() && !Settings.HIDE_GUI.b()) {
			if (!in.profilingEnabled) in.clearProfiling();

			in.profilingEnabled = true;
			this.displayDebugInfo(i1);
		} else {
			in.profilingEnabled = false;
			this.prevFrameTime = System.nanoTime();
		}

		this.guiAchievement.updateAchievementWindow();
		this.framebufferMc.unbindFramebuffer();
		net.minecraft.client.renderer.G.popMatrix();
		net.minecraft.client.renderer.G.pushMatrix();
		this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
		net.minecraft.client.renderer.G.popMatrix();
		in.startSection("root");
		this.updateDisplay();
		Thread.yield();
		in.startSection("stream");
		in.startSection("update");
		in.endStartSection("submit");
		in.endSection();
		in.endSection();
		this.checkGLError("Post render");
		++this.fpsCounter;
		this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
		long k = System.nanoTime();
		this.field_181542_y.func_181747_a(k - this.field_181543_z);
		this.field_181543_z = k;

		while (getSystemTime() >= this.debugUpdateTime + 1000L) {
			debugFPS = this.fpsCounter;
			this.debug = String.format("FPS: §a%d §f(CU: §a%d§f)", debugFPS, RenderChunk.renderChunksUpdated);
			RenderChunk.renderChunksUpdated = 0;
			this.debugUpdateTime += 1000L;
			this.fpsCounter = 0;
		}

		if (this.isFramerateLimitBelowMax()) {
			in.startSection("fpslimit_wait");
			Display.sync(this.getLimitFramerate());
			in.endSection();
		}

		in.endSection();
	}

	public void updateDisplay() {
		in.startSection("display_update");
		Display.update();
		in.endSection();
		this.checkWindowResize();
	}

	protected void checkWindowResize() {
		if (this.fullscreen || !Display.wasResized()) return;
		int i = this.displayWidth;
		int j = this.displayHeight;
		this.displayWidth = Display.getWidth();
		this.displayHeight = Display.getHeight();

		if (this.displayWidth == i && this.displayHeight == j) return;
		if (this.displayWidth <= 0) this.displayWidth = 1;
		if (this.displayHeight <= 0) this.displayHeight = 1;

		this.resize(this.displayWidth, this.displayHeight);
	}

	public int getLimitFramerate() {
		return this.theWorld == null && this.currentScreen != null ? 60 : Settings.FRAMERATE_LIMIT.i();
	}

	public boolean isFramerateLimitBelowMax() {
		return (float) this.getLimitFramerate() < ((SliderSetting) Settings.FRAMERATE_LIMIT.getBase()).getMax();
	}

	public void freeMemory() {
		try {
			this.renderGlobal.deleteAllDisplayLists();
		} catch (Throwable ignored) {}

		try {
			System.gc();
			this.loadWorld(null);
		} catch (Throwable ignored) {}

		System.gc();
	}

	/**
	 * Parameter appears to be unused
	 */
	private void displayDebugInfo(long elapsedTicksTime) {
		if (!in.profilingEnabled) return;
		List<Profiler.Result> list = in.getProfilingData(inputHandler.getDebugProfilerName());
		Profiler.Result profiler$result = list.remove(0);
		G.clear(256);
		G.matrixMode(5889);
		G.enableColorMaterial();
		G.loadIdentity();
		G.ortho(0.0D, (double) this.displayWidth, (double) this.displayHeight, 0.0D, 1000.0D, 3000.0D);
		G.matrixMode(5888);
		G.loadIdentity();
		G.translate(0.0F, 0.0F, -2000.0F);
		GL11.glLineWidth(1.0F);
		net.minecraft.client.renderer.G.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		int i = 160;
		int j = this.displayWidth - i - 10;
		int k = this.displayHeight - i * 2;
		net.minecraft.client.renderer.G.enableBlend();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double) ((float) j - (float) i * 1.1F), (double) ((float) k - (float) i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j - (float) i * 1.1F), (double) (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j + (float) i * 1.1F), (double) (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j + (float) i * 1.1F), (double) ((float) k - (float) i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
		tessellator.draw();
		net.minecraft.client.renderer.G.disableBlend();
		double d0 = 0.0D;

		for (Profiler.Result res : list) {
			int i1 = MathHelper.floor_double(res.a / 4.0D) + 1;
			worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
			int j1 = res.hash();
			int k1 = j1 >> 16 & 255;
			int l1 = j1 >> 8 & 255;
			int i2 = j1 & 255;
			worldrenderer.pos((double) j, (double) k, 0.0D).color(k1, l1, i2, 255).endVertex();

			for (int j2 = i1; j2 >= 0; --j2) {
				float f = (float) ((d0 + res.a * (double) j2 / (double) i1) * Math.PI * 2.0D / 100.0D);
				float f1 = MathHelper.sin(f) * (float) i;
				float f2 = MathHelper.cos(f) * (float) i * 0.5F;
				worldrenderer.pos((double) ((float) j + f1), (double) ((float) k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
			}

			tessellator.draw();
			worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

			for (int i3 = i1; i3 >= 0; --i3) {
				float f3 = (float) ((d0 + res.a * (double) i3 / (double) i1) * Math.PI * 2.0D / 100.0D);
				float f4 = MathHelper.sin(f3) * (float) i;
				float f5 = MathHelper.cos(f3) * (float) i * 0.5F;
				worldrenderer.pos((double) ((float) j + f4), (double) ((float) k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
				worldrenderer.pos((double) ((float) j + f4), (double) ((float) k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
			}

			tessellator.draw();
			d0 += res.a;
		}

		DecimalFormat decimalformat = new DecimalFormat("##0.00");
		net.minecraft.client.renderer.G.enableTexture2D();
		String s = "";

		if (!profiler$result.s.equals("unspecified")) s = s + "[0] ";
		if (profiler$result.s.length() == 0) s = s + "ROOT ";
		else s = s + profiler$result.s + " ";

		int l2 = 16777215;
		this.fontRendererObj.drawStringWithShadow(s, (float) (j - i), (float) (k - i / 2 - 16), l2);
		this.fontRendererObj.drawStringWithShadow(s = decimalformat.format(profiler$result.b) + "%", (float) (j + i - this.fontRendererObj.getStringWidth(s)), (float) (k - i / 2 - 16), l2);

		for (int k2 = 0; k2 < list.size(); k2++) {
			Profiler.Result profiler$result2 = list.get(k2);
			String s1 = "";

			if (profiler$result2.s.equals("unspecified")) s1 = s1 + "[?] ";
			else s1 = s1 + "[" + (k2 + 1) + "] ";

			s1 = s1 + profiler$result2.s;
			this.fontRendererObj.drawStringWithShadow(s1, (float) (j - i), (float) (k + i / 2 + k2 * 8 + 20), profiler$result2.hash());
			this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.a) + "%", (float) (j + i - 50 - this.fontRendererObj.getStringWidth(s1)),
					(float) (k + i / 2 + k2 * 8 + 20), profiler$result2.hash());
			this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.b) + "%", (float) (j + i - this.fontRendererObj.getStringWidth(s1)),
					(float) (k + i / 2 + k2 * 8 + 20), profiler$result2.hash());
		}
	}

	/**
	 * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
	 */
	public void shutdown() {
		this.running = false;
	}

	/**
	 * Displays the ingame menu
	 */
	public void displayInGameMenu() {
		if (this.currentScreen != null) return;
		this.displayGuiScreen(new GuiIngameMenu());

		if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) this.mcSoundHandler.pauseSounds();
	}

	/**
	 * Toggles fullscreen mode.
	 */
	public void toggleFullscreen() {
		try {
			fullscreen = !fullscreen;
			Settings.USE_FULLSCREEN.set(fullscreen);

			if (this.fullscreen) {
				this.updateDisplayMode();
				this.displayWidth = Display.getDisplayMode().getWidth();
				this.displayHeight = Display.getDisplayMode().getHeight();

				if (this.displayWidth <= 0) {
					this.displayWidth = 1;
				}

				if (this.displayHeight <= 0) {
					this.displayHeight = 1;
				}
			} else {
				Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
				this.displayWidth = this.tempDisplayWidth;
				this.displayHeight = this.tempDisplayHeight;

				if (this.displayWidth <= 0) this.displayWidth = 1;
				if (this.displayHeight <= 0) this.displayHeight = 1;
			}

			if (this.currentScreen != null) this.resize(this.displayWidth, this.displayHeight);
			else this.updateFramebufferSize();

			Display.setFullscreen(this.fullscreen);
			Display.setVSyncEnabled(Settings.ENABLE_VSYNC.b());
			this.updateDisplay();
		} catch (Exception ex) {
			MAIN.error("Не удалось переключить полноэкранный режим");
			MAIN.exception(ex);
		}
	}

	/**
	 * Called to resize the current screen.
	 */
	private void resize(int width, int height) {
		this.displayWidth = Math.max(1, width);
		this.displayHeight = Math.max(1, height);

		if (this.currentScreen != null) {
			ScaledResolution scaledresolution = new ScaledResolution(this);
			this.currentScreen.onResize(this, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
		}

		this.loadingScreen = new LoadingScreenRenderer(this);
		this.updateFramebufferSize();
	}

	private void updateFramebufferSize() {
		this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);

		if (this.entityRenderer != null) {
			this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
		}
	}

	public MusicTicker getMusicTicker() {
		return this.mcMusicTicker;
	}

	/**
	 * Runs the current tick.
	 */
	public void runTick() throws IOException {
		inputHandler.runTick();

		in.startSection("gui");

		if (!this.isGamePaused) this.ingameGUI.updateTick();

		in.endSection();
		this.entityRenderer.getMouseOver(1.0F);

		// Передвижение игрока
		in.startSection("gameMode");
		if (!this.isGamePaused && this.theWorld != null) this.playerController.updateController();

		in.endStartSection("textures");

		// Рендер мира
		if (!this.isGamePaused) this.renderEngine.tick();

		if (this.currentScreen == null && this.thePlayer != null) {
			if (this.thePlayer.getHealth() <= 0.0F) {
				this.displayGuiScreen(null);
			} else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
				this.displayGuiScreen(new GuiSleepMP());
			}
		} else if (this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
			this.displayGuiScreen(null);
		}


		if (this.currentScreen != null) {
			try {
				this.currentScreen.handleInput();
			} catch (Throwable throwable1) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
				crashreportcategory.addCrashSectionCallable("Screen name", () -> this.currentScreen.getClass().getCanonicalName());
				throw new ReportedException(crashreport);
			}

			if (this.currentScreen != null) {
				try {
					this.currentScreen.updateScreen();
				} catch (Throwable throwable) {
					CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
					CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
					crashreportcategory1.addCrashSectionCallable("Screen name", () -> this.currentScreen.getClass().getCanonicalName());
					throw new ReportedException(crashreport1);
				}
			}
		}

		if (this.currentScreen == null || this.currentScreen.allowUserInput) {

			in.endStartSection("mouse");
			inputHandler.processMouse();

			in.endStartSection("keyboard");
			inputHandler.processKeyboard();

		}

		if (this.theWorld != null) {
			if (this.thePlayer != null) {
				++this.joinPlayerCounter;

				if (this.joinPlayerCounter == 30) {
					this.joinPlayerCounter = 0;
					this.theWorld.joinEntityInSurroundings(this.thePlayer);
				}
			}

			in.endStartSection("gameRenderer");

			if (!this.isGamePaused) {
				this.entityRenderer.updateRenderer();
			}

			in.endStartSection("levelRenderer");

			if (!this.isGamePaused) {
				this.renderGlobal.updateClouds();
			}

			in.endStartSection("level");

			if (!this.isGamePaused) {
				if (this.theWorld.getLastLightningBolt() > 0) {
					this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
				}

				this.theWorld.updateEntities();
			}
		} else if (this.entityRenderer.isShaderActive()) {
			this.entityRenderer.removeShaderGroup();
		}

		if (!this.isGamePaused) {
			this.mcMusicTicker.update();
			this.mcSoundHandler.update();
		}

		if (this.theWorld != null) {
			if (!this.isGamePaused) {
				this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

				try {
					this.theWorld.tick();
				} catch (Throwable throwable2) {
					CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

					if (this.theWorld == null) {
						CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
						crashreportcategory2.addCrashSection("Problem", "Level is null!");
					} else {
						this.theWorld.addWorldInfoToCrashReport(crashreport2);
					}

					throw new ReportedException(crashreport2);
				}
			}

			in.endStartSection("animateTick");

			if (!this.isGamePaused && this.theWorld != null)
				this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));

			in.endStartSection("particles");

			if (!this.isGamePaused) this.effectRenderer.updateEffects();
		} else if (this.myNetworkManager != null) {
			in.endStartSection("pendingConnection");
			this.myNetworkManager.processReceivedPackets();
		}

		in.endSection();
		this.systemTime = getSystemTime();
	}

	/**
	 * Arguments: World foldername,  World ingame name, WorldSettings
	 */
	public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
		this.loadWorld(null);
		System.gc();
		ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
		WorldInfo worldinfo = isavehandler.loadWorldInfo();

		if (worldinfo == null && worldSettingsIn != null) {
			worldinfo = new WorldInfo(worldSettingsIn, folderName);
			isavehandler.saveWorldInfo(worldinfo);
		}

		if (worldSettingsIn == null) {
			worldSettingsIn = new WorldSettings(worldinfo);
		}

		try {
			this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
			this.theIntegratedServer.startServerThread();
			this.integratedServerIsRunning = true;
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
			crashreportcategory.addCrashSection("Level ID", folderName);
			crashreportcategory.addCrashSection("Level Name", worldName);
			throw new ReportedException(crashreport);
		}

		this.loadingScreen.displaySavingString(Lang.format("menu.loadingLevel"));

		while (!this.theIntegratedServer.serverIsInRunLoop()) {
			String s = this.theIntegratedServer.getUserMessage();

			if (s != null) {
				this.loadingScreen.displayLoadingString(Lang.format(s));
			} else {
				this.loadingScreen.displayLoadingString("");
			}

			try {
				Thread.sleep(200L);
			} catch (InterruptedException ignored) {}
		}

		this.displayGuiScreen(null);
		SocketAddress socketaddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
		NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
		networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, null));
		networkmanager.sendPacket(new C00Handshake(47, socketaddress.toString(), 0, EnumConnectionState.LOGIN));
		networkmanager.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
		this.myNetworkManager = networkmanager;
	}

	/**
	 * unloads the current world first
	 */
	public void loadWorld(WorldClient worldClientIn) {
		this.loadWorld(worldClientIn, "");
	}

	/**
	 * par2Str is displayed on the loading screen to the user unloads the current world first
	 */
	public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
		if (worldClientIn == null) {
			NetHandlerPlayClient nethandlerplayclient = this.getNetHandler();

			if (nethandlerplayclient != null) {
				nethandlerplayclient.cleanup();
			}

			if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
				this.theIntegratedServer.initiateShutdown();
				this.theIntegratedServer.setStaticInstance();
			}

			this.theIntegratedServer = null;
			this.guiAchievement.clearAchievements();
			this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
		}

		this.renderViewEntity = null;
		this.myNetworkManager = null;

		if (this.loadingScreen != null) {
			this.loadingScreen.resetProgressAndMessage(loadingMessage);
			this.loadingScreen.displayLoadingString("");
		}

		if (worldClientIn == null && this.theWorld != null) {
			this.mcResourcePackRepository.func_148529_f();
			this.ingameGUI.func_181029_i();
			this.setServerData(null);
			this.integratedServerIsRunning = false;
		}

		this.mcSoundHandler.stopSounds();
		this.theWorld = worldClientIn;

		if (worldClientIn != null) {
			if (this.renderGlobal != null) {
				this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
			}

			if (this.effectRenderer != null) {
				this.effectRenderer.clearEffects(worldClientIn);
			}

			if (this.thePlayer == null) {
				this.thePlayer = this.playerController.func_178892_a(worldClientIn, new StatFileWriter());
				this.playerController.flipPlayer(this.thePlayer);
			}

			this.thePlayer.preparePlayerToSpawn();
			worldClientIn.spawnEntityInWorld(this.thePlayer);
			this.thePlayer.movementInput = new MovementInputFromOptions();
			this.playerController.setPlayerCapabilities(this.thePlayer);
			this.renderViewEntity = this.thePlayer;
		} else {
			this.saveLoader.flushCache();
			this.thePlayer = null;
		}

		System.gc();
		this.systemTime = 0L;
	}

	public void setDimensionAndSpawnPlayer(int dimension) {
		this.theWorld.setInitialSpawnLocation();
		this.theWorld.removeAllEntities();
		int i = 0;
		String s = null;

		if (this.thePlayer != null) {
			i = this.thePlayer.getEntityId();
			this.theWorld.removeEntity(this.thePlayer);
			s = this.thePlayer.getClientBrand();
		}

		this.renderViewEntity = null;
		EntityPlayerSP entityplayersp = this.thePlayer;
		this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
		this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(entityplayersp.getDataWatcher().getAllWatched());
		this.thePlayer.dimension = dimension;
		this.renderViewEntity = this.thePlayer;
		this.thePlayer.preparePlayerToSpawn();
		this.thePlayer.setClientBrand(s);
		this.theWorld.spawnEntityInWorld(this.thePlayer);
		this.playerController.flipPlayer(this.thePlayer);
		this.thePlayer.movementInput = new MovementInputFromOptions();
		this.thePlayer.setEntityId(i);
		this.playerController.setPlayerCapabilities(this.thePlayer);
		this.thePlayer.setReducedDebug(entityplayersp.hasReducedDebug());

		if (this.currentScreen instanceof GuiGameOver) {
			this.displayGuiScreen(null);
		}
	}

	public NetHandlerPlayClient getNetHandler() {
		return this.thePlayer != null ? this.thePlayer.sendQueue : null;
	}

	public static boolean isGuiEnabled() {
		return !Settings.HIDE_GUI.b();
	}

	public static boolean isFancyGraphicsEnabled() {
		return true;
	}

	/**
	 * Returns if ambient occlusion is enabled
	 */
	public static boolean isAmbientOcclusionEnabled() {
		return Settings.AO_LEVEL.f() > 0;
	}


	/**
	 * adds core server Info (GL version , Texture pack, isModded, type), and the worldInfo to the crash report
	 */
	public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
		theCrash.getCategory().addCrashSectionCallable("Launched Version", () -> this.launchedVersion);
		theCrash.getCategory().addCrashSectionCallable("LWJGL", Sys::getVersion);
		theCrash.getCategory().addCrashSectionCallable("OpenGL", () -> GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR));
		theCrash.getCategory().addCrashSectionCallable("GL Caps", OpenGlHelper::getLogText);
		theCrash.getCategory().addCrashSectionCallable("Using VBOs", () -> Settings.USE_VBO.b() ? "Yes" : "No");
		theCrash.getCategory().addCrashSectionCallable("Type", () -> "Client (map_client.txt)");
		theCrash.getCategory().addCrashSectionCallable("Resource Packs", () -> {
			StringBuilder stringbuilder = new StringBuilder();

			for (Object s : Settings.resourcePacks) {
				if (stringbuilder.length() > 0) {
					stringbuilder.append(", ");
				}

				stringbuilder.append(s);

				if (Settings.incompatibleResourcePacks.contains(String.valueOf(s))) {
					stringbuilder.append(" (incompatible)");
				}
			}

			return stringbuilder.toString();
		});
		theCrash.getCategory().addCrashSectionCallable("Current Language",
				() -> this.mcLanguageManager.getCurrentLanguage().toString());
		theCrash.getCategory().addCrashSectionCallable("Profiler Position",
				() -> in.profilingEnabled ? in.getNameOfLastSection() : "N/A (disabled)");
		theCrash.getCategory().addCrashSectionCallable("CPU", OpenGlHelper::getCPU);

		if (this.theWorld != null) {
			this.theWorld.addWorldInfoToCrashReport(theCrash);
		}

		return theCrash;
	}

	/**
	 * Return the singleton Minecraft instance for the game
	 */
	public static Minecraft getMinecraft() {
		return theMinecraft;
	}

	public ListenableFuture<Object> scheduleResourcesRefresh() {
		return this.addScheduledTask(this::refreshResources);
	}

	/**
	 * Used in the usage snooper.
	 */
	public static int getGLMaximumTextureSize() {
		for (int i = 16384; i > 0; i >>= 1) {
			GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
			int j = GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			if (j != 0) return i;
		}

		return -1;
	}

	/**
	 * Set the current ServerData instance.
	 */
	public void setServerData(ServerData serverDataIn) {
		this.currentServerData = serverDataIn;
	}

	public ServerData getCurrentServerData() {
		return this.currentServerData;
	}

	public boolean isIntegratedServerRunning() {
		return this.integratedServerIsRunning;
	}

	/**
	 * Returns true if there is only one player playing, and the current server is the integrated one.
	 */
	public boolean isSingleplayer() {
		return this.integratedServerIsRunning && this.theIntegratedServer != null;
	}

	/**
	 * Returns the currently running integrated server
	 */
	public IntegratedServer getIntegratedServer() {
		return this.theIntegratedServer;
	}

	public static void stopIntegratedServer() {
		if (theMinecraft == null) return;
		IntegratedServer integratedserver = theMinecraft.getIntegratedServer();
		if (integratedserver != null) integratedserver.stopServer();
	}

	/**
	 * Gets the system time in milliseconds.
	 */
	public static long getSystemTime() {
		return Sys.getTime() * 1000L / Sys.getTimerResolution();
	}

	/**
	 * Returns whether we're in full screen or not.
	 */
	public boolean isFullScreen() {
		return this.fullscreen;
	}

	public Session getSession() {
		return this.session;
	}

	public PropertyMap getPropertyMap() {
		if (this.propertyMap.isEmpty()) {
			GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
			this.propertyMap.putAll(gameprofile.getProperties());
		}

		return this.propertyMap;
	}

	public Proxy getProxy() {
		return this.proxy;
	}

	public TextureManager getTextureManager() {
		return this.renderEngine;
	}

	public IResourceManager getResourceManager() {
		return this.mcResourceManager;
	}

	public ResourcePackRepository getResourcePackRepository() {
		return this.mcResourcePackRepository;
	}

	public LanguageManager getLanguageManager() {
		return this.mcLanguageManager;
	}

	public TextureMap getTextureMapBlocks() {
		return this.textureMapBlocks;
	}

	public boolean isJava64bit() {
		return this.jvm64bit;
	}

	public boolean isGamePaused() {
		return this.isGamePaused;
	}

	public SoundHandler getSoundHandler() {
		return this.mcSoundHandler;
	}

	public MusicTicker.MusicType getAmbientMusicType() {
		return this.thePlayer != null ? this.thePlayer.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER : this.thePlayer.worldObj.provider instanceof WorldProviderEnd ? BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END : this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME : MusicTicker.MusicType.MENU;
	}

	public ModelManager getModelManager() {
		return modelManager;
	}

	public MinecraftSessionService getSessionService() {
		return this.sessionService;
	}

	public SkinManager getSkinManager() {
		return this.skinManager;
	}

	public Entity getRenderViewEntity() {
		return this.renderViewEntity;
	}

	public void setRenderViewEntity(Entity viewingEntity) {
		this.renderViewEntity = viewingEntity;
		this.entityRenderer.loadEntityShader(viewingEntity);
	}

	public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
		Validate.notNull(callableToSchedule);

		if (!this.isCallingFromMinecraftThread()) {
			ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callableToSchedule);

			synchronized (this.scheduledTasks) {
				this.scheduledTasks.add(listenablefuturetask);
				return listenablefuturetask;
			}
		}
		try {
			return Futures.immediateFuture(callableToSchedule.call());
		} catch (Exception exception) {
			return Futures.immediateFailedCheckedFuture(exception);
		}
	}

	public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
		Validate.notNull(runnableToSchedule);
		return this.addScheduledTask(Executors.callable(runnableToSchedule));
	}

	public boolean isCallingFromMinecraftThread() {
		return Thread.currentThread() == this.mcThread;
	}

	public BlockRendererDispatcher getBlockRendererDispatcher() {
		return this.blockRenderDispatcher;
	}

	public RenderManager getRenderManager() {
		return this.renderManager;
	}

	public RenderItem getRenderItem() {
		return this.renderItem;
	}

	public ItemRenderer getItemRenderer() {
		return this.itemRenderer;
	}

	public static int getDebugFPS() {
		return debugFPS;
	}

	public FrameTimer func_181539_aj() {
		return this.field_181542_y;
	}

	public static Map<String, String> getSessionInfo() {
		Map<String, String> map = Maps.newHashMap();
		map.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
		map.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
		map.put("X-Minecraft-Version", "1.8.8");
		return map;
	}

	public boolean func_181540_al() {
		return this.field_181541_X;
	}

	public void func_181537_a(boolean value) {
		this.field_181541_X = value;
	}

}
//