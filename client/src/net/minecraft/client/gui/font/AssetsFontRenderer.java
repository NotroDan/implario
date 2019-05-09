package net.minecraft.client.gui.font;

import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.CustomColors;
import optifine.FontUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static net.minecraft.client.Minecraft.getMinecraft;

public class AssetsFontRenderer implements IResourceManagerReloadListener, IFontRenderer {

	private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
	private final String allChars = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000" +
			"\u0000\u0000\u0000" +
			"\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb" +
			"\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba" +
			"\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c" +
			"\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0" +
			"\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";

	// Массив с ширинами всех символов из default.png
	private float[] charWidth = new float[256];

	// RNG для обфусцированного текста
	public Random fontRandom = new Random();

	// Массив с ширинами всех глифов в папке /font
	private byte[] glyphWidth = new byte[65536];

	private ResourceLocation locationFontTexture;
	public ResourceLocation locationFontTextureBase;
	private final TextureManager renderEngine;

	// Текущие координаты, на которых будет нарисован следующий символ
	private float posX;
	private float posY;

	// Использовать шрифты Unicode вместо default.png
	private boolean unicodeFlag;

	// Текущие цвета
	private float red;
	private float blue;
	private float green;
	private float alpha;
	private int textColor;

	// Активные стили
	private boolean randomStyle;
	private boolean boldStyle;
	private boolean italicStyle;
	private boolean underlineStyle;
	private boolean strikethroughStyle;

	public boolean enabled = true;
	public float offsetBold = 1.0F;

	public AssetsFontRenderer(ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
		this.locationFontTextureBase = location;
		this.locationFontTexture = location;
		this.renderEngine = textureManagerIn;
		this.unicodeFlag = unicode;
		this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
		this.bindTexture(this.locationFontTexture);


		this.readGlyphSizes();
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

		for (int i = 0; i < unicodePageLocations.length; ++i) {
			unicodePageLocations[i] = null;
		}

		this.readFontTexture();
		this.readGlyphSizes();
	}

	private void readFontTexture() {
		BufferedImage bufferedimage;

		try {
			bufferedimage = TextureUtil.readBufferedImage(this.getResourceInputStream(this.locationFontTexture));
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		}

		Properties properties = FontUtils.readFontProperties(this.locationFontTexture);
		int i = bufferedimage.getWidth();
		int j = bufferedimage.getHeight();
		int k = i / 16;
		int l = j / 16;
		float f = (float) i / 128.0F;
		float f1 = Config.limit(f, 1.0F, 2.0F);
		this.offsetBold = 1.0F / f1;

		float f2 = FontUtils.readFloat(properties, "offsetBold", -1.0F);
		if (f2 >= 0.0F) this.offsetBold = f2;

		int[] aint = new int[i * j];
		bufferedimage.getRGB(0, 0, i, j, aint, 0, i);

		for (int i1 = 0; i1 < 256; ++i1) {
			int j1 = i1 % 16;
			int k1 = i1 / 16;
			int l1;

			for (l1 = k - 1; l1 >= 0; --l1) {
				int i2 = j1 * k + l1;
				boolean flag = true;

				for (int j2 = 0; j2 < l && flag; ++j2) {
					int k2 = (k1 * l + j2) * i;
					int l2 = aint[i2 + k2];
					int i3 = l2 >> 24 & 255;

					if (i3 > 16) {
						flag = false;
					}
				}

				if (!flag) {
					break;
				}
			}

			if (i1 == 32) {
				if (k <= 8) {
					l1 = (int) (2.0F * f);
				} else {
					l1 = (int) (1.5F * f);
				}
			}

			this.charWidth[i1] = (float) (l1 + 1) / f + 1.0F;
		}

		FontUtils.readCustomCharWidths(properties, this.charWidth);
	}

	private void readGlyphSizes() {
		InputStream inputstream = null;

		try {
			inputstream = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
			inputstream.read(this.glyphWidth);
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		} finally {
			IOUtils.closeQuietly(inputstream);
		}
	}


	public float renderChar(char c, boolean bold, boolean italic) {
		if (c == 32) return this.unicodeFlag ? 4.0F : this.charWidth[c];


		int i = allChars.indexOf(c);
		boolean unicode = i == -1 || unicodeFlag;
		float f = unicode ? renderUnicodeChar(c, italic) : renderDefaultChar(i, italic);
		float w = 0;
		if (bold) {
			float offsetBold = unicode ? 0.5F : this.offsetBold;
			this.posX += offsetBold;
			renderChar(c, false, this.italicStyle);
			this.posX -= offsetBold;
			w += offsetBold;
		}
		return f + w;
	}

	@Override
	public int getFontHeight() {
		return 9;
	}

	/**
	 * Render a single character with the default.png font at current (posX,posY) location...
	 */
	private float renderDefaultChar(int p_78266_1_, boolean p_78266_2_) {
		int i = p_78266_1_ % 16 * 8;
		int j = p_78266_1_ / 16 * 8;
		int k = p_78266_2_ ? 1 : 0;
		this.bindTexture(this.locationFontTexture);
		float f = this.charWidth[p_78266_1_];
		float f1 = 7.99F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f((float) i / 128.0F, (float) j / 128.0F);
		GL11.glVertex3f(this.posX + (float) k, this.posY, 0.0F);
		GL11.glTexCoord2f((float) i / 128.0F, ((float) j + 7.99F) / 128.0F);
		GL11.glVertex3f(this.posX - (float) k, this.posY + 7.99F, 0.0F);
		GL11.glTexCoord2f(((float) i + f1 - 1.0F) / 128.0F, (float) j / 128.0F);
		GL11.glVertex3f(this.posX + f1 - 1.0F + (float) k, this.posY, 0.0F);
		GL11.glTexCoord2f(((float) i + f1 - 1.0F) / 128.0F, ((float) j + 7.99F) / 128.0F);
		GL11.glVertex3f(this.posX + f1 - 1.0F - (float) k, this.posY + 7.99F, 0.0F);
		GL11.glEnd();
		return f;
	}

	private ResourceLocation getUnicodePageLocation(int number) {
		if (unicodePageLocations[number] == null) {
			unicodePageLocations[number] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", number));
			unicodePageLocations[number] = FontUtils.getHdFontLocation(unicodePageLocations[number]);
		}

		return unicodePageLocations[number];
	}

	/**
	 * Load one of the /font/glyph_XX.png into a new GL texture and store the texture ID in glyphTextureName array.
	 */
	private void loadGlyphTexture(int page) {
		ResourceLocation unicodePageLocation = this.getUnicodePageLocation(page);
		this.bindTexture(unicodePageLocation);
	}

	/**
	 * Render a single Unicode character at current (posX,posY) location using one of the /font/glyph_XX.png files...
	 */
	public float renderUnicodeChar(char c, boolean italic) {
		if (this.glyphWidth[c] == 0) return 0.0F;

		int i = c / 256;
		this.loadGlyphTexture(i);
		int rightOffset = this.glyphWidth[c] >>> 4 & 0b1111;
		int w = (this.glyphWidth[c] & 15) + 1;
		float width = (float) w;
		float tx = (float) (c % 16 * 16) + rightOffset;
		float ty = (float) (c & 0xf0);
		float tw = width - rightOffset;
//		System.out.print(c + "-" + tx + "x" + ty + "  ");

		// Сдвиг верхней границы текста вправо, а нижней влево для создания эффекта курсива
		float italicness = italic ? 1.0F : 0.0F;


		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

		GL11.glTexCoord2f(tx / 256, ty / 256);
		GL11.glVertex3f(posX + italicness, this.posY, 0);

		GL11.glTexCoord2f(tx / 256, (ty + 16F) / 256);
		GL11.glVertex3f(posX - italicness, this.posY + 8, 0);

		GL11.glTexCoord2f((tx + tw) / 256, ty / 256);
		GL11.glVertex3f(posX + tw / 2 + italicness, this.posY, 0);

		GL11.glTexCoord2f((tx + tw) / 256, (ty + 16) / 256);
		GL11.glVertex3f(posX + tw / 2 - italicness, this.posY + 8, 0);

		GL11.glEnd();


		return (width - rightOffset) / 2.0F + 1.0F;
	}

	/**
	 * Draws the specified string with a shadow.
	 */
	public int drawStringWithShadow(String text, float x, float y, int color) {
		return this.drawString(text, x, y, color, true);
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, int x, int y, int color) {
		return !this.enabled ? 0 : this.drawString(text, (float) x, (float) y, color, false);
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, float x, float y, int color, boolean dropShadow) {
		G.enableAlpha();
		this.resetStyles();
		int i;

		if (dropShadow) i = Math.max(renderString(text, x + 1, y + 1, color, true),
				this.renderString(text, x, y, color, false));
		else i = this.renderString(text, x, y, color, false);

		return i;
	}

	/**
	 * Reset all style flag fields in the class to false; called at the start of string rendering
	 */
	private void resetStyles() {
		this.randomStyle = false;
		this.boldStyle = false;
		this.italicStyle = false;
		this.underlineStyle = false;
		this.strikethroughStyle = false;
	}

	/**
	 * Рендерит строку (без переноса) на текущей позиции GL, сдвигая её по оси X.
	 */
	private void renderStringAtPos(String s, boolean dropShadow) {
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);

			// Обработка кодов форматирования
			if (c == '§' && i + 1 < s.length()) {
				// Номер формата
				int format = "0123456789abcdefklmnor".indexOf(s.toLowerCase().charAt(i + 1));

				// Обработка цвета
				if (format < 16) {

					// Сбрасываем все стили
					resetStyles();

					if (format < 0 || format > 15) format = 15;

					// Тёмный аналог цвета n имеет номер n + 1
					if (dropShadow) format += 16;

					int color = net.minecraft.client.gui.font.FontUtils.colorCodes[format];

					if (Config.isCustomColors()) color = CustomColors.getTextColor(format, color);

					this.textColor = color;
					this.setColor((float) (color >> 16) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, this.alpha);
				}
				// Стили текста
				else if (format == 16) this.randomStyle = true;
				else if (format == 17) this.boldStyle = true;
				else if (format == 18) this.strikethroughStyle = true;
				else if (format == 19) this.underlineStyle = true;
				else if (format == 20) this.italicStyle = true;
				else if (format == 21) {
					resetStyles();
					this.setColor(this.red, this.blue, this.green, this.alpha);
				}

				++i;
				continue;
			}
			int index = allChars.indexOf(c);

			if (this.randomStyle && index != -1) {
				float k = this.getCharWidthFloat(c);

				do {
					index = this.fontRandom.nextInt(allChars.length());
					c = allChars.charAt(index);

				} while (k != this.getCharWidthFloat(c));

			}

			float offsetBold = index == -1 || this.unicodeFlag ? 0.5F : this.offsetBold;
			dropShadow &= c == 0 || index == -1 || this.unicodeFlag;

			if (dropShadow) offset(-offsetBold);
			float charWidth = this.renderChar(c, boldStyle, this.italicStyle);
			if (dropShadow) offset(offsetBold);



			G.translate(posX, posY, 0);
			if (strikethroughStyle)	net.minecraft.client.gui.font.FontUtils.strike(charWidth, 9);
			if (underlineStyle) net.minecraft.client.gui.font.FontUtils.underline(charWidth, 9, 0);
			G.translate(-posX, -posY, 0);

//			if (this.underlineStyle) {
//				Tessellator tessellator1 = getMinecraft().preloader == null ? Tessellator.getInstance() : getMinecraft().preloader.getTesselator();
//				WorldRenderer worldrenderer1 = tessellator1.getWorldRenderer();
//				G.disableTexture2D();
//				worldrenderer1.begin(7, DefaultVertexFormats.POSITION);
//				int l = this.underlineStyle ? -1 : 0;
//				worldrenderer1.pos((double) (this.posX + (float) l), (double) (this.posY + (float) this.getFontHeight()), 0.0D).endVertex();
//				worldrenderer1.pos((double) (this.posX + charWidth), (double) (this.posY + (float) this.getFontHeight()), 0.0D).endVertex();
//				worldrenderer1.pos((double) (this.posX + charWidth), (double) (this.posY + (float) this.getFontHeight() - 1.0F), 0.0D).endVertex();
//				worldrenderer1.pos((double) (this.posX + (float) l), (double) (this.posY + (float) this.getFontHeight() - 1.0F), 0.0D).endVertex();
//				tessellator1.draw();
//				G.enableTexture2D();
//			}

			this.posX += charWidth;
		}
	}

	private void offset(float offset) {
		posX += offset;
		posY += offset;
	}

	/**
	 * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
	 */
	private int renderString(String text, float x, float y, int color, boolean dropShadow) {
		if (text == null) {
			return 0;
		}

		//		if ((color & 0xfc000000) == 0) color |= 0xff000000;
		if (color != -2) {

			if (dropShadow) color = (color & 0xfcfcfc) >> 2 | color & 0xff000000;

			this.red = (float) (color >> 16 & 255) / 255.0F;
			this.blue = (float) (color >> 8 & 255) / 255.0F;
			this.green = (float) (color & 255) / 255.0F;
			this.alpha = (float) (color >> 24 & 255) / 255.0F;
			if (alpha == 0) alpha = 1;
			this.setColor(this.red, this.blue, this.green, this.alpha);
		}
		this.posX = x;
		this.posY = y;
		this.renderStringAtPos(text, dropShadow);
		return (int) this.posX;
	}




	/**
	 * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
	 */
	public int getStringWidth(String text) {
		if (text == null) return 0;
		float f = 0.0F;
		boolean flag = false;

		for (int i = 0; i < text.length(); ++i) {
			char c0 = text.charAt(i);
			float f1 = this.getCharWidthFloat(c0);

			if (f1 < 0.0F && i < text.length() - 1) {
				++i;
				c0 = text.charAt(i);

				if (c0 != 108 && c0 != 76) {
					if (c0 == 114 || c0 == 82) flag = false;
				} else flag = true;

				f1 = 0.0F;
			}

			f += f1;
			if (flag && f1 > 0.0F) f += this.unicodeFlag ? 1.0F : this.offsetBold;
		}

		return (int) f;
	}

	private float getCharWidthFloat(char c) {
		if (c == 167) {
			return -1.0F;
		}
		if (c == 32) return this.charWidth[32];

		int i = allChars.indexOf(c);

		if (c > 0 && i != -1 && !this.unicodeFlag) return this.charWidth[i];

		if (this.glyphWidth[c] != 0) {
			int rightOffset = this.glyphWidth[c] >>> 4;
			int width = this.glyphWidth[c] & 15;
			rightOffset = rightOffset & 15;
			++width;
			return (float) (width - rightOffset) / 2F + 1F;
		}
		return 0.0F;
	}

	public void dlyaLogana() throws IOException {
		FileOutputStream s = new FileOutputStream("lolkek");

		for (char c = 0; c < 65535; c++) {

			int w = 0, o = 0;
			if (c == 32) w = (int) this.charWidth[32];


			int i = allChars.indexOf(c);

//			if (c > 0 && i != -1 && !this.unicodeFlag) return this.charWidth[i];

			if (this.glyphWidth[c] != 0) {
				o = this.glyphWidth[c] >>> 4;
				w = this.glyphWidth[c] & 15;
				o = o & 15;
			}
			String s1 = (int) c + "-" + w + "-" + o + "\n";
			s.write(s1.getBytes());
		}
		s.flush();
		s.close();
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	public String trimStringToWidth(String text, int width) {
		return this.trimStringToWidth(text, width, false);
	}

	/**
	 * Trims a string to a specified width, and will reverse it if par3 is set.
	 */
	public String trimStringToWidth(String text, int width, boolean reverse) {
		StringBuilder stringbuilder = new StringBuilder();
		float currentWidth = 0.0F;
		int i = reverse ? text.length() - 1 : 0;
		int j = reverse ? -1 : 1;
		boolean expectFormatCode = false;
		boolean isBoldNow = false;

		for (int k = i; k >= 0 && k < text.length() && currentWidth < (float) width; k += j) {
			char c0 = text.charAt(k);
			float f1 = this.getCharWidthFloat(c0);

			if (expectFormatCode) {
				expectFormatCode = false;

				if (c0 != 'l' && c0 != 'L') {
					if (c0 == 'r' || c0 == 'R') isBoldNow = false;
				} else isBoldNow = true;
			} else if (f1 < 0.0F) expectFormatCode = true;
			else {
				currentWidth += f1;
				if (isBoldNow) ++currentWidth;
			}

			if (currentWidth > (float) width) break;

			if (reverse) stringbuilder.insert(0, c0);
			else stringbuilder.append(c0);
		}

		return stringbuilder.toString();
	}

	/**
	 * Remove all newline characters from the end of the string
	 */
	private String trimStringNewline(String text) {
		while (text != null && text.endsWith("\n"))
			text = text.substring(0, text.length() - 1);

		return text;
	}

	/**
	 * Splits and draws a String with wordwrap (maximum length is parameter k)
	 */
	public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
		this.resetStyles();
		this.textColor = textColor;
		str = this.trimStringNewline(str);
		this.renderSplitString(str, x, y, wrapWidth, false);
	}

	/**
	 * Perform actual work of rendering a multi-line string with wordwrap and with darker drop shadow color if flag is
	 * set
	 */
	private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
		for (Object s : this.listFormattedStringToWidth(str, wrapWidth)) {
			this.renderString((String) s, x, y, textColor, addShadow);
			y += this.getFontHeight();
		}
	}

	/**
	 * Returns the width of the wordwrapped String (maximum length is parameter k)
	 */
	public int splitStringWidth(String p_78267_1_, int p_78267_2_) {
		return this.getFontHeight() * this.listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
	}

	/**
	 * Set unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
	 * font.
	 */
	public void setUnicodeFlag(boolean unicodeFlagIn) {
		this.unicodeFlag = unicodeFlagIn;
	}

	/**
	 * Get unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
	 * font.
	 */
	public boolean getUnicodeFlag() {
		return false;
	}

	/**
	 * Breaks a string into a list of pieces that will fit a specified width.
	 */
	public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
		return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
	}

	/**
	 * Inserts newline and formatting into a string to wrap it within the specified width.
	 */
	String wrapFormattedStringToWidth(String str, int wrapWidth) {
		int i = this.sizeStringToWidth(str, wrapWidth);

		if (str.length() <= i) {
			return str;
		}
		String s = str.substring(0, i);
		char c0 = str.charAt(i);
		boolean flag = c0 == 32 || c0 == 10;
		String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
		return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
	}

	/**
	 * Determines how many characters from the string will fit into the specified width.
	 */
	private int sizeStringToWidth(String str, int wrapWidth) {
		int i = str.length();
		float f = 0.0F;
		int j = 0;
		int k = -1;

		for (boolean flag = false; j < i; ++j) {
			char c0 = str.charAt(j);

			switch (c0) {
				case '\n':
					--j;
					break;

				case ' ':
					k = j;

				default:
					f += this.getCharWidthFloat(c0);

					if (flag) {
						++f;
					}

					break;

				case '\u00a7':
					if (j < i - 1) {
						++j;
						char c1 = str.charAt(j);

						if (c1 != 108 && c1 != 76) {
							if (c1 == 114 || c1 == 82 || isFormatColor(c1)) {
								flag = false;
							}
						} else {
							flag = true;
						}
					}
			}

			if (c0 == 10) {
				++j;
				k = j;
				break;
			}

			if (f > (float) wrapWidth) {
				break;
			}
		}

		return j != i && k != -1 && k < j ? k : j;
	}

	/**
	 * Checks if the char code is a hexadecimal character, used to set colour.
	 */
	private static boolean isFormatColor(char colorChar) {
		return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
	}

	/**
	 * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
	 */
	private static boolean isFormatSpecial(char formatChar) {
		return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114 || formatChar == 82;
	}

	/**
	 * Digests a string for nonprinting formatting characters then returns a string containing only that formatting.
	 */
	public static String getFormatFromString(String text) {
		StringBuilder s = new StringBuilder();
		int i = -1;
		int j = text.length();

		while ((i = text.indexOf(167, i + 1)) != -1) {
			if (i < j - 1) {
				char c0 = text.charAt(i + 1);

				if (isFormatColor(c0)) s = new StringBuilder("\u00a7").append(c0);
				else if (isFormatSpecial(c0)) s.append("\u00a7").append(c0);
			}
		}

		return s.toString();
	}

	protected void setColor(float r, float b, float g, float a) {
		G.color(r, b, g, a);
	}

	protected void bindTexture(ResourceLocation p_bindTexture_1_) {
		this.renderEngine.bindTexture(p_bindTexture_1_);
	}

	protected InputStream getResourceInputStream(ResourceLocation p_getResourceInputStream_1_) throws IOException {
		return getMinecraft().getResourceManager().getResource(p_getResourceInputStream_1_).getInputStream();
	}


	@Override
	public int getShadowOffset() {
		return 1;
	}

}
