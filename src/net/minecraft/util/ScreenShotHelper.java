package net.minecraft.util;

import net.minecraft.Logger;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenShotHelper {

	private static final Logger logger = Logger.getInstance();
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH-mm-ss");

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
	 * Saves a screenshot in the game directory with the given file name (or null to generate a time-stamped name).
	 * Args: gameDirectory, fileName, requestedWidthInPixels, requestedHeightInPixels, frameBuffer
	 */
	public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer) {
		try {
			File file1 = new File(gameDirectory, "screenshots");
			file1.mkdir();

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

			File file2;

			if (screenshotName == null) {
				file2 = getTimestampedPNGFileForDirectory(file1);
			} else {
				file2 = new File(file1, screenshotName);
			}

			ImageIO.write(bufferedimage, "png", file2);
			IChatComponent ichatcomponent = new ChatComponentText(file2.getName());
			ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
			ichatcomponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_FILE, new ChatComponentText(file2.getName())));
//			ichatcomponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file1.getAbsolutePath()));
			ichatcomponent.getChatStyle().setUnderlined(Boolean.TRUE);

			TransferableImage image = new TransferableImage(bufferedimage);
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			c.setContents(image, (cb, t) -> {});

			return new ChatComponentTranslation("screenshot.success", ichatcomponent);
		} catch (Exception exception) {
			logger.warn("Couldn\'t save screenshot", exception);
			return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
		}
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

	private static class TransferableImage implements Transferable {

		Image i;

		public TransferableImage( Image i ) {
			this.i = i;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if ( flavor.equals( DataFlavor.imageFlavor ) && i != null ) return i;
			throw new UnsupportedFlavorException( flavor );
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor[] flavors = new DataFlavor[ 1 ];
			flavors[ 0 ] = DataFlavor.imageFlavor;
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			DataFlavor[] flavors = getTransferDataFlavors();
			for ( int i = 0; i < flavors.length; i++ ) if (flavor.equals(flavors[i])) return true;
			return false;
		}
	}

}
