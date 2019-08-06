package net.minecraft.logging;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ProfilerResult implements Comparable<ProfilerResult> {

	public final String name;
	public final double localPercentage;
	public final double globalPercentage;

	public int compareTo(ProfilerResult that) {
		return that.localPercentage < this.localPercentage ? -1 :
				that.localPercentage > this.localPercentage ? 1 :
						that.name.compareTo(this.name);
	}

}
