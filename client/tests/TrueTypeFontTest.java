import net.minecraft.client.gui.font.TrueTypeFontRenderer;
import net.minecraft.client.renderer.G;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class TrueTypeFontTest {

	final public static int FRAME_RATE = 60;
	protected static final String TITLE = "Template v1";
	public static int width = 1000, height = 600;
	static boolean running = true;
	static FPSCounter fpsCounter;
	//	static TrueTypeFont trueTypeFont;
	static TrueTypeFontRenderer t;
	static TrueTypeFontRenderer t2;
	static String lastFPS;

	public TrueTypeFontTest() {
		try {
			if (true) throw new Exception();
			initDisplay(false);
			initGL();
			loadResources();
			run();
			cleanup();
		} catch (Exception e) {
			e.printStackTrace();
			Sys.alert(TITLE, "Error 101: " + e.toString());
		}
	}

	public static void main(String args[]) {
		new TrueTypeFontTest();
	}

	private static void initDisplay(boolean fullscreen) throws Exception {

		Display.setTitle(TITLE); // sets aplication name
		Display.setFullscreen(fullscreen);  // create a full screen window if possible

		DisplayMode displayMode = null;
		DisplayMode d[] = Display.getAvailableDisplayModes();
		for (int i = d.length - 1; i >= 0; i--) {
			displayMode = d[i];
			if (d[i].getWidth() == width
					&& d[i].getHeight() == height
					&& d[i].getBitsPerPixel() >= 24 && d[i].getBitsPerPixel() <= 24 + 8
					&& d[i].getFrequency() == FRAME_RATE) {
				break;
			}
		} // Finds a suitable resolution
		Display.setDisplayMode(displayMode); // sets display resoulation
		if (fullscreen) Display.setVSyncEnabled(true); // enable vsync if poosible
		Display.create();
	}

	public static void initGL() {


		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
		GL11.glClearColor(0.1f, 0.1f, 0.1f, 0f); // Black Background
		GL11.glDisable(GL11.GL_DITHER);
		GL11.glDepthFunc(GL11.GL_LESS); // Depth function less or equal
		GL11.glEnable(GL11.GL_NORMALIZE); // calculated normals when scaling
		GL11.glEnable(GL11.GL_CULL_FACE); // prevent render of back surface
		GL11.glEnable(GL11.GL_BLEND); // Enabled blending
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // selects blending method
		GL11.glEnable(GL11.GL_ALPHA_TEST); // allows alpha channels or transperancy
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f); // sets aplha function
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // High quality visuals
		GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST); //  Really Nice Perspective Calculations
		GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
		GL11.glViewport(0, 0, 1000, 600);
		GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
		GL11.glLoadIdentity(); // Reset The Projection Matrix
		GLU.gluPerspective(30, width / (float) height, 1f, 300f);  //Aspect Ratio Of The Window
		GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix
		GL11.glDepthMask(true);                             // Enable Depth Mask


	}

	private static void loadResources() {
		Keyboard.enableRepeatEvents(false);
		// initialise the font

		String font = "Verdana";
		t2 = new TrueTypeFontRenderer(font, 18);
		t = new TrueTypeFontRenderer(font, 22);


		//		String fontName = "Segoe UI";
		//		if (!TrueTypeFont.isSupported(fontName)) fontName = "Arial";
		//		Font font = new Font(fontName, Font.BOLD + Font.ITALIC, 22);
		//		trueTypeFont = new TrueTypeFont(font, true);
		fpsCounter = new FPSCounter();
		fpsCounter.init();
		// render some text to the screen
		//trueTypeFont.render(20.0f, 20.0f, "Slick displaying True Type Fonts", Color.green);

	}

	public static void run() {
		while (running) {
			if (Display.isCloseRequested()) running = false;

			//  Display.sync(FRAME_RATE);
			logic();
			if (Display.isActive() && Display.isVisible()) {
				render();
				Display.update();
			} else {
				Display.update();
			}
		}
	}

	public static void logic() {
		if (fpsCounter != null) {
			if (fpsCounter.update()) lastFPS = "FPS " + fpsCounter.getFPS();
			Display.setTitle(lastFPS);
		}
		int x = Mouse.getX();
		int y = Mouse.getY();

		int wheel = Mouse.getDWheel();
		if (wheel != 0) {
			boolean b = wheel >>> 31 == 1;
			t = new TrueTypeFontRenderer(t.getFontname(), t.getSize() + (b ? 1 : -1));
			System.out.println("switched to size " + t.getSize());
		}

		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				int mouse = Mouse.getEventButton();
				switch (mouse) {
					case 0: {
						System.out.println("" + x + " " + y);

						break;
					}
					case 1: {
						System.out.println("" + x + " " + y);

						break;
					}
					case 2: {
						break;
					}
				}
			}
		}

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				int key = Keyboard.getEventKey();

				switch (key) {
					case Keyboard.KEY_SPACE: {

						break;
					}
					case Keyboard.KEY_RETURN: {

						break;
					}
				}
			}
		}


	}


	public static void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);     // Clear Screen And Depth Buffer


		//2D Render Code
		set2DMode(0, width, 0, height);


		G.color(1, 1, 1, 1);
		String text = "§3§l[Гл. Админ] xtrafrancyz§7: §aНастоящие пираты не боятся красного плавания?";
		//		t2.render(text, 0, t2.getPlainFont().getHeight(), 0, false);
		t.renderString(text, 0, t.getPlainFont().getHeight() * 1.5f, false);
		//		int error = GL11.glGetError();
		//		if (error != 0) System.out.println(error);
		//
		//		trueTypeFont.render(0, trueTypeFont.getHeight() * 10, "I wrote this song about you!\nIsn't that cliche of me, to do?");
		//
		//		trueTypeFont.render(0, trueTypeFont.getHeight() * 6, "But its nothing for you,\n" +
		//						"the band just needed something more to play.\n" +
		//						"So dont blush or hooray,\n");
		//
		//		trueTypeFont.render(0, trueTypeFont.getHeight() * 3,
		//				"at the possible sound of your name.\n" +
		//						"No I wouldnt go that far.\n" +
		//						"No.");

		//End 2D Render Code
		set3DMode();
	}

	public static void cleanup() {
		//		trueTypeFont.destroy();
		Keyboard.destroy();
		Mouse.destroy();
		Display.destroy();
		System.gc();
		try {
			Thread.sleep(1000);
		} catch (Exception ignored) {}

		System.exit(0);
	}

	public static void set2DMode(float x, float width, float y, float height) {
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glMatrixMode(GL11.GL_PROJECTION);                        // Select The Projection Matrix
		GL11.glPushMatrix();                                     // Store The Projection Matrix
		GL11.glLoadIdentity();                                   // Reset The Projection Matrix
		GL11.glOrtho(x, width, y, height, -1, 1);                          // Set Up An Ortho Screen
		GL11.glMatrixMode(GL11.GL_MODELVIEW);                         // Select The Modelview Matrix
		GL11.glPushMatrix();                                     // Store The Modelview Matrix
		GL11.glLoadIdentity();                                   // Reset The Modelview Matrix
	}

	public static void set3DMode() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);                        // Select The Projection Matrix
		GL11.glPopMatrix();                                      // Restore The Old Projection Matrix
		GL11.glMatrixMode(GL11.GL_MODELVIEW);                         // Select The Modelview Matrix
		GL11.glPopMatrix();                                      // Restore The Old Projection Matrix
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

}

class FPSCounter {

	private float FPS = 0, fc = 0;

	private long updateFrequency,
			currentTime,
			elapsedTime,
			lastTime;

	void init() {
		updateFrequency = Sys.getTimerResolution() >> 1;
		currentTime = 0;
		elapsedTime = Sys.getTime();
		lastTime = Sys.getTime();

	}

	boolean update() {
		currentTime = Sys.getTime();

		fc++;

		if ((elapsedTime = currentTime - lastTime) >= updateFrequency) {
			FPS = fc / elapsedTime * updateFrequency * 2;
			fc = 0;
			lastTime = currentTime;
			elapsedTime = 0;
			return true;
		}
		return false;
	}

	float getFPS() {
		return FPS;
	}

}