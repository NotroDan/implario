package net.minecraft.client.renderer;

import net.minecraft.client.game.shader.Framebuffer;
import net.minecraft.client.network.services.imgur.ImgurAPI;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.settings.Settings;
import net.minecraft.logging.Log;
import net.minecraft.util.Clipboard;
import net.minecraft.util.chat.ChatComponentBuilder;
import net.minecraft.util.chat.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.minecraft.util.chat.event.ClickEvent.Action.OPEN_FILE;
import static net.minecraft.util.chat.event.HoverEvent.Action.SHOW_FILE;

public class ScreenShotHelper {

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	/**
	 * A buffer to hold pixel values returned by OpenGL.
	 */
	private static IntBuffer pixelBuffer;

	/**
	 * The built-up array that contains all the pixel values returned by OpenGL.
	 */
	private static int[] pixelValues;

	/**
	 * Saves a screenshot in the game directory with a time-stamped filename.  Args: gameDirectory,
	 * requestedWidthInPixels, requestedHeightInPixels, frameBuffer
	 */
	public static IChatComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
		return saveScreenshot(gameDirectory, null, width, height, buffer);
	}

	/**
	 * Создаёт и сохраняет скриншот в папку screenshots.
	 *
	 * @return Чат-компонент, являющийся ссылкой на файл
	 */
	public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer) {
		try {
			File dir = new File(gameDirectory, "screenshots");
			dir.mkdir();
			File f = screenshotName == null ? getTimestampedPNGFileForDirectory(dir) : new File(dir, screenshotName);

			BufferedImage screenshot = takeScreenshot(width, height, buffer);
			ImageIO.write(screenshot, "png", f);
			if (Settings.SCREEN_TO_BUFFER.b()) Clipboard.push(screenshot);

			return new ChatComponentBuilder(f.getName())
					.click(OPEN_FILE, f.getAbsolutePath())
					.hover(SHOW_FILE, f.getName())
					.underline()
					.translate("screenshot.success")
					.build();


		} catch (Exception exception) {
			Log.MAIN.error("Не удалось сохранить скриншот!");
			Log.MAIN.exception(exception);
			return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
		}
	}

	/**
	 * Загружает изображение на хостинг картинок Imgur.
	 *
	 * @return URL-адрес изображения в сети.
	 */
	public static String uploadToImgur(BufferedImage image) {
		// ToDo: Асинхронная загрузка.
		// ToDo: Обработка ошибок и предупреждение при превышении максимального размера PNG.
		return ImgurAPI.postImage(image);
	}


	/**
	 * Превращает текущий кадр в BufferedImage
	 */
	private static BufferedImage takeScreenshot(int width, int height, Framebuffer buffer) {

		if (OpenGlHelper.isFramebufferEnabled()) {
			width = buffer.framebufferTextureWidth;
			height = buffer.framebufferTextureHeight;
		}

		int i = width * height;

		if (pixelBuffer == null || pixelBuffer.capacity() < i) {
			pixelBuffer = BufferUtils.createIntBuffer(i);
			pixelValues = new int[i];
		}

		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		pixelBuffer.clear();

		if (OpenGlHelper.isFramebufferEnabled()) {
			G.bindTexture(buffer.framebufferTexture);
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
		} else {
			GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
		}

		pixelBuffer.get(pixelValues);
		TextureUtil.processPixelValues(pixelValues, width, height);
		BufferedImage bufferedimage;

		if (OpenGlHelper.isFramebufferEnabled()) {
			bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
			int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;

			for (int k = j; k < buffer.framebufferTextureHeight; ++k) {
				for (int l = 0; l < buffer.framebufferWidth; ++l) {
					bufferedimage.setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
				}
			}
		} else {
			bufferedimage = new BufferedImage(width, height, 1);
			bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
		}

		return bufferedimage;

	}

	/**
	 * Creates a unique PNG file in the given directory named by a timestamp.  Handles cases where the timestamp alone
	 * is not enough to create a uniquely named file, though it still might suffer from an unlikely race condition where
	 * the filename was unique when this method was called, but another process or thread created a file at the same
	 * path immediately after this method returned.
	 */
	private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
		String s = dateFormat.format(new Date());
		int i = 1;

		while (true) {
			File file1 = new File(gameDirectory, s + (i == 1 ? "" : " - " + i) + ".png");
			if (!file1.exists()) return file1;
			++i;
		}
	}

}
