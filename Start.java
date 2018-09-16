import net.minecraft.client.main.Main;

import java.io.File;
import java.util.Arrays;

public class Start
{
    public static void main(String[] args)
    {
        System.out.println(new File(".").getAbsolutePath());
        Main.main(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets1", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
