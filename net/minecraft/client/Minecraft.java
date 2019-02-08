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
import net.minecraft.Auth;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiContainerItems;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.*;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.SelectorSetting;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
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
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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

import static net.minecraft.client.keystrokes.Key.*;
import static net.minecraft.client.settings.KeyBinding.*;
import static net.minecraft.util.Util.OS.OSX;

public class Minecraft implements IThreadListener {

	private static final Logger logger = new Logger();
	private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
	public static final boolean isRunningOnMac = Util.getOSType() == OSX;

	private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
	private final File fileResourcepacks;
	private final PropertyMap twitchDetails;
	private final PropertyMap field_181038_N;
	private ServerData currentServerData;

	private TextureManager renderEngine;

	protected static Minecraft theMinecraft;
	public PlayerControllerMP playerController;
	private boolean fullscreen;
	private boolean enableGLErrorChecking = true;
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
	public FontRenderer fontRendererObj;
	public FontRenderer standardGalacticFontRenderer;
	public GuiScreen currentScreen;
	public LoadingScreenRenderer loadingScreen;
	public EntityRenderer entityRenderer;

	private int leftClickCounter;

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
	private final File fileAssets;
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
	private int rightClickDelayTimer;
	private String serverName;
	private int serverPort;

	/**
	 * Does the actual gameplay have focus. If so then mouse and keys will effect the player instead of menus.
	 */
	public boolean inGameHasFocus;
	long systemTime = getSystemTime();

	/**
	 * Join player counter
	 */
	private int joinPlayerCounter;
	public final FrameTimer field_181542_y = new FrameTimer();
	long field_181543_z = System.nanoTime();
	private final boolean jvm64bit;
	private NetworkManager myNetworkManager;
	private boolean integratedServerIsRunning;

	/**
	 * The profiler instance
	 */
	public final Profiler mcProfiler = new Profiler();

	/**
	 * Keeps track of how long the debug crash keycombo (F3+C) has been pressed for, in order to crash after 10 seconds.
	 */
	private long debugCrashKeyPressTime = -1L;
	private IReloadableResourceManager mcResourceManager;
	private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
	private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
	private final DefaultResourcePack mcDefaultResourcePack;
	private ResourcePackRepository mcResourcePackRepository;
	private LanguageManager mcLanguageManager;
	private Framebuffer framebufferMc;
	private TextureMap textureMapBlocks;
	private SoundHandler mcSoundHandler;
	private MusicTicker mcMusicTicker;
	private ResourceLocation mojangLogo;
	private final MinecraftSessionService sessionService;
	private SkinManager skinManager;
	private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
	private final Thread mcThread = Thread.currentThread();
	private ModelManager modelManager;

	/**
	 * The BlockRenderDispatcher instance that will be used based off gamesettings
	 */
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

	/**
	 * Profiler currently displayed in the debug screen pie chart
	 */
	private String debugProfilerName = "root";

	public Minecraft(GameConfiguration gameConfig) {
		theMinecraft = this;
		this.mcDataDir = gameConfig.folderInfo.mcDataDir;
		this.fileAssets = gameConfig.folderInfo.assetsDir;
		this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
		this.launchedVersion = gameConfig.gameInfo.version;
		this.twitchDetails = gameConfig.userInfo.userProperties;
		this.field_181038_N = gameConfig.userInfo.field_181172_c;
		this.mcDefaultResourcePack = new DefaultResourcePack(new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex).getResourceMap());
		this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
		this.sessionService = new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.session = gameConfig.userInfo.session;
		logger.info("Установлено имя " + this.session.getUsername() + ", ID сессии: " + session.getSessionID());
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

	public static String getGlobalName() {
		return getMinecraft().getSession().defaultName;
	}

	public void run() {
		this.running = true;

		Auth.loadPassword();

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
				if (!this.hasCrashed || this.crashReporter == null) {
					try {
						this.runGameLoop();
					} catch (OutOfMemoryError var10) {
						this.freeMemory();
						this.displayGuiScreen(new GuiMemoryErrorScreen());
						System.gc();
					}
				} else {
					this.displayCrashReport(this.crashReporter);
				}
			}
		} catch (MinecraftError ignored) {
		} catch (ReportedException reportedexception) {
			this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
			this.freeMemory();
			logger.fatal("Reported exception thrown!", reportedexception);
			this.displayCrashReport(reportedexception.getCrashReport());
		} catch (Throwable throwable1) {
			CrashReport crashreport1 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
			this.freeMemory();
			logger.fatal("Unreported exception thrown!", throwable1);
			this.displayCrashReport(crashreport1);
		} finally {
			this.shutdownMinecraftApplet();
		}
	}

	/**
	 * Starts the game: initializes the canvas, the title, the settings, etcetera.
	 */
	private void startGame() throws LWJGLException {
		long start = System.currentTimeMillis();
		Settings.init();
		this.defaultResourcePacks.add(this.mcDefaultResourcePack);
		this.startTimerHackThread();

		logger.info("Используемая версия LWJGL: " + Sys.getVersion() + " (Вау, почти как новая)");
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
		this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
		this.drawMojangLogo(this.renderEngine);
		this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
		this.mcSoundHandler = new SoundHandler(this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
		this.mcMusicTicker = new MusicTicker(this);
		this.fontRendererObj = new FontRenderer(new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

		if (Settings.language != null) {
			this.fontRendererObj.setUnicodeFlag(this.isUnicode());
			this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
		}

		long end = System.currentTimeMillis();
		System.out.println("# Преинициализация завершена за " + (end - start) + " мс.");

		this.standardGalacticFontRenderer = new FontRenderer(new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
		this.mcResourceManager.registerReloadListener(this.fontRendererObj);
		this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
		this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
		this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
		AchievementList.openInventory.setStatStringFormatter(s -> String.format(s, "E"));
		this.mouseHelper = new MouseHelper();
		this.checkGLError("Pre startup");
		GlStateManager.enableTexture2D();
		GlStateManager.shadeModel(7425);
		GlStateManager.clearDepth(1.0D);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.cullFace(1029);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		this.checkGLError("Startup");
		this.textureMapBlocks = new TextureMap("textures");
		this.textureMapBlocks.setMipmapLevels((int) Settings.MIPMAP_LEVELS.f());
		this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
		this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		this.textureMapBlocks.setBlurMipmapDirect(false, Settings.MIPMAP_LEVELS.i() > 0);
		this.modelManager = new ModelManager(this.textureMapBlocks);
		this.mcResourceManager.registerReloadListener(this.modelManager);
		this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
		this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
		this.itemRenderer = new ItemRenderer(this);
		this.mcResourceManager.registerReloadListener(this.renderItem);
		this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.entityRenderer);
		this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes());
		this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
		this.renderGlobal = new RenderGlobal(this);
		this.mcResourceManager.registerReloadListener(this.renderGlobal);
		this.guiAchievement = new GuiAchievement(this);
		GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
		this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
		this.checkGLError("Post startup");
		this.ingameGUI = new GuiIngame(this);

		if (this.serverName != null) this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
		else this.displayGuiScreen(new GuiMainMenu());

		this.renderEngine.deleteTexture(this.mojangLogo);
		this.mojangLogo = null;
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
		Display.setTitle("Implario Client");

		try {
			Display.create(new PixelFormat().withDepthBits(24));
		} catch (LWJGLException lwjglexception) {
			logger.error("Couldn\'t set pixel format", lwjglexception);

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException ignored) {}

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

		InputStream inputstream = null;
		InputStream inputstream1 = null;

		try {
			inputstream = Minecraft.class.getResourceAsStream("/icon16.png");
			inputstream1 = Minecraft.class.getResourceAsStream("/icon32.png");

//				inputstream = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
//				inputstream1 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));

			if (inputstream != null && inputstream1 != null) {
				Display.setIcon(new ByteBuffer[] {this.readImageToBuffer(inputstream), this.readImageToBuffer(inputstream1)});
			}
		} catch (IOException ioexception) {
			logger.error("Couldn\'t set icon", ioexception);
		} finally {
			IOUtils.closeQuietly(inputstream);
			IOUtils.closeQuietly(inputstream1);
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
		File file1 = new File(getMinecraft().mcDataDir, "crash-reports");
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
			logger.info("Caught error stitching, removing all assigned resourcepacks", runtimeexception);
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

					while (true) {
						if (!iterator.hasNext()) {
							continue label53;
						}

						displaymode3 = (DisplayMode) iterator.next();

						if (displaymode3.getBitsPerPixel() == 32 && displaymode3.getWidth() == displaymode1.getWidth() / 2 && displaymode3.getHeight() == displaymode1.getHeight() / 2) {
							break;
						}
					}

					displaymode = displaymode3;
				}
			}
		}

		Display.setDisplayMode(displaymode);
		this.displayWidth = displaymode.getWidth();
		this.displayHeight = displaymode.getHeight();
	}

	private void drawMojangLogo(TextureManager txtmgr) {
		ScaledResolution scaledresolution = new ScaledResolution(this);
		int i = scaledresolution.getScaleFactor();
		Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i, true);
		framebuffer.bindFramebuffer(false);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, (double) scaledresolution.getScaledWidth(), (double) scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepth();
		GlStateManager.enableTexture2D();
		InputStream inputstream = null;

		try {
			inputstream = this.mcDefaultResourcePack.getInputStream(locationMojangPng);
			this.mojangLogo = txtmgr.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(inputstream)));
			txtmgr.bindTexture(this.mojangLogo);
		} catch (IOException ioexception) {
			logger.error("Unable to load logo: " + locationMojangPng, ioexception);
		} finally {
			IOUtils.closeQuietly(inputstream);
		}

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(0.0D, (double) this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		worldrenderer.pos((double) this.displayWidth, (double) this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		worldrenderer.pos((double) this.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		int j = 256;
		int k = 256;
		this.func_181536_a((scaledresolution.getScaledWidth() - j) / 2, (scaledresolution.getScaledHeight() - k) / 2, 0, 0, j, k, 255, 255, 255, 255);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		framebuffer.unbindFramebuffer();
		framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i);
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		this.updateDisplay();
	}

	public void func_181536_a(int p_181536_1_, int p_181536_2_, int p_181536_3_, int p_181536_4_, int p_181536_5_, int p_181536_6_, int p_181536_7_, int p_181536_8_, int p_181536_9_, int p_181536_10_) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos((double) p_181536_1_, (double) (p_181536_2_ + p_181536_6_), 0.0D).tex((double) ((float) p_181536_3_ * f), (double) ((float) (p_181536_4_ + p_181536_6_) * f1)).color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).endVertex();
		worldrenderer.pos((double) (p_181536_1_ + p_181536_5_), (double) (p_181536_2_ + p_181536_6_), 0.0D).tex((double) ((float) (p_181536_3_ + p_181536_5_) * f), (double) ((float) (p_181536_4_ + p_181536_6_) * f1)).color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).endVertex();
		worldrenderer.pos((double) (p_181536_1_ + p_181536_5_), (double) p_181536_2_, 0.0D).tex((double) ((float) (p_181536_3_ + p_181536_5_) * f), (double) ((float) p_181536_4_ * f1)).color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).endVertex();
		worldrenderer.pos((double) p_181536_1_, (double) p_181536_2_, 0.0D).tex((double) ((float) p_181536_3_ * f), (double) ((float) p_181536_4_ * f1)).color(p_181536_7_, p_181536_8_, p_181536_9_, p_181536_10_).endVertex();
		Tessellator.getInstance().draw();
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
			this.setIngameNotInFocus();
			ScaledResolution scaledresolution = new ScaledResolution(this);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
			guiScreenIn.setWorldAndResolution(this, i, j);
			this.skipRenderWorld = false;
		} else {
			this.mcSoundHandler.resumeSounds();
			this.setIngameFocus();
		}
	}

	/**
	 * Checks for an OpenGL error. If there is one, prints the error ID and error string.
	 */
	private void checkGLError(String message) {
		if (!this.enableGLErrorChecking) return;
		int i = GL11.glGetError();
		if (i == 0) return;

		String s = GLU.gluErrorString(i);
		logger.error("########## ОШИБКА OpenGL ##########");
		logger.error("@ " + message);
		logger.error(i + ": " + s);
	}

	/**
	 * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
	 * application (or web page) is exited.
	 */
	public void shutdownMinecraftApplet() {
		try {
			logger.info("Завершаем работу Minecraft...");
			try {
				this.loadWorld(null);
			} catch (Throwable ignored) {}

			Auth.savePassword();
			this.mcSoundHandler.unloadSounds();
		} finally {
			Display.destroy();
			if (!this.hasCrashed) System.exit(0);
		}

		System.gc();
	}

	/**
	 * Called repeatedly from run()
	 */
	private void runGameLoop() throws IOException {
		long i = System.nanoTime();
		this.mcProfiler.startSection("root");

		if (Display.isCreated() && Display.isCloseRequested()) this.shutdown();

		if (!this.isGamePaused || this.theWorld == null) this.timer.updateTimer();
		else {
			float f = this.timer.renderPartialTicks;
			this.timer.updateTimer();
			this.timer.renderPartialTicks = f;
		}

		this.mcProfiler.startSection("scheduledExecutables");

		synchronized (this.scheduledTasks) {
			while (!this.scheduledTasks.isEmpty()) Util.schedule((FutureTask) this.scheduledTasks.poll(), logger);
		}

		this.mcProfiler.endSection();
		long l = System.nanoTime();
		this.mcProfiler.startSection("tick");

		for (int j = 0; j < this.timer.elapsedTicks; ++j) this.runTick();

		this.mcProfiler.endStartSection("preRenderErrors");
		long i1 = System.nanoTime() - l;
		this.checkGLError("Pre render");
		this.mcProfiler.endStartSection("sound");
		this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
		this.mcProfiler.endSection();
		this.mcProfiler.startSection("render");
		GlStateManager.pushMatrix();
		GlStateManager.clear(16640);
		this.framebufferMc.bindFramebuffer(true);
		this.mcProfiler.startSection("display");
		GlStateManager.enableTexture2D();

		if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) Settings.PERSPECTIVE.set(0);

		this.mcProfiler.endSection();

		if (!this.skipRenderWorld) {
			this.mcProfiler.endStartSection("gameRenderer");
			this.entityRenderer.func_181560_a(this.timer.renderPartialTicks, i);
			this.mcProfiler.endSection();
		}

		this.mcProfiler.endSection();

		if (Settings.SHOW_DEBUG.b() && Settings.PROFILER.b() && !Settings.HIDE_GUI.b()) {
			if (!this.mcProfiler.profilingEnabled) this.mcProfiler.clearProfiling();

			this.mcProfiler.profilingEnabled = true;
			this.displayDebugInfo(i1);
		} else {
			this.mcProfiler.profilingEnabled = false;
			this.prevFrameTime = System.nanoTime();
		}

		this.guiAchievement.updateAchievementWindow();
		this.framebufferMc.unbindFramebuffer();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
		GlStateManager.popMatrix();
		this.mcProfiler.startSection("root");
		this.updateDisplay();
		Thread.yield();
		this.mcProfiler.startSection("stream");
		this.mcProfiler.startSection("update");
		this.mcProfiler.endStartSection("submit");
		this.mcProfiler.endSection();
		this.mcProfiler.endSection();
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
			this.mcProfiler.startSection("fpslimit_wait");
			Display.sync(this.getLimitFramerate());
			this.mcProfiler.endSection();
		}

		this.mcProfiler.endSection();
	}

	public void updateDisplay() {
		this.mcProfiler.startSection("display_update");
		Display.update();
		this.mcProfiler.endSection();
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
	 * Update debugProfilerName in response to number keys in debug screen
	 */
	private void updateDebugProfilerName(int keyCount) {
		List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);

		if (list == null || list.isEmpty()) return;
		Profiler.Result profiler$result = list.remove(0);

		if (keyCount == 0) {
			if (profiler$result.field_76331_c.length() > 0) {
				int i = this.debugProfilerName.lastIndexOf(".");
				if (i >= 0) this.debugProfilerName = this.debugProfilerName.substring(0, i);
			}
		} else {
			--keyCount;

			if (keyCount < list.size() && !list.get(keyCount).field_76331_c.equals("unspecified")) {
				if (this.debugProfilerName.length() > 0) {
					this.debugProfilerName = this.debugProfilerName + ".";
				}

				this.debugProfilerName = this.debugProfilerName + list.get(keyCount).field_76331_c;
			}
		}
	}

	/**
	 * Parameter appears to be unused
	 */
	private void displayDebugInfo(long elapsedTicksTime) {
		if (!this.mcProfiler.profilingEnabled) return;
		List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
		Profiler.Result profiler$result = list.remove(0);
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.enableColorMaterial();
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, (double) this.displayWidth, (double) this.displayHeight, 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GL11.glLineWidth(1.0F);
		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		int i = 160;
		int j = this.displayWidth - i - 10;
		int k = this.displayHeight - i * 2;
		GlStateManager.enableBlend();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double) ((float) j - (float) i * 1.1F), (double) ((float) k - (float) i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j - (float) i * 1.1F), (double) (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j + (float) i * 1.1F), (double) (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j + (float) i * 1.1F), (double) ((float) k - (float) i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
		tessellator.draw();
		GlStateManager.disableBlend();
		double d0 = 0.0D;

		for (Profiler.Result profiler$result1 : list) {
			int i1 = MathHelper.floor_double(profiler$result1.field_76332_a / 4.0D) + 1;
			worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
			int j1 = profiler$result1.func_76329_a();
			int k1 = j1 >> 16 & 255;
			int l1 = j1 >> 8 & 255;
			int i2 = j1 & 255;
			worldrenderer.pos((double) j, (double) k, 0.0D).color(k1, l1, i2, 255).endVertex();

			for (int j2 = i1; j2 >= 0; --j2) {
				float f = (float) ((d0 + profiler$result1.field_76332_a * (double) j2 / (double) i1) * Math.PI * 2.0D / 100.0D);
				float f1 = MathHelper.sin(f) * (float) i;
				float f2 = MathHelper.cos(f) * (float) i * 0.5F;
				worldrenderer.pos((double) ((float) j + f1), (double) ((float) k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
			}

			tessellator.draw();
			worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

			for (int i3 = i1; i3 >= 0; --i3) {
				float f3 = (float) ((d0 + profiler$result1.field_76332_a * (double) i3 / (double) i1) * Math.PI * 2.0D / 100.0D);
				float f4 = MathHelper.sin(f3) * (float) i;
				float f5 = MathHelper.cos(f3) * (float) i * 0.5F;
				worldrenderer.pos((double) ((float) j + f4), (double) ((float) k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
				worldrenderer.pos((double) ((float) j + f4), (double) ((float) k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
			}

			tessellator.draw();
			d0 += profiler$result1.field_76332_a;
		}

		DecimalFormat decimalformat = new DecimalFormat("##0.00");
		GlStateManager.enableTexture2D();
		String s = "";

		if (!profiler$result.field_76331_c.equals("unspecified")) s = s + "[0] ";
		if (profiler$result.field_76331_c.length() == 0) s = s + "ROOT ";
		else s = s + profiler$result.field_76331_c + " ";

		int l2 = 16777215;
		this.fontRendererObj.drawStringWithShadow(s, (float) (j - i), (float) (k - i / 2 - 16), l2);
		this.fontRendererObj.drawStringWithShadow(s = decimalformat.format(profiler$result.field_76330_b) + "%", (float) (j + i - this.fontRendererObj.getStringWidth(s)), (float) (k - i / 2 - 16), l2);

		for (int k2 = 0; k2 < list.size(); k2++) {
			Profiler.Result profiler$result2 = list.get(k2);
			String s1 = "";

			if (profiler$result2.field_76331_c.equals("unspecified")) s1 = s1 + "[?] ";
			else s1 = s1 + "[" + (k2 + 1) + "] ";

			s1 = s1 + profiler$result2.field_76331_c;
			this.fontRendererObj.drawStringWithShadow(s1, (float) (j - i), (float) (k + i / 2 + k2 * 8 + 20), profiler$result2.func_76329_a());
			this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.field_76332_a) + "%", (float) (j + i - 50 - this.fontRendererObj.getStringWidth(s1)), (float) (k + i / 2 + k2 * 8 + 20), profiler$result2.func_76329_a());
			this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.field_76330_b) + "%", (float) (j + i - this.fontRendererObj.getStringWidth(s1)), (float) (k + i / 2 + k2 * 8 + 20), profiler$result2.func_76329_a());
		}
	}

	/**
	 * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
	 */
	public void shutdown() {
		this.running = false;
	}

	/**
	 * Will set the focus to ingame if the Minecraft window is the active with focus. Also clears any GUI screen
	 * currently displayed
	 */
	public void setIngameFocus() {
		if (!Display.isActive()) return;
		if (this.inGameHasFocus) return;
		this.inGameHasFocus = true;
		this.mouseHelper.grabMouseCursor();
		this.displayGuiScreen(null);
		this.leftClickCounter = 10000;
	}

	/**
	 * Resets the player keystate, disables the ingame focus, and ungrabs the mouse cursor.
	 */
	public void setIngameNotInFocus() {
		if (!this.inGameHasFocus) return;
		KeyBinding.unPressAllKeys();
		this.inGameHasFocus = false;
		this.mouseHelper.ungrabMouseCursor();
	}

	/**
	 * Displays the ingame menu
	 */
	public void displayInGameMenu() {
		if (this.currentScreen != null) return;
		this.displayGuiScreen(new GuiIngameMenu());

		if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) this.mcSoundHandler.pauseSounds();
	}

	private void sendClickBlockToController(boolean leftClick) {
		if (!leftClick) {
			this.leftClickCounter = 0;
		}

		if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
			if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				BlockPos blockpos = this.objectMouseOver.getBlockPos();

				if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
					this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
					this.thePlayer.swingItem();
				}
			} else {
				this.playerController.resetBlockRemoving();
			}
		}
	}

	private void clickMouse() {
		if (this.leftClickCounter > 0) return;
		this.thePlayer.swingItem();

		if (this.objectMouseOver == null) {
			logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");
			if (this.playerController.isNotCreative()) this.leftClickCounter = 10;
			return;
		}
		switch (this.objectMouseOver.typeOfHit) {
			case ENTITY:
				this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
				break;
			case BLOCK:
				BlockPos blockpos = this.objectMouseOver.getBlockPos();
				if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
					this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
					break;
				}
			default:
				if (this.playerController.isNotCreative()) this.leftClickCounter = 10;
		}
	}

	/*
	  Called when user clicked he's mouse right button (place)
	 */
	private void rightClickMouse() {
		if (this.playerController.func_181040_m()) return;
		this.rightClickDelayTimer = Settings.FAST_PLACE.b() ? 0 : 4;
		boolean flag = true;
		ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();

		if (this.objectMouseOver == null) {
			logger.warn("Null returned as \'hitResult\', this shouldn\'t happen!");
			return;
		}
		switch (this.objectMouseOver.typeOfHit) {
			case ENTITY:
				if (this.playerController.func_178894_a(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver)) flag = false;
				else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) flag = false;

				break;

			case BLOCK:
				BlockPos blockpos = this.objectMouseOver.getBlockPos();

				if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) break;
				int i = itemstack != null ? itemstack.stackSize : 0;

				if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, itemstack, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
					flag = false;
					this.thePlayer.swingItem();
				}

				if (itemstack == null) return;

				if (itemstack.stackSize == 0) this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
				else if (itemstack.stackSize != i || this.playerController.isInCreativeMode()) this.entityRenderer.itemRenderer.resetEquippedProgress();
		}

		if (flag) {
			ItemStack itemstack1 = this.thePlayer.inventory.getCurrentItem();

			if (itemstack1 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, itemstack1)) {
				this.entityRenderer.itemRenderer.resetEquippedProgress2();
			}
		}
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
		} catch (Exception exception) {
			logger.error("Couldn\'t toggle fullscreen", exception);
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

	public MusicTicker func_181535_r() {
		return this.mcMusicTicker;
	}

	/**
	 * Runs the current tick.
	 */
	public void runTick() throws IOException {
		if (this.rightClickDelayTimer > 0) --this.rightClickDelayTimer;

		this.mcProfiler.startSection("gui");

		if (!this.isGamePaused) this.ingameGUI.updateTick();

		this.mcProfiler.endSection();
		this.entityRenderer.getMouseOver(1.0F);

		// Передвижение игрока
		this.mcProfiler.startSection("gameMode");
		if (!this.isGamePaused && this.theWorld != null) this.playerController.updateController();

		this.mcProfiler.endStartSection("textures");

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

		if (this.currentScreen != null) this.leftClickCounter = 10000;

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
			this.mcProfiler.endStartSection("mouse");

			while (Mouse.next()) {
				int i = Mouse.getEventButton();
				KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

				if (Mouse.getEventButtonState()) {
					if (this.thePlayer.isSpectator() && i == 2) {
						this.ingameGUI.getSpectatorGui().func_175261_b();
					} else {
						KeyBinding.onTick(i - 100);
					}
				}

				long i1 = getSystemTime() - this.systemTime;

				if (i1 <= 200L) {
					int j = Mouse.getEventDWheel();

					if (j != 0) {
						if (this.thePlayer.isSpectator()) {
							j = j < 0 ? -1 : 1;

							if (this.ingameGUI.getSpectatorGui().func_175262_a()) {
								this.ingameGUI.getSpectatorGui().func_175259_b(-j);
							} else {
								float f = MathHelper.clamp_float(this.thePlayer.capabilities.getFlySpeed() + (float) j * 0.005F, 0.0F, 0.2F);
								this.thePlayer.capabilities.setFlySpeed(f);
							}
						} else {
							this.thePlayer.inventory.changeCurrentItem(j);
						}
					}

					if (this.currentScreen == null) {
						if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
							this.setIngameFocus();
						}
					} else if (this.currentScreen != null) {
						this.currentScreen.handleMouseInput();
					}
				}
			}

			if (this.leftClickCounter > 0) {
				--this.leftClickCounter;
			}

			this.mcProfiler.endStartSection("keyboard");

			while (Keyboard.next()) {
				int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
				KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());

				if (Keyboard.getEventKeyState()) {
					KeyBinding.onTick(k);
				}

				if (this.debugCrashKeyPressTime > 0L) {
					if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
						throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
					}

					if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
						this.debugCrashKeyPressTime = -1L;
					}
				} else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
					this.debugCrashKeyPressTime = getSystemTime();
				}

				this.dispatchKeypresses();

				if (Keyboard.getEventKeyState()) {
					if (k == F4 && this.entityRenderer != null)
						this.entityRenderer.switchUseShader();

					if (this.currentScreen != null) this.currentScreen.handleKeyboardInput();
					else {
						if (k == ESC) this.displayInGameMenu();

						if (Keyboard.isKeyDown(F3)) switch (k) {
							case 32:
								MC.clearChat();
								break; // D
							case 31:
							case 20:
								refreshResources();
								break; // S & T
							case 30:
								renderGlobal.loadRenderers();
								break; // A
							case 35:
								Settings.ITEM_TOOLTIPS.toggle();
								Settings.saveOptions();
								break; // H
							case 48:
								MC.toggleHitboxes();
								break; // B
							case 25:
								Settings.PAUSE_FOCUS.toggle();
								Settings.saveOptions();
								break; // P
						}

						if (k == F1) Settings.HIDE_GUI.toggle();

						if (k == F3) {
							Settings.SHOW_DEBUG.toggle();
							Settings.PROFILER.set(GuiScreen.isShiftKeyDown());
							Settings.LAGOMETER.set(GuiScreen.isAltKeyDown());
						}

						if (KeyBinding.PERSPECTIVE.isPressed()) {
							int view = ((SelectorSetting) Settings.PERSPECTIVE.getBase()).toggle();

							if (view == 0) this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
							else if (view == 1) this.entityRenderer.loadEntityShader(null);

							this.renderGlobal.setDisplayListEntitiesDirty();
						}

						if (KeyBinding.SMOOTH_CAMERA.isPressed()) Settings.SMOOTH_CAMERA.toggle();
					}

					if (Settings.SHOW_DEBUG.b() && Settings.PROFILER.b()) {
						if (k == 11) this.updateDebugProfilerName(0);
						for (int j1 = 0; j1 < 9; ++j1) if (k == 2 + j1) this.updateDebugProfilerName(j1 + 1);
					}
				}
			}

			for (int l = 0; l < 9; ++l) {
				if (KeyBinding.HOTBAR[l].isPressed()) {
					if (this.thePlayer.isSpectator()) {
						this.ingameGUI.getSpectatorGui().func_175260_a(l);
					} else {
						this.thePlayer.inventory.currentItem = l;
					}
				}
			}

			boolean flag = Settings.CHAT_VISIBILITY.i() != 2;

			while (KeyBinding.INVENTORY.isPressed()) {
				if (this.playerController.isRidingHorse()) this.thePlayer.sendHorseInventory();
				else {
					this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
					this.displayGuiScreen(new GuiInventory(this.thePlayer));
				}
			}
			while (KeyBinding.ITEMS.isPressed()) {
				if (this.playerController.isRidingHorse()) this.thePlayer.sendHorseInventory();
				else {
					this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
					this.displayGuiScreen(new GuiContainerItems(this.thePlayer));
				}
			}

			while (KeyBinding.DROP.isPressed()) if (!this.thePlayer.isSpectator()) this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());

			while (KeyBinding.CHAT.isPressed() && flag) this.displayGuiScreen(new GuiChat());

			if (this.currentScreen == null && KeyBinding.COMMAND.isPressed() && flag) this.displayGuiScreen(new GuiChat("/"));

			if (this.thePlayer.isUsingItem()) {
				if (!USE.isKeyDown()) this.playerController.onStoppedUsingItem(this.thePlayer);
				while (ATTACK.isPressed());
				while (USE.isPressed());
				while (PICK.isPressed());
			} else {
				while (ATTACK.isPressed()) this.clickMouse();
				while (USE.isPressed()) this.rightClickMouse();
				while (PICK.isPressed()) this.middleClickMouse();
			}

			if (USE.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) this.rightClickMouse();

			this.sendClickBlockToController(this.currentScreen == null && ATTACK.isKeyDown() && this.inGameHasFocus);
		}

		if (this.theWorld != null) {
			if (this.thePlayer != null) {
				++this.joinPlayerCounter;

				if (this.joinPlayerCounter == 30) {
					this.joinPlayerCounter = 0;
					this.theWorld.joinEntityInSurroundings(this.thePlayer);
				}
			}

			this.mcProfiler.endStartSection("gameRenderer");

			if (!this.isGamePaused) {
				this.entityRenderer.updateRenderer();
			}

			this.mcProfiler.endStartSection("levelRenderer");

			if (!this.isGamePaused) {
				this.renderGlobal.updateClouds();
			}

			this.mcProfiler.endStartSection("level");

			if (!this.isGamePaused) {
				if (this.theWorld.getLastLightningBolt() > 0) {
					this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
				}

				this.theWorld.updateEntities();
			}
		} else if (this.entityRenderer.isShaderActive()) {
			this.entityRenderer.func_181022_b();
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

			this.mcProfiler.endStartSection("animateTick");

			if (!this.isGamePaused && this.theWorld != null)
				this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));

			this.mcProfiler.endStartSection("particles");

			if (!this.isGamePaused) this.effectRenderer.updateEffects();
		} else if (this.myNetworkManager != null) {
			this.mcProfiler.endStartSection("pendingConnection");
			this.myNetworkManager.processReceivedPackets();
		}

		this.mcProfiler.endSection();
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
	 * Called when user clicked he's mouse middle button (pick block)
	 */
	private void middleClickMouse() {
		if (this.objectMouseOver != null) {
			boolean flag = this.thePlayer.capabilities.isCreativeMode;
			int i = 0;
			boolean flag1 = false;
			TileEntity tileentity = null;
			Item item;

			if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				BlockPos blockpos = this.objectMouseOver.getBlockPos();
				Block block = this.theWorld.getBlockState(blockpos).getBlock();

				if (block.getMaterial() == Material.air) {
					return;
				}

				item = block.getItem(this.theWorld, blockpos);

				if (item == null) {
					return;
				}

				if (flag && GuiScreen.isCtrlKeyDown()) {
					tileentity = this.theWorld.getTileEntity(blockpos);
				}

				Block block1 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
				i = block1.getDamageValue(this.theWorld, blockpos);
				flag1 = item.getHasSubtypes();
			} else {
				if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !flag) {
					return;
				}

				if (this.objectMouseOver.entityHit instanceof EntityPainting) {
					item = Items.painting;
				} else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
					item = Items.lead;
				} else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
					EntityItemFrame entityitemframe = (EntityItemFrame) this.objectMouseOver.entityHit;
					ItemStack itemstack = entityitemframe.getDisplayedItem();

					if (itemstack == null) {
						item = Items.item_frame;
					} else {
						item = itemstack.getItem();
						i = itemstack.getMetadata();
						flag1 = true;
					}
				} else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
					EntityMinecart entityminecart = (EntityMinecart) this.objectMouseOver.entityHit;

					switch (entityminecart.getMinecartType()) {
						case FURNACE:
							item = Items.furnace_minecart;
							break;

						case CHEST:
							item = Items.chest_minecart;
							break;

						case TNT:
							item = Items.tnt_minecart;
							break;

						case HOPPER:
							item = Items.hopper_minecart;
							break;

						case COMMAND_BLOCK:
							item = Items.command_block_minecart;
							break;

						default:
							item = Items.minecart;
					}
				} else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
					item = Items.boat;
				} else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
					item = Items.armor_stand;
				} else {
					item = Items.spawn_egg;
					i = EntityList.getEntityID(this.objectMouseOver.entityHit);
					flag1 = true;

					if (!EntityList.entityEggs.containsKey(i)) {
						return;
					}
				}
			}

			InventoryPlayer inventoryplayer = this.thePlayer.inventory;

			if (tileentity == null) {
				inventoryplayer.setCurrentItem(item, i, flag1, flag);
			} else {
				ItemStack itemstack1 = this.func_181036_a(item, i, tileentity);
				inventoryplayer.setInventorySlotContents(inventoryplayer.currentItem, itemstack1);
			}

			if (flag) {
				int j = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventoryplayer.currentItem;
				this.playerController.sendSlotPacket(inventoryplayer.getStackInSlot(inventoryplayer.currentItem), j);
			}
		}
	}

	private ItemStack func_181036_a(Item p_181036_1_, int p_181036_2_, TileEntity p_181036_3_) {
		ItemStack itemstack = new ItemStack(p_181036_1_, 1, p_181036_2_);
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		p_181036_3_.writeToNBT(nbttagcompound);

		if (p_181036_1_ == Items.skull && nbttagcompound.hasKey("Owner")) {
			NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
			NBTTagCompound nbttagcompound3 = new NBTTagCompound();
			nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
			itemstack.setTagCompound(nbttagcompound3);
			return itemstack;
		}
		itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		NBTTagList nbttaglist = new NBTTagList();
		nbttaglist.appendTag(new NBTTagString("(+NBT)"));
		nbttagcompound1.setTag("Lore", nbttaglist);
		itemstack.setTagInfo("display", nbttagcompound1);
		return itemstack;
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
		theCrash.getCategory().addCrashSectionCallable("Is Modded", () -> {
			String s = ClientBrandRetriever.getClientModName();
			return !s.equals("vanilla") ? "Definitely; Client brand changed to \'" + s + "\'" : Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
		});
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
				() -> this.mcProfiler.profilingEnabled ? this.mcProfiler.getNameOfLastSection() : "N/A (disabled)");
		theCrash.getCategory().addCrashSectionCallable("CPU", OpenGlHelper::func_183029_j);

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
		if (theMinecraft != null) {
			IntegratedServer integratedserver = theMinecraft.getIntegratedServer();

			if (integratedserver != null) {
				integratedserver.stopServer();
			}
		}
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

	public PropertyMap getTwitchDetails() {
		return this.twitchDetails;
	}

	public PropertyMap func_181037_M() {
		if (this.field_181038_N.isEmpty()) {
			GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
			this.field_181038_N.putAll(gameprofile.getProperties());
		}

		return this.field_181038_N;
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

	public void dispatchKeypresses() {
		int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

		if (i != 0 && !Keyboard.isRepeatEvent()) {
			if (!(this.currentScreen instanceof GuiControls) || ((GuiControls) this.currentScreen).time <= getSystemTime() - 20L) {
				if (Keyboard.getEventKeyState())
					if (i == KeyBinding.FULLSCREEN.getKeyCode()) this.toggleFullscreen();
					else if (i == KeyBinding.SCREENSHOT.getKeyCode())
						this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
			}
		}
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