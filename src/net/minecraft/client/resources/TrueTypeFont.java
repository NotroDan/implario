package net.minecraft.client.resources;

import net.minecraft.client.renderer.G;
import net.minecraft.logging.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;


/**
 * A TrueType font implementation originally for Slick, edited for Bobjob's Engine
 *
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias4444)
 * @author Kevin Glass (kevglass)
 * @author Peter Korzuszek (genail)
 * @version edited by David Aaron Muhar (bobjob)
 */
public class TrueTypeFont {

	public static final char[] RUSSIAN = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя\u2714".toCharArray();

	public final static int
			ALIGN_LEFT = 0,
			ALIGN_RIGHT = 1,
			ALIGN_CENTER = 2;
	/**
	 * Array that holds necessary information about the font characters
	 */
	private IntObject[] charArray = new IntObject[256];

	/**
	 * Map of user defined font characters (Character <-> IntObject)
	 */
	private Map customChars = new HashMap();

	private boolean antiAlias;

	private int fontSize;

	private int fontHeight = 0;

	/**
	 * Texture used to cache the font 0-255 characters
	 */
	private int fontTextureID;

	private int textureWidth = 512;
	private int textureHeight = 512;

	private Font font;

	private int correctL = 9, correctR = 8;

	private class IntObject {

		/**
		 * Character's width
		 */
		public int width;

		/**
		 * Character's height
		 */
		public int height;

		/**
		 * Character's stored x position
		 */
		public int storedX;

		/**
		 * Character's stored y position
		 */
		public int storedY;

	}


	public TrueTypeFont(Font font, boolean antiAlias, char[] additionalChars) {
		this.font = font;
		this.fontSize = font.getSize() + 3;
		this.antiAlias = antiAlias;

		createSet(additionalChars);

		fontHeight -= 1;
		if (fontHeight <= 0) fontHeight = 1;
	}

	public TrueTypeFont(Font font, boolean antiAlias) {
		this(font, antiAlias, RUSSIAN);
	}

	public void setCorrection(boolean on) {
		if (on) {
			correctL = 2;
			correctR = 1;
		} else {
			correctL = 0;
			correctR = 0;
		}
	}

	private BufferedImage getFontImage(char ch) {
		// Create a temporary image to extract the character's size
		BufferedImage tempfontImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();

		if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(font);
		FontMetrics fontMetrics = g.getFontMetrics();
		int charwidth = fontMetrics.charWidth(ch) + 8;

		if (charwidth <= 0) charwidth = 7;
		int charheight = fontMetrics.getHeight() + 3;
		if (charheight <= 0) charheight = fontSize;

		// Create another image holding the character we are creating
		BufferedImage fontImage = new BufferedImage(charwidth, charheight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if (antiAlias) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		gt.setFont(font);

		gt.setColor(Color.WHITE);
		int charx = 3;
		int chary = 1;
		gt.drawString(String.valueOf(ch), charx, chary
				+ fontMetrics.getAscent());

		return fontImage;

	}

	private void createSet(char[] customCharsArray) {
		// If there are custom chars then I expand the font texture twice
		if (customCharsArray != null && customCharsArray.length > 0) textureWidth *= 2;



		// In any case this should be done in other way. Texture with size 512x512
		// can maintain only 256 characters with resolution of 32x32. The texture
		// size should be calculated dynamicaly by looking at character sizes.
		int customCharsLength = customCharsArray != null ? customCharsArray.length : 0;

		for (int i = 0; i < 256 + customCharsLength; i++) {

			// get 0-255 characters and then custom characters
			char ch = i < 256 ? (char) i : customCharsArray[i - 256];

		}
		try {

			BufferedImage imgTemp = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) imgTemp.getGraphics();

			g.setColor(new Color(0, 0, 0, 1));
			g.fillRect(0, 0, textureWidth, textureHeight);

			int rowHeight = 0;
			int positionX = 0;
			int positionY = 0;


			for (int i = 0; i < 256 + customCharsLength; i++) {

				// get 0-255 characters and then custom characters
				char ch = i < 256 ? (char) i : customCharsArray[i - 256];

				BufferedImage fontImage = getFontImage(ch);

				IntObject newIntObject = new IntObject();

				newIntObject.width = fontImage.getWidth();
				newIntObject.height = fontImage.getHeight();


				if (positionX + newIntObject.width >= textureWidth) {
					positionX = 0;
					positionY += rowHeight;
					rowHeight = 0;
				}

				newIntObject.storedX = positionX;
				newIntObject.storedY = positionY;

				if (newIntObject.height > fontHeight) {
					fontHeight = newIntObject.height;
				}

				if (newIntObject.height > rowHeight) {
					rowHeight = newIntObject.height;
				}

				// Draw it here
				g.drawImage(fontImage, positionX, positionY, null);

				positionX += newIntObject.width;

				if (i < 256) { // standard characters
					charArray[i] = newIntObject;
				} else { // custom characters
					customChars.put(ch, newIntObject);
				}

			}

//			ImageIO.write(imgTemp, "PNG", new File(font.getFamily() + "_" + font.getStyle() + "_" + font.getSize() + ".png"));

			fontTextureID = loadImage(imgTemp);


			//.getTexture(font.toString(), imgTemp);

		} catch (Exception e) {
			Log.MAIN.error("Failed to create font.");
			Log.MAIN.exception(e);
		}
	}

	private void drawQuad(float drawX, float drawY, float drawX2, float drawY2,
						  float srcX, float srcY, float srcX2, float srcY2) {
		float DrawWidth = drawX2 - drawX;
		float DrawHeight = drawY2 - drawY;
		float TextureSrcX = srcX / textureWidth;
		float TextureSrcY = srcY / textureHeight;
		float SrcWidth = srcX2 - srcX;
		float SrcHeight = srcY2 - srcY;
		float RenderWidth = SrcWidth / textureWidth;
		float RenderHeight = SrcHeight / textureHeight;

		GL11.glTexCoord2f(TextureSrcX, TextureSrcY);
		GL11.glVertex3f(drawX, drawY, 0);
		GL11.glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
		GL11.glVertex3f(drawX, drawY + DrawHeight, 0);
		GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
		GL11.glVertex3f(drawX + DrawWidth, drawY + DrawHeight, 0);
		GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
		GL11.glVertex3f(drawX + DrawWidth, drawY, 0);
	}

	public int getStringWidth(String whatchars) {
		int totalwidth = 0;
		for (int i = 0; i < whatchars.length(); i++)
			totalwidth += getIntObject(whatchars.charAt(i)).width;
		return totalwidth;
	}

	private IntObject getIntObject(char c) {
		if (c < 256) return charArray[c];
		IntObject io = (IntObject) customChars.get(c);
		if (io == null) return charArray[2];
		return io;
	}

	public Font getFont() {
		return font;
	}

	public int getHeight() {
		return fontHeight;
	}

	public int drawString(float x, float y, String whatchars) {
		return drawString(x, y, whatchars.toCharArray(), 0, whatchars.length() - 1);
	}

	public int drawString(float x, float y, char[] whatchars, int startIndex, int endIndex) {

		IntObject intObject;
		char charCurrent;


		int totalwidth = 0;
		int i = startIndex;

		G.bindTexture(fontTextureID);
		GL11.glBegin(GL11.GL_QUADS);

		while (i >= startIndex && i <= endIndex) {

			charCurrent = whatchars[i];
			intObject = getIntObject(charCurrent);


			if (intObject != null) {
				renderChar(intObject, totalwidth + x, y);
				totalwidth += intObject.width - correctL;
			}
			i++;
		}
		GL11.glEnd();
		return totalwidth;
	}

	public void glHeader() {
		G.bindTexture(fontTextureID);
		GL11.glBegin(GL11.GL_QUADS);
	}

	public void glFooter() {
		GL11.glEnd();
	}

	public int drawChar(char c, float x, float y) {

		IntObject io = getIntObject(c);
		renderChar(io, x, y);
		return io.width - correctL;
	}

	private void renderChar(IntObject intObject, float x, float y) {
		drawQuad(intObject.width + x, y,
				x,
				(float) intObject.height + y, intObject.storedX + intObject.width,
				intObject.storedY, intObject.storedX,
				intObject.storedY + intObject.height);
	}

	public static int loadImage(BufferedImage bufferedImage) {
		try {
			short width = (short) bufferedImage.getWidth();
			short height = (short) bufferedImage.getHeight();
			int bpp = (byte) bufferedImage.getColorModel().getPixelSize();
			ByteBuffer byteBuffer;
			DataBuffer db = bufferedImage.getData().getDataBuffer();
			if (db instanceof DataBufferInt) {
				int intI[] = ((DataBufferInt) bufferedImage.getData().getDataBuffer()).getData();
				byte newI[] = new byte[intI.length * 4];
				for (int i = 0; i < intI.length; i++) {
					byte b[] = intToByteArray(intI[i]);
					int newIndex = i * 4;

					newI[newIndex] = b[1];
					newI[newIndex + 1] = b[2];
					newI[newIndex + 2] = b[3];
					newI[newIndex + 3] = b[0];
				}

				byteBuffer = ByteBuffer.allocateDirect(
						width * height * (bpp / 8))
						.order(ByteOrder.nativeOrder())
						.put(newI);
			} else {
				byteBuffer = ByteBuffer.allocateDirect(
						width * height * (bpp / 8))
						.order(ByteOrder.nativeOrder())
						.put(((DataBufferByte) bufferedImage.getData().getDataBuffer()).getData());
			}
			byteBuffer.flip();


			int internalFormat = GL11.GL_RGBA8,
					format = GL11.GL_RGBA;
			IntBuffer textureId = BufferUtils.createIntBuffer(1);

			GL11.glGenTextures(textureId);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId.get(0));


			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

			GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);


			GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D,
					internalFormat,
					width,
					height,
					format,
					GL11.GL_UNSIGNED_BYTE,
					byteBuffer);
			return textureId.get(0);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		return -1;
	}

	public static boolean isSupported(String fontname) {
		Font font[] = getFonts();
		for (int i = font.length - 1; i >= 0; i--) {
//			System.out.print(font[i].getName() + "  ");
			if (font[i].getName().equalsIgnoreCase(fontname))
				return true;
		}
		return false;
	}

	public static Font[] getFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	}

	public static byte[] intToByteArray(int value) {
		return new byte[] {
				(byte) (value >>> 24),
				(byte) (value >>> 16),
				(byte) (value >>> 8),
				(byte) value
		};
	}

	public void destroy() {
		IntBuffer scratch = BufferUtils.createIntBuffer(1);
		scratch.put(0, fontTextureID);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDeleteTextures(scratch);
	}

}
