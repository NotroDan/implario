package net.minecraft.client.network.services.imgur;


import net.minecraft.client.network.services.Method;
import net.minecraft.client.network.services.Request;
import net.minecraft.util.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static net.minecraft.client.network.services.Method.POST;

public class ImgurAPI {

	public static final String ADDRESS = "https://api.imgur.com/";

	private static Request request(String path, Method method) {
		return new Request(ADDRESS + path, method).header("Authorization", "Client-ID d7044dce12f4f92");
	}

	public static String postImage(BufferedImage image) {

		try {
			String base64 = StringUtils.imageToBase64(image);
			String json = request("3/upload", POST).body("image", base64).execute();
			JSONObject j = new JSONObject(json);
			JSONObject data = j.getJSONObject("data");
			return data.getString("link");
		} catch (IOException | JSONException e) {
			throw new RuntimeException(e);
		}

	}


}
