package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation {

	protected final String resourceDomain;
	protected final String resourcePath;

	public ResourceLocation(String domain, String path) {
		this.resourceDomain = domain == null || domain.isEmpty() ? "minecraft" : domain.toLowerCase();
		this.resourcePath = Validate.notNull(path);
	}

	public ResourceLocation(String path) {
		int colon = path.indexOf(':');
		this.resourceDomain = colon > 0 ? path.substring(0, colon) : "minecraft";
		this.resourcePath = colon > 0 ? path.substring(colon + 1) : path;
	}

	/**
	 * Splits an object name (such as minecraft:apple) into the domain and path parts and returns these as an array of
	 * length 2. If no colon is present in the passed value the returned array will contain {null, toSplit}.
	 */
	protected static String[] splitObjectName(String toSplit) {
		String[] split = new String[] {null, toSplit};
		int i = toSplit.indexOf(':');

		if (i < 0) return split;

		split[1] = toSplit.substring(i + 1);
		if (i > 1) split[0] = toSplit.substring(0, i);

		return split;
	}

	public String getResourcePath() {
		return this.resourcePath;
	}

	public String getResourceDomain() {
		return this.resourceDomain;
	}

	public String toString() {
		return this.resourceDomain + ':' + this.resourcePath;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ResourceLocation)) return false;
		ResourceLocation resourcelocation = (ResourceLocation) o;
		return this.resourceDomain.equals(resourcelocation.resourceDomain) && this.resourcePath.equals(resourcelocation.resourcePath);
	}

	public int hashCode() {
		return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
	}

}
