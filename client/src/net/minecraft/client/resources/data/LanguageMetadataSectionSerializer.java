package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.Language;
import net.minecraft.util.JsonUtils;

import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.Set;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer<LanguageMetadataSection> {

	public LanguageMetadataSection deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
		JsonObject jsonobject = json.getAsJsonObject();
		Set<Language> set = Sets.newHashSet();

		for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
			String s = entry.getKey();
			JsonObject jsonobject1 = JsonUtils.getJsonObject(entry.getValue(), "language");
			String s1 = JsonUtils.getString(jsonobject1, "region");
			String s2 = JsonUtils.getString(jsonobject1, "name");

			if (s1.isEmpty()) {
				throw new JsonParseException("Invalid language->\'" + s + "\'->region: empty value");
			}

			if (s2.isEmpty()) {
				throw new JsonParseException("Invalid language->\'" + s + "\'->name: empty value");
			}

			if (!set.add(new Language(s, s1, s2))) {
				throw new JsonParseException("Duplicate language->\'" + s + "\' defined");
			}
		}

		return new LanguageMetadataSection(set);
	}

	/**
	 * The name of this section type as it appears in JSON.
	 */
	public String getSectionName() {
		return "language";
	}

}
