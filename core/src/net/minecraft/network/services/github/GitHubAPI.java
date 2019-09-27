package net.minecraft.network.services.github;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import net.minecraft.network.services.Method;
import net.minecraft.network.services.Request;

import java.util.ArrayList;
import java.util.List;

public class GitHubAPI {

	public static final String ADDRESS = "https://api.github.com/";

	private static Request request(String path, Method method) {
		return new Request(ADDRESS + path, method).header("Authorization", "Client-ID d7044dce12f4f92");
	}

	public static List<Release> getReleases(String owner, String repo) {
		Request request = request("repos/" + owner + "/" + repo + "/releases", Method.GET);
		String json = request.execute();
		Any any = JsonIterator.deserialize(json);
		List<Release> list = new ArrayList<>();
		for (Any release : any) {
			List<GitHubAsset> assets = new ArrayList<>();
			for (Any asset : release.get("assets")) {
				assets.add(new GitHubAsset(asset.toString("name"), asset.toLong("size"), asset.toString("url")));
			}
			list.add(new Release(release.toString("tag"), release.toBoolean("prerelease"), release.toBoolean("draft"), assets));
		}
		return list;
	}

	public static void main(String[] args) {
		long total = 0;
		int count = 20;
		for (int i = 0; i < count; i++) {
			long start = System.currentTimeMillis();
			for (Release release : getReleases("DelfikPro", "Implario")) {
			}
			long end = System.currentTimeMillis();
			total += end - start;
		}
		System.out.println(total / count + "ms");
	}

//	public static String postImage(BufferedImage image) {
//
//		try {
//			String base64 = StringUtils.imageToBase64(image);
//			String json = request("3/upload", POST).body("image", base64).execute();
//			JSONObject j = new JSONObject(json);
//			JSONObject data = j.getJSONObject("data");
//			return data.getString("link");
//		} catch (IOException | JSONException e) {
//			throw new RuntimeException(e);
//		}
//
//	}

}
