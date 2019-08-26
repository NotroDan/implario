package net.minecraft.client.network.services.github;

import lombok.Data;

@Data
public class GitHubAsset {

	/**
	 * Название файла с расширением (client.zip, update.patch)
	 */
	private final String name;

	/**
	 * Размер файла в байтах
	 */
	private final long size;

	/**
	 * URL-адрес, по которому можно скачать файл
	 */
	private final String path;

}
