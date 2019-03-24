package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.logging.Log;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import java.util.Set;

public class DefaultResourcePack implements IResourcePack {

	public static final Set defaultResourceDomains = ImmutableSet.of("minecraft");

	public InputStream getInputStream(ResourceLocation location) throws IOException {
		InputStream inputstream = this.getInternalInputStream(location);
		if (inputstream != null) return inputstream;

		InputStream inputstream1 = this.getExternalInputStream(location);
		if (inputstream1 != null) return inputstream1;

		throw new FileNotFoundException(location.getResourcePath());
	}

	public InputStream getExternalInputStream(ResourceLocation location) throws IOException {
		File domain = new File("gamedata/defaultresourcepack/" + location.getResourceDomain());
		File f = new File(domain, location.getResourcePath());
		return f.isFile() && f.exists() ? new FileInputStream(f) : null;
	}

	private InputStream getInternalInputStream(ResourceLocation location) {
		String s = "/assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
		return DefaultResourcePack.class.getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
	}

	public boolean resourceExists(ResourceLocation location) {
		try {
			return this.getInternalInputStream(location) != null || this.getExternalInputStream(location) != null;
		} catch (IOException e) {
			Log.MAIN.error("При проверке ресурса " + location + " на факт существования произошла ошибка.");
			Log.MAIN.exception(e);
			return false;
		}
	}


	public IMetadataSection getPackMetadata(IMetadataSerializer serializer, String s) {
		try {
			FileInputStream fileinputstream = new FileInputStream("gamedata/defaultresourcepack/pack.mcmeta");
			return AbstractResourcePack.readMetadata(serializer, fileinputstream, s);
		} catch (RuntimeException | FileNotFoundException e) {
			return null;
		}
	}

	public BufferedImage getPackImage() throws IOException {
		String path = "/" + new ResourceLocation("pack.png").getResourcePath();
		return TextureUtil.readBufferedImage(DefaultResourcePack.class.getResourceAsStream(path));
	}

	public Set getResourceDomains() {
		return defaultResourceDomains;
	}
	public String getPackName() {
		return "Стандартный";
	}

}
