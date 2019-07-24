package net.minecraft.client.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LoadingScreenRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.Settings;
import net.minecraft.server.Profiler;
import net.minecraft.util.FileUtil;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static net.minecraft.logging.Log.MAIN;
import static net.minecraft.server.Profiler.in;
import static net.minecraft.util.Util.OS.OSX;

public class DisplayGuy {

	private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
	private final Minecraft mc;
	public boolean fullscreen;
	private int tempDisplayWidth;
	private int tempDisplayHeight;

	public DisplayGuy(Minecraft mc, GameConfiguration.DisplayInformation info) {
		this.mc = mc;
		this.fullscreen = info.fullscreen;
		this.tempDisplayWidth = info.width;
		this.tempDisplayHeight = info.height;
	}

	public void createDisplay() throws LWJGLException {
		Display.setResizable(true);
		Display.setTitle("Implario");

		try {
			Display.create(new PixelFormat().withDepthBits(24));
		} catch (LWJGLException lwjglexception) {
			MAIN.error("Не удалось создать окно для игры с крутым форматом пикселизации");
			MAIN.exception(lwjglexception);

			Utils.sleep(1000);

			if (fullscreen) updateDisplayMode();
			Display.create();
		}
	}

	public void setInitialDisplayMode() throws LWJGLException {
		if (fullscreen) {
			Display.setFullscreen(true);
			DisplayMode displaymode = Display.getDisplayMode();
			mc.displayWidth = Math.max(1, displaymode.getWidth());
			mc.displayHeight = Math.max(1, displaymode.getHeight());
		} else {
			Display.setDisplayMode(new DisplayMode(mc.displayWidth, mc.displayHeight));
		}
	}

	public void setWindowIcon() {
		if (Util.getOSType() == OSX) return;

		InputStream icon16 = null;
		InputStream icon32 = null;

		try {
			icon16 = Minecraft.class.getResourceAsStream("/icon16.png");
			icon32 = Minecraft.class.getResourceAsStream("/icon32.png");

			if (icon16 != null && icon32 != null) {
				Display.setIcon(new ByteBuffer[] {FileUtil.readImageToBuffer(icon16), FileUtil.readImageToBuffer(icon32)});
			}
		} catch (IOException e) {
			MAIN.error("Не удалось установить иконку окна");
			MAIN.exception(e);
		} finally {
			IOUtils.closeQuietly(icon16);
			IOUtils.closeQuietly(icon32);
		}
	}

	public void updateDisplayMode() throws LWJGLException {
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

					do {
						if (!iterator.hasNext()) continue label53;
						displaymode3 = (DisplayMode) iterator.next();

					} while (displaymode3.getBitsPerPixel() != 32 || displaymode3.getWidth() != displaymode1.getWidth() / 2 || displaymode3.getHeight() != displaymode1.getHeight() / 2);

					displaymode = displaymode3;
				}
			}
		}

		Display.setDisplayMode(displaymode);
		mc.displayWidth = displaymode.getWidth();
		mc.displayHeight = displaymode.getHeight();
	}

	public void checkWindowResize() {
		if (fullscreen || !Display.wasResized()) return;
		int i = mc.displayWidth;
		int j = mc.displayHeight;
		mc.displayWidth = Display.getWidth();
		mc.displayHeight = Display.getHeight();

		if (mc.displayWidth == i && mc.displayHeight == j) return;
		if (mc.displayWidth <= 0) mc.displayWidth = 1;
		if (mc.displayHeight <= 0) mc.displayHeight = 1;

		resize(mc.displayWidth, mc.displayHeight);
	}

	public void updateDisplay(Minecraft mc) {
		in.startSection("display_update");
		Display.update();
		in.endSection();
		checkWindowResize();
	}

	/**
	 * Toggles fullscreen mode.
	 */
	public void toggleFullscreen() {
		try {
			fullscreen = !fullscreen;
			Settings.USE_FULLSCREEN.set(fullscreen);

			if (fullscreen) {
				updateDisplayMode();
				mc.displayWidth = Display.getDisplayMode().getWidth();
				mc.displayHeight = Display.getDisplayMode().getHeight();

				if (mc.displayWidth <= 0) {
					mc.displayWidth = 1;
				}

				if (mc.displayHeight <= 0) {
					mc.displayHeight = 1;
				}
			} else {
				Display.setDisplayMode(new DisplayMode(tempDisplayWidth, tempDisplayHeight));
				mc.displayWidth = tempDisplayWidth;
				mc.displayHeight = tempDisplayHeight;

				if (mc.displayWidth <= 0) mc.displayWidth = 1;
				if (mc.displayHeight <= 0) mc.displayHeight = 1;
			}

			if (mc.currentScreen != null) resize(mc.displayWidth, mc.displayHeight);
			else updateFramebufferSize();

			Display.setFullscreen(fullscreen);
			Display.setVSyncEnabled(false);//Settings.ENABLE_VSYNC.b());
			updateDisplay(mc);
		} catch (Exception ex) {
			MAIN.error("Не удалось переключить полноэкранный режим");
			MAIN.exception(ex);
		}
	}

	/**
	 * Called to resize the current screen.
	 */
	private void resize(int width, int height) {
		mc.displayWidth = Math.max(1, width);
		mc.displayHeight = Math.max(1, height);

		if (mc.currentScreen != null) {
			ScaledResolution scaledresolution = new ScaledResolution(mc);
			mc.currentScreen.onResize(mc, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
		}

		mc.loadingScreen = new LoadingScreenRenderer(mc);
		updateFramebufferSize();
	}


	private void updateFramebufferSize() {
		mc.getFramebuffer().createBindFramebuffer(mc.displayWidth, mc.displayHeight);

		if (mc.entityRenderer != null) {
			mc.entityRenderer.updateShaderGroupSize(mc.displayWidth, mc.displayHeight);
		}
	}

	/**
	 * Parameter appears to be unused
	 */
	public void displayDebugInfo(long elapsedTicksTime) {
		if (!in.profilingEnabled) return;
		List<Profiler.Result> list = in.getProfilingData(mc.inputHandler.getDebugProfilerName());
		Profiler.Result profiler$result = list.remove(0);
		G.clear(256);
		G.matrixMode(5889);
		G.enableColorMaterial();
		G.loadIdentity();
		G.ortho(0.0D, (double) mc.displayWidth, (double) mc.displayHeight, 0.0D, 1000.0D, 3000.0D);
		G.matrixMode(5888);
		G.loadIdentity();
		G.translate(0.0F, 0.0F, -2000.0F);
		GL11.glLineWidth(1.0F);
		G.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		int i = 160;
		G.scale(2, 2, 2);
		int j = mc.displayWidth / 2 - i - 10;
		int k = mc.displayHeight / 2 - i * 2;
		G.enableBlend();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos((double) ((float) j - (float) i * 1.1F), (double) ((float) k - (float) i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j - (float) i * 1.1F), (double) (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j + (float) i * 1.1F), (double) (k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
		worldrenderer.pos((double) ((float) j + (float) i * 1.1F), (double) ((float) k - (float) i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
		tessellator.draw();
		G.disableBlend();
		double d0 = 0.0D;

		for (Profiler.Result res : list) {
			int i1 = MathHelper.floor_double(res.a / 4.0D) + 1;
			worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
			int j1 = res.hash();
			int k1 = j1 >> 16 & 255;
			int l1 = j1 >> 8 & 255;
			int i2 = j1 & 255;
			worldrenderer.pos((double) j, (double) k, 0.0D).color(k1, l1, i2, 255).endVertex();

			for (int j2 = i1; j2 >= 0; --j2) {
				float f = (float) ((d0 + res.a * (double) j2 / (double) i1) * Math.PI * 2.0D / 100.0D);
				float f1 = MathHelper.sin(f) * (float) i;
				float f2 = MathHelper.cos(f) * (float) i * 0.5F;
				worldrenderer.pos((double) ((float) j + f1), (double) ((float) k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
			}

			tessellator.draw();
			worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

			for (int i3 = i1; i3 >= 0; --i3) {
				float f3 = (float) ((d0 + res.a * (double) i3 / (double) i1) * Math.PI * 2.0D / 100.0D);
				float f4 = MathHelper.sin(f3) * (float) i;
				float f5 = MathHelper.cos(f3) * (float) i * 0.5F;
				worldrenderer.pos((double) ((float) j + f4), (double) ((float) k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
				worldrenderer.pos((double) ((float) j + f4), (double) ((float) k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
			}

			tessellator.draw();
			d0 += res.a;
		}

		DecimalFormat decimalformat = new DecimalFormat("##0.00");
		G.enableTexture2D();
		String s = "";

		if (!profiler$result.s.equals("unspecified")) s = s + "[0] ";
		if (profiler$result.s.length() == 0) s = s + "ROOT ";
		else s = s + profiler$result.s + " ";

		int l2 = 16777215;
		mc.fontRenderer.drawStringWithShadow(s, (float) (j - i), (float) (k - i / 2 - 16), l2);
		mc.fontRenderer.drawStringWithShadow(s = decimalformat.format(profiler$result.b) + "%", (float) (j + i - mc.fontRenderer.getStringWidth(s)), (float) (k - i / 2 - 16), l2);

		for (int k2 = 0; k2 < list.size(); k2++) {
			Profiler.Result profiler$result2 = list.get(k2);
			String s1 = "";

			if (profiler$result2.s.equals("unspecified")) s1 = s1 + "[?] ";
			else s1 = s1 + "[" + (k2 + 1) + "] ";

			s1 = s1 + profiler$result2.s;
			mc.fontRenderer.drawStringWithShadow(s1, (float) (j - i), (float) (k + i / 2 + k2 * 8 + 20), profiler$result2.hash());
			mc.fontRenderer.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.a) + "%", (float) (j + i - 50 - mc.fontRenderer.getStringWidth(s1)),
					(float) (k + i / 2 + k2 * 8 + 20), profiler$result2.hash());
			mc.fontRenderer.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.b) + "%", (float) (j + i - mc.fontRenderer.getStringWidth(s1)),
					(float) (k + i / 2 + k2 * 8 + 20), profiler$result2.hash());
		}
		G.scale(0.5, 0.5, 0.5);
	}

}
