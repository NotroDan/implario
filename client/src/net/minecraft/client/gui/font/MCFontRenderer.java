package net.minecraft.client.gui.font;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MC;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.logging.IProfiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.VBOHelper;
import optifine.Config;
import optifine.CustomColors;
import optifine.FontUtils;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static net.minecraft.client.Minecraft.get;

public class MCFontRenderer implements IResourceManagerReloadListener, IFontRenderer {

	/**
	 * Есть шрифт Minecraftia - изначальный шрифт на английском языке без поддержки кириллицы.
	 * Чуть меньше чем все ресурс-паки с заменой шрифтов используют именно его.
	 *
	 * Есть шрифт UC - шрифт с поддержкой большинства первых 65536 символов из юникода, включая русский язык.
	 * Если в Minecraftia нет нужного символа, то он обращается к UC.
	 * UC в два раза меньше чем Minecraftia.
	 * UC несовместим с размерами интерфейса х1, х3, х5... (Искажается)
	 *
	 * Этот флаг позволяет включить принудительное использование UC для английских букв
	 * и других символов, которые поддерживаются в Minecraftia.
	 */
	@Getter
	@Setter
	private boolean ucEnabled;

	/**
	 * Таблица символов шрифта Minecraftia
	 * Minecraftia использует всего 256 символов юникода.
	 * Используется модифицированная кодировка CP437 (первые 32 символа заменены на интернациональные)
	 */
	private final String allChars = new String(new char[] {
			'À', 'Á', 'Â', 'È', 'Ê', 'Ë', 'Í', 'Ó', 'Ô', 'Õ', 'Ú', 'ß', 'ã', 'õ', 'ğ', 'İ',
			'ı', 'Œ', 'œ', 'Ş', 'ş', 'Ŵ', 'ŵ', 'ž', 'ȇ',  0 ,  0 ,  0 ,  0 ,  0 ,  0 ,  0 ,
			' ', '!', '"', '#', '$', '%', '&',  39, '(', ')', '*', '+', ',', '-', '.', '/',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',
			'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[',  92, ']', '^', '_',
			'`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
			'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~',  0 ,
			'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å', 'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å',
			'É', 'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü', 'ø', '£', 'Ø', '×', 'ƒ',
			'á', 'í', 'ó', 'ú', 'ñ', 'Ñ', 'ª', 'º', '¿', '®', '¬', '½', '¼', '¡', '«', '»',
			'░', '▒', '▓', '│', '┤', '╡', '╢', '╖', '╕', '╣', '║', '╗', '╝', '╜', '╛', '┐',
			'└', '┴', '┬', '├', '─', '┼', '╞', '╟', '╚', '╔', '╩', '╦', '╠', '═', '╬', '╧',
			'╨', '╤', '╥', '╙', '╘', '╒', '╓', '╫', '╪', '┘', '┌', '█', '▄', '▌', '▐', '▀',
			'α', 'β', 'Γ', 'π', 'Σ', 'σ', 'μ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', '∞',8709, '∈', '∩',
			'≡', '±', '≥', '≤', '⌠', '⌡', '÷', '≈', '°', '∙', '·', '√', 'ⁿ', '²', '■',  0 ,

	});

	/**
	 * Массив с ширинами всех символов для шрифта Minecraftia
	 * В отличие от minecraftUC, ширины для этого шрифта определяются автоматически.
	 */
	private float[] charWidth = new float[256];

	/**
	 * Текстура шрифта Minercaftia
	 */
	private ResourceLocation minecraftiaTexture;

	/**
	 * Базовая текстура шрифта.
	 * Отличается от аналога в случае если шрифт заменён через OptiFine
	 */
	private ResourceLocation minecraftiaTextureBase;

	/**
	 * Таблица Юникода (65536 символов) делится на 256 страниц по 256 символов.
	 * В этом массиве хранятся ссылки на текстуры для соответствующих страниц.
	 * Запись происходит при первом обращении к странице. Большую часть времени этот массив полупуст.
	 * @apiNote Общая для всех рендереров (static).
	 */
	private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];

	/**
	 * Массив с ширинами всех символов для шрифта minecraftUC
	 * <p>
	 * У каждого символа есть ширина и отступ слева (и то и другое занимает по 4 бита)
	 * Они записываются друг за другом в файле glyph_sizes.bin для всех 65536 символов.
	 * Ширина может быть от 1 до 16 и указывает саму ширину символа (место, занимаемое в строке)
	 * Отступ слева нужен для повышения читабельности текстур, вычитается из ширины в качестве коррекции.
	 */
	private byte[] glyphWidth = new byte[65536];

	private VBOHelper.VBO[] vaos = new VBOHelper.VBO[256];


	/**
	 * Высота шрифта одинакова для всех символов.
	 * 8 пикселей на символ и 1 пиксель в промежуток между строками.
	 * Из-за ScaledResolution при стандартном интерфейсе эта цифра увеличивается вдвое.
	 */
	@Getter
	public final int fontHeight = 9;


	// RNG для обфусцированного текста
	public Random fontRandom = new Random();


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

	public MCFontRenderer(ResourceLocation location, boolean unicode) {
		this.minecraftiaTextureBase = location;
		this.minecraftiaTexture = location;
		this.ucEnabled = unicode;
		this.minecraftiaTexture = FontUtils.getHdFontLocation(this.minecraftiaTextureBase);
		MC.bindTexture(this.minecraftiaTexture);


		this.prepareUc();
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.minecraftiaTexture = FontUtils.getHdFontLocation(this.minecraftiaTextureBase);

		Arrays.fill(unicodePageLocations, null);

		this.prepareMinecraftia();
		this.prepareUc();
	}

	private void prepareMinecraftia() {
		BufferedImage img = TextureUtil.readImageOrPanic(getResourceInputStream(minecraftiaTexture));
		Properties properties = FontUtils.readFontProperties(minecraftiaTexture);

		int width = img.getWidth();
		int height = img.getHeight();
		int xStep = width / 16;
		int yStep = height / 16;
		float f = (float) width / 128.0F;
		float f1 = Config.limit(f, 1.0F, 2.0F);
		this.offsetBold = 1.0F / f1;

		float f2 = FontUtils.readFloat(properties, "offsetBold", -1.0F);
		if (f2 >= 0.0F) this.offsetBold = f2;

		int[] aint = new int[width * height];
		img.getRGB(0, 0, width, height, aint, 0, width);

		for (int charId = 0; charId < 256; ++charId) {
			int col = charId % 16;
			int row = charId / 16;
			int l1;

			for (l1 = xStep - 1; l1 >= 0; --l1) {
				int i2 = col * xStep + l1;
				boolean flag = true;

				for (int i = 0; i < yStep; ++i) {
					int k2 = (row * yStep + i) * width;
					int l2 = aint[i2 + k2];
					int i3 = l2 >> 24 & 255;

					if (i3 > 16) {
						flag = false;
						break;
					}
				}

				if (!flag) break;
			}

			if (charId == 32) l1 = (int) ((xStep <= 8 ? 2.0F : 1.5F) * f);

			this.charWidth[charId] = (float) (l1 + 1) / f + 1.0F;
		}

		FontUtils.readCustomCharWidths(properties, this.charWidth);
	}

	private void prepareUc() {
		try (InputStream is = getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"))) {
			is.read(this.glyphWidth);
		} catch (IOException ioexception) {
			throw new RuntimeException(ioexception);
		}
	}

	@Override
	public void renderHeader() {
		cachedPage = -1;
	}

	public float renderChar(char c, boolean bold, boolean italic) {
		if (c == 32) return this.ucEnabled ? 4.0F : this.charWidth[c];
		IProfiler profiler = get().getProfiler();
		profiler.startSection("fontrender");

		float charWidth;
		int ucIndex = -1;
		if (ucEnabled || (ucIndex = allChars.indexOf(c)) == -1) charWidth = renderCharUc(c, italic);
		else charWidth = renderCharMinecraftia(ucIndex, italic);

		float widthBias = 0;
		if (bold) {
			float offsetBold = ucIndex == -1 ? 0.5F : this.offsetBold;
			G.translate(offsetBold, 0, 0);
			renderChar(c, false, this.italicStyle);
			G.translate(-offsetBold, 0, 0);
			widthBias += offsetBold;
		}
		profiler.endSection();
		return charWidth + widthBias;
	}

	/**
	 * Render a single character with the default.png font at current (posX,posY) location...
	 */
	private float renderCharMinecraftia(int character, boolean italic) {
		int i = character % 16 * 8;
		int j = character / 16 * 8;
		int k = italic ? 1 : 0;
		MC.bindTexture(this.minecraftiaTexture);
		float f = this.charWidth[character];
		float f1 = 7.99F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f((float) i / 128.0F, (float) j / 128.0F);
		GL11.glVertex3f((float) k, 0, 0.0F);
		GL11.glTexCoord2f((float) i / 128.0F, ((float) j + 7.99F) / 128.0F);
		GL11.glVertex3f(- (float) k, 7.99F, 0.0F);
		GL11.glTexCoord2f(((float) i + f1 - 1.0F) / 128.0F, (float) j / 128.0F);
		GL11.glVertex3f(f1 - 1.0F + (float) k, 0, 0.0F);
		GL11.glTexCoord2f(((float) i + f1 - 1.0F) / 128.0F, ((float) j + 7.99F) / 128.0F);
		GL11.glVertex3f(f1 - 1.0F - (float) k, 7.99F, 0.0F);
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

	private int cachedPage = -1;

	private void loadGlyphTexture(int page) {
		if (page == cachedPage) return;
		if (vaos[page] == null) {
			float[] buf = new float[256 * 4 * 4];
			for (int c = 0; c < 256; c++) {
				byte bin = this.glyphWidth[page * 256 + c];
				int rightOffset = bin >>> 4 & 0b1111;
				int width = (bin & 15) + 1;
				float tx = (float) (c % 16 * 16 + rightOffset);
				float ty = (float) (c & 0xf0);
				int tw = width - rightOffset;

				float u1 = tx / 256, x2 = tw / 2F;
				float v1 = ty / 256, u2 = (tx + tw) / 256;
				float v2 = (ty + 16) / 256;

				int cc = c * 16;
				buf[cc] = 0;
				buf[cc + 1] = 0;
				buf[cc + 2] = u1;
				buf[cc + 3] = v1;

				buf[cc + 4] = 0;
				buf[cc + 5] = 8;
				buf[cc + 6] = u1;
				buf[cc + 7] = v2;

				buf[cc + 8] = x2;
				buf[cc + 9] = 0;
				buf[cc + 10] = u2;
				buf[cc + 11] = v1;

				buf[cc + 12] = x2;
				buf[cc + 13] = 8;
				buf[cc + 14] = u2;
				buf[cc + 15] = v2;

			}
			vaos[page] = VBOHelper.create2Dtextured(buf);

		}
		ResourceLocation unicodePageLocation = this.getUnicodePageLocation(page);
		MC.bindTexture(unicodePageLocation);
		cachedPage = page;
	}

	/**
	 * Render a single Unicode character at current (posX,posY) location using one of the /font/glyph_XX.png files...
	 */
	private float renderCharUc(char c, boolean italic) {
		if (this.glyphWidth[c] == 0) return 0.0F;

		int page = c / 256;
		this.loadGlyphTexture(page);

		int rightOffset = this.glyphWidth[c] >>> 4 & 0b1111;
		float width = (this.glyphWidth[c] & 15) + 1;

		VBOHelper.draw2DTextured(vaos[page], GL11.GL_TRIANGLE_STRIP, (int) c % 256 * 4, 4);

		if (true) return (width - rightOffset) / 2.0F + 1.0F;


		float tx = (float) (c % 16 * 16) + rightOffset;
		float ty = (float) (c & 0xf0);
		float tw = width - rightOffset;

		// Сдвиг верхней границы текста вправо, а нижней влево для создания эффекта курсива
		float italicness = italic ? 1.0F : 0.0F;

		float txCached = tx / 256, oneTwCached = tw / 2;
		float tyCached = ty / 256, txTwCache = (tx + tw) / 256;
		float tyCache = (ty + 16) / 256;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

		GL11.glTexCoord2f(txCached, tyCached);
		GL11.glVertex2f(italicness, 0);

		GL11.glTexCoord2f(txCached, tyCache);
		GL11.glVertex2f(-italicness, (float) 8);

		GL11.glTexCoord2f(txTwCache, tyCached);
		GL11.glVertex2f(oneTwCached + italicness, 0);

		GL11.glTexCoord2f(txTwCache, tyCache);
		GL11.glVertex2f(oneTwCached - italicness, (float) 8);

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
		G.pushMatrix();
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

			float offsetBold = index == -1 || this.ucEnabled ? 0.5F : this.offsetBold;
			dropShadow &= c == 0 || index == -1 || this.ucEnabled;

			if (dropShadow) offset(-offsetBold);
			float charWidth = this.renderChar(c, boldStyle, this.italicStyle);
			if (dropShadow) offset(offsetBold);
			G.translate(charWidth, 0, 0);


			if (strikethroughStyle) net.minecraft.client.gui.font.FontUtils.strike(charWidth, 9);
			if (underlineStyle) net.minecraft.client.gui.font.FontUtils.underline(charWidth, 9, 0);

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

		}
		G.popMatrix();
		cachedPage = -1;
	}

	private void offset(float offset) {
		G.translate(offset, offset, 0);
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
		G.translate(x, y, 0);
		this.renderStringAtPos(text, dropShadow);
		G.translate(-x, -y, 0);
//		this.posX = x;
//		this.posY = y;
//		return (int) this.posX;
		return 1;
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
			if (flag && f1 > 0.0F) f += this.ucEnabled ? 1.0F : this.offsetBold;
		}

		return (int) f;
	}

	private float getCharWidthFloat(char c) {
		if (c == 167) return -1.0F;
		if (c == 32) return this.charWidth[32];

		if (!ucEnabled) {
			int i = allChars.indexOf(c);
			if (c > 0 && i != -1) return this.charWidth[i];
		}

		if (this.glyphWidth[c] == 0) return 0;

		int rightOffset = this.glyphWidth[c] >>> 4;
		int width = this.glyphWidth[c] & 15;
		rightOffset = rightOffset & 15;
		++width;
		return (float) (width - rightOffset) / 2F + 1F;
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

	protected InputStream getResourceInputStream(ResourceLocation loc) {
		try {
			return get().getResourceManager().getResource(loc).getInputStream();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}


	@Override
	public int getShadowOffset() {
		return 1;
	}

}
