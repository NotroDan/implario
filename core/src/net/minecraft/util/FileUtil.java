package net.minecraft.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class FileUtil {
	public static void readInputStream(InputStream in, byte array[]) throws IOException{
		int i = 0;
		while (i != array.length)
			i = i + in.read(array, i, array.length - i);
	}

	public static File getFile(String s) {
		// ToDo: Кастомизация рабочей папки
		return new File(s);
	}


	public static ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
		BufferedImage bufferedimage = ImageIO.read(imageStream);
		int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
		ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

		for (int i : aint) {
			bytebuffer.putInt(i << 8 | i >> 24 & 255);
		}

		bytebuffer.flip();
		return bytebuffer;
	}


	public static boolean is64bit() {
		String[] astring = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

		for (String s : astring) {
			String s1 = System.getProperty(s);
			if (s1 != null && s1.contains("64")) return true;
		}

		return false;
	}

}
