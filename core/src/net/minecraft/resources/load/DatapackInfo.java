package net.minecraft.resources.load;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(access = AccessLevel.PUBLIC)
public class DatapackInfo {

	private final String
			domain,
			serverMain,
			clientMain,
			dependencies[],
			repo,
			releasePrefix,
			description;

	@Override
	public boolean equals(Object o) {
		return o instanceof DatapackInfo && ((DatapackInfo) o).domain.equals(domain);
	}

	public int hashCode() {
		return domain.hashCode();
	}

}
