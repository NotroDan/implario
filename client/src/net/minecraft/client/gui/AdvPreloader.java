package net.minecraft.client.gui;

import net.minecraft.Utils;
import net.minecraft.client.MC;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.gui.font.TrueTypeFontRenderer;
import net.minecraft.util.VBOHelper;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;

public class AdvPreloader extends Preloader {

	private final int verts = 28;
	private final int height, width;
	private int vaoId;
	private long start;

	public AdvPreloader(ScaledResolution res) {
		super(res);
		this.width = mc.displayWidth;
		this.height = mc.displayHeight;
		start = System.currentTimeMillis();
	}

	@Override
	public void header() {
		glMatrixMode(GL11.GL_PROJECTION);                        // Select The Projection Matrix
		glOrtho(0.0D, width, height, 0.0D, -100, 100);
		this.exitOnGLError("Error in creating projection");
		glEnable(GL11.GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_SRC_COLOR); // selects blending method
		glEnable(GL_LINE_SMOOTH);
		glLineWidth(2);

		glClearColor(0.1f, 0.1f, 0.1f, 0f);

		// Map the internal OpenGL coordinate system to the entire screen
		glViewport(0, 0, width, height);
		this.exitOnGLError("Error in header");
		vaoId = VBOHelper.circle(verts);
		this.exitOnGLError("Error in circle generation");
	}

	@Override
	public void render() {

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		long time = System.currentTimeMillis();
		double part = (double) state / states.length;

		glPushMatrix(); {
			glTranslatef(width / 2, height / 2, 0);
			glPushMatrix(); {
				glDisable(GL_TEXTURE_2D);
				glColor4f(1, 1, 1, 1);
				int scale = 1000;
				glScaled(scale, scale, scale);


				glRotatef(90, 0, 0, 1);
				glLineWidth(8);
				glColor4f(0.2f, 0.2f, 0.2f, 1f);
				VBOHelper.draw(vaoId, GL_LINES, 0, verts * 4 + 2);
				glLineWidth(6);
				glRotatef(-360f / verts * (float) (int) (time % 1000 / 1000f * verts), 0, 0, 1);
				int v = (int) ((double) (verts * 2 + 1) * part);
				Utils.glColorNoAlpha(0x3772FF); // Синий
//				Utils.glColorNoAlpha(0xff8c00); // Оранжевый
//				Utils.glColorNoAlpha(0xEF709D); // Розовый
				for (int i = 0; i < 3; i++) {
					int from = (verts * 2 + 1) / 3 * i * 2;
					int size = v * 2 - from;
					VBOHelper.draw(vaoId, GL_LINES, from, Math.min(Math.max(size, 0), (verts * 4 + 2) / 3 * (i + 1)));
				}
			} glPopMatrix();
			glPushMatrix(); {
				glEnable(GL_TEXTURE_2D);
				glColor4f(0.9f, 0.9f, 0.9f, 1);
				BakedFont.SEGOE_BIG.getRenderer().renderString((int) (part * 100) + "%", -65, -53, false);
				String elapsed = time - start + "ms.";
				glColor4f(0.4f, 0.4f, 0.4f, 1);
				BakedFont.VERDANA.getRenderer().renderString(elapsed, -60, 30, false);
				TrueTypeFontRenderer renderer = BakedFont.VERDANA.getRenderer();
				renderer.renderString(states[state] + "...", renderer.getStringWidth(states[state] + "...") / -4 - 5, height / 2 - 40, false);
			} glPopMatrix();
		} glPopMatrix();

		MC.frame();
		this.exitOnGLError("Error while preloader rendering");
	}

	public void exitOnGLError(String errorMessage) {
		int errorValue = GL11.glGetError();

		if (errorValue != GL11.GL_NO_ERROR) {
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);

			if (Display.isCreated()) Display.destroy();
			System.exit(-1);
		}
	}

	@Override
	public void dissolve() {
		super.dissolve();
		glEnable(GL_TEXTURE_2D);
	}

}
