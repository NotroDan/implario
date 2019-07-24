import net.minecraft.client.main.Main;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Datapacks;

import java.io.File;
import java.util.Arrays;

public class Start {

	public static void main(String[] args) throws Exception {
		Main.main(concat(new String[]{"--accessToken", "0", "--assetsDir", "assets",
				"--height", "600", "--width", "1000"}, args));
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

}
