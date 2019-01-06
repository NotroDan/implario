package net.minecraft.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ChatComponentText;

import static net.minecraft.client.Minecraft.theMinecraft;

public final class MC {
	
	public static FontRenderer FR;

	private MC() {throw new Error("Ну ты чо совсем шоле ебанулся");}

	public static FontRenderer getFontRenderer() {
		return theMinecraft.fontRendererObj;
	}

	public static TextureManager getTextureManager() {
		return theMinecraft.getTextureManager();
	}

	public static RenderItem getRenderItem() {
		return theMinecraft.getRenderItem();
	}

	public static EntityPlayerSP getPlayer() {
		return theMinecraft.thePlayer;
	}

	public static Minecraft i() {
		return Minecraft.getMinecraft();
	}

	public static void chat(String s) {
		MC.i().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(s));
	}

	public static int getWidth() {
		return new ScaledResolution(i()).getScaledWidth();
	}
	public static int getHeight() {
		return new ScaledResolution(i()).getScaledHeight();
	}

	public static void clearChat() {
		if (i().ingameGUI != null) i().ingameGUI.getChatGUI().clearChatMessages();
	}

	public static void toggleHitboxes() {
		i().getRenderManager().setDebugBoundingBox(!i().getRenderManager().isDebugBoundingBox());
	}
	
	public static void frame() {
		i().updateDisplay();
		if (FR == null) FR = getFontRenderer();
	}
	
	public static int sqd(int a, int b) {
		return a * a + b * b;
	}
}
