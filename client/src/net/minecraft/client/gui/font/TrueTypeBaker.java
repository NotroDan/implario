package net.minecraft.client.gui.font;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TrueTypeBaker {


	private final boolean antiAlias;
	private final Font font;
	private final FontMetrics metrics;
	private final FontRenderContext ctx;// = new FontRenderContext(null, true, true);
	private int height;

	public TrueTypeBaker(Font font, boolean antiAlias) {
		BufferedImage tempfontImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();

		this.font = font;
		this.antiAlias = antiAlias;

		if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(font);
		metrics = g.getFontMetrics(font);
		height = metrics.getHeight() + 3;
		if (height <= 0) height = font.getSize();
		ctx = g.getFontRenderContext();
	}

	public void bake() {

		int maxWidth = 0;
		Glyph[] glyphs = new Glyph[65536];

		for (char c = 0; c < 0xFFFF; c++) {
			if (!font.canDisplay(c)) continue;
			Glyph g = getGlyph(font, c);
			if (g == null) continue;
			if (g.width > maxWidth) maxWidth = g.width;
			glyphs[c] = g;
		}


		try {

			List<BufferedImage> pages = new ArrayList<>();

			BufferedImage buffer = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) buffer.getGraphics();

			g.setColor(new Color(0, 0, 0, 0));
			g.fillRect(0, 0, 1024, 1024);

			int positionX = 0;
			int positionY = 0;

			for (int i = 0; i < glyphs.length; i++) {
				Glyph glyph = glyphs[i];
				if (glyph == null) continue;

				BufferedImage fontImage = getFontImage(glyph);

				if (positionX + glyph.width >= 1024) {
					positionX = 0;
					positionY += height;
				}

				if (positionY + height >= 1024) {
					pages.add(buffer);
					buffer = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
					g = (Graphics2D) buffer.getGraphics();
					positionX = 0;
					positionY = 0;
				}

				glyph.texX = positionX;
				glyph.texY = positionY;

				g.drawImage(fontImage, positionX, positionY, null);

				positionX += glyph.width;

			}

			pages.add(buffer);

			int is = 0;
			for (BufferedImage page : pages) {
				ImageIO.write(page, "PNG", new File(font.getFamily() + "_" + font.getSize() + "_" + font.getStyle() + "_" + is++ + ".png"));
			}

			//			fontTextureID = loadImage(buffer);


			//.getTexture(font.toString(), buffer);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FileOutputStream s = new FileOutputStream("glyphs.bin");

			for (char c = 0; c < 0xFFFF; c++) {
				Glyph g = glyphs[c];
				if (g == null) {
					s.write(new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
				} else {
					s.write(g.zip());
				}
			}
			s.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private BufferedImage getFontImage(Glyph glyph) {

		BufferedImage fontImage = new BufferedImage(glyph.getWidth(), height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if (antiAlias) gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gt.setFont(font);

		gt.setColor(Color.WHITE);
		int charx = 3;
		int chary = 1;
		gt.drawString(String.valueOf(glyph.c), charx, chary + metrics.getAscent());

		return fontImage;

	}


	private Glyph getGlyph(Font font, char c) {

		//		GlyphVector glyphVector = font.createGlyphVector(ctx, new char[] {c});
		//		System.out.println(glyphVector);
		//		glyphVector.getVisualBounds().setFrame(0, 0, 400, 400);
		//		int width = glyphVector.getPixelBounds(ctx, 1, 1).width;


		TextLayout layout = new TextLayout(String.valueOf(c), font, ctx);
		Rectangle2D vBounds = layout.getBounds();
		Rectangle2D lBounds = new Rectangle2D.Float(0f,
				-layout.getAscent() - layout.getLeading(),
				layout.getAdvance(),
				layout.getAscent() + layout.getDescent() + 2f * layout.getLeading());
		Rectangle2D bounds = lBounds.createUnion(vBounds);
		int width = (int) Math.ceil(bounds.getWidth());
		//		(width == 0 ? System.err : System.out).println("char " + (int) c + " '" + c + "': width = " + width);


		if (width == 0) return null;


		//		int charwidth = metrics.charWidth(c) + 8;
		//		if (charwidth <= 0) charwidth = 7;


		return new Glyph(c, (int) Math.ceil(width));

	}


	//	private void createSet(char[] customCharsArray) {
	//
	//		int textureWidth = 512, textureHeight = 512;
	//
	//
	//		// If there are custom chars then I expand the font texture twice
	//		if (customCharsArray != null && customCharsArray.length > 0) textureWidth *= 2;
	//
	//		// In any case this should be done in other way. Texture with size 512x512
	//		// can maintain only 256 characters with resolution of 32x32. The texture
	//		// size should be calculated dynamicaly by looking at character sizes.
	//
	//		try {
	//
	//			BufferedImage imgTemp = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
	//			Graphics2D g = (Graphics2D) imgTemp.getGraphics();
	//
	//			g.setColor(new Color(0, 0, 0, 1));
	//			g.fillRect(0, 0, textureWidth, textureHeight);
	//
	//			int rowHeight = 0;
	//			int positionX = 0;
	//			int positionY = 0;
	//
	//			int customCharsLength = customCharsArray != null ? customCharsArray.length : 0;
	//
	//			for (int i = 0; i < 256 + customCharsLength; i++) {
	//
	//				// get 0-255 characters and then custom characters
	//				char ch = i < 256 ? (char) i : customCharsArray[i - 256];
	//
	//				BufferedImage fontImage = getFontImage(ch);
	//
	//				IntObject newIntObject = new TrueTypeFont.IntObject();
	//
	//				newIntObject.width = fontImage.getWidth();
	//				newIntObject.height = fontImage.getHeight();
	//
	//
	//				if (positionX + newIntObject.width >= textureWidth) {
	//					positionX = 0;
	//					positionY += rowHeight;
	//					rowHeight = 0;
	//				}
	//
	//				newIntObject.storedX = positionX;
	//				newIntObject.storedY = positionY;
	//
	//				if (newIntObject.height > fontHeight) {
	//					fontHeight = newIntObject.height;
	//				}
	//
	//				if (newIntObject.height > rowHeight) {
	//					rowHeight = newIntObject.height;
	//				}
	//
	//				// Draw it here
	//				g.drawImage(fontImage, positionX, positionY, null);
	//
	//				positionX += newIntObject.width;
	//
	//				if (i < 256) { // standard characters
	//					charArray[i] = newIntObject;
	//				} else { // custom characters
	//					customChars.put(ch, newIntObject);
	//				}
	//
	//			}
	//
	//			//			ImageIO.write(imgTemp, "PNG", new File(font.getFamily() + "_" + font.getStyle() + "_" + font.getSize() + ".png"));
	//
	//			fontTextureID = loadImage(imgTemp);
	//
	//
	//			//.getTexture(font.toString(), imgTemp);
	//
	//		} catch (Exception e) {
	//			Log.MAIN.error("Failed to create font.");
	//			Log.MAIN.exception(e);
	//		}
	//	}


	private static class Glyph {

		private final int width;
		private final char c;
		public float texX, texY;

		private Glyph(char c, int width) {
			this.c = c;
			this.width = width;
		}

		public static Glyph unzip(char c, byte[] array) {
			ByteBuffer b = ByteBuffer.wrap(array);
			float texX = b.getFloat();
			float texY = b.getFloat();
			int width = b.getInt();
			Glyph g = new Glyph(c, width);
			g.texX = texX;
			g.texY = texY;
			return g;
		}

		public char getChar() {
			return c;
		}

		public int getWidth() {
			return width;
		}

		@Override
		public String toString() {
			return "G[width=" + width + "]";
		}

		public byte[] zip() {

			ByteBuffer bb = ByteBuffer.allocate(12);
			bb.putFloat(texX);
			bb.putFloat(texY);
			bb.putInt(width);
			return bb.array();

		}

	}

}
