package net.minecraft.util;

import com.google.gson.*;
import net.minecraft.util.chat.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

public interface IChatComponent extends Iterable<IChatComponent> {

	ChatStyle getChatStyle();

	IChatComponent setChatStyle(ChatStyle style);

	/**
	 * Appends the given text to the end of this component.
	 */
	IChatComponent appendText(String text);

	/**
	 * Appends the given component to the end of this one.
	 */
	IChatComponent appendSibling(IChatComponent component);

	/**
	 * Gets the text of this component, without any special formatting codes added, for chat.  TODO: why is this two
	 * different methods?
	 */
	String getUnformattedTextForChat();

	/**
	 * Get the text of this component, <em>and all child components</em>, with all special formatting codes removed.
	 */
	String getUnformattedText();

	/**
	 * Gets the text of this component, with formatting codes added for rendering.
	 */
	String getFormattedText();

	List<IChatComponent> getSiblings();

	/**
	 * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
	 */
	IChatComponent createCopy();

	class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent> {

		private static final Gson GSON;
		static {
			GsonBuilder gsonbuilder = new GsonBuilder();
			gsonbuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
			gsonbuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
			gsonbuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
			GSON = gsonbuilder.create();
		}

		public static String componentToJson(IChatComponent component) {
			return GSON.toJson(component);
		}

		public static IChatComponent jsonToComponent(String json) {
			return GSON.fromJson(json, IChatComponent.class);
		}

		public IChatComponent deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
			if (json.isJsonPrimitive()) {
				return new ChatComponentText(json.getAsString());
			}
			if (!json.isJsonObject()) {
				if (json.isJsonArray()) {
					JsonArray jsonarray1 = json.getAsJsonArray();
					IChatComponent ichatcomponent1 = null;

					for (JsonElement jsonelement : jsonarray1) {
						IChatComponent ichatcomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), ctx);

						if (ichatcomponent1 == null) {
							ichatcomponent1 = ichatcomponent2;
						} else {
							ichatcomponent1.appendSibling(ichatcomponent2);
						}
					}

					return ichatcomponent1;
				}
				throw new JsonParseException("Don\'t know how to turn " + json.toString() + " into a Component");
			}
			JsonObject jsonobject = json.getAsJsonObject();
			IChatComponent ichatcomponent;

			if (jsonobject.has("text")) {
				ichatcomponent = new ChatComponentText(jsonobject.get("text").getAsString());
			} else if (jsonobject.has("translate")) {
				String s = jsonobject.get("translate").getAsString();

				if (jsonobject.has("with")) {
					JsonArray jsonarray = jsonobject.getAsJsonArray("with");
					Object[] aobject = new Object[jsonarray.size()];

					for (int i = 0; i < aobject.length; ++i) {
						aobject[i] = this.deserialize(jsonarray.get(i), type, ctx);

						if (aobject[i] instanceof ChatComponentText) {
							ChatComponentText chatcomponenttext = (ChatComponentText) aobject[i];

							if (chatcomponenttext.getChatStyle().isEmpty() && chatcomponenttext.getSiblings().isEmpty()) {
								aobject[i] = chatcomponenttext.getChatComponentText_TextValue();
							}
						}
					}

					ichatcomponent = new ChatComponentTranslation(s, aobject);
				} else {
					ichatcomponent = new ChatComponentTranslation(s);
				}
			} else if (jsonobject.has("score")) {
				JsonObject jsonobject1 = jsonobject.getAsJsonObject("score");

				if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
					throw new JsonParseException("A score component needs a least a name and an objective");
				}

				ichatcomponent = new ChatComponentScore(JsonUtils.getString(jsonobject1, "name"), JsonUtils.getString(jsonobject1, "objective"));

				if (jsonobject1.has("value")) {
					((ChatComponentScore) ichatcomponent).setValue(JsonUtils.getString(jsonobject1, "value"));
				}
			} else {
				if (!jsonobject.has("selector")) {
					throw new JsonParseException("Don\'t know how to turn " + json.toString() + " into a Component");
				}

				ichatcomponent = new ChatComponentSelector(JsonUtils.getString(jsonobject, "selector"));
			}

			if (jsonobject.has("extra")) {
				JsonArray jsonarray2 = jsonobject.getAsJsonArray("extra");

				if (jsonarray2.size() <= 0) {
					throw new JsonParseException("Unexpected empty array of components");
				}

				for (int j = 0; j < jsonarray2.size(); ++j) {
					ichatcomponent.appendSibling(this.deserialize(jsonarray2.get(j), type, ctx));
				}
			}

			ichatcomponent.setChatStyle(ctx.deserialize(json, ChatStyle.class));
			return ichatcomponent;
		}

		private void serializeChatStyle(ChatStyle style, JsonObject object, JsonSerializationContext ctx) {
			JsonElement jsonelement = ctx.serialize(style);

			if (jsonelement.isJsonObject()) {
				JsonObject jsonobject = (JsonObject) jsonelement;

				for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
					object.add(entry.getKey(), entry.getValue());
				}
			}
		}

		public JsonElement serialize(IChatComponent c, Type t, JsonSerializationContext ctx) {
			if (c instanceof ChatComponentText && c.getChatStyle().isEmpty() && c.getSiblings().isEmpty()) {
				return new JsonPrimitive(((ChatComponentText) c).getChatComponentText_TextValue());
			}
			JsonObject jsonobject = new JsonObject();

			if (!c.getChatStyle().isEmpty()) {
				this.serializeChatStyle(c.getChatStyle(), jsonobject, ctx);
			}

			if (!c.getSiblings().isEmpty()) {
				JsonArray jsonarray = new JsonArray();

				for (IChatComponent ichatcomponent : c.getSiblings()) {
					jsonarray.add(this.serialize(ichatcomponent, ichatcomponent.getClass(), ctx));
				}

				jsonobject.add("extra", jsonarray);
			}

			if (c instanceof ChatComponentText) {
				jsonobject.addProperty("text", ((ChatComponentText) c).getChatComponentText_TextValue());
			} else if (c instanceof ChatComponentTranslation) {
				ChatComponentTranslation chatcomponenttranslation = (ChatComponentTranslation) c;
				jsonobject.addProperty("translate", chatcomponenttranslation.getKey());

				if (chatcomponenttranslation.getFormatArgs() != null && chatcomponenttranslation.getFormatArgs().length > 0) {
					JsonArray jsonarray1 = new JsonArray();

					for (Object object : chatcomponenttranslation.getFormatArgs()) {
						if (object instanceof IChatComponent) {
							jsonarray1.add(this.serialize((IChatComponent) object, object.getClass(), ctx));
						} else {
							jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
						}
					}

					jsonobject.add("with", jsonarray1);
				}
			} else if (c instanceof ChatComponentScore) {
				ChatComponentScore chatcomponentscore = (ChatComponentScore) c;
				JsonObject jsonobject1 = new JsonObject();
				jsonobject1.addProperty("name", chatcomponentscore.getName());
				jsonobject1.addProperty("objective", chatcomponentscore.getObjective());
				jsonobject1.addProperty("value", chatcomponentscore.getUnformattedTextForChat());
				jsonobject.add("score", jsonobject1);
			} else {
				if (!(c instanceof ChatComponentSelector)) {
					throw new IllegalArgumentException("Don\'t know how to serialize " + c + " as a Component");
				}

				ChatComponentSelector chatcomponentselector = (ChatComponentSelector) c;
				jsonobject.addProperty("selector", chatcomponentselector.getSelector());
			}

			return jsonobject;
		}
	}

}
