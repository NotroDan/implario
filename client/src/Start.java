import net.minecraft.client.main.Main;
import net.minecraft.security.MinecraftSecurityManager;
import net.minecraft.security.Restart;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

public class Start {
	static {
		if (System.getSecurityManager() == null) System.setSecurityManager(new MinecraftSecurityManager());
	}

	public static void main(String[] args) {
		Restart.setArgs(args);
		Main.main(concat(new String[] {
				"--accessToken", "0", "--assetsDir", "assets",
				"--height", "600", "--width", "1000"
		}, args));
	}

	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
}
