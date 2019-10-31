package net.minecraft.resources;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Domain domain = (Domain) o;
		return Objects.equals(address, domain.address);
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}
}
