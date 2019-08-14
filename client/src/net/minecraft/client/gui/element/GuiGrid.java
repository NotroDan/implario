package net.minecraft.client.gui.element;

import lombok.Getter;
import lombok.ToString;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.G;
import net.minecraft.util.VBOHelper;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

@ToString
public abstract class GuiGrid<T> extends GuiScreen {

	@Getter
	private final short w, h, xGap, yGap;
	protected int vao;
	private int maxWidth, maxHeight;
	private int xcount, ycount;
	protected Supplier<T[]> supplier;
	protected T[] gridElems;

	public GuiGrid(int w, int elementHeight, int xGap, int yGap, Supplier<T[]> supplier) {
		this.w = (short) w;
		this.h = (short) elementHeight;
		this.xGap = (short) xGap;
		this.yGap = (short) yGap;
		this.supplier = supplier;
	}

	@Override
	public void initGui() {
		if (this.vao != 0) VBOHelper.clear(vao);
		this.vao = VBOHelper.create(new short[] {
				0, 0, 0, this.h, this.w, this.h, this.w, 0
		}, 2);
		this.gridElems = supplier.get();
		this.maxWidth = getAllowedWidth();
		this.maxHeight = getAllowedHeight();
		this.xcount = maxWidth / w;
		while (xcount * w + (xcount - 1) * xGap > maxWidth) xcount--;
		this.ycount = maxHeight / h;
		while (ycount * h + (ycount - 1) * yGap > maxHeight) ycount--;
	}

	private int getAllowedWidth() {
		return width;
	}

	private int getAllowedHeight() {
		return height;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawElements();
		drawForeground();
		drawOverlay();
	}

	private void drawOverlay() {

	}


	private void drawElements() {
		int x = 0, y = 0;

		glPushMatrix();
		glPushMatrix();
		for (T elem : gridElems) {
			G.disableAlpha();
			G.color(0.8f, 0.4f, 0.4f, 1);
			VBOHelper.draw(vao, GL_QUADS, 0, 4);
			drawElement(elem);
			if (++x > xcount) {
				glPopMatrix();
				G.translate(0, h + yGap, 0);
				glPushMatrix();
				x = 0;
				y++;
			} else {
				glTranslatef(w + xGap, 0, 0);
			}

		}
		glPopMatrix();
		glPopMatrix();


	}

	protected abstract void drawForeground();

	protected abstract void drawElement(T element);

	protected abstract void drawBackground();

}
