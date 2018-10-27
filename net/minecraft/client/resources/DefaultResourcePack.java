package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
import java.util.Set;

public class DefaultResourcePack implements IResourcePack {

	public static final Set defaultResourceDomains = ImmutableSet.of("minecraft", "realms");
	private final Map<String, File> mapAssets;

	public DefaultResourcePack(Map<String, File> mapAssetsIn) {
		this.mapAssets = mapAssetsIn;
	}

	public InputStream getInputStream(ResourceLocation location) throws IOException {
		InputStream inputstream = this.getResourceStream(location);
		if (inputstream != null) return inputstream;

		InputStream inputstream1 = this.getInputStreamAssets(location);
		if (inputstream1 != null) return inputstream1;

		throw new FileNotFoundException(location.getResourcePath());
	}

	public InputStream getInputStreamAssets(ResourceLocation location) throws IOException {
		File f = this.mapAssets.get(location.toString());
		return f != null && f.isFile() ? new FileInputStream(f) : null;
	}

	private InputStream getResourceStream(ResourceLocation location) {
		String s = "/assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
		return DefaultResourcePack.class.getResourceAsStream("/assets/" + location.getResourceDomain() + "/" + location.getResourcePath());
	}

	public boolean resourceExists(ResourceLocation location) {
		return this.getResourceStream(location) != null || this.mapAssets.containsKey(location.toString());
	}


	public IMetadataSection getPackMetadata(IMetadataSerializer serializer, String s) {
		try {
			FileInputStream fileinputstream = new FileInputStream(this.mapAssets.get("pack.mcmeta"));
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
