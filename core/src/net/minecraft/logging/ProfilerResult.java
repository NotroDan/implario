package net.minecraft.logging;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ProfilerResult implements Comparable<ProfilerResult> {

	public final String name;
	public final double localPercentage;
	public final double globalPercentage;

	public int compareTo(ProfilerResult that) {
		return that.localPercentage < this.localPercentage ? -1 :
				that.localPercentage > this.localPercentage ? 1 :
						that.name.compareTo(this.name);
	}

	@Override
	public int hashCode() {
		return (this.name.hashCode() & 0xaaaaaa) + 0x444444;
	}

}
