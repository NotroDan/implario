/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/** The Gears demo implemented using GLFW. */
public class Gears {
	
	private Callback debugProc;
	
	private long window;
	
	private int xpos;
	private int ypos;
	private int width;
	private int height;
	
	public static void main(String[] args) {
		new Gears().run();
	}
	
	private void run() {
		try {
			init();
			initGLState();
			
			loop();
		} finally {
			try {
				destroy();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	private static void framebufferSizeChanged(long window, int width, int height) {
		if (width == 0 || height == 0) {
			return;
		}
		
		float f = height / (float)width;
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glFrustum(-1.0f, 1.0f, -f, f, 5.0f, 100.0f);
		glMatrixMode(GL_MODELVIEW);
		
		glViewport(0, 0, width, height);
	}
	
	private void init() {
		GLFWErrorCallback.createPrint().set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize glfw");
		}
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
		if (Platform.get() == Platform.MACOSX) {
			glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
		}
		
		int WIDTH  = 300;
		int HEIGHT = 300;
		
		long window = glfwCreateWindow(WIDTH, HEIGHT, "GLFW Gears Demo", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		/*
        // This code did the equivalent of glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE) before GLFW 3.3
		if ( Platform.get() == Platform.MACOSX ) {
			long cocoaWindow = glfwGetCocoaWindow(window);
			long objc_msgSend = ObjCRuntime.getLibrary().getFunctionAddress("objc_msgSend");
			long contentView = invokePPP(cocoaWindow, sel_getUid("contentView"), objc_msgSend);
			invokePPV(contentView, sel_getUid("setWantsBestResolutionOpenGLSurface:"), false, objc_msgSend);
			boolean bool = invokePPZ(contentView, sel_getUid("wantsBestResolutionOpenGLSurface"), objc_msgSend);
			System.out.println("wantsBestResolutionOpenGLSurface = " + bool);
		}
		*/
		
		glfwSetWindowSizeLimits(window, WIDTH, HEIGHT, GLFW_DONT_CARE, GLFW_DONT_CARE);
		//glfwSetWindowAspectRatio(window, 1, 1);
		
		long monitor = glfwGetPrimaryMonitor();
		
		GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(monitor));
		// Center window
		glfwSetWindowPos(
				window,
				(vidmode.width() - WIDTH) / 2,
				(vidmode.height() - HEIGHT) / 2
		);
		
		glfwSetKeyCallback(window, (windowHnd, key, scancode, action, mods) -> {
			if (action != GLFW_RELEASE) {
				return;
			}
			
			switch (key) {
				case GLFW_KEY_ESCAPE:
					glfwSetWindowShouldClose(windowHnd, true);
					break;
				case GLFW_KEY_A:
					glfwRequestWindowAttention(windowHnd);
					break;
				case GLFW_KEY_F:
					if (glfwGetWindowMonitor(windowHnd) == NULL) {
						try (MemoryStack s = stackPush()) {
							IntBuffer a = s.ints(0);
							IntBuffer b = s.ints(0);
							
							glfwGetWindowPos(windowHnd, a, b);
							xpos = a.get(0);
							ypos = b.get(0);
							
							glfwGetWindowSize(windowHnd, a, b);
							width = a.get(0);
							height = b.get(0);
						}
						glfwSetWindowMonitor(windowHnd, monitor, 0, 0, vidmode.width(), vidmode.height(), vidmode.refreshRate());
						glfwSwapInterval(1);
					}
					break;
				case GLFW_KEY_G:
					glfwSetInputMode(windowHnd, GLFW_CURSOR, glfwGetInputMode(windowHnd, GLFW_CURSOR) == GLFW_CURSOR_NORMAL
							? GLFW_CURSOR_DISABLED
							: GLFW_CURSOR_NORMAL
					);
					break;
				case GLFW_KEY_O:
					glfwSetWindowOpacity(window, glfwGetWindowOpacity(window) == 1.0f ? 0.5f : 1.0f);
					break;
				case GLFW_KEY_R:
					glfwSetWindowAttrib(windowHnd, GLFW_RESIZABLE, 1 - glfwGetWindowAttrib(windowHnd, GLFW_RESIZABLE));
					break;
				case GLFW_KEY_U:
					glfwSetWindowAttrib(windowHnd, GLFW_DECORATED, 1 - glfwGetWindowAttrib(windowHnd, GLFW_DECORATED));
					break;
				case GLFW_KEY_W:
					if (glfwGetWindowMonitor(windowHnd) != NULL) {
						glfwSetWindowMonitor(windowHnd, NULL, xpos, ypos, width, height, 0);
					}
					break;
			}
		});
		
		glfwSetFramebufferSizeCallback(window, Gears::framebufferSizeChanged);
		
		glfwSetWindowRefreshCallback(window, windowHnd -> {
			renderLoop();
			glfwSwapBuffers(windowHnd);
		});
		
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		debugProc = GLUtil.setupDebugMessageCallback();
		
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		glfwInvoke(window, null, Gears::framebufferSizeChanged);
		
		this.window = window;
	}
	
	
	/**
	 * Invokes the specified callbacks using the current window and framebuffer sizes of the specified GLFW window.
	 *
	 * @param window            the GLFW window
	 * @param windowSizeCB      the window size callback, may be null
	 * @param framebufferSizeCB the framebuffer size callback, may be null
	 */
	public static void glfwInvoke(
			long window,
			@Nullable GLFWWindowSizeCallbackI windowSizeCB,
			@Nullable GLFWFramebufferSizeCallbackI framebufferSizeCB
	) {
		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			
			if (windowSizeCB != null) {
				glfwGetWindowSize(window, w, h);
				windowSizeCB.invoke(window, w.get(0), h.get(0));
			}
			
			if (framebufferSizeCB != null) {
				glfwGetFramebufferSize(window, w, h);
				framebufferSizeCB.invoke(window, w.get(0), h.get(0));
			}
		}
		
	}
	
	
	private void loop() {
		long lastUpdate = System.currentTimeMillis();
		
		int frames = 0;
		while (!glfwWindowShouldClose(window)) {
			glfwPollEvents();
			
			renderLoop();
			
			glfwSwapBuffers(window);
			
			frames++;
			
			long time = System.currentTimeMillis();
			
			int UPDATE_EVERY = 5; // seconds
			if (UPDATE_EVERY * 1000L <= time - lastUpdate) {
				lastUpdate = time;
				
				System.out.printf("%d frames in %d seconds = %.2f fps\n", frames, UPDATE_EVERY, (frames / (float)UPDATE_EVERY));
				frames = 0;
			}
		}
	}
	
	private void destroy() {
		GL.setCapabilities(null);
		
		if (debugProc != null) {
			debugProc.free();
		}
		
		if (window != NULL) {
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		}
		
		glfwTerminate();
		Objects.requireNonNull(glfwSetErrorCallback(null)).free();
	}
	
	private static final float
			view_rotx = 40.0f,
			view_roty = 30.0f;
	
	private float view_rotz;
	
	private int
			gear1,
			gear2,
			gear3;
	
	private float angle;
	
	public void initGLState() {
		System.err.println("GL_VENDOR: " + glGetString(GL_VENDOR));
		System.err.println("GL_RENDERER: " + glGetString(GL_RENDERER));
		System.err.println("GL_VERSION: " + glGetString(GL_VERSION));
		
		try (MemoryStack s = stackPush()) {
			// setup ogl
			glEnable(GL_CULL_FACE);
			glEnable(GL_LIGHTING);
			glEnable(GL_LIGHT0);
			glEnable(GL_DEPTH_TEST);
			
			// make the gears
			gear1 = glGenLists(1);
			glNewList(gear1, GL_COMPILE);
			glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, s.floats(0.8f, 0.1f, 0.0f, 1));
			gear(1.0f, 4.0f, 1.0f, 20, 1f);
			glEndList();
			
			gear2 = glGenLists(1);
			glNewList(gear2, GL_COMPILE);
			glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, s.floats(0.0f, 0.8f, 0.2f, 1));
			gear(0.5f, 2.0f, 2.0f, 10, 0.7f);
			glEndList();
			
			gear3 = glGenLists(1);
			glNewList(gear3, GL_COMPILE);
			glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, s.floats(0.2f, 0.2f, 1.0f, 1));
			gear(1.3f, 2.0f, 0.5f, 10, 0.7f);
			glEndList();
		}
		
		glEnable(GL_NORMALIZE);
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslatef(0.0f, 0.0f, -40.0f);
	}
	
	public void renderLoop() {
		angle += 2.0f;
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glPushMatrix();
		glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
		glRotatef(view_roty, 0.0f, 1.0f, 0.0f);
		glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);
		
		try (MemoryStack s = stackPush()) {
			glLightfv(GL_LIGHT0, GL_POSITION, s.floats(5.0f, 5.0f, 10.0f, 0.0f));
		}
		
		glPushMatrix();
		glTranslatef(-3.0f, -2.0f, 0.0f);
		glRotatef(angle, 0.0f, 0.0f, 1.0f);
		glCallList(gear1);
		glPopMatrix();
		
		glPushMatrix();
		glTranslatef(3.1f, -2.0f, 0.0f);
		glRotatef(-2.0f * angle - 9.0f, 0.0f, 0.0f, 1.0f);
		glCallList(gear2);
		glPopMatrix();
		
		glPushMatrix();
		glTranslatef(-3.1f, 4.2f, 0.0f);
		glRotatef(-2.0f * angle - 25.0f, 0.0f, 0.0f, 1.0f);
		glCallList(gear3);
		glPopMatrix();
		
		glPopMatrix();
		
	}
	
	/**
	 * Draw a gear wheel.  You'll probably want to call this function when
	 * building a display list since we do a lot of trig here.
	 *
	 * @param inner_radius radius of hole at center
	 * @param outer_radius radius at center of teeth
	 * @param width        width of gear
	 * @param teeth        number of teeth
	 * @param tooth_depth  depth of tooth
	 */
	private static void gear(float inner_radius, float outer_radius, float width, int teeth, float tooth_depth) {
		float angle, da;
		
		float r0 = inner_radius;
		float r1 = outer_radius - tooth_depth / 2.0f;
		float r2 = outer_radius + tooth_depth / 2.0f;
		
		da = 2.0f * (float)Math.PI / teeth / 4.0f;
		
		glShadeModel(GL_FLAT);
		
		glNormal3f(0.0f, 0.0f, 1.0f);
		
		int i;
		
		/* draw front face */
		glBegin(GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float)Math.PI / teeth;
			
			glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
			glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
			if (i < teeth) {
				glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
				glVertex3f(r1 * (float)Math.cos(angle + 3.0f * da), r1 * (float)Math.sin(angle + 3.0f * da),
						width * 0.5f);
			}
		}
		glEnd();
		
		/* draw front sides of teeth */
		glBegin(GL_QUADS);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float)Math.PI / teeth;
			
			glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
			glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), width * 0.5f);
			glVertex3f(r2 * (float)Math.cos(angle + 2.0f * da), r2 * (float)Math.sin(angle + 2.0f * da), width * 0.5f);
			glVertex3f(r1 * (float)Math.cos(angle + 3.0f * da), r1 * (float)Math.sin(angle + 3.0f * da), width * 0.5f);
		}
		glEnd();
		
		/* draw back face */
		glBegin(GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float)Math.PI / teeth;
			
			glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
			glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
			glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
			glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
		}
		glEnd();
		
		/* draw back sides of teeth */
		glBegin(GL_QUADS);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float)Math.PI / teeth;
			
			glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
			glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), -width * 0.5f);
			glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), -width * 0.5f);
			glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
		}
		glEnd();
		
		/* draw outward faces of teeth */
		glBegin(GL_QUAD_STRIP);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float)Math.PI / teeth;
			
			glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
			glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
			
			float u = r2 * (float)Math.cos(angle + da) - r1 * (float)Math.cos(angle);
			float v = r2 * (float)Math.sin(angle + da) - r1 * (float)Math.sin(angle);
			
			float len = (float)Math.sqrt(u * u + v * v);
			
			u /= len;
			v /= len;
			
			glNormal3f(v, -u, 0.0f);
			glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), width * 0.5f);
			glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), -width * 0.5f);
			glNormal3f((float)Math.cos(angle), (float)Math.sin(angle), 0.0f);
			glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), width * 0.5f);
			glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), -width * 0.5f);
			
			u = r1 * (float)Math.cos(angle + 3 * da) - r2 * (float)Math.cos(angle + 2 * da);
			v = r1 * (float)Math.sin(angle + 3 * da) - r2 * (float)Math.sin(angle + 2 * da);
			
			glNormal3f(v, -u, 0.0f);
			glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), width * 0.5f);
			glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
			glNormal3f((float)Math.cos(angle), (float)Math.sin(angle), 0.0f);
		}
		glVertex3f(r1 * (float)Math.cos(0), r1 * (float)Math.sin(0), width * 0.5f);
		glVertex3f(r1 * (float)Math.cos(0), r1 * (float)Math.sin(0), -width * 0.5f);
		glEnd();
		
		glShadeModel(GL_SMOOTH);
		
		/* draw inside radius cylinder */
		glBegin(GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float)Math.PI / teeth;
			
			glNormal3f(-(float)Math.cos(angle), -(float)Math.sin(angle), 0.0f);
			glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
			glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
		}
		glEnd();
	}
	
}
