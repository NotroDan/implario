package net.minecraft.client.resources;

import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.logging.Log;
import net.minecraft.server.Todo;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FallbackResourceManager implements IResourceManager {
	protected final List<IResourcePack> resourcePacks = new ArrayList<>();
	private final IMetadataSerializer frmMetadataSerializer;

	public FallbackResourceManager(IMetadataSerializer frmMetadataSerializerIn) {
		this.frmMetadataSerializer = frmMetadataSerializerIn;
	}

	public void addResourcePack(IResourcePack resourcePack) {
		this.resourcePacks.add(resourcePack);
	}

	public Set<String> getResourceDomains() {
		return null;
	}

	public IResource getResource(ResourceLocation loc) throws IOException {
		IResourcePack rp = null;
		ResourceLocation resourcelocation = getLocationMcmeta(loc);

		for (int i = this.resourcePacks.size() - 1; i >= 0; --i) {
			IResourcePack iresourcepack1 = this.resourcePacks.get(i);

			if (rp == null && iresourcepack1.resourceExists(resourcelocation)) {
				rp = iresourcepack1;
			}

			if (iresourcepack1.resourceExists(loc)) {
				InputStream inputstream = null;

				if (rp != null) {
					inputstream = this.getInputStream(resourcelocation, rp);
				}

				return new SimpleResource(iresourcepack1.getPackName(), loc, this.getInputStream(loc, iresourcepack1), inputstream, this.frmMetadataSerializer);
			}
		}

		throw new FileNotFoundException(loc.toString());
	}

	protected InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException {
		InputStream inputstream = resourcePack.getInputStream(location);
		return Todo.instance.debugEnabled() ? new InputStreamLeakedResourceLogger(inputstream, location, resourcePack.getPackName()) : inputstream;
	}

	public List<IResource> getAllResources(ResourceLocation location) throws IOException {
		List<IResource> list = new ArrayList<>();
		ResourceLocation resourcelocation = getLocationMcmeta(location);

		for (IResourcePack iresourcepack : this.resourcePacks) {
			if (iresourcepack.resourceExists(location)) {
				InputStream inputstream = iresourcepack.resourceExists(resourcelocation) ? this.getInputStream(resourcelocation, iresourcepack) : null;
				list.add(new SimpleResource(iresourcepack.getPackName(), location, this.getInputStream(location, iresourcepack), inputstream, this.frmMetadataSerializer));
			}
		}

		if (list.isEmpty()) {
			throw new FileNotFoundException(location.toString());
		}
		return list;
	}

	static ResourceLocation getLocationMcmeta(ResourceLocation location) {
		return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta");
	}

	static class InputStreamLeakedResourceLogger extends InputStream {

		private final InputStream field_177330_a;
		private final String field_177328_b;
		private boolean field_177329_c = false;

		public InputStreamLeakedResourceLogger(InputStream p_i46093_1_, ResourceLocation location, String p_i46093_3_) {
			this.field_177330_a = p_i46093_1_;
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			new Exception().printStackTrace(new PrintStream(bytearrayoutputstream));
			this.field_177328_b = "Leaked resource: \'" + location + "\' loaded from pack: \'" + p_i46093_3_ + "\'\n" + bytearrayoutputstream.toString();
		}

		public void close() throws IOException {
			this.field_177330_a.close();
			this.field_177329_c = true;
		}

		protected void finalize() {
			if (!this.field_177329_c) {
				Log.MAIN.warn(this.field_177328_b);
			}
		}

		public int read() throws IOException {
			return this.field_177330_a.read();
		}

	}

}
