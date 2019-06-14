import net.minecraft.client.main.Main;
import net.minecraft.resources.Datapack;
import net.minecraft.resources.load.DatapackLoader;

import java.io.File;
import java.util.Arrays;

public class Start {

	public static void main(String[] args) throws Exception {
		DatapackLoader loader = new DatapackLoader(new File("Vanilla.jar"));
		Class clazz = loader.loadClass("vanilla.Vanilla");
		Datapack datapack = (Datapack)clazz.newInstance();
		Datapack.LOADED.add(datapack);
		Main.main(concat(new String[]{"--accessToken", "0", "--assetsDir", "assets",
				"--height", "600", "--width", "1000"}, args));
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

}
