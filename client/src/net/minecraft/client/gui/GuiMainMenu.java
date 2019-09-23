package net.minecraft.client.gui;

import __google_.util.Exceptions;
import net.minecraft.CyclicIterator;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.gui.element.GuiGridTest;
import net.minecraft.client.gui.settings.GuiSettings;
import net.minecraft.client.main.Main;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.Lang;
import net.minecraft.client.settings.Settings;
import net.minecraft.logging.Log;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.Datapacks;
import net.minecraft.resources.load.JarDatapackLoader;
import net.minecraft.security.update.JarFile;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Skybox;
import org.apache.logging.log4j.core.config.NullConfiguration;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GLContext;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

	public static String birthday, birthdayComment;
	private final long openedAt;

	private static final ResourceLocation[] skyboxTiles = new ResourceLocation[] {
			new ResourceLocation("textures/gui/title/background/panorama_0.png"),
			new ResourceLocation("textures/gui/title/background/panorama_1.png"),
			new ResourceLocation("textures/gui/title/background/panorama_2.png"),
			new ResourceLocation("textures/gui/title/background/panorama_3.png"),
			new ResourceLocation("textures/gui/title/background/panorama_4.png"),
			new ResourceLocation("textures/gui/title/background/panorama_5.png")
	};

	private Skybox skybox;
	private int devCounter;

	public GuiMainMenu() {
		this.openedAt = System.currentTimeMillis();

		if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
			Log.MAIN.warn("Ваш компьютер не удовлетворяет минимальным системным требованиям. Вы что, сидите через картошку?");
		}
	}

	@Override
	public void updateScreen() {
		if (skybox != null) skybox.updateScreen();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_RETURN) {
			if (++devCounter >= 5) {
				Settings.DEBUG.toggle();
				devCounter = 0;
				initGui();
			}
		}
	}

	@Override
	public void initGui() {
		buttonList.clear();
		DynamicTexture viewport = new DynamicTexture(256, 256);
		ResourceLocation background = mc.getTextureManager().getDynamicTextureLocation("background", viewport);
		if (skybox == null) skybox = new Skybox(skyboxTiles, background, viewport, this);
		else skybox.update(background, viewport);

		int j = (height >> 2) + 12;
		addSingleplayerMultiplayerButtons(j + 36);
		int cacheWidth = width >> 1;
		buttonList.add(new GuiButton(0, cacheWidth - 100, j + 84, 98, 20, Lang.format("menu.options")));
		buttonList.add(new GuiButton(97, cacheWidth + 2, j + 84, 98, 20, "Смена ника"));
		if (Settings.DEBUG.b()) {
			buttonList.add(new GuiButton(4, cacheWidth - 100, j + 108, 98, 20, "§8Dev §fVanilla"));
			buttonList.add(new GuiButton(54, cacheWidth + 2, j + 108, 98, 20, "§8Dev §fПерезапуск"));
			buttonList.add(new GuiButton(5, cacheWidth - 100, j + 132, 98, 20, "§8Dev §fЛоги"));
			buttonList.add(new GuiButton(6, cacheWidth + 2, j + 132, 98, 20, "§8Dev §fНастройки"));
			buttonList.add(new GuiButton(3, cacheWidth - 100, j + 156, 98, 20, "§8Dev §fСервера"));
			buttonList.add(new GuiButton(7, cacheWidth + 2, j + 156, 98, 20, "§8Dev §fСетка"));
		}

	}

	private void addSingleplayerMultiplayerButtons(int y) {
		int cacheWidth = (width >> 1) - 100;
		buttonList.add(new GuiButton(1, cacheWidth, y, Lang.format("menu.singleplayer")));
		buttonList.add(new GuiButton(2, cacheWidth, y + 24, Lang.format("menu.multiplayer")));
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) this.mc.displayGuiScreen(new GuiOptions());
		if (button.id == 1) this.mc.displayGuiScreen(new GuiSelectWorld(this));
		if (button.id == 2) this.mc.displayGuiScreen(new GuiMultiplayer(this));
		if (button.id == 3) this.mc.displayGuiScreen(new GuiServers(this));
		if (button.id == 5) this.mc.displayGuiScreen(new GuiLogs());
		if (button.id == 7) this.mc.displayGuiScreen(new GuiGridTest());
		if (button.id == 54) {
            try {
                Exceptions.RunOutException.class.getName();
                try {
                    Class.forName("org.lwjgl.opengl.CallbackUtil").getName();
                    Class.forName("org.lwjgl.opengl.ContextGL").getName();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                JarFile edit = new JarFile(new File("gamedata/client.jar"));
                edit.writeToJar(new File("gamedata/client.jar"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Main.restart();
        }
		if (button.id == 6) this.mc.displayGuiScreen(new GuiSettings(this));


		if (button.id == 97) this.mc.displayGuiScreen(new GuiPlayername(this));

		if (button.id == 4) {
			if (Datapacks.getLoaders().isEmpty()) {
				long time = System.currentTimeMillis();
				Datapack datapack = Datapacks.load(new JarDatapackLoader(new File("gamedata/datapacks/vanilla.jar")));
				System.out.println("Vanilla loaded to memory in " + (System.currentTimeMillis() - time));
				time = System.currentTimeMillis();
				Datapacks.initSingleDatapack(datapack);
				System.out.println("Vanilla loaded in " + (System.currentTimeMillis() - time));
			}
			else {
				long time = System.currentTimeMillis();
				Datapacks.shutdown();
				System.out.println("Vanilla UNloaded in " + (System.currentTimeMillis() - time));
			}
		}

	}



	public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy");

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		G.disableAlpha();
		//		if (skybox != null) skybox.render(partialTicks);
		//		else
		renderBackground(mouseX, mouseY);
		G.enableAlpha();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		float j = width / 16 - 21;

		//        drawRect(0, 0, width, height, 0xff202020);

		renderTitle();
		String s = "Implario Client";

		this.drawString(this.fontRendererObj, s, 2, this.height - 10, -1);
		Date date = new Date();
		String today = birthday == null ? "Сегодня §a" + DATE_FORMAT.format(date) : "§eСегодня день рождения у " + birthday;
		String time = birthdayComment == null ? "Сейчас §a" + TIME_FORMAT.format(date) : birthdayComment + "";
		this.drawString(this.fontRendererObj, today, this.width - this.fontRendererObj.getStringWidth(today) - 6, this.height - 10 - fontRendererObj.getFontHeight(), -1);
		this.drawString(this.fontRendererObj, time, this.width - this.fontRendererObj.getStringWidth(time) - 6, this.height - 10, -1);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void renderBackground(int mouseX, int mouseY) {
		G.disableLighting();
		G.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		this.mc.getTextureManager().bindTexture(modernBackground);
		G.color(1.0F, 1.0F, 1.0F, 1.0F);
		float f = 32.0F;
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(0.0D, (double) this.height, 0.0D).tex(0.0D, (double) ((float) this.height / 32.0F)).color(64, 64, 64, 255).endVertex();
		worldrenderer.pos((double) this.width, (double) this.height, 0.0D).tex((double) ((float) this.width / 32.0F), (double) ((float) this.height / 32.0F)).color(64, 64, 64,
				255).endVertex();
		worldrenderer.pos((double) this.width, 0.0D, 0.0D).tex((double) ((float) this.width / 32.0F), 0).color(64, 64, 64, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
		//		mc.getTextureManager().bindTexture(TileEntityEndPortalRenderer.END_PORTAL_TEXTURE);
		//		drawRect(0, 0, width, height, 0xff101010);
		//
		//		Tessellator tessellator = Tessellator.getInstance();
		//		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		//		float offset = (float) (Minecraft.getSystemTime() % 700000L) / 700000.0F;
		//		random.setSeed(31100L);
		//		GlStateManager.enableAlpha();
		//		GlStateManager.enableBlend();
		//
		//		for (int layer = 15; layer >= 0; layer--) {
		//			GlStateManager.pushMatrix();
		//			float f = 0.00390625F * (1 + (float) layer / 3);
		//
		//
		//			float r = random.nextFloat() * 0.5F + 0.1F;
		//			float g = random.nextFloat() * 0.5F + 0.4F;
		//			float b = random.nextFloat() * 0.5F + 0.5F;
		//			GlStateManager.color(r, g, b, 0.5f);
		//			GlStateManager.translate(width / 2, height / 2, 0);
		//			float mouseOffsetX = (float) mouseX / (float) width * (16 - layer) * 5;
		//			float mouseOffsetY = (float) mouseY / (float) width * (16 - layer) * 10;
		//			GlStateManager.translate(mouseOffsetX, mouseOffsetY, 0);
		//			GlStateManager.rotate((float) (layer * layer * 4321 + layer * 9) * 2.0F, 0, 0, 1);
		//
		//			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		//
		//			float x1 = (float) width * -1.5f, x2 = (float) width * 1.5f;
		//			float y1 = x1 + offset * x1, y2 = x2 - offset * x2;
		//
		//			worldrenderer.pos(width * -1.5, y2, zLevel).tex(0d, (double) (255 * f)).endVertex();
		//			worldrenderer.pos(x2, y2, zLevel).tex((double) (255 * f), (double) (255 * f)).endVertex();
		//			worldrenderer.pos(x2, y1, zLevel).tex((double) (255 * f), 0d).endVertex();
		//			worldrenderer.pos(x1, y1, zLevel).tex(0d, 0d).endVertex();
		//
		//			tessellator.draw();
		//			GlStateManager.popMatrix();
		//			drawRect(0, 0, width, height, 0x25101010);
		//		}


	}

	private static final CyclicIterator<String> titles = new CyclicIterator<>(new String[] {"Implario", "BedWars", "MLGRush"});
	private static final CyclicIterator<String> headers = new CyclicIterator<>(new String[] {"Клиент Minecraft 1.8.8", "Киберспортивная версия", "Тренируйте свои навыки на"});
	private static boolean switched = true;
	private static String title = titles.current();
	private static String header = headers.current();

	private void renderTitle() {
		long t = System.currentTimeMillis();
		long d = (t - openedAt) % 10000;

		String render;

		if (d < 1000) {
			if (!switched) {
				title = titles.next();
				header = headers.next();
				switched = true;
			}
			render = "§7> §f" + title.substring(0, (int) ((float) d / 1000 * (title.length() + 1)));
		} else if (d > 9500) {
			switched = false;
			d -= 9750;
			d = -d + 250;
			render = "§7> §f" + title.substring(0, (int) ((float) d / 500 * (title.length() + 1)));
		} else render = "§7> §f" + title + (d % 500 > 250 ? "§7_" : "");

		G.scale(8, 8, 8);
		if (d > 4000 && d < 4200) {
			int a;
			a = Math.abs(Math.abs((int) d - 4100) - 50) / 25;
			fontRendererObj.drawString("§c> " + title, width / 16 - 21 - a, 2.5f + a, 0xffff0000, false);
			fontRendererObj.drawString("§b> " + title, width / 16 - 21 + a, 2.5f - a, 0xff00ffff, false);
		}
		fontRendererObj.drawString(render, width / 16 - 21, 2.5f, -1, false);
		G.scale(0.25, 0.25, 0.25);
		renderHeader(d);
		G.scale(0.5, 0.5, 0.5);
	}

	private void renderHeader(long deltaTime) {
		int opacity = 0xff;
		int offset = 0;
		int d = (int) (deltaTime - 5000);
		if (d < 0) d = -d;
		if ((d -= 4500) > 0) offset = d / 10;
		drawCenteredString(fontRendererObj, header, width / 4, 6 - offset, 0xf4d742);
	}

}
