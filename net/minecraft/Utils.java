package net.minecraft;

import __google_.crypt.async.RSA;
import __google_.net.Response;
import __google_.net.client.Client;
import __google_.util.ByteZip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import optifine.CustomColormap;
import optifine.CustomSkyLayer;
import org.lwjgl.opengl.GL11;
import shadersmod.client.ShaderOption;

public class Utils {

	public static final Object[] OBJECT = new Object[0];
	public static final ShaderOption[] SHADEROPTION = new ShaderOption[0];
	public static final CustomColormap[] CUSTOMCOLORMAP = new CustomColormap[0];
	public static final String[] STRING = new String[0];
	public static final Integer[] INTEGER = new Integer[0];
	public static final Class[] CLASS = new Class[0];
	public static final CustomSkyLayer[] CUSTOMSKYLAYER = new CustomSkyLayer[0];

	public static void drawFloatingText(String text, float x, float y, float z) {
		drawFloatingText0(text, x, y, z, false);
		drawFloatingText0(text, x, y, z, true);
	}

	private static void drawFloatingText0(String text, float x, float y, float z, boolean back) {
		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
		float f = 1.6F;
		float f1 = 0.016666668F * f;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		if (back) GlStateManager.rotate(180, 0, 1,0);
//            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//            GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-f1, -f1, f1);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		byte b0 = 0;

		int width = fontrenderer.getStringWidth(text);
		int i = width / 2;
		GlStateManager.disableTexture2D();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double)(-i - 1), (double)(-1 + b0), 0.0D).color(0.0F, 0.0F, 0F, 0.5F).endVertex();
		worldrenderer.pos((double)(-i - 1), (double)(8 + b0), 0.0D).color(0.0F, 0.0F, 0F, 0.5F).endVertex();
		worldrenderer.pos((double)(i + 1), (double)(8 + b0), 0.0D).color(0.0F, 0.0F, 0F, 0.5F).endVertex();
		worldrenderer.pos((double)(i + 1), (double)(-1 + b0), 0.0D).color(0.0F, 0.0F, 0F, 0.5F).endVertex();
		tessellator.draw();
			GlStateManager.enableTexture2D();
		if (!back) fontrenderer.drawString(text, -width / 2, b0, 553648127);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		if (!back) fontrenderer.drawString(text, -width / 2, b0, -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public static void connect() {

		Client client = new Client("lmaomc.ru", 1424);
		client.connect();
		client.getCertificate();

	}

	public static void log(String pass, String nick){
		RSA rsa = RSA.generate(pass, 2048, 12);
		Client client = new Client("lmaomc.ru", 1424);
		client.connect();
		client.getCertificate();
		byte decoded[] = rsa.decodeByte(client.apply(new Response(2, new ByteZip().add(nick).build())).getContent());
		client.apply(new Response(0, new ByteZip().add(nick).add(decoded).build()));
		client.close();
	}

	public static void reg(String pass, String nick){
		RSA rsa = RSA.generate(pass, 2048, 12);
		Client client = new Client("lmaomc.ru", 1424);
		client.connect();
		client.getCertificate();
		client.apply(new Response(1, new ByteZip().add(nick).add(rsa.getBytePublicKey()).build()));
		client.close();
	}

	public static void processCommand(String line) {
		if (line.equals("c")) {
			Minecraft.getMinecraft().ingameGUI.setLoading(5000, "Тестовая загрузка");
		}
	}

	public static int easeIn(int t,int b , int c, int d) {
		return c*(t/=d)*t + b;
	}

	public static int  easeOut(int t,int b , int c, int d) {
		return -c *(t/=d)*(t-2) + b;
	}

	public static int  easeInOut(int t,int b , int c, int d) {
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
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

}
