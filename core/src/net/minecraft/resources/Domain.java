package net.minecraft.resources;

public class Domain {

	public static final Domain MINECRAFT = new Domain("minecraft");

	private final String address;

	public Domain(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return getAddress();
	}

}
