package optifine;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.ITickableTextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import shadersmod.client.MultiTexID;
import shadersmod.client.Shaders;

public class TextureUtils {
	public static TextureAtlasSprite iconGrassTop;
	public static TextureAtlasSprite iconGrassSide;
	public static TextureAtlasSprite iconGrassSideOverlay;
	public static TextureAtlasSprite iconSnow;
	public static TextureAtlasSprite iconGrassSideSnowed;
	public static TextureAtlasSprite iconMyceliumSide;
	public static TextureAtlasSprite iconMyceliumTop;
	public static TextureAtlasSprite iconWaterStill;
	public static TextureAtlasSprite iconWaterFlow;
	public static TextureAtlasSprite iconLavaStill;
	public static TextureAtlasSprite iconLavaFlow;
	public static TextureAtlasSprite iconPortal;
	public static TextureAtlasSprite iconFireLayer0;
	public static TextureAtlasSprite iconFireLayer1;
	public static TextureAtlasSprite iconGlass;
	public static TextureAtlasSprite iconGlassPaneTop;
	public static TextureAtlasSprite iconCompass;
	public static TextureAtlasSprite iconClock;
	public static final String SPRITE_PREFIX_BLOCKS = "minecraft:blocks/";
	public static final String SPRITE_PREFIX_ITEMS = "minecraft:items/";

	public static void update() {
		TextureMap texturemap = getTextureMapBlocks();

		if (texturemap != null) {
			iconGrassTop = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "grass_top");
			iconGrassSide = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "grass_side");
			iconGrassSideOverlay = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "grass_side_overlay");
			iconSnow = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "snow");
			iconGrassSideSnowed = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "grass_side_snowed");
			iconMyceliumSide = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "mycelium_side");
			iconMyceliumTop = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "mycelium_top");
			iconWaterStill = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "water_still");
			iconWaterFlow = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "water_flow");
			iconLavaStill = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "lava_still");
			iconLavaFlow = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "lava_flow");
			iconFireLayer0 = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "fire_layer_0");
			iconFireLayer1 = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "fire_layer_1");
			iconPortal = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "portal");
			iconGlass = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "glass");
			iconGlassPaneTop = texturemap.getSpriteSafe(SPRITE_PREFIX_BLOCKS + "glass_pane_top");
			iconCompass = texturemap.getSpriteSafe(SPRITE_PREFIX_ITEMS + "compass");
			iconClock = texturemap.getSpriteSafe(SPRITE_PREFIX_ITEMS + "clock");
		}
	}

	public static BufferedImage fixTextureDimensions(String path, BufferedImage image) {
		if (path.startsWith("/mob/zombie") || path.startsWith("/mob/pigzombie")) {
			int width = image.getWidth();
			int height = image.getHeight();

			if (width == height * 2) {
				BufferedImage bufferedimage = new BufferedImage(width, height * 2, 2);
				Graphics2D graphics2d = bufferedimage.createGraphics();
				graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				graphics2d.drawImage(image, 0, 0, width, height, null);
				return bufferedimage;
			}
		}

		return image;
	}

	public static int ceilPowerOfTwo(int j) {
		int i;
		for (i = 1; i < j; i <<= 1);
		return i;
	}

	public static ITextureObject getTexture(ResourceLocation location) {
		ITextureObject itextureobject = Config.getTextureManager().getTexture(location);

		if (itextureobject != null)
			return itextureobject;
		if (!Config.hasResource(location))
			return null;
		SimpleTexture simpletexture = new SimpleTexture(location);
		Config.getTextureManager().loadTexture(location, simpletexture);
		return simpletexture;
	}

	public static void resourcesReloaded() {
		if (getTextureMapBlocks() != null) {
			Config.dbg("*** Reloading custom textures ***");
			CustomSky.reset();
			TextureAnimations.reset();
			update();
			NaturalTextures.update();
			BetterGrass.update();
			BetterSnow.update();
			TextureAnimations.update();
			CustomColors.update();
			CustomSky.update();
			RandomMobs.resetTextures();
			CustomItems.updateModels();
			Shaders.resourcesReloaded();
			Lang.resourcesReloaded();
			Config.updateTexturePackClouds();
			SmartLeaves.updateLeavesModels();
			Config.getTextureManager().tick();
		}
	}

	public static TextureMap getTextureMapBlocks() {
		return Minecraft.get().getTextureMapBlocks();
	}

	public static void registerResourceListener() {
		IResourceManager iresourcemanager = Config.getResourceManager();

		if (iresourcemanager instanceof IReloadableResourceManager) {
			IReloadableResourceManager ireloadableresourcemanager = (IReloadableResourceManager) iresourcemanager;
			IResourceManagerReloadListener iresourcemanagerreloadlistener = (unused) -> TextureUtils.resourcesReloaded();
			ireloadableresourcemanager.registerReloadListener(iresourcemanagerreloadlistener);
		}

		ITickableTextureObject itickabletextureobject = new ITickableTextureObject() {
			public void tick() {
				TextureAnimations.updateCustomAnimations();
			}

			public void loadTexture(IResourceManager resourceManager){}

			public int getGlTextureId() {
				return 0;
			}

			public void setBlurMipmap(boolean p_174936_1_, boolean p_174936_2_) {}

			public void restoreLastBlurMipmap() {}

			public MultiTexID getMultiTexID() {
				return null;
			}
		};
		ResourceLocation resourcelocation = new ResourceLocation("optifine/TickableTextures");
		Config.getTextureManager().loadTickableTexture(resourcelocation, itickabletextureobject);
	}

	public static String fixResourcePath(String pathOne, String pathTwo) {
		String s = "assets/minecraft/";

		if (pathOne.startsWith(s))
			return pathOne.substring(s.length());
		if (pathOne.startsWith("./")) {
			pathOne = pathOne.substring(2);

			if (!pathTwo.endsWith("/"))
				pathTwo = pathTwo + "/";

			return pathTwo + pathOne;
		}
		if (pathOne.startsWith("/~"))
			pathOne = pathOne.substring(1);

		String s1 = "mcpatcher/";

		if (pathOne.startsWith("~/"))
			return s1 + pathOne.substring(2);
		if (pathOne.startsWith("/"))
			return s1 + pathOne.substring(1);
		return pathOne;
	}

	public static String getBasePath(String path) {
		int i = path.lastIndexOf(47);
		return i < 0 ? "" : path.substring(0, i);
	}

	public static void applyAnisotropicLevel() {
		if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
			float f = GL11.glGetFloat(34047);
			float f1 = (float) Config.getAnisotropicFilterLevel();
			f1 = Math.min(f1, f);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, 34046, f1);
		}
	}

	public static void bindTexture(int p_bindTexture_0_) {
		G.bindTexture(p_bindTexture_0_);
	}

	public static boolean isPowerOfTwo(int p_isPowerOfTwo_0_) {
		int i = MathHelper.roundUpToPowerOfTwo(p_isPowerOfTwo_0_);
		return i == p_isPowerOfTwo_0_;
	}

	public static BufferedImage scaleImage(BufferedImage p_scaleImage_0_, int p_scaleImage_1_) {
		int i = p_scaleImage_0_.getWidth();
		int j = p_scaleImage_0_.getHeight();
		int k = j * p_scaleImage_1_ / i;
		BufferedImage bufferedimage = new BufferedImage(p_scaleImage_1_, k, 2);
		Graphics2D graphics2d = bufferedimage.createGraphics();
		Object object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

		if (p_scaleImage_1_ < i || p_scaleImage_1_ % i != 0) {
			object = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		}

		graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
		graphics2d.drawImage(p_scaleImage_0_, 0, 0, p_scaleImage_1_, k, (ImageObserver) null);
		return bufferedimage;
	}

	public static BufferedImage scaleToPowerOfTwo(BufferedImage p_scaleToPowerOfTwo_0_, int p_scaleToPowerOfTwo_1_) {
		if (p_scaleToPowerOfTwo_0_ == null) {
			return p_scaleToPowerOfTwo_0_;
		}
		int i = p_scaleToPowerOfTwo_0_.getWidth();
		int j = p_scaleToPowerOfTwo_0_.getHeight();
		int k = Math.max(i, p_scaleToPowerOfTwo_1_);
		k = MathHelper.roundUpToPowerOfTwo(k);

		if (k == i) {
			return p_scaleToPowerOfTwo_0_;
		}
		int l = j * k / i;
		BufferedImage bufferedimage = new BufferedImage(k, l, 2);
		Graphics2D graphics2d = bufferedimage.createGraphics();
		Object object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

		if (k % i != 0) {
			object = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		}

		graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
		graphics2d.drawImage(p_scaleToPowerOfTwo_0_, 0, 0, k, l, (ImageObserver) null);
		return bufferedimage;
	}

	public static BufferedImage scaleMinTo(BufferedImage p_scaleMinTo_0_, int p_scaleMinTo_1_) {
		if (p_scaleMinTo_0_ == null) {
			return p_scaleMinTo_0_;
		}
		int i = p_scaleMinTo_0_.getWidth();
		int j = p_scaleMinTo_0_.getHeight();

		if (i >= p_scaleMinTo_1_) {
			return p_scaleMinTo_0_;
		}
		int k;

		for (k = i; k < p_scaleMinTo_1_; k *= 2) {
			;
		}

		int l = j * k / i;
		BufferedImage bufferedimage = new BufferedImage(k, l, 2);
		Graphics2D graphics2d = bufferedimage.createGraphics();
		Object object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, object);
		graphics2d.drawImage(p_scaleMinTo_0_, 0, 0, k, l, (ImageObserver) null);
		return bufferedimage;
	}

	public static Dimension getImageSize(InputStream p_getImageSize_0_, String p_getImageSize_1_) {
		Iterator iterator = ImageIO.getImageReadersBySuffix(p_getImageSize_1_);

		while (true) {
			if (iterator.hasNext()) {
				ImageReader imagereader = (ImageReader) iterator.next();
				Dimension dimension;

				try {
					ImageInputStream imageinputstream = ImageIO.createImageInputStream(p_getImageSize_0_);
					imagereader.setInput(imageinputstream);
					int i = imagereader.getWidth(imagereader.getMinIndex());
					int j = imagereader.getHeight(imagereader.getMinIndex());
					dimension = new Dimension(i, j);
				} catch (IOException var11) {
					continue;
				} finally {
					imagereader.dispose();
				}

				return dimension;
			}

			return null;
		}
	}

	public static void dbgMipmaps(TextureAtlasSprite p_dbgMipmaps_0_) {
		int[][] aint = p_dbgMipmaps_0_.getFrameTextureData(0);

		for (int i = 0; i < aint.length; ++i) {
			int[] aint1 = aint[i];

			if (aint1 == null) {
				Config.dbg("" + i + ": " + aint1);
			} else {
				Config.dbg("" + i + ": " + aint1.length);
			}
		}
	}

	public static void saveGlTexture(String p_saveGlTexture_0_, int p_saveGlTexture_1_, int p_saveGlTexture_2_, int p_saveGlTexture_3_, int p_saveGlTexture_4_) {
		bindTexture(p_saveGlTexture_1_);
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		File file1 = new File(p_saveGlTexture_0_);
		File file2 = file1.getParentFile();

		if (file2 != null) {
			file2.mkdirs();
		}

		for (int i = 0; i < 16; ++i) {
			File file3 = new File(p_saveGlTexture_0_ + "_" + i + ".png");
			file3.delete();
		}

		for (int i1 = 0; i1 <= p_saveGlTexture_2_; ++i1) {
			File file4 = new File(p_saveGlTexture_0_ + "_" + i1 + ".png");
			int j = p_saveGlTexture_3_ >> i1;
			int k = p_saveGlTexture_4_ >> i1;
			int l = j * k;
			IntBuffer intbuffer = BufferUtils.createIntBuffer(l);
			int[] aint = new int[l];
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, i1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) intbuffer);
			intbuffer.get(aint);
			BufferedImage bufferedimage = new BufferedImage(j, k, 2);
			bufferedimage.setRGB(0, 0, j, k, aint, 0, j);

			try {
				ImageIO.write(bufferedimage, "png", (File) file4);
				Config.dbg("Exported: " + file4);
			} catch (Exception exception) {
				Config.warn("Error writing: " + file4);
				Config.warn("" + exception.getClass().getName() + ": " + exception.getMessage());
			}
		}
	}

	public static int getGLMaximumTextureSize() {
		for (int i = 65536; i > 0; i >>= 1) {
			GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (IntBuffer) (IntBuffer) null);
			int j = GL11.glGetError();
			int k = GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

			if (k != 0) {
				return i;
			}
		}

		return -1;
	}

}
