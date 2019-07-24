package net.minecraft.client.gui.settings;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.gui.settings.tabs.BasicTabScreen;
import net.minecraft.client.gui.settings.tabs.element.Selector;
import net.minecraft.client.gui.settings.tabs.element.Slider;
import net.minecraft.client.gui.settings.tabs.element.Switch;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiSettings extends GuiScreen {

	public static final int SIDEBARW = 230, SELECTIONOFFSET = -9, CELLHEIGHT = 80,
			COLOR1 = 0xFF_1F2E54, COLOR2 = 0xf0_12171a, COLOR3 = 0xFF_121F3E, COLORF = 0xFF_1F542E,
			COLUMNWIDTH = 300;
	private final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/actions.png");
	private final GuiScreen parent;
	private final Tab[] tabs;
	private ScaledResolution resolution;
	private Tab active;
	private int factor;


	public GuiSettings(GuiScreen parent) {
		this.parent = parent;

		TexQuad iconGraphics = new TexQuad(0, 0, 16, 16, 256, 256);
		TexQuad iconControls = new TexQuad(96, 0, 16, 16, 256, 256);
		TexQuad iconShaders = new TexQuad(112, 0, 16, 16, 256, 256);

		BasicTabScreen graphics = new BasicTabScreen().add(
				new Switch("Покачивание камеры", Settings.SMOOTH_CAMERA),
				new Switch("Быстрый рендер", Settings.FAST_RENDER),
				new Switch("Динамическое освещение", Settings.DYNAMIC_LIGHTS),
				new Selector(Settings.MIPMAP_TYPE, "Уровень сглаживания"),
				new Switch("Динамические чанки", Settings.CHUNK_UPDATES_DYNAMIC),
				new Switch("Использовать FBO", Settings.FBO_ENABLE),
				new Switch("Использовать VBO", Settings.USE_VBO),
				new Slider(Settings.RENDER_DISTANCE, "Прорисовка"),
				new Slider(Settings.FRAMERATE_LIMIT, "FPS"),
				new Slider(Settings.MIPMAP_LEVELS, "MipMaps"),
				new Slider(Settings.AA_LEVEL, "Сглаживание"),
				new Slider(Settings.AF_LEVEL, "Чёткость"),
				new Slider(Settings.AO_LEVEL, "Мягкий свет")
														  );

		List<Tab> tabs = new ArrayList<>();

		tabs.add(active = new Tab("Графика", iconGraphics, graphics, "Влияет на FPS"));
		tabs.add(new Tab("Управление", iconControls, graphics, "Горячие клавиши"));
		tabs.add(new Tab("Шейдеры", iconShaders, graphics, "Реалистичные кубики"));

		this.tabs = tabs.toArray(new Tab[0]);

	}

	@Override
	public void initGui() {
		resolution = new ScaledResolution(mc);
		factor = resolution.getScaleFactor();
		parent.initGui();
	}

	@Override
	public void drawScreen(int mx, int my, float ticks) {
		//		drawRect(0, 0, resolution.getScaledWidth(), resolution.getScaledHeight(), 0x131313);
		G.translate(0, 0, -0.5);
		parent.drawScreen(mx, my, ticks);
		G.translate(0, 0, 0.5);

		G.matrixMode(GL11.GL_PROJECTION);                        // Select The Projection Matrix
		G.loadIdentity();                                   // Reset The Projection Matrix
		G.ortho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, -1, 1);//1000.0D, 3000.0D);
		G.matrixMode(GL11.GL_MODELVIEW);                         // Select The Modelview Matrix
		G.loadIdentity(); // Reset The Modelview Matrix
		G.disableLighting();
		G.enableTexture2D();
		GL11.glDisable(GL11.GL_DITHER);
		GL11.glDepthFunc(GL11.GL_LEQUAL);


		G.pushMatrix();

		drawRect(0, 0, SIDEBARW, mc.displayHeight, COLOR1);
		drawRect(SIDEBARW, 0, mc.displayWidth, mc.displayHeight, COLOR2);

		G.translate(0, 20, 0);
		for (Tab tab : tabs) {
			if (tab == active) drawRect(0, SELECTIONOFFSET, SIDEBARW, SELECTIONOFFSET + CELLHEIGHT, COLOR3);
			G.color(1, 1, 1, 1);
			G.pushMatrix();
			G.translate(14, 0, 0);
			G.scale(4, 4, 1);
			tab.getTexture().draw(TEXTURE);
			G.scale(0.25, 0.25, 1);
			G.translate(72, 7, 0);
			if (tab == active) G.colorNoAlpha(0xFF_488EDA);
			BakedFont.CALIBRI.getRenderer().renderString(tab.getName(), 0, 0, false);
			G.color(1, 1, 1, 1);
			BakedFont.CALIBRI_SMALL.getRenderer().renderString(tab.getDescription(), 1, 24, false);
			G.popMatrix();
			G.translate(0, CELLHEIGHT, 0);
		}

		G.popMatrix();

		G.translate(SIDEBARW + 20, 20, 0);
		active.getRender().render(mx * factor - SIDEBARW - 20, my * factor, ticks, mc.displayWidth - SIDEBARW - 20);
	}


	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		active.getRender().mouseDown(mouseX * factor - SIDEBARW - 20, mouseY * factor, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		active.getRender().mouseUp(mouseX * factor - SIDEBARW - 20, mouseY * factor, state);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		active.getRender().mouseDrag(mouseX * factor - SIDEBARW - 20, mouseY * factor, clickedMouseButton, timeSinceLastClick);
	}

}
