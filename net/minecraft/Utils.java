package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.keystrokes.KeyStrokes;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import optifine.CustomColormap;
import optifine.CustomSkyLayer;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import shadersmod.client.ShaderOption;

import java.util.Arrays;
import java.util.Random;

public class Utils {

	public static final Object[] OBJECT = new Object[0];
	public static final ShaderOption[] SHADEROPTION = new ShaderOption[0];
	public static final CustomColormap[] CUSTOMCOLORMAP = new CustomColormap[0];
	public static final String[] STRING = new String[0];
	public static final Integer[] INTEGER = new Integer[0];
	public static final Class[] CLASS = new Class[0];
	public static final CustomSkyLayer[] CUSTOMSKYLAYER = new CustomSkyLayer[0];
	public static final StackTraceElement[] STACKTRACEELEMENT = new StackTraceElement[0];
	public static final IResourcePack[] IRESOURCEPACK = new IResourcePack[0];
	public static final DisplayMode[] DISPLAYMODE = new DisplayMode[0];

	public static void drawFloatingText(String text, float x, float y, float z) {
		drawFloatingText0(text, x, y, z, false);
		drawFloatingText0(text, x, y, z, true);
	}

	private static void drawFloatingText0(String text, float x, float y, float z, boolean back) {
		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
		float f = 1.6F;
		float f1 = 0.016666668F * f;
		G.pushMatrix();
		G.translate(x, y, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		if (back) G.rotate(180, 0, 1,0);
//            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//            GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		G.scale(-f1, -f1, f1);
		G.disableLighting();
		G.depthMask(false);
		G.disableDepth();
		G.enableBlend();
		G.tryBlendFuncSeparate(770, 771, 1, 0);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		byte b0 = 0;

		int width = fontrenderer.getStringWidth(text);
		int i = width / 2;
		G.disableTexture2D();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double)(-i - 1), (double)(-1 + b0), 0.0D).color(0.0F, 0.0F, 0F, 0.5F).endVertex();
		worldrenderer.pos((double)(-i - 1), (double)(8 + b0), 0.0D).color(0.0F, 0.0F, 0F, 0.5F).endVertex();
		worldrenderer.pos((double)(i + 1), (double)(8 + b0), 0.0D).color(0.0F, 0.0F, 0F, 0.5F).endVertex();
		worldrenderer.pos((double)(i + 1), (double)(-1 + b0), 0.0D).color(0.0F, 0.0F, 0F, 0.5F).endVertex();
		tessellator.draw();
			G.enableTexture2D();
		if (!back) fontrenderer.drawString(text, -width / 2, b0, 553648127);
		G.enableDepth();
		G.depthMask(true);
		if (!back) fontrenderer.drawString(text, -width / 2, b0, -1);
		G.enableLighting();
		G.disableBlend();
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		G.popMatrix();
	}

	public static void processCommand(String line) {
		if (line.equals("c")) {
			Minecraft.getMinecraft().ingameGUI.setLoading(5000, "Тестовая загрузка");
		}
		if (line.equals("key")) KeyStrokes.addKeyStroke(KeyBinding.FORWARD, 10, 10, 2f);
	}

	public static int easeIn(int t,int b , int c, int d) {
		return c*(t/=d)*t + b;
	}

	public static int  easeOut(int t,int b , int c, int d) {
		return -c *(t/=d)*(t-2) + b;
	}

	public static int  easeInOut(int t,int b , int c, int d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * (--t *(t-2) - 1) + b;
	}


	// seed: 0 - 1535
	public static int rainbowGradient(int seed) {
		int s = seed / 256;
		seed %= 256;
		int r = 0x00;
		int g = 0x00;
		int b = 0x00;
		switch (s) {
			case 0:
				r = seed;
				g = 0xff;
				break;
			case 1:
				g = 0xff - seed;
				r = 0xff;
				break;
			case 2:
				r = 0xff;
				b = seed;
				break;
			case 3:
				b = 0xff;
				r = 0xff - seed;
				break;
			case 4:
				b = 0xff;
				g = seed;
				break;
			case 5:
				g = 0xff;
				b = 0xff - seed;
				break;
		}
		return 0xff000000 | r << 16 | g << 8 | b;
	}


	public static String bool(boolean b) {
		return b ? "§aДа" : "§cНет";
	}

	public static int gradient(int a, int b, float p) {
		int 	a1 = a >> 24 & 0xff, a2 = b >> 24 & 0xff,
				r1 = a >> 16 & 0xff, r2 = b >> 16 & 0xff,
				g1 = a >> 8  & 0xff, g2 = b >> 8  & 0xff,
				b1 = a 		 & 0xff, b2 = b & 0xff;
		int a0 = (int) (a1 * p + a2 * (1 - p));
		int r0 = (int) (r1 * p + r2 * (1 - p));
		int g0 = (int) (g1 * p + g2 * (1 - p));
		int b0 = (int) (b1 * p + b2 * (1 - p));
		return a0 << 24 | r0 << 16 | g0 << 8 | b0;
	}

	public static <T> void cyclicShift(T[] array, int offset) {
		T[] copy = Arrays.copyOf(array, offset);
		System.arraycopy(array, offset, array, 0, array.length - offset);
		System.arraycopy(copy, 0, array, array.length - offset, offset);
	}


	public static void shuffleArray(Object[] array) {
		int index;
		Object temp;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
	}

	public static void shuffleArray(int[] array) {
		int index, temp;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
	}

	public static ItemStack createItemStack(int id, int data) {
		try {
			Item i = Item.itemRegistry.getObjectById(id);
			return new ItemStack(i, 1, data);
		} catch (NullPointerException e) {
			Block b = Block.blockRegistry.getObjectById(id);
			return new ItemStack(b, 1, data);
		}
	}

}
