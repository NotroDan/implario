package net.minecraft.client;

import net.minecraft.client.game.entity.CPlayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.font.AssetsFontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.chat.ChatComponentText;

import java.util.Map;

import static net.minecraft.client.Minecraft.theMinecraft;

public final class MC {

	public static AssetsFontRenderer FR;

	private MC() {throw new Error("Ну ты чо совсем шоле ебанулся");}

	public static AssetsFontRenderer getFontRenderer() {
		return theMinecraft.fontRenderer;
	}

	public static TextureManager getTextureManager() {
		return theMinecraft.getTextureManager();
	}

	public static RenderItem getRenderItem() {
		return theMinecraft.getRenderItem();
	}

	public static CPlayer getPlayer() {
		return theMinecraft.thePlayer;
	}

	public static Minecraft i() {
		return Minecraft.getMinecraft();
	}

	public static void chat(String s) {
		MC.i().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(s));
	}

	public static void printMap(Map<?, ?> map) {
		map.forEach((k, v) -> chat("§e" + k + "§7: §f" + v));
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
		i().displayGuy.updateDisplay(i());
		if (FR == null) FR = getFontRenderer();
	}

	public static int sqd(int a, int b) {
		return a * a + b * b;
	}

	public static WorldClient getWorld() {
		return i().theWorld;
	}

	public static void bindTexture(ResourceLocation resourceLocation) {
		getTextureManager().bindTexture(resourceLocation);
	}


	public static BlockRendererDispatcher getBlockRendererDispatcher() {
		return i().getBlockRendererDispatcher();
	}

	public static void displayGuiScreen(GuiScreen gui) {
		i().displayGuiScreen(gui);
	}

}
