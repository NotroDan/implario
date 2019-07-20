package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import com.mojang.util.UUIDTypeAdapter;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Datapacks;
import net.minecraft.resources.load.JarDatapackLoader;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;

import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import java.util.UUID;

public class Main {

	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		OptionParser optionparser = new OptionParser();
		optionparser.allowsUnrecognizedOptions();
		optionparser.accepts("fullscreen");
		optionparser.accepts("checkGlErrors");
		optionparser.accepts("vanilla");
		OptionSpec<String> optionspec = optionparser.accepts("server").withRequiredArg();
		OptionSpec<Integer> optionspec1 = optionparser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
		OptionSpec<File> optionspec2 = optionparser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
		OptionSpec<File> optionspec3 = optionparser.accepts("assetsDir").withRequiredArg().ofType(File.class);
		OptionSpec<File> optionspec4 = optionparser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
		OptionSpec<String> optionspec5 = optionparser.accepts("proxyHost").withRequiredArg();
		OptionSpec<Integer> optionspec6 = optionparser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
		OptionSpec<String> optionspec7 = optionparser.accepts("proxyUser").withRequiredArg();
		OptionSpec<String> optionspec8 = optionparser.accepts("proxyPass").withRequiredArg();
		OptionSpec<String> optionspec10 = optionparser.accepts("uuid").withRequiredArg();
		OptionSpec<String> optionspec11 = optionparser.accepts("accessToken").withRequiredArg().required();
		OptionSpec<Integer> optionspec13 = optionparser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
		OptionSpec<Integer> optionspec14 = optionparser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
		OptionSpec<String> optionspec16 = optionparser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
		OptionSpec<String> optionspec18 = optionparser.accepts("userType").withRequiredArg().defaultsTo("legacy");
		OptionSpec<String> optionspec19 = optionparser.nonOptions();
		OptionSet optionset = optionparser.parse(args);
		List<String> list = optionset.valuesOf(optionspec19);

		if (!list.isEmpty()) System.out.println("Completely ignored arguments: " + list);

		String s = optionset.valueOf(optionspec5);
		Proxy proxy = Proxy.NO_PROXY;

		if (s != null) try {
			proxy = new Proxy(Type.SOCKS, new InetSocketAddress(s, optionset.valueOf(optionspec6)));
		} catch (Exception ignored) {}

		final String proxyUser = optionset.valueOf(optionspec7);
		final String proxyPass = optionset.valueOf(optionspec8);

		if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(proxyUser) && isNullOrEmpty(proxyPass))
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(proxyUser, proxyPass.toCharArray());
				}
			});

		int i = optionset.valueOf(optionspec13);
		int j = optionset.valueOf(optionspec14);
		boolean flag = optionset.has("fullscreen");
		boolean flag1 = optionset.has("checkGlErrors");
		boolean vanilla = optionset.has("vanilla");
		if (vanilla) Datapacks.load(new JarDatapackLoader(new File("gamedata/datapacks/vanilla.jar")));
		Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
		PropertyMap propertymap1 = gson.fromJson(optionset.valueOf(optionspec16), PropertyMap.class);
		File file1 = optionset.valueOf(optionspec2);
		File file2 = optionset.has(optionspec3) ? optionset.valueOf(optionspec3) : new File(file1, "assets/");
		File file3 = optionset.has(optionspec4) ? optionset.valueOf(optionspec4) : new File(file1, "resourcepacks/");
		String playername = StringUtils.getWittyName();
		String uuid = optionset.has(optionspec10) ? optionspec10.value(optionset) : UUIDTypeAdapter.fromUUID(UUID.randomUUID());
		String s6 = optionset.valueOf(optionspec);
		Integer integer = optionset.valueOf(optionspec1);
		Session session = new Session(playername, uuid, optionspec11.value(optionset), optionspec18.value(optionset));
		GameConfiguration gameconfiguration = new GameConfiguration(
				new GameConfiguration.UserInformation(session, propertymap1, proxy),
				new GameConfiguration.DisplayInformation(i, j, flag, flag1),
				new GameConfiguration.FolderInformation(file1, file3, file2),
				new GameConfiguration.ServerInformation(s6, integer)
		);
		Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
			public void run()
			{
				Minecraft.stopIntegratedServer();
			}
		});
		Thread.currentThread().setName("Client thread");
		new Minecraft(gameconfiguration).run();
	}

	private static boolean isNullOrEmpty(String str)
	{
		return str != null && !str.isEmpty();
	}
}
