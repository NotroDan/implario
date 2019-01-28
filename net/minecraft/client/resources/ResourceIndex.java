package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.Logger;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;

public class ResourceIndex {

	private static final Logger logger = Logger.getInstance();
	private final Map<String, File> resourceMap = Maps.newHashMap();

	public ResourceIndex(File f, String path) {
		if (path == null) return;
		File file1 = new File(f, "objects");
		File file2 = new File(f, "indexes/" + path + ".json");
		BufferedReader bufferedreader = null;

		try {
			bufferedreader = Files.newReader(file2, Charsets.UTF_8);
			JsonObject jsonobject = new JsonParser().parse(bufferedreader).getAsJsonObject();
			JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "objects", null);

			if (jsonobject1 != null) {
				for (Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
					JsonObject jsonobject2 = (JsonObject) entry.getValue();
					String s = entry.getKey();
					String[] astring = s.split("/", 2);
					String s1 = astring.length == 1 ? astring[0] : astring[0] + ":" + astring[1];
					String s2 = JsonUtils.getString(jsonobject2, "hash");
					File file3 = new File(file1, s2.substring(0, 2) + "/" + s2);
					this.resourceMap.put(s1, file3);
				}
			}
		} catch (JsonParseException var20) {
			logger.error("Unable to parse resource index file: " + file2);
		} catch (FileNotFoundException var21) {
			logger.error("Can\'t find the resource index file: " + file2);
		} finally {
			IOUtils.closeQuietly(bufferedreader);
		}
	}

	public Map<String, File> getResourceMap() {
		return this.resourceMap;
	}

}
