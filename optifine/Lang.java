package optifine;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import net.minecraft.Utils;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.Settings;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Lang {

	private static final Splitter splitter = Splitter.on('=').limit(2);
	private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

	public static void resourcesReloaded() {
		Map map = net.minecraft.client.resources.Lang.getLocaleProperties();
		List<String> list = new ArrayList();
		String s = "optifine/lang/";
		String s1 = "en_US";
		String s2 = ".lang";
		list.add(s + s1 + s2);

		if (!Settings.language.equals(s1))
			list.add(s + Settings.language + s2);

		String[] astring = list.toArray(Utils.STRING);
		loadResources(Config.getDefaultResourcePack(), astring, map);
		IResourcePack[] airesourcepack = Config.getResourcePacks();

		for (IResourcePack iresourcepack : airesourcepack) loadResources(iresourcepack, astring, map);
	}

	private static void loadResources(IResourcePack p_loadResources_0_, String[] p_loadResources_1_, Map p_loadResources_2_) {
		try {
			for (String s : p_loadResources_1_) {
				ResourceLocation resourcelocation = new ResourceLocation(s);

				if (p_loadResources_0_.resourceExists(resourcelocation)) {
					InputStream inputstream = p_loadResources_0_.getInputStream(resourcelocation);

					if (inputstream != null) loadLocaleData(inputstream, p_loadResources_2_);
				}
			}
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
	}

	public static void loadLocaleData(InputStream p_loadLocaleData_0_, Map p_loadLocaleData_1_) throws IOException {
		for (String s : IOUtils.readLines(p_loadLocaleData_0_, Charsets.UTF_8))
			if (!s.isEmpty() && s.charAt(0) != 35) {
				String[] astring = Iterables.toArray(splitter.split(s), String.class);

				if (astring != null && astring.length == 2) {
					String s1 = astring[0];
					String s2 = pattern.matcher(astring[1]).replaceAll("%$1s");
					p_loadLocaleData_1_.put(s1, s2);
				}
			}
	}

	public static String get(String p_get_0_) {
		return net.minecraft.client.resources.Lang.format(p_get_0_);
	}

	public static String get(String p_get_0_, String p_get_1_) {
		String s = net.minecraft.client.resources.Lang.format(p_get_0_);
		return s != null && !s.equals(p_get_0_) ? s : p_get_1_;
	}

	public static String getOn() {
		return net.minecraft.client.resources.Lang.format("options.on");
	}

	public static String getOff() {
		return net.minecraft.client.resources.Lang.format("options.off");
	}

	public static String getFast() {
		return net.minecraft.client.resources.Lang.format("options.graphics.fast");
	}

	public static String getFancy() {
		return net.minecraft.client.resources.Lang.format("options.graphics.fancy");
	}

	public static String getDefault() {
		return net.minecraft.client.resources.Lang.format("generator.default");
	}

}
