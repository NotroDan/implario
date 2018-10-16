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
import org.lwjgl.opengl.GL11;

public class Utils {

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
}
