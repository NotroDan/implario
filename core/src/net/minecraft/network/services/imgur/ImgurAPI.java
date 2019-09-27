package net.minecraft.network.services.imgur;


import net.minecraft.network.services.Method;
import net.minecraft.network.services.Request;
import org.apache.commons.lang3.NotImplementedException;

import java.awt.image.BufferedImage;

public class ImgurAPI {

	public static final String ADDRESS = "https://api.imgur.com/";

	private static Request request(String path, Method method) {
		return new Request(ADDRESS + path, method).header("Authorization", "Client-ID d7044dce12f4f92");
	}

	public static String postImage(BufferedImage image) {
		throw new NotImplementedException("nope");
//		try {
//			String base64 = StringUtils.imageToBase64(image);
//			String json = request("3/upload", POST).body("image", base64).execute();
//			JSONObject j = new JSONObject(json);
//			JSONObject data = j.getJSONObject("data");
//			return data.getString("link");
//		} catch (IOException | JSONException e) {
//			throw new RuntimeException(e);
//		}

	}


}
