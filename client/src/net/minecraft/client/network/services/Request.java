package net.minecraft.client.network.services;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Request {

	private final String address;
	private final Method method;
	private final Map<String, String> parameters = new HashMap<>();
	private final Map<String, String> headers = new HashMap<>();
	private final Map<String, String> body = new HashMap<>();

	public Request(String address, Method method) {
		this.address = address;
		this.method = method;
	}

	private static String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	public HttpURLConnection prepare() {
		try {
			URL url = new URL(address + bakeParameters());
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(method.name());
			//			for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) System.out.println(stringStringEntry);
			headers.forEach(con::setRequestProperty);
			if (!body.isEmpty()) {
				con.setDoOutput(true);
				DataOutputStream out = new DataOutputStream(con.getOutputStream());
				for (Iterator<Map.Entry<String, String>> iterator = body.entrySet().iterator(); iterator.hasNext(); ) {
					Map.Entry<String, String> entry = iterator.next();
					//					System.out.println(entry.getKey() + "=" + entry.getValue());
					out.writeBytes(entry.getKey());
					out.writeBytes("=");
					out.writeBytes(entry.getValue());
					if (iterator.hasNext()) out.writeBytes("&");
				}
				out.flush();
				out.close();
			}

			return con;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String execute() {
		try {
			HttpURLConnection con = prepare();
			//			System.out.println(con.getResponseCode());
			//			System.out.println(con.getResponseMessage());

			InputStream inputStream = con.getInputStream();
			int i;
			StringBuilder sb = new StringBuilder();
			while ((i = inputStream.read()) != -1) sb.append((char) i);

			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Request header(String header, String value) {
		headers.put(header, value);
		return this;
	}

	public Request body(String key, String value) {
		body.put(encode(key), encode(value));
		return this;
	}

	private String bakeParameters() {
		if (parameters.isEmpty()) return "";
		StringBuilder sb = new StringBuilder(address.contains("?") ? "&" : "?");
		for (Iterator<Map.Entry<String, String>> iterator = parameters.entrySet().iterator(); iterator.hasNext(); ) {
			Map.Entry<String, String> entry = iterator.next();
			sb.append(encode(entry.getKey())).append("=").append(entry.getValue());
			if (iterator.hasNext()) sb.append("&");
		}
		return sb.toString();

	}

	public Map<String, String> getBody() {
		return body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getAddress() {
		return address;
	}

}
