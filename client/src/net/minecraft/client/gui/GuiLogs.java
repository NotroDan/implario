package net.minecraft.client.gui;

import net.minecraft.logging.Log;
import net.minecraft.logging.LogReader;
import net.minecraft.Utils;
import net.minecraft.client.MC;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class GuiLogs extends GuiScreen {

	LogReader reader = new LogReader(Log.CHAT);
	float scroll;
	int scrollBy;
	long scrollTime;

	public GuiLogs() {
		super();
	}

	@Override
	public void initGui() {
	}


	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		//		drawRect();
		super.drawScreen(mouseX, mouseY, partialTicks);
		MC.FR.drawString("", 0, 0, 0);
		List<LogReader.Line> lines = reader.getLines();
		GlStateManager.pushMatrix();
		GlStateManager.translate(10, 0, 0);
		for (int i = 0; i < lines.size(); i++) {
			LogReader.Line line = lines.get(i);
			Utils.glColorNoAlpha(line.getLevel().getColor());
			G.translate(0, 10, 0);
			GlStateManager.pushMatrix();
			for (char c : line.getTime()) {
				float f = MC.FR.renderChar(c, false, false);
				GlStateManager.translate(0.5, 0, 0);
				MC.FR.renderChar(c, false, false);
				GlStateManager.translate(f, 0, 0);
			}
			if (line.getTime().length != 0) G.translate(5, 0, 0);
			for (char c : line.getMessage()) {
				float f = MC.FR.renderChar(c, false, false);
				GlStateManager.translate(f, 0, 0);
			}
			GlStateManager.popMatrix();
			if (i * 10 > height) break;

		}
		GlStateManager.popMatrix();
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int d = Mouse.getDWheel();
		if (d == 0) return;
		//		System.out.println(d);
		if (d >> 31 != scrollBy >> 31) {
			scrollBy = d;
			scrollBy += d / 10;
		}
		scrollTime = System.currentTimeMillis() + 500;

	}

}
