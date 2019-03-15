package optifine;

import net.minecraft.Utils;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.Settings;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.apache.commons.io.IOUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;
import shadersmod.client.Shaders;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {

	public static final String OF_NAME = "OptiFine";
	public static final String MC_VERSION = "1.8.8";
	public static final String OF_EDITION = "HD_U";
	public static final String OF_RELEASE = "H8";
	public static final String VERSION = "OptiFine_1.8.8_HD_U_H8";
	private static String newRelease = null;
	private static boolean notify64BitJava = false;
	public static String openGlVersion = null;
	public static String openGlRenderer = null;
	public static String openGlVendor = null;
	public static String[] openGlExtensions = null;
	public static GlVersion glVersion = null;
	public static GlVersion glslVersion = null;
	public static int minecraftVersionInt = -1;
	public static boolean fancyFogAvailable = false;
	public static boolean occlusionAvailable = false;
	private static Minecraft minecraft = Minecraft.getMinecraft();
	private static boolean initialized = false;
	private static Thread minecraftThread = null;
	private static DisplayMode desktopDisplayMode = null;
	private static DisplayMode[] displayModes = null;
	private static int antialiasingLevel = 0;
	private static int availableProcessors = 0;
	public static boolean zoomMode = false;
	private static int texturePackClouds = 0;
	public static boolean waterOpacityChanged = false;
	private static boolean fullscreenModeChecked = false;
	private static boolean desktopModeChecked = false;
	private static DefaultResourcePack defaultResourcePackLazy = null;
	public static final Float DEF_ALPHA_FUNC_LEVEL = 0.1F;
	private static final Logger LOGGER = Logger.getInstance();

	public static String getVersion() {
		return "OptiFine_1.8.8_HD_U_H8";
	}

	public static String getVersionDebug() {
		StringBuilder stringbuffer = new StringBuilder(32);

		if (isDynamicLights()) {
			stringbuffer.append("DL: ");
			stringbuffer.append(String.valueOf(DynamicLights.getCount()));
			stringbuffer.append(", ");
		}

		stringbuffer.append("OptiFine_1.8.8_HD_U_H8");
		String s = Shaders.getShaderPackName();

		if (s != null) {
			stringbuffer.append(", ");
			stringbuffer.append(s);
		}

		return stringbuffer.toString();
	}

	public static void initGameSettings() {
		desktopDisplayMode = Display.getDesktopDisplayMode();
		updateAvailableProcessors();
	}

	public static void initDisplay() {
		checkInitialized();
		antialiasingLevel = Settings.AA_LEVEL.i();
		checkDisplaySettings();
		checkDisplayMode();
		minecraftThread = Thread.currentThread();
		updateThreadPriorities();
		Shaders.startup(Minecraft.getMinecraft());
	}

	public static void checkInitialized() {
		if (!initialized) {
			if (Display.isCreated()) {
				initialized = true;
				checkOpenGlCaps();
			}
		}
	}

	private static void checkOpenGlCaps() {
		log("");
		log(getVersion());
		log("Build: " + getBuild());
		log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
		log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
		log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
		log("LWJGL: " + Sys.getVersion());
		openGlVersion = GL11.glGetString(GL11.GL_VERSION);
		openGlRenderer = GL11.glGetString(GL11.GL_RENDERER);
		openGlVendor = GL11.glGetString(GL11.GL_VENDOR);
		log("OpenGL: " + openGlRenderer + ", version " + openGlVersion + ", " + openGlVendor);
		log("OpenGL Version: " + getOpenGlVersionString());

		if (!GLContext.getCapabilities().OpenGL12) {
			log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
		}

		fancyFogAvailable = GLContext.getCapabilities().GL_NV_fog_distance;

		if (!fancyFogAvailable) {
			log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
		}

		occlusionAvailable = GLContext.getCapabilities().GL_ARB_occlusion_query;

		if (!occlusionAvailable) {
			log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
		}

		int i = TextureUtils.getGLMaximumTextureSize();
		dbg("Maximum texture size: " + i + "x" + i);
	}

	private static String getBuild() {
		try {
			InputStream inputstream = Config.class.getResourceAsStream("/buildof.txt");

			if (inputstream == null) {
				return null;
			}
			return readLines(inputstream)[0];
		} catch (Exception exception) {
			warn("" + exception.getClass().getName() + ": " + exception.getMessage());
			return null;
		}
	}

	public static boolean isFancyFogAvailable() {
		return fancyFogAvailable;
	}

	public static boolean isOcclusionAvailable() {
		return occlusionAvailable;
	}

	public static int getMinecraftVersionInt() {
		if (minecraftVersionInt < 0) {
			String[] astring = tokenize("1.8.8", ".");
			int i = 0;

			if (astring.length > 0) {
				i += 10000 * parseInt(astring[0], 0);
			}

			if (astring.length > 1) {
				i += 100 * parseInt(astring[1], 0);
			}

			if (astring.length > 2) {
				i += 1 * parseInt(astring[2], 0);
			}

			minecraftVersionInt = i;
		}

		return minecraftVersionInt;
	}

	public static String getOpenGlVersionString() {
		GlVersion glversion = getGlVersion();
		return "" + glversion.getMajor() + "." + glversion.getMinor() + "." + glversion.getRelease();
	}

	private static GlVersion getGlVersionLwjgl() {
		return GLContext.getCapabilities().OpenGL44 ? new GlVersion(4, 4) : GLContext.getCapabilities().OpenGL43 ? new GlVersion(4, 3) : GLContext.getCapabilities().OpenGL42 ? new GlVersion(4,
				2) : GLContext.getCapabilities().OpenGL41 ? new GlVersion(4, 1) : GLContext.getCapabilities().OpenGL40 ? new GlVersion(4, 0) : GLContext.getCapabilities().OpenGL33 ? new GlVersion(
				3, 3) : GLContext.getCapabilities().OpenGL32 ? new GlVersion(3, 2) : GLContext.getCapabilities().OpenGL31 ? new GlVersion(3,
				1) : GLContext.getCapabilities().OpenGL30 ? new GlVersion(3, 0) : GLContext.getCapabilities().OpenGL21 ? new GlVersion(2, 1) : GLContext.getCapabilities().OpenGL20 ? new GlVersion(
				2, 0) : GLContext.getCapabilities().OpenGL15 ? new GlVersion(1, 5) : GLContext.getCapabilities().OpenGL14 ? new GlVersion(1,
				4) : GLContext.getCapabilities().OpenGL13 ? new GlVersion(1, 3) : GLContext.getCapabilities().OpenGL12 ? new GlVersion(1, 2) : GLContext.getCapabilities().OpenGL11 ? new GlVersion(
				1, 1) : new GlVersion(1, 0);
	}

	public static GlVersion getGlVersion() {
		if (glVersion == null) {
			String s = GL11.glGetString(GL11.GL_VERSION);
			glVersion = parseGlVersion(s, null);

			if (glVersion == null) glVersion = getGlVersionLwjgl();

			if (glVersion == null) glVersion = new GlVersion(1, 0);
		}

		return glVersion;
	}

	public static GlVersion getGlslVersion() {
		if (glslVersion == null) {
			String s = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
			glslVersion = parseGlVersion(s, null);
			if (glslVersion == null) glslVersion = new GlVersion(1, 10);
		}

		return glslVersion;
	}

	public static GlVersion parseGlVersion(String p_parseGlVersion_0_, GlVersion p_parseGlVersion_1_) {
		try {
			if (p_parseGlVersion_0_ == null) {
				return p_parseGlVersion_1_;
			}
			Pattern pattern = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?");
			Matcher matcher = pattern.matcher(p_parseGlVersion_0_);

			if (!matcher.matches()) {
				return p_parseGlVersion_1_;
			}
			int i = Integer.parseInt(matcher.group(1));
			int j = Integer.parseInt(matcher.group(2));
			int k = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
			String s = matcher.group(5);
			return new GlVersion(i, j, k, s);
		} catch (Exception exception) {
			exception.printStackTrace();
			return p_parseGlVersion_1_;
		}
	}

	public static String[] getOpenGlExtensions() {
		if (openGlExtensions == null) {
			openGlExtensions = detectOpenGlExtensions();
		}

		return openGlExtensions;
	}

	private static String[] detectOpenGlExtensions() {
		try {
			GlVersion glversion = getGlVersion();

			if (glversion.getMajor() >= 3) {
				int i = GL11.glGetInteger(33309);

				if (i > 0) {
					String[] astring = new String[i];

					for (int j = 0; j < i; ++j) {
						astring[j] = GL30.glGetStringi(7939, j);
					}

					return astring;
				}
			}
		} catch (Exception exception1) {
			exception1.printStackTrace();
		}

		try {
			String s = GL11.glGetString(GL11.GL_EXTENSIONS);
			return s.split(" ");
		} catch (Exception exception) {
			exception.printStackTrace();
			return Utils.STRING;
		}
	}

	public static void updateThreadPriorities() {
		updateAvailableProcessors();
		int i = 8;

		if (isSingleProcessor()) {
			if (isSmoothWorld()) {
				minecraftThread.setPriority(10);
				setThreadPriority("Server thread", 1);
			} else {
				minecraftThread.setPriority(5);
				setThreadPriority("Server thread", 5);
			}
		} else {
			minecraftThread.setPriority(10);
			setThreadPriority("Server thread", 5);
		}
	}

	private static void setThreadPriority(String p_setThreadPriority_0_, int p_setThreadPriority_1_) {
		try {
			ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();

			if (threadgroup == null) {
				return;
			}

			int i = (threadgroup.activeCount() + 10) * 2;
			Thread[] athread = new Thread[i];
			threadgroup.enumerate(athread, false);

			for (Thread thread : athread) {
				if (thread != null && thread.getName().startsWith(p_setThreadPriority_0_)) {
					thread.setPriority(p_setThreadPriority_1_);
				}
			}
		} catch (Throwable throwable) {
			warn(throwable.getClass().getName() + ": " + throwable.getMessage());
		}
	}

	public static boolean isMinecraftThread() {
		return Thread.currentThread() == minecraftThread;
	}

	public static boolean isMipmaps() {
		return getMipmapLevels() > 0;
	}

	public static int getMipmapLevels() {
		return (int) Settings.MIPMAP_LEVELS.f();
	}

	public static int getMipmapType() {
		switch (Settings.MIPMAP_TYPE.i()) {
			case 0:
				return 9986;

			case 1:
				return 9986;

			case 2:
				if (isMultiTexture()) {
					return 9985;
				}

				return 9986;

			case 3:
				if (isMultiTexture()) {
					return 9987;
				}

				return 9986;

			default:
				return 9986;
		}
	}

	public static boolean isUseAlphaFunc() {
		float f = getAlphaFuncLevel();
		return f > DEF_ALPHA_FUNC_LEVEL + 1.0E-5F;
	}

	public static float getAlphaFuncLevel() {
		return DEF_ALPHA_FUNC_LEVEL;
	}

	public static boolean isFogFancy() {
		return isFancyFogAvailable() && Settings.FOG_FANCY.i() == 2;
	}

	public static boolean isFogFast() {
		return Settings.FOG_FANCY.i() == 1;
	}

	public static boolean isFogOff() {
		return Settings.FOG_FANCY.i() == 0;
	}

	public static float getFogStart() {
		return Settings.FOG_START.f();
	}

	public static void dbg(String p_dbg_0_) {
		if (LOGGER.isDebugEnabled()) LOGGER.info("[OptiFine] " + p_dbg_0_);
	}

	public static void warn(String p_warn_0_) {
		LOGGER.warn("[OptiFine] " + p_warn_0_);
	}

	public static void error(String p_error_0_) {
		LOGGER.error("[OptiFine] " + p_error_0_);
	}

	public static void log(String p_log_0_) {
		dbg(p_log_0_);
	}

	public static int getUpdatesPerFrame() {
		return (int) Settings.CHUNK_UPDATES.f();
	}

	public static boolean isDynamicUpdates() {
		return Settings.CHUNK_UPDATES_DYNAMIC.b();
	}

	public static boolean isRainFancy() {
		return Settings.RAIN.i() == 2;
	}

	public static boolean isRainOff() {
		return Settings.RAIN.i() == 0;
	}

	public static boolean isCloudsFancy() {
		return Settings.CLOUDS.i() == 2;
	}

	public static boolean isCloudsOff() {
		return Settings.CLOUDS.i() == 0;
	}

	public static void updateTexturePackClouds() {
		texturePackClouds = 0;
		IResourceManager iresourcemanager = getResourceManager();

		if (iresourcemanager != null) {
			try {
				InputStream inputstream = iresourcemanager.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();

				if (inputstream == null) {
					return;
				}

				Properties properties = new Properties();
				properties.load(inputstream);
				inputstream.close();
				String s = properties.getProperty("clouds");

				if (s == null) {
					return;
				}

				dbg("Texture pack clouds: " + s);
				s = s.toLowerCase();

				if (s.equals("fast")) {
					texturePackClouds = 1;
				}

				if (s.equals("fancy")) {
					texturePackClouds = 2;
				}

				if (s.equals("off")) {
					texturePackClouds = 3;
				}
			} catch (Exception ignored) {}
		}
	}

	public static ModelManager getModelManager() {
		return minecraft.getRenderItem().modelManager;
	}

	public static boolean isTreesFancy() {
		return Settings.TREES.i() == 1;
	}

	public static boolean isTreesSmart() {
		return false;
	}

	public static boolean isCullFacesLeaves() {
		return false;
	}

	public static boolean isDroppedItemsFancy() {
		return Settings.DROPPED_ITEMS.b();
	}

	public static int limit(int p_limit_0_, int p_limit_1_, int p_limit_2_) {
		return p_limit_0_ < p_limit_1_ ? p_limit_1_ : p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_;
	}

	public static float limit(float p_limit_0_, float p_limit_1_, float p_limit_2_) {
		return p_limit_0_ < p_limit_1_ ? p_limit_1_ : p_limit_0_ > p_limit_2_ ? p_limit_2_ : p_limit_0_;
	}

	public static double limit(double p_limit_0_, double p_limit_2_, double p_limit_4_) {
		return p_limit_0_ < p_limit_2_ ? p_limit_2_ : p_limit_0_ > p_limit_4_ ? p_limit_4_ : p_limit_0_;
	}

	public static float limitTo1(float p_limitTo1_0_) {
		return p_limitTo1_0_ < 0.0F ? 0.0F : p_limitTo1_0_ > 1.0F ? 1.0F : p_limitTo1_0_;
	}

	public static boolean isAnimatedWater() {
		return Settings.ANIMATED_WATER.b();
	}

	public static boolean isGeneratedWater() {
		return isAnimatedWater();
	}

	public static boolean isAnimatedPortal() {
		return Settings.ANIMATED_PORTAL.b();
	}

	public static boolean isAnimatedLava() {
		return Settings.ANIMATED_LAVA.b();
	}

	public static boolean isGeneratedLava() {
		return isAnimatedLava();
	}

	public static boolean isAnimatedFire() {
		return Settings.ANIMATED_FIRE.b();
	}

	public static boolean isAnimatedRedstone() {
		return Settings.ANIMATED_REDSTONE.b();
	}

	public static boolean isAnimatedExplosion() {
		return Settings.ANIMATED_EXPLOSION.b();
	}

	public static boolean isAnimatedFlame() {
		return Settings.ANIMATED_FLAME.b();
	}

	public static boolean isAnimatedSmoke() {
		return Settings.ANIMATED_SMOKE.b();
	}

	public static boolean isVoidParticles() {
		return Settings.VOID_PARTICLES.b();
	}

	public static boolean isWaterParticles() {
		return Settings.WATER_PARTICLES.b();
	}

	public static boolean isRainSplash() {
		return Settings.WATER_PARTICLES.b();
	}

	public static boolean isPortalParticles() {
		return Settings.PORTAL_PARTICLES.b();
	}

	public static boolean isPotionParticles() {
		return Settings.POTION_PARTICLES.b();
	}

	public static boolean isFireworkParticles() {
		return Settings.FIREWORK_PARTICLES.b();
	}

	public static float getAmbientOcclusionLevel() {
		return isShaders() && Shaders.aoLevel >= 0.0F ? Shaders.aoLevel : Settings.AO_LEVEL.f();
	}

	public static String arrayToString(Object[] p_arrayToString_0_) {
		if (p_arrayToString_0_ == null) return "";
		StringBuilder stringbuffer = new StringBuilder(p_arrayToString_0_.length * 5);

		for (int i = 0; i < p_arrayToString_0_.length; ++i) {
			Object object = p_arrayToString_0_[i];

			if (i > 0) {
				stringbuffer.append(", ");
			}

			stringbuffer.append(String.valueOf(object));
		}

		return stringbuffer.toString();
	}

	public static String arrayToString(int[] p_arrayToString_0_) {
		if (p_arrayToString_0_ == null) {
			return "";
		}
		StringBuilder stringbuffer = new StringBuilder(p_arrayToString_0_.length * 5);

		for (int i = 0; i < p_arrayToString_0_.length; ++i) {
			int j = p_arrayToString_0_[i];

			if (i > 0) {
				stringbuffer.append(", ");
			}

			stringbuffer.append(String.valueOf(j));
		}

		return stringbuffer.toString();
	}

	public static Minecraft getMinecraft() {
		return minecraft;
	}

	public static TextureManager getTextureManager() {
		return minecraft.getTextureManager();
	}

	public static IResourceManager getResourceManager() {
		return minecraft.getResourceManager();
	}

	public static InputStream getResourceStream(ResourceLocation p_getResourceStream_0_) throws IOException {
		return getResourceStream(minecraft.getResourceManager(), p_getResourceStream_0_);
	}

	public static InputStream getResourceStream(IResourceManager p_getResourceStream_0_, ResourceLocation p_getResourceStream_1_) throws IOException {
		IResource iresource = p_getResourceStream_0_.getResource(p_getResourceStream_1_);
		return iresource == null ? null : iresource.getInputStream();
	}

	public static IResource getResource(ResourceLocation p_getResource_0_) throws IOException {
		return minecraft.getResourceManager().getResource(p_getResource_0_);
	}

	public static boolean hasResource(ResourceLocation p_hasResource_0_) {
		IResourcePack iresourcepack = getDefiningResourcePack(p_hasResource_0_);
		return iresourcepack != null;
	}

	public static boolean hasResource(IResourceManager resourceMgr, ResourceLocation loc) {
		try {
			IResource iresource = resourceMgr.getResource(loc);
			return iresource != null;
		} catch (IOException var3) {
			return false;
		}
	}

	public static IResourcePack[] getResourcePacks() {
		ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
		List list = resourcepackrepository.getRepositoryEntries();
		List<IResourcePack> list1 = new ArrayList<>();
		for (Object e : list) list1.add(((Entry) e).getResourcePack());
		if (resourcepackrepository.getResourcePackInstance() != null) list1.add(resourcepackrepository.getResourcePackInstance());
		return list1.toArray(Utils.IRESOURCEPACK);
	}

	public static String getResourcePackNames() {
		if (minecraft.getResourcePackRepository() == null) return "";
		IResourcePack[] airesourcepack = getResourcePacks();
		if (airesourcepack.length <= 0) return getDefaultResourcePack().getPackName();
		String[] astring = new String[airesourcepack.length];
		for (int i = 0; i < airesourcepack.length; ++i) astring[i] = airesourcepack[i].getPackName();
		return arrayToString(astring);
	}

	public static DefaultResourcePack getDefaultResourcePack() {
		if (defaultResourcePackLazy == null) {
			Minecraft minecraft = Minecraft.getMinecraft();
			ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();

			if (resourcepackrepository != null) defaultResourcePackLazy = (DefaultResourcePack) resourcepackrepository.rprDefaultResourcePack;
		}

		return defaultResourcePackLazy;
	}

	public static boolean isFromDefaultResourcePack(ResourceLocation p_isFromDefaultResourcePack_0_) {
		IResourcePack iresourcepack = getDefiningResourcePack(p_isFromDefaultResourcePack_0_);
		return iresourcepack == getDefaultResourcePack();
	}

	public static IResourcePack getDefiningResourcePack(ResourceLocation loc) {
		ResourcePackRepository resourcepackrepository = minecraft.getResourcePackRepository();
		IResourcePack iresourcepack = resourcepackrepository.getResourcePackInstance();

		if (iresourcepack != null && iresourcepack.resourceExists(loc)) return iresourcepack;
		//		List<ResourcePackRepository.Entry> list = (List) Reflector.getFieldValue(resourcepackrepository, Reflector.ResourcePackRepository_repositoryEntries);
		//
		//		if (list != null) for (int i = list.size() - 1; i >= 0; --i) {
		//			Entry e = list.get(i);
		//			IResourcePack rp = e.getResourcePack();
		//			if (rp.resourceExists(loc)) return rp;
		//		}
		return getDefaultResourcePack().resourceExists(loc) ? getDefaultResourcePack() : null;
	}

	public static RenderGlobal getRenderGlobal() {
		return minecraft.renderGlobal;
	}

	public static boolean isBetterGrass() {
		return Settings.BETTER_GRASS.i() != 0;
	}

	public static boolean isBetterGrassFancy() {
		return Settings.BETTER_GRASS.i() == 2;
	}

	public static boolean isWeatherEnabled() {
		return Settings.WEATHER.b();
	}

	public static boolean isSkyEnabled() {
		return Settings.SKY.b();
	}

	public static boolean isSunMoonEnabled() {
		return Settings.SUN_MOON.b();
	}

	public static boolean isSunTexture() {
		return isSunMoonEnabled() && (!isShaders() || Shaders.isSun());
	}

	public static boolean isMoonTexture() {
		return isSunMoonEnabled() && (!isShaders() || Shaders.isMoon());
	}

	public static boolean isVignetteEnabled() {
		return (!isShaders() || Shaders.isVignette()) && Settings.VIGNETTE.i() != 0;
	}

	public static boolean isStarsEnabled() {
		return Settings.STARS.b();
	}

	public static void sleep(long p_sleep_0_) {
		try {
			Thread.sleep(p_sleep_0_);
		} catch (InterruptedException interruptedexception) {
			interruptedexception.printStackTrace();
		}
	}

	public static boolean isTimeDayOnly() {
		return Settings.TIME.i() == 1;
	}

	public static boolean isTimeDefault() {
		return Settings.TIME.i() == 0;
	}

	public static boolean isTimeNightOnly() {
		return Settings.TIME.i() == 2;
	}

	public static boolean isClearWater() {
		return Settings.CLEAR_WATER.b();
	}

	public static int getAnisotropicFilterLevel() {
		return (int) Settings.AF_LEVEL.f();
	}

	public static boolean isAnisotropicFiltering() {
		return getAnisotropicFilterLevel() > 1;
	}

	public static int getAntialiasingLevel() {
		return antialiasingLevel;
	}

	public static boolean isAntialiasing() {
		return getAntialiasingLevel() > 0;
	}

	public static boolean isAntialiasingConfigured() {
		return Settings.AA_LEVEL.f() > 0;
	}

	public static boolean isMultiTexture() {
		return getAnisotropicFilterLevel() > 1 || getAntialiasingLevel() > 0;
	}

	public static boolean between(int p_between_0_, int p_between_1_, int p_between_2_) {
		return p_between_0_ >= p_between_1_ && p_between_0_ <= p_between_2_;
	}

	public static boolean isDrippingWaterLava() {
		return Settings.DRIPPING_WATER_LAVA.b();
	}

	public static boolean isBetterSnow() {
		return Settings.BETTER_SNOW.b();
	}

	public static Dimension getFullscreenDimension() {
		if (desktopDisplayMode == null) return null;
		return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
	}

	public static int parseInt(String p_parseInt_0_, int p_parseInt_1_) {
		try {
			if (p_parseInt_0_ == null) {
				return p_parseInt_1_;
			}
			p_parseInt_0_ = p_parseInt_0_.trim();
			return Integer.parseInt(p_parseInt_0_);
		} catch (NumberFormatException var3) {
			return p_parseInt_1_;
		}
	}

	public static float parseFloat(String p_parseFloat_0_, float p_parseFloat_1_) {
		try {
			if (p_parseFloat_0_ == null) {
				return p_parseFloat_1_;
			}
			p_parseFloat_0_ = p_parseFloat_0_.trim();
			return Float.parseFloat(p_parseFloat_0_);
		} catch (NumberFormatException var3) {
			return p_parseFloat_1_;
		}
	}

	public static boolean parseBoolean(String p_parseBoolean_0_, boolean p_parseBoolean_1_) {
		try {
			if (p_parseBoolean_0_ == null) {
				return p_parseBoolean_1_;
			}
			p_parseBoolean_0_ = p_parseBoolean_0_.trim();
			return Boolean.parseBoolean(p_parseBoolean_0_);
		} catch (NumberFormatException var3) {
			return p_parseBoolean_1_;
		}
	}

	public static String[] tokenize(String p_tokenize_0_, String p_tokenize_1_) {
		StringTokenizer stringtokenizer = new StringTokenizer(p_tokenize_0_, p_tokenize_1_);
		List<String> list = new ArrayList<>();

		while (stringtokenizer.hasMoreTokens()) {
			String s = stringtokenizer.nextToken();
			list.add(s);
		}

		return list.toArray(Utils.STRING);
	}

	public static DisplayMode getDesktopDisplayMode() {
		return desktopDisplayMode;
	}

	public static DisplayMode[] getDisplayModes() {
		if (displayModes == null) {
			try {
				DisplayMode[] adisplaymode = Display.getAvailableDisplayModes();
				Set<Dimension> set = getDisplayModeDimensions(adisplaymode);
				List<DisplayMode> list = new ArrayList<>();

				for (Dimension dimension : set) {
					DisplayMode[] adisplaymode1 = getDisplayModes(adisplaymode, dimension);
					DisplayMode displaymode = getDisplayMode(adisplaymode1, desktopDisplayMode);

					if (displaymode != null) list.add(displaymode);
				}

				DisplayMode[] adisplaymode2 = list.toArray(Utils.DISPLAYMODE);
				Arrays.sort(adisplaymode2, new DisplayModeComparator());
				return adisplaymode2;
			} catch (Exception exception) {
				exception.printStackTrace();
				displayModes = new DisplayMode[] {desktopDisplayMode};
			}
		}

		return displayModes;
	}

	public static DisplayMode getLargestDisplayMode() {
		DisplayMode[] adisplaymode = getDisplayModes();

		if (adisplaymode != null && adisplaymode.length >= 1) {
			DisplayMode displaymode = adisplaymode[adisplaymode.length - 1];
			return desktopDisplayMode.getWidth() > displaymode.getWidth() ? desktopDisplayMode : desktopDisplayMode.getWidth() == displaymode.getWidth() && desktopDisplayMode.getHeight() > displaymode.getHeight() ? desktopDisplayMode : displaymode;
		}
		return desktopDisplayMode;
	}

	private static Set<Dimension> getDisplayModeDimensions(DisplayMode[] p_getDisplayModeDimensions_0_) {
		Set<Dimension> set = new HashSet();

		for (DisplayMode displaymode : p_getDisplayModeDimensions_0_) {
			Dimension dimension = new Dimension(displaymode.getWidth(), displaymode.getHeight());
			set.add(dimension);
		}

		return set;
	}

	private static DisplayMode[] getDisplayModes(DisplayMode[] modes, Dimension dim) {
		List<DisplayMode> list = new ArrayList<>();

		for (DisplayMode displaymode : modes)
			if (displaymode.getWidth() == dim.getWidth() && displaymode.getHeight() == dim.getHeight()) list.add(displaymode);

		return list.toArray(Utils.DISPLAYMODE);
	}

	private static DisplayMode getDisplayMode(DisplayMode[] p_getDisplayMode_0_, DisplayMode p_getDisplayMode_1_) {
		if (p_getDisplayMode_1_ != null) {
			for (DisplayMode displaymode : p_getDisplayMode_0_) {
				if (displaymode.getBitsPerPixel() == p_getDisplayMode_1_.getBitsPerPixel() && displaymode.getFrequency() == p_getDisplayMode_1_.getFrequency()) {
					return displaymode;
				}
			}
		}

		if (p_getDisplayMode_0_.length <= 0) {
			return null;
		}
		Arrays.sort(p_getDisplayMode_0_, new DisplayModeComparator());
		return p_getDisplayMode_0_[p_getDisplayMode_0_.length - 1];
	}

	public static String[] getDisplayModeNames() {
		DisplayMode[] adisplaymode = getDisplayModes();
		String[] astring = new String[adisplaymode.length];

		for (int i = 0; i < adisplaymode.length; ++i) {
			DisplayMode displaymode = adisplaymode[i];
			String s = "" + displaymode.getWidth() + "x" + displaymode.getHeight();
			astring[i] = s;
		}

		return astring;
	}

	public static DisplayMode getDisplayMode(Dimension p_getDisplayMode_0_) throws LWJGLException {
		DisplayMode[] adisplaymode = getDisplayModes();

		for (DisplayMode displaymode : adisplaymode) {
			if (displaymode.getWidth() == p_getDisplayMode_0_.width && displaymode.getHeight() == p_getDisplayMode_0_.height) {
				return displaymode;
			}
		}

		return desktopDisplayMode;
	}

	public static boolean isAnimatedTerrain() {
		return Settings.ANIMATED_TERRAIN.b();
	}

	public static boolean isAnimatedTextures() {
		return Settings.ANIMATED_TEXTURES.b();
	}

	public static boolean isSwampColors() {
		return Settings.SWAMP_COLORS.b();
	}

	public static boolean isRandomMobs() {
		return Settings.RANDOM_MOBS.b();
	}

	public static void checkGlError(String section) {
		int i = GL11.glGetError();

		if (i != 0) {
			String s = GLU.gluErrorString(i);
			error("OpenGlError: " + i + " (" + s + "), at: " + section);
		}
	}

	public static boolean isSmoothBiomes() {
		return Settings.SMOOTH_BIOMES.b();
	}

	public static boolean isCustomColors() {
		return Settings.CUSTOM_COLORS.b();
	}

	public static boolean isCustomSky() {
		return Settings.CUSTOM_SKY.b();
	}

	public static boolean isCustomFonts() {
		return Settings.CUSTOM_FONTS.b();
	}

	public static boolean isShowCapes() {
		return Settings.SHOW_CAPES.b();
	}

	public static boolean isConnectedTextures() {
		return Settings.CONNECTED_TEXTURES.b();
	}

	public static boolean isNaturalTextures() {
		return Settings.NATURAL_TEXTURES.b();
	}

	public static boolean isConnectedTexturesFancy() {
		return isConnectedTextures();
	}

	public static boolean isFastRender() {
		return Settings.FAST_RENDER.b();
	}

	public static boolean isTranslucentBlocksFancy() {
		return Settings.TRANSLUCENT_BLOCKS.i() == 1;
	}

	public static boolean isShaders() {
		return Shaders.shaderPackLoaded;
	}

	public static String[] readLines(File f) throws IOException {
		FileInputStream stream = new FileInputStream(f);
		return readLines(stream);
	}

	public static String[] readLines(InputStream p_readLines_0_) throws IOException {
		List<String> list = new ArrayList<>();
		InputStreamReader inputstreamreader = new InputStreamReader(p_readLines_0_, StandardCharsets.US_ASCII);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

		while (true) {
			String s = bufferedreader.readLine();
			if (s == null) return list.toArray(Utils.STRING);
			list.add(s);
		}
	}

	public static String readFile(File p_readFile_0_) throws IOException {
		FileInputStream fileinputstream = new FileInputStream(p_readFile_0_);
		return readInputStream(fileinputstream, "ASCII");
	}

	public static String readInputStream(InputStream p_readInputStream_0_) throws IOException {
		return readInputStream(p_readInputStream_0_, "ASCII");
	}

	public static String readInputStream(InputStream p_readInputStream_0_, String p_readInputStream_1_) throws IOException {
		InputStreamReader inputstreamreader = new InputStreamReader(p_readInputStream_0_, p_readInputStream_1_);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		StringBuilder stringbuffer = new StringBuilder();

		while (true) {
			String s = bufferedreader.readLine();

			if (s == null) return stringbuffer.toString();

			stringbuffer.append(s);
			stringbuffer.append("\n");
		}
	}

	public static byte[] readAll(InputStream p_readAll_0_) throws IOException {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		byte[] abyte = new byte[1024];

		while (true) {
			int i = p_readAll_0_.read(abyte);

			if (i < 0) {
				p_readAll_0_.close();
				return bytearrayoutputstream.toByteArray();
			}

			bytearrayoutputstream.write(abyte, 0, i);
		}
	}

	//	public static GameSettings getGameSettings() {
	//		return gameSettings;
	//	}

	public static String getNewRelease() {
		return newRelease;
	}

	public static void setNewRelease(String p_setNewRelease_0_) {
		newRelease = p_setNewRelease_0_;
	}

	public static int compareRelease(String p_compareRelease_0_, String p_compareRelease_1_) {
		String[] astring = splitRelease(p_compareRelease_0_);
		String[] astring1 = splitRelease(p_compareRelease_1_);
		String s = astring[0];
		String s1 = astring1[0];

		if (!s.equals(s1)) {
			return s.compareTo(s1);
		}
		int i = parseInt(astring[1], -1);
		int j = parseInt(astring1[1], -1);

		if (i != j) {
			return i - j;
		}
		String s2 = astring[2];
		String s3 = astring1[2];

		if (!s2.equals(s3)) {
			if (s2.isEmpty()) {
				return 1;
			}

			if (s3.isEmpty()) {
				return -1;
			}
		}

		return s2.compareTo(s3);
	}

	private static String[] splitRelease(String p_splitRelease_0_) {
		if (p_splitRelease_0_ != null && p_splitRelease_0_.length() > 0) {
			Pattern pattern = Pattern.compile("([A-Z])([0-9]+)(.*)");
			Matcher matcher = pattern.matcher(p_splitRelease_0_);

			if (!matcher.matches()) {
				return new String[] {"", "", ""};
			}
			String s = normalize(matcher.group(1));
			String s1 = normalize(matcher.group(2));
			String s2 = normalize(matcher.group(3));
			return new String[] {s, s1, s2};
		}
		return new String[] {"", "", ""};
	}

	public static int intHash(int p_intHash_0_) {
		p_intHash_0_ = p_intHash_0_ ^ 61 ^ p_intHash_0_ >> 16;
		p_intHash_0_ = p_intHash_0_ + (p_intHash_0_ << 3);
		p_intHash_0_ = p_intHash_0_ ^ p_intHash_0_ >> 4;
		p_intHash_0_ = p_intHash_0_ * 668265261;
		p_intHash_0_ = p_intHash_0_ ^ p_intHash_0_ >> 15;
		return p_intHash_0_;
	}

	public static int getRandom(BlockPos p_getRandom_0_, int p_getRandom_1_) {
		int i = intHash(p_getRandom_1_ + 37);
		i = intHash(i + p_getRandom_0_.getX());
		i = intHash(i + p_getRandom_0_.getZ());
		i = intHash(i + p_getRandom_0_.getY());
		return i;
	}

	public static WorldServer getWorldServer() {
		World world = minecraft.theWorld;

		if (world == null) return null;
		if (!minecraft.isIntegratedServerRunning()) return null;

		IntegratedServer integratedserver = minecraft.getIntegratedServer();

		if (integratedserver == null) {
			return null;
		}
		WorldProvider worldprovider = world.provider;

		if (worldprovider == null) {
			return null;
		}
		int i = worldprovider.getDimensionId();

		try {
			return integratedserver.worldServerForDimension(i);
		} catch (NullPointerException var5) {
			return null;
		}
	}

	public static int getAvailableProcessors() {
		return availableProcessors;
	}

	public static void updateAvailableProcessors() {
		availableProcessors = Runtime.getRuntime().availableProcessors();
	}

	public static boolean isSingleProcessor() {
		return getAvailableProcessors() <= 1;
	}

	public static boolean isSmoothWorld() {
		return Settings.SMOOTH_WORLD.b();
	}

	public static boolean isLazyChunkLoading() {
		return isSingleProcessor() && Settings.LAZY_CHUNK_LOADING.b();
	}

	public static boolean isDynamicFov() {
		return Settings.DYNAMIC_FOV.b();
	}

	public static boolean isAlternateBlocks() {
		return Settings.BLOCK_ALTERNATIVES.b();
	}

	public static int getChunkViewDistance() {
		return Settings.RENDER_DISTANCE.i();
	}

	public static boolean equals(Object a, Object b) {
		return a == b || a != null && a.equals(b);
	}

	public static boolean equalsOne(Object a, Object[] o) {
		if (o == null) return false;
		for (Object object : o) if (equals(a, object)) return true;
		return false;
	}

	public static boolean isSameOne(Object a, Object[] b) {
		if (b == null) return false;
		for (Object object : b) if (a == object) return true;
		return false;
	}

	public static String normalize(String s) {
		return s == null ? "" : s;
	}

	public static void checkDisplaySettings() {
		int i = getAntialiasingLevel();

		if (i > 0) {
			DisplayMode displaymode = Display.getDisplayMode();
			dbg("FSAA Samples: " + i);

			try {
				Display.destroy();
				Display.setDisplayMode(displaymode);
				Display.create(new PixelFormat().withDepthBits(24).withSamples(i));
				Display.setResizable(false);
				Display.setResizable(true);
			} catch (LWJGLException lwjglexception2) {
				warn("Error setting FSAA: " + i + "x");
				lwjglexception2.printStackTrace();

				try {
					Display.setDisplayMode(displaymode);
					Display.create(new PixelFormat().withDepthBits(24));
					Display.setResizable(false);
					Display.setResizable(true);
				} catch (LWJGLException lwjglexception1) {
					lwjglexception1.printStackTrace();

					try {
						Display.setDisplayMode(displaymode);
						Display.create();
						Display.setResizable(false);
						Display.setResizable(true);
					} catch (LWJGLException lwjglexception) {
						lwjglexception.printStackTrace();
					}
				}
			}

			if (!Minecraft.isRunningOnMac && getDefaultResourcePack() != null) {
				InputStream inputstream = null;
				InputStream inputstream1 = null;

				try {
					inputstream = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
					inputstream1 = getDefaultResourcePack().getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));

					if (inputstream != null && inputstream1 != null) {
						Display.setIcon(new ByteBuffer[] {readIconImage(inputstream), readIconImage(inputstream1)});
					}
				} catch (IOException ioexception) {
					warn("Error setting window icon: " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
				} finally {
					IOUtils.closeQuietly(inputstream);
					IOUtils.closeQuietly(inputstream1);
				}
			}
		}
	}

	private static ByteBuffer readIconImage(InputStream p_readIconImage_0_) throws IOException {
		BufferedImage bufferedimage = ImageIO.read(p_readIconImage_0_);
		int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
		ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

		for (int i : aint) {
			bytebuffer.putInt(i << 8 | i >> 24 & 255);
		}

		bytebuffer.flip();
		return bytebuffer;
	}

	public static void checkDisplayMode() {
		try {
			if (minecraft.isFullScreen()) {
				if (fullscreenModeChecked) return;

				fullscreenModeChecked = true;
				desktopModeChecked = false;
				DisplayMode displaymode = Display.getDisplayMode();
				Dimension dimension = getFullscreenDimension();

				if (dimension == null) return;

				if (displaymode.getWidth() == dimension.width && displaymode.getHeight() == dimension.height) {
					return;
				}

				DisplayMode displaymode1 = getDisplayMode(dimension);

				if (displaymode1 == null) {
					return;
				}

				Display.setDisplayMode(displaymode1);
				minecraft.displayWidth = Display.getDisplayMode().getWidth();
				minecraft.displayHeight = Display.getDisplayMode().getHeight();

				if (minecraft.displayWidth <= 0) {
					minecraft.displayWidth = 1;
				}

				if (minecraft.displayHeight <= 0) {
					minecraft.displayHeight = 1;
				}

				if (minecraft.currentScreen != null) {
					ScaledResolution scaledresolution = new ScaledResolution(minecraft);
					int i = scaledresolution.getScaledWidth();
					int j = scaledresolution.getScaledHeight();
					minecraft.currentScreen.setWorldAndResolution(minecraft, i, j);
				}

				minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
				updateFramebufferSize();
				Display.setFullscreen(true);
				Settings.updateVSync();
				G.enableTexture2D();
			} else {
				if (desktopModeChecked) {
					return;
				}

				desktopModeChecked = true;
				fullscreenModeChecked = false;
				Settings.updateVSync();
				Display.update();
				G.enableTexture2D();
				Display.setResizable(false);
				Display.setResizable(true);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void updateFramebufferSize() {
		minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);

		if (minecraft.entityRenderer != null) {
			minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
		}
	}

	public static Object[] addObjectToArray(Object[] array, Object object) {
		if (array == null) throw new NullPointerException("The given array is NULL");
		int i = array.length;
		int j = i + 1;
		Object[] aobject = (Object[]) Array.newInstance(array.getClass().getComponentType(), j);
		System.arraycopy(array, 0, aobject, 0, i);
		aobject[i] = object;
		return aobject;
	}

	public static Object[] addObjectToArray(Object[] array, Object object, int i) {
		List list = new ArrayList(Arrays.asList(array));
		list.add(i, object);
		Object[] aobject = (Object[]) Array.newInstance(array.getClass().getComponentType(), list.size());
		return list.toArray(aobject);
	}

	public static Object[] addObjectsToArray(Object[] array, Object[] objects) {
		if (array == null) throw new NullPointerException("The given array is NULL");
		if (objects.length == 0) return array;

		int i = array.length;
		int j = i + objects.length;
		Object[] aobject = (Object[]) Array.newInstance(array.getClass().getComponentType(), j);
		System.arraycopy(array, 0, aobject, 0, i);
		System.arraycopy(objects, 0, aobject, i, objects.length);
		return aobject;
	}

	public static boolean isCustomItems() {
		return Settings.CUSTOM_ITEMS.b();
	}

	public static void drawFps() {
		int i = Minecraft.getDebugFPS();
		String s = minecraft.debug;
		int j = minecraft.renderGlobal.getCountActiveRenderers();
		int k = minecraft.renderGlobal.getCountEntitiesRendered();
		int l = minecraft.renderGlobal.getCountTileEntitiesRendered();
		String s1 = "" + i + " fps, C: " + j + ", E: " + k + "+" + l + ", U: " + s;
		minecraft.fontRendererObj.drawString(s1, 2, 2, -2039584);
	}

	private static String getUpdates(String s) {
		int i = s.indexOf(40);
		if (i < 0) return "";
		int j = s.indexOf(32, i);
		return j < 0 ? "" : s.substring(i + 1, j);
	}

	public static int getBitsOs() {
		String s = System.getenv("ProgramFiles(X86)");
		return s != null ? 64 : 32;
	}

	public static int getBitsJre() {
		String[] astring = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

		for (String s : astring) {
			String s1 = System.getProperty(s);
			if (s1 != null && s1.contains("64")) return 64;
		}

		return 32;
	}

	public static boolean isNotify64BitJava() {
		return notify64BitJava;
	}

	public static void setNotify64BitJava(boolean p_setNotify64BitJava_0_) {
		notify64BitJava = p_setNotify64BitJava_0_;
	}

	public static boolean isConnectedModels() {
		return false;
	}

	public static void showGuiMessage(String p_showGuiMessage_0_, String p_showGuiMessage_1_) {
		GuiMessage guimessage = new GuiMessage(minecraft.currentScreen, p_showGuiMessage_0_, p_showGuiMessage_1_);
		minecraft.displayGuiScreen(guimessage);
	}

	public static int[] addIntToArray(int[] p_addIntToArray_0_, int p_addIntToArray_1_) {
		return addIntsToArray(p_addIntToArray_0_, new int[] {p_addIntToArray_1_});
	}

	public static int[] addIntsToArray(int[] p_addIntsToArray_0_, int[] p_addIntsToArray_1_) {
		if (p_addIntsToArray_0_ != null && p_addIntsToArray_1_ != null) {
			int i = p_addIntsToArray_0_.length;
			int j = i + p_addIntsToArray_1_.length;
			int[] aint = new int[j];
			System.arraycopy(p_addIntsToArray_0_, 0, aint, 0, i);

			System.arraycopy(p_addIntsToArray_1_, 0, aint, i, p_addIntsToArray_1_.length);

			return aint;
		}
		throw new NullPointerException("The given array is NULL");
	}

	public static DynamicTexture getMojangLogoTexture(DynamicTexture texture) {
		try {
			ResourceLocation resourcelocation = new ResourceLocation("textures/gui/title/mojang.png");
			InputStream inputstream = getResourceStream(resourcelocation);
			if (inputstream == null) return texture;
			BufferedImage bufferedimage = ImageIO.read(inputstream);
			return bufferedimage == null ? texture : new DynamicTexture(bufferedimage);
		} catch (Exception exception) {
			warn(exception.getClass().getName() + ": " + exception.getMessage());
			return texture;
		}
	}

	public static void writeFile(File p_writeFile_0_, String p_writeFile_1_) throws IOException {
		FileOutputStream fileoutputstream = new FileOutputStream(p_writeFile_0_);
		byte[] abyte = p_writeFile_1_.getBytes(StandardCharsets.US_ASCII);
		fileoutputstream.write(abyte);
		fileoutputstream.close();
	}

	public static TextureMap getTextureMap() {
		return getMinecraft().getTextureMapBlocks();
	}

	public static boolean isDynamicLights() {
		return Settings.DYNAMIC_LIGHTS.b();
	}

	public static boolean isDynamicLightsFast() {
		return isDynamicLights();
	}

	public static boolean isDynamicHandLight() {
		return isDynamicLights() && (!isShaders() || Shaders.isDynamicHandLight());
	}

}
