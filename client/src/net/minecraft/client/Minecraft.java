package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.game.DisplayGuy;
import net.minecraft.client.game.ErrorGuy;
import net.minecraft.client.game.GameWorldController;
import net.minecraft.client.game.entity.CPlayer;
import net.minecraft.client.game.input.InputHandler;
import net.minecraft.client.game.input.MouseHelper;
import net.minecraft.client.game.particle.EffectRenderer;
import net.minecraft.client.game.shader.Framebuffer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.font.MCFontRenderer;
import net.minecraft.client.gui.ingame.GuiIngame;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.protocol.minecraft_47.NetHandlerPlayClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Bootstrap;
import net.minecraft.logging.IProfiler;
import net.minecraft.logging.Log;
import net.minecraft.logging.Profiler;
import net.minecraft.network.NetworkManager;
import net.minecraft.client.resources.ClientRegistrar;
import net.minecraft.client.resources.ClientSideDatapack;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.DatapackManager;
import net.minecraft.resources.load.DatapackLoader;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.*;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.functional.StringUtils;
import net.minecraft.world.EnumDifficulty;
import optifine.Lagometer;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.Executors;

import static net.minecraft.logging.Log.MAIN;
import static net.minecraft.util.Util.OS.OSX;

public class Minecraft extends MinecraftCore {

	public static final boolean isRunningOnMac = Util.getOSType() == OSX;

	private final File fileResourcepacks;
	private final PropertyMap propertyMap;

	@Setter
	@Getter
	private ServerData currentServerData;

	@Getter
	private TextureManager textureManager;

	protected static Minecraft theMinecraft;
	public PlayerControllerMP playerController;
	private boolean hasCrashed;

	private CrashReport crashReport;
	public int displayWidth;
	public int displayHeight;

	@Getter
	private Timer timer = new Timer(20.0F);

	public WorldClient theWorld;
	public RenderGlobal renderGlobal;

	@Getter
	private RenderManager renderManager;

	@Getter
	private RenderItem renderItem;

	@Getter
	private ItemRenderer itemRenderer;
	public CPlayer thePlayer;
	public Entity pointedEntity;
	public EffectRenderer effectRenderer;

	@Getter
	private final Session session;

	@Getter
	private boolean isGamePaused;
	public MCFontRenderer fontRenderer;
	public MCFontRenderer standardGalacticFontRenderer;
	public GuiScreen currentScreen;
	public LoadingScreenRenderer loadingScreen;
	public EntityRenderer entityRenderer;

	@Getter
	public IntegratedServer integratedServer;

	public GuiAchievement guiAchievement;
	public GuiIngame ingameGUI;

	// The ray trace hit that the mouse is over.
	public MovingObjectPosition objectMouseOver;

	public MouseHelper mouseHelper;
	public final File mcDataDir;

	@Getter
	private final Proxy proxy;

	/**
	 * This is set to fpsCounter every debug screen update, and is shown on the debug screen. It's also sent as part of
	 * the usage snooping.
	 */
	@Getter
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

	@Getter
	private final FrameTimer frameTimer = new FrameTimer();

	long frameTiming = System.nanoTime();

	@Getter
	private final boolean isJava64bit;

	public NetworkManager myNetworkManager;

	@Getter
	public boolean integratedServerRunning;

	@Getter
	private IReloadableResourceManager resourceManager;

	private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
	private final List<IResourcePack> defaultResourcePacks = new ArrayList<>();
	private final DefaultResourcePack mcDefaultResourcePack;

	@Getter
	private ResourcePackRepository resourcePackRepository;

	@Getter
	public LanguageManager languageManager;
	private Framebuffer framebufferMc;

	@Getter
	private TextureMap textureMapBlocks;

	@Getter
	private SoundHandler soundHandler;

	@Getter
	private MusicTicker musicTicker;

	@Getter
	private final MinecraftSessionService sessionService;

	@Getter
	private SkinManager skinManager;
	private final Thread mcThread = Thread.currentThread();

	@Getter
	private ModelManager modelManager;

	@Getter
	private BlockRendererDispatcher blockRendererDispatcher;

	/**
	 * Set to true to keep the game loop running. Set to false by shutdown() to allow the game loop to exit cleanly.
	 */
	public volatile boolean running = true;

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
	public GameWorldController worldController;
	public ErrorGuy errorGuy;
	public DisplayGuy displayGuy;

	@Getter
	private IProfiler profiler;

	public Minecraft(GameConfiguration gameConfig) {
		theMinecraft = this;
		profiler = new Profiler();
		this.errorGuy = new ErrorGuy(this);
		this.mcDataDir = gameConfig.folderInfo.mcDataDir;
		this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
		this.propertyMap = gameConfig.userInfo.field_181172_c;
		this.mcDefaultResourcePack = new DefaultResourcePack();
		this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
		this.sessionService = new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.session = gameConfig.userInfo.session;
		this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
		this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
		this.displayGuy = new DisplayGuy(this, gameConfig.displayInfo);
		this.isJava64bit = FileUtil.is64bit();
		this.integratedServer = new IntegratedServer(this);
		StringUtils.class.getCanonicalName();

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
			this.errorGuy.displayCrashReport(this.errorGuy.addGraphicsAndWorldToCrashReport(crashreport));
			return;
		}

		try {
			while (this.running) {
				if (this.hasCrashed && this.crashReport != null) this.errorGuy.displayCrashReport(this.crashReport);
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
			this.errorGuy.addGraphicsAndWorldToCrashReport(e.getCrashReport());
			this.freeMemory();
			MAIN.error("Minecraft: Хм, а что это за кнопочка?");
			MAIN.error("Minecraft: *тык*");
			MAIN.error("Minecraft:", e);
			this.errorGuy.displayCrashReport(e.getCrashReport());
		} catch (Throwable t) {
			CrashReport report = this.errorGuy.addGraphicsAndWorldToCrashReport(new CrashReport("Неожиданная ошибка", t));
			this.freeMemory();
			MAIN.error("Произошла непонятная фигня! Спасайтесь!", t);
			this.errorGuy.displayCrashReport(report);
		} finally {
			this.shutdownMinecraftApplet();
		}
	}

	private final Thread loader = new Thread(this::loadStuff);
	public Preloader preloader;
	private volatile boolean blabla;
	private Drawable drawable;

	private void loadStuff() {


		try {
			drawable.makeCurrent();
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}

		this.worldController = new GameWorldController(this);
		preloader.nextState();

		this.soundHandler = new SoundHandler(this.resourceManager);
		this.resourceManager.registerReloadListener(this.soundHandler);

		this.musicTicker = new MusicTicker(this);
		preloader.nextState();


		this.standardGalacticFontRenderer = new MCFontRenderer(new ResourceLocation("textures/font/ascii_sga.png"), false);
		this.resourceManager.registerReloadListener(this.fontRenderer);
		this.resourceManager.registerReloadListener(this.standardGalacticFontRenderer);
		this.resourceManager.registerReloadListener(new GrassColorReloadListener());
		this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
		AchievementList.openInventory.setStatStringFormatter(s -> String.format(s, "E"));
		this.mouseHelper = new MouseHelper();
		preloader.nextState();
		this.errorGuy.checkGLError("Pre startup");
		this.errorGuy.checkGLError("Startup");
		this.textureMapBlocks = new TextureMap("textures");
		this.textureMapBlocks.setMipmapLevels((int) Settings.MIPMAP_LEVELS.f());
		this.textureManager.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
		this.textureManager.bindTexture(TextureMap.locationBlocksTexture);
		this.textureMapBlocks.setBlurMipmapDirect(false, Settings.MIPMAP_LEVELS.i() > 0);
		preloader.nextState();
		this.modelManager = new ModelManager(this.textureMapBlocks);
		preloader.nextState();
		this.resourceManager.registerReloadListener(this.modelManager);
		preloader.nextState();
		this.renderItem = new RenderItem(this.textureManager, this.modelManager);
		this.renderManager = new RenderManager(this.textureManager, this.renderItem);
		this.itemRenderer = new ItemRenderer(this);
		this.resourceManager.registerReloadListener(this.renderItem);
		preloader.nextState();
		this.entityRenderer = new EntityRenderer(this, this.resourceManager);
		this.resourceManager.registerReloadListener(this.entityRenderer);
		preloader.nextState();
		this.blockRendererDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes());
		this.resourceManager.registerReloadListener(this.blockRendererDispatcher);
		preloader.nextState();
		this.renderGlobal = new RenderGlobal(this);
		this.resourceManager.registerReloadListener(this.renderGlobal);
		preloader.nextState();
		this.guiAchievement = new GuiAchievement(this);
		this.effectRenderer = new EffectRenderer(this.theWorld, this.textureManager);
		for (DatapackLoader loader : DatapackManager.getTree().unloadingOrder()) {
			Datapack datapack = loader.getInstance();
			if (datapack.clientSide instanceof ClientSideDatapack) {
				ClientRegistrar registrar = new ClientRegistrar(datapack.getRegistrar());
				((ClientSideDatapack) datapack.clientSide).clientInit(registrar);
			}
		}
		blabla = true;
	}

	/**
	 * Starts the game: initializes the canvas, the title, the settings, etcetera.
	 */
	private void startGame() throws LWJGLException {
		Settings.init();
		this.inputHandler = new InputHandler(this);
		MAIN.info("Сессия аккаунта " + this.session.getUsername() + ", ID сессии: " + session.getSessionID());

		this.defaultResourcePacks.add(this.mcDefaultResourcePack);
		this.startTimerHackThread();

		this.displayGuy.setWindowIcon();
		this.displayGuy.setInitialDisplayMode();
		this.displayGuy.createDisplay();

		OpenGlHelper.initializeTextures();
		this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
		this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
		this.registerMetadataSerializers();
		this.resourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_);
		this.resourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
		this.languageManager = new LanguageManager(this.metadataSerializer_, Settings.language);
		this.resourceManager.registerReloadListener(this.languageManager);
		this.refreshResources();
		this.textureManager = new TextureManager(this.resourceManager);
		this.resourceManager.registerReloadListener(this.textureManager);
		this.skinManager = new SkinManager(this.textureManager, new File("gamedata/defaultresourcepack/skins"), this.sessionService);
		this.fontRenderer = new MCFontRenderer(new ResourceLocation("textures/font/ascii.png"), false);
		if (Settings.language != null) this.fontRenderer.setUcEnabled(this.isUnicode());

		preloader = new AdvPreloader(new ScaledResolution(this));
		preloader.header();
		preloader.render();
		drawable = new SharedDrawable(Display.getDrawable());
		for (DatapackLoader loader : DatapackManager.getTree().unloadingOrder()) loader.getInstance().init();
		loader.start();
		preloader.render();
		while (!blabla) {
			Display.sync(60);
			preloader.render();
		}

		G.enableTexture2D();
		G.shadeModel(GL11.GL_SMOOTH);
		G.clearDepth(1.0D);
		G.enableDepth();
		G.depthFunc(GL11.GL_LEQUAL);
		G.enableAlpha();
		G.alphaFunc(GL11.GL_GREATER, 0.1F);
		G.cullFace(1029);
		G.matrixMode(GL11.GL_PROJECTION);
		G.loadIdentity();
		G.matrixMode(GL11.GL_MODELVIEW);
		G.viewport(0, 0, this.displayWidth, this.displayHeight);
		this.errorGuy.checkGLError("Post startup");
		this.ingameGUI = new GuiIngame(this);

		if (this.serverName != null) this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
		else this.displayGuiScreen(new GuiMainMenu());

		this.loadingScreen = new LoadingScreenRenderer(this);

		if (Settings.USE_FULLSCREEN.b() && !this.displayGuy.fullscreen) this.displayGuy.toggleFullscreen();

		try {
			Display.setVSyncEnabled(Settings.ENABLE_VSYNC.b());
		} catch (OpenGLException var2) {
			Settings.ENABLE_VSYNC.set(false);
			Settings.saveOptions();
		}

		this.renderGlobal.makeEntityOutlineShader();
		preloader.dissolve();
	}

	private void registerMetadataSerializers() {
		this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
	}

	public Framebuffer getFramebuffer() {
		return this.framebufferMc;
	}

	private void startTimerHackThread() {
		Thread thread = new Thread("TimerAccuracyIncreaser") {
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
		this.crashReport = crash;
	}

	public boolean isUnicode() {
		return this.languageManager.isCurrentLocaleUnicode() || Settings.FORCE_UNICODE_FONT.b();
	}

	public void refreshResources() {
		List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);

		for (ResourcePackRepository.Entry resourcepackrepository$entry : this.resourcePackRepository.getRepositoryEntries()) {
			list.add(resourcepackrepository$entry.getResourcePack());
		}

		if (this.resourcePackRepository.getResourcePackInstance() != null) {
			list.add(this.resourcePackRepository.getResourcePackInstance());
		}

		try {
			this.resourceManager.reloadResources(list);
		} catch (RuntimeException runtimeexception) {
			MAIN.warn("Во время перезагрузки ресурсов произошла ошибка. Выключаем все ресурс-паки.", runtimeexception);
			list.clear();
			list.addAll(this.defaultResourcePacks);
			this.resourcePackRepository.setRepositories(Collections.emptyList());
			this.resourceManager.reloadResources(list);
			Settings.resourcePacks.clear();
			Settings.incompatibleResourcePacks.clear();
			Settings.saveOptions();
		}

		this.languageManager.parseLanguageMetadata(list);
		if (this.renderGlobal != null) this.renderGlobal.loadRenderers();
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
		} else {
			this.soundHandler.resumeSounds();
			this.inputHandler.setIngameFocus();
		}
	}

	/**
	 * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
	 * application (or web page) is exited.
	 */
	public void shutdownMinecraftApplet() {
		try {
			MAIN.info("Завершаем работу Minecraft...");
			try {
				this.worldController.loadWorld(null, this);
			} catch (Throwable ignored) {}

			this.soundHandler.unloadSounds();
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
		profiler.startSection("root");

		if (Display.isCreated() && Display.isCloseRequested()) this.shutdown();

		if (!this.isGamePaused || this.theWorld == null) this.timer.updateTimer();
		else {
			float f = this.timer.renderPartialTicks;
			this.timer.updateTimer();
			this.timer.renderPartialTicks = f;
		}


		Lagometer.timerScheduledExecutables.start();
		this.executeQueued();
		Lagometer.timerScheduledExecutables.end();

		Lagometer.timerTick.start();
		long l = System.nanoTime();
		profiler.startSection("tick");

		for (int j = 0; j < this.timer.elapsedTicks; ++j) this.runTick();

		Lagometer.timerTick.end();
		profiler.endStartSection("preRenderErrors");
		long i1 = System.nanoTime() - l;
		this.errorGuy.checkGLError("Pre render");
		profiler.endStartSection("sound");
		this.soundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
		profiler.endSection();
		profiler.startSection("render");
		G.pushMatrix();
		if (!Settings.DIRECT_RENDER.b()) G.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		this.framebufferMc.bindFramebuffer(true);
		profiler.startSection("display");
		G.enableTexture2D();

		if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) Settings.PERSPECTIVE.set(0);

		profiler.endSection();

		profiler.endStartSection("gameRenderer");
		this.entityRenderer.func_181560_a(this.timer.renderPartialTicks, i);
		profiler.endSection();

		profiler.endSection();

		if (Settings.SHOW_DEBUG.b() && Settings.PROFILER.b() && !Settings.HIDE_GUI.b()) {
			if (!profiler.isEnabled()) profiler.clearProfiling();

			profiler.setEnabled(true);
			this.displayGuy.displayDebugInfo(i1);
		} else {
			profiler.setEnabled(false);
			this.prevFrameTime = System.nanoTime();
		}

		this.guiAchievement.updateAchievementWindow();
		this.framebufferMc.unbindFramebuffer();
		G.popMatrix();
		G.pushMatrix();
		this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
		G.popMatrix();
		profiler.startSection("root");
		this.displayGuy.updateDisplay(this);
		Thread.yield();
		this.errorGuy.checkGLError("Post render");
		++this.fpsCounter;
		this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.integratedServer.getPublic();
		long k = System.nanoTime();
		this.getFrameTimer().func_181747_a(k - this.frameTiming);
		this.frameTiming = k;

		while (getSystemTime() >= this.debugUpdateTime + 1000L) {
			debugFPS = this.fpsCounter;
			this.debug = String.format("FPS: §a%d §f(CU: §a%d§f)", debugFPS, RenderChunk.renderChunksUpdated);
			RenderChunk.renderChunksUpdated = 0;
			this.debugUpdateTime += 1000L;
			this.fpsCounter = 0;
		}

		if (this.isFramerateLimitBelowMax()) {
			profiler.startSection("fpslimit_wait");
			Display.sync(this.getLimitFramerate());
			profiler.endSection();
		}

		profiler.endSection();
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
			this.worldController.loadWorld(null, this);
		} catch (Throwable ignored) {}

		System.gc();
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

		if (this.isSingleplayer() && !this.integratedServer.getPublic()) this.soundHandler.pauseSounds();
	}


	/**
	 * Runs the current tick.
	 */
	public void runTick() throws IOException {
		inputHandler.runTick();

		profiler.startSection("gui");
		if (!this.isGamePaused) this.ingameGUI.updateTick();
		profiler.endSection();

		this.entityRenderer.getMouseOver(1.0F);

		// Передвижение игрока
		profiler.startSection("gameMode");
		if (!this.isGamePaused && this.theWorld != null) this.playerController.updateController();

		profiler.endStartSection("textures");

		// Рендер мира
		if (!this.isGamePaused) this.textureManager.tick();

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

			profiler.endStartSection("mouse");
			inputHandler.processMouse();

			profiler.endStartSection("keyboard");
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

			profiler.endStartSection("gameRenderer");

			if (!this.isGamePaused) {
				this.entityRenderer.updateRenderer();
			}

			profiler.endStartSection("levelRenderer");

			if (!this.isGamePaused) {
				this.renderGlobal.updateClouds();
			}

			profiler.endStartSection("level");

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
			this.musicTicker.update();
			this.soundHandler.update();
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

			profiler.endStartSection("animateTick");

			if (!this.isGamePaused && this.theWorld != null)
				this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));

			profiler.endStartSection("particles");

			if (!this.isGamePaused) this.effectRenderer.updateEffects();
		} else if (this.myNetworkManager != null) {
			profiler.endStartSection("pendingConnection");
			this.myNetworkManager.processReceivedPackets();
		}

		profiler.endSection();
		this.systemTime = getSystemTime();
	}

	public NetHandlerPlayClient getNetHandler() {
		return this.thePlayer != null ? this.thePlayer.sendQueue : null;
	}

	public static boolean isAmbientOcclusionEnabled() {
		return Settings.AO_LEVEL.f() > 0;
	}

	public static Minecraft get() {
		return theMinecraft;
	}

	public ListenableFuture<Object> scheduleResourcesRefresh() {
		return this.queue(Executors.callable(this::refreshResources));
	}

	/**
	 * Returns true if there is only one player playing, and the current server is the integrated one.
	 */
	public boolean isSingleplayer() {
		return this.integratedServerRunning && this.integratedServer != null;
	}

	public static void stopIntegratedServer() {
		if (theMinecraft == null) return;
		IntegratedServer integratedserver = theMinecraft.getIntegratedServer();
		if (integratedserver != null) integratedserver.stopServer();
	}

	public static long getSystemTime() {
		return Sys.getTime() * 1000L / Sys.getTimerResolution();
	}

	public PropertyMap getPropertyMap() {
		if (this.propertyMap.isEmpty()) {
			GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
			this.propertyMap.putAll(gameprofile.getProperties());
		}

		return this.propertyMap;
	}

	public Entity getRenderViewEntity() {
		return this.worldController.renderViewEntity;
	}

	public void setRenderViewEntity(Entity viewingEntity) {
		this.worldController.renderViewEntity = viewingEntity;
		this.entityRenderer.loadEntityShader(viewingEntity);
	}

	public boolean isCallingFromMinecraftThread() {
		return Thread.currentThread() == this.mcThread;
	}

}