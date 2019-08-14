import net.minecraft.Utils;
import net.minecraft.client.gui.font.BakedFont;
import net.minecraft.client.gui.font.TrueTypeFontRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.util.VBOHelper;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;

public class VBOTest {
	// Entry point for the application
	public static void main(String[] args) {
		new VBOTest();
	}

	// Setup variables
	private final String WINDOW_TITLE = "vbo ето круто я же всётаки наруто";
	private final int WIDTH = 640;
	private final int HEIGHT = 640;
	// Quad variables
	private int vaoId = 0;

	public VBOTest() {
		// Initialize OpenGL (Display)
		this.setupOpenGL();

		this.createCircle();

		while (!Display.isCloseRequested()) {
			// Do a single loop (logic/render)
			this.loopCycle();

			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}

		Display.destroy();
	}

	public void setupOpenGL() {
		// Setup an OpenGL context with API version 3.2
		try {
			PixelFormat pixelFormat = new PixelFormat();

			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle(WINDOW_TITLE);
			Display.setResizable(true);
			Display.create(pixelFormat);


			glMatrixMode(GL11.GL_PROJECTION);                        // Select The Projection Matrix
			glOrtho(0.0D, WIDTH, HEIGHT, 0.0D, -100, 100);//1000.0D, 3000.0D);
//			glDisable(GL_LIGHTING);
//			glEnable(GL_TEXTURE_2D);




			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL_SRC_ALPHA, GL_SRC_COLOR); // selects blending method
			glEnable(GL_LINE_SMOOTH);
			glLineWidth(2);
//			GL11.glShadeModel(GL_SMOOTH);
//			glEnable( GL_POLYGON_SMOOTH );
//			glHint( GL_POLYGON_SMOOTH_HINT, GL_FASTEST );

			GL11.glViewport(0, 0, WIDTH, HEIGHT);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Setup an XNA like background color
		GL11.glClearColor(0.1f, 0.1f, 0.1f, 0f);

		// Map the internal OpenGL coordinate system to the entire screen
		GL11.glViewport(0, 0, WIDTH, HEIGHT);

		this.exitOnGLError("Error in setupOpenGL");
	}

	int verts = 32;
	public void createCircle() {

		double radius1 = 0.2f, radius2 = 0.16f;

		double[] array = new double[verts * 8 + 4];

		int a = 0;
		for (double i = 0; i <= 2 * PI; i += PI / (double) verts) {
			System.out.println("a=" + a + ", i=" + i + " " + PI);
			array[a] = cos(i) * radius2;
			array[a + 1] = -sin(i) * radius2;
			array[a + 2] = cos(i) * radius1;
			array[a + 3] = -sin(i) * radius1;
			a += 4;
		}
		int l = array.length;
		array[l - 4] = radius2;
		array[l - 3] = 0;
		array[l - 2] = radius1;
		array[l - 1] = 0;
		System.out.println(a);

		this.vaoId = VBOHelper.create(array, 2);
		this.exitOnGLError("Error in setupQuad");
	}

	public void loopCycle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		glPushMatrix();
		glTranslatef(WIDTH / 2, HEIGHT / 2, 0);
		glPushMatrix();
		glColor4f(1, 1, 1, 1);
		int scale = 1000;
		glScaled(scale, scale, scale);


		glRotatef(90, 0, 0, 1);
		glLineWidth(8);
		glColor4f(0.2f, 0.2f, 0.2f, 1f);
		VBOHelper.draw(vaoId, GL_LINES, 0, verts * 4 + 2);
		glLineWidth(6);
		glScaled(0.9f, 0.9f, 0.9f);
		long time = System.currentTimeMillis();
		double part = ((double) (time % 5000) / 5000);
		int v = (int) ((double) (verts * 2 + 1) * part);
		for (int i = 0; i < 3; i++) {
			if (i == 0) Utils.glColorNoAlpha(0xEF709D);
			if (i == 2) Utils.glColorNoAlpha(0x3772FF);
			int from = (verts * 2 + 1) / 3 * i * 2;
			int size = v * 2 - from;
			VBOHelper.draw(vaoId, GL_LINES, from, Math.min(Math.max(size, 0), (verts * 4 + 2) / 3 * (i + 1)));
		}

		glPopMatrix();
		glPushMatrix();
		glEnable(GL_TEXTURE_2D);
		glColor4f(0.9f, 0.9f, 0.9f, 1);
		BakedFont.SEGOE_BIG.getRenderer().renderString((int) (part * 100) + "%", -60, -53, false);
		glDisable(GL_TEXTURE_2D);
		glPopMatrix();
		glPopMatrix();
		this.exitOnGLError("Error in loopCycle");
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
}
