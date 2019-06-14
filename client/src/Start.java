import net.minecraft.client.main.Main;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.load.DatapackClassLoader;
import net.minecraft.resources.load.DatapackLoader;
import paulscode.sound.libraries.LibraryJavaSound;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Start {

	public static void main(String[] args) throws Exception {
		DatapackLoader loader = new DatapackLoader(new File("vanilla.jar"));
		Class clazz = loader.loadClass("vanilla.Vanilla");
		Datapack datapack = (Datapack)clazz.newInstance();
		datapack.init();
		if(true)return;
		Main.main(concat(new String[]{"--accessToken", "0", "--assetsDir", "assets",
				"--height", "600", "--width", "1000"}, args));
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

}
