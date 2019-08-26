package net.minecraft.client.network.services.github;

import lombok.Data;

import java.util.List;

@Data
public class Release {

	/**
	 * Тег релиза на гитхабе (напр. vanilla-0.0.1)
	 */
	private final String tag;

	/**
	 * Как дела
	 */
	private final boolean prerelease;
	private final boolean draft;
	private final List<GitHubAsset> assets;

}
