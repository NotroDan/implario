package net.minecraft.client.renderer.block.model;

import __google_.util.ByteUnzip;
import __google_.util.ByteZip;
import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.logging.Log;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class ModelBlock {

	public static final Gson SERIALIZER = new GsonBuilder()
			.registerTypeAdapter(ModelBlock.class, new ModelBlock.Deserializer())
			.registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer())
			.registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer())
			.registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer())
			.registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer())
			.registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer())
			.create();

	protected final Map<String, String> textures;
	private final List<BlockPart> elements;
	private final boolean gui3d;
	private final boolean ambientOcclusion;
	public String name;
	protected ModelBlock parent;
	protected ResourceLocation parentLocation;
	private ItemCameraTransforms cameraTransforms;

	protected ModelBlock(List<BlockPart> elements, Map<String, String> textures, boolean ambientOcclusion, boolean gui3d, ItemCameraTransforms cameraTransforms) {
		this(null, elements, textures, ambientOcclusion, gui3d, cameraTransforms);
	}

	protected ModelBlock(ResourceLocation resourceLocation, Map<String, String> textures, boolean ambientOcclusion, boolean gui3d, ItemCameraTransforms cameraTransforms) {
		this(resourceLocation, Collections.emptyList(), textures, ambientOcclusion, gui3d, cameraTransforms);
	}

	private ModelBlock(ResourceLocation parentLocation, List<BlockPart> elements, Map<String, String> textures, boolean ambientOcclusion, boolean gui3d,
					   ItemCameraTransforms cameraTransforms) {
		this.name = "";
		this.elements = elements;
		this.ambientOcclusion = ambientOcclusion;
		this.gui3d = gui3d;
		this.textures = textures;
		this.parentLocation = parentLocation;
		this.cameraTransforms = cameraTransforms;
	}

	public static ModelBlock readModel(InputStream in) {
		byte[] data;
		int i = 0;
		try {
			data = new byte[in.available()];
			while (true) {
				int read = in.read();
				if (read == -1) break;
				data[i++] = (byte) read;
			}
		} catch (IOException ex) {
			Log.MAIN.exception(ex);
			return null;
		}
		if (data[0] == 0) return readBinModel(data);
		else return readJsonModel(new String(data, StandardCharsets.UTF_8));
	}

	public static ModelBlock readJsonModel(String str) {
		return SERIALIZER.fromJson(str, ModelBlock.class);
	}

	public static void checkModelHierarchy(Map<ResourceLocation, ModelBlock> p_178312_0_) {
		for (ModelBlock modelblock : p_178312_0_.values()) {
			try {
				ModelBlock modelblock1 = modelblock.parent;

				for (ModelBlock modelblock2 = modelblock1.parent; modelblock1 != modelblock2; modelblock2 = modelblock2.parent.parent) {
					modelblock1 = modelblock1.parent;
				}

				throw new ModelBlock.LoopException();
			} catch (NullPointerException ignored) {
			}
		}
	}

	public static ModelBlock readBinModel(byte[] array) {
		ByteUnzip unzip = new ByteUnzip(array);
		unzip.getBytes();
		int size = unzip.getInt();
		List<BlockPart> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			list.add(BlockPart.deserialize(unzip.getBytes()));
		byte[] prnt = unzip.getBytes();
		String parent = prnt.length == 0 ? null : new String(prnt);
		size = unzip.getInt();
		Map<String, String> textures = new HashMap<>(size);
		for (int i = 0; i < size; i++)
			textures.put(unzip.getString(), unzip.getString());
		boolean flag = unzip.getBoolean();
		ItemCameraTransforms itemCamera = ItemCameraTransforms.DEFAULT;
		byte[] next = unzip.getBytes();
		if (next.length != 0)
			itemCamera = ItemCameraTransforms.deserialize(next);
		return list.isEmpty() ?
				new ModelBlock(new ResourceLocation(parent), textures, flag, true, itemCamera) :
				new ModelBlock(list, textures, flag, true, itemCamera);
	}

	public static byte[] serialize(ModelBlock block) {
		ByteZip zip = new ByteZip();
		zip.add(new byte[] {});
		int size = block.elements.size();
		zip.add(size);
		for (int i = 0; i < size; i++)
			zip.add(BlockPart.serialize(block.elements.get(i)));
		zip.add(block.parentLocation == null ? new byte[] {} : block.parentLocation.getResourceDomain() + ":" + block.parentLocation.getResourcePath());
		size = block.textures.size();
		zip.add(size);
		for (Entry<String, String> entry : block.textures.entrySet())
			zip.add(entry.getKey()).add(entry.getValue());
		zip.add(block.ambientOcclusion);
		if (ItemCameraTransforms.DEFAULT.equals(block.cameraTransforms)) zip.add(new byte[] {});
		else zip.add(ItemCameraTransforms.serialize(block.cameraTransforms));
		return zip.build();
	}

	public List<BlockPart> getElements() {
		return this.hasParent() ? this.parent.getElements() : this.elements;
	}

	private boolean hasParent() {
		return this.parent != null;
	}

	public boolean isAmbientOcclusion() {
		return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
	}

	public boolean isGui3d() {
		return this.gui3d;
	}

	public boolean isResolved() {
		return this.parentLocation == null || this.parent != null && this.parent.isResolved();
	}

	public void getParentFromMap(Map<ResourceLocation, ModelBlock> p_178299_1_) {
		if (this.parentLocation != null) {
			this.parent = p_178299_1_.get(this.parentLocation);
		}
	}

	public boolean isTexturePresent(String textureName) {
		return !"missingno".equals(this.resolveTextureName(textureName));
	}

	public String resolveTextureName(String textureName) {
		if (!this.startsWithHash(textureName)) {
			textureName = '#' + textureName;
		}

		return this.resolveTextureName(textureName, new ModelBlock.Bookkeep(this));
	}

	private String resolveTextureName(String textureName, ModelBlock.Bookkeep p_178302_2_) {
		if (this.startsWithHash(textureName)) {
			if (this == p_178302_2_.modelExt) {
				Log.MAIN.warn("Unable to resolve texture due to upward reference: " + textureName + " in " + this.name);
				return "missingno";
			}
			String s = this.textures.get(textureName.substring(1));

			if (s == null && this.hasParent()) {
				s = this.parent.resolveTextureName(textureName, p_178302_2_);
			}

			p_178302_2_.modelExt = this;

			if (s != null && this.startsWithHash(s)) {
				s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
			}

			return s != null && !this.startsWithHash(s) ? s : "missingno";
		}
		return textureName;
	}

	private boolean startsWithHash(String hash) {
		return hash.charAt(0) == 35;
	}

	public ResourceLocation getParentLocation() {
		return this.parentLocation;
	}

	public ModelBlock getRootModel() {
		return this.hasParent() ? this.parent.getRootModel() : this;
	}

	public ItemCameraTransforms func_181682_g() {
		ItemTransformVec3f itemtransformvec3f = this.func_181681_a(ItemCameraTransforms.TransformType.THIRD_PERSON);
		ItemTransformVec3f itemtransformvec3f1 = this.func_181681_a(ItemCameraTransforms.TransformType.FIRST_PERSON);
		ItemTransformVec3f itemtransformvec3f2 = this.func_181681_a(ItemCameraTransforms.TransformType.HEAD);
		ItemTransformVec3f itemtransformvec3f3 = this.func_181681_a(ItemCameraTransforms.TransformType.GUI);
		ItemTransformVec3f itemtransformvec3f4 = this.func_181681_a(ItemCameraTransforms.TransformType.GROUND);
		ItemTransformVec3f itemtransformvec3f5 = this.func_181681_a(ItemCameraTransforms.TransformType.FIXED);
		return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5);
	}

	private ItemTransformVec3f func_181681_a(ItemCameraTransforms.TransformType p_181681_1_) {
		return this.parent != null && !this.cameraTransforms.func_181687_c(p_181681_1_) ? this.parent.func_181681_a(p_181681_1_) : this.cameraTransforms.getTransform(p_181681_1_);
	}

	static final class Bookkeep {

		public final ModelBlock model;
		public ModelBlock modelExt;

		private Bookkeep(ModelBlock p_i46223_1_) {
			this.model = p_i46223_1_;
		}

	}

	public static class Deserializer implements JsonDeserializer<ModelBlock> {

		public ModelBlock deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
			JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
			List<BlockPart> list = this.getModelElements(p_deserialize_3_, jsonobject);
			String s = this.getParent(jsonobject);
			boolean flag = StringUtils.isEmpty(s);
			boolean flag1 = list.isEmpty();

			if (flag1 && flag) {
				throw new JsonParseException("BlockModel requires either elements or parent, found neither");
			}
			if (!flag && !flag1) {
				throw new JsonParseException("BlockModel requires either elements or parent, found both");
			}
			Map<String, String> map = this.getTextures(jsonobject);
			boolean flag2 = this.getAmbientOcclusionEnabled(jsonobject);
			ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;

			if (jsonobject.has("display")) {
				JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "display");
				itemcameratransforms = p_deserialize_3_.deserialize(jsonobject1, ItemCameraTransforms.class);
			}

			return flag1 ? new ModelBlock(new ResourceLocation(s), map, flag2, true, itemcameratransforms)
					: new ModelBlock(list, map, flag2, true, itemcameratransforms);
		}

		private Map<String, String> getTextures(JsonObject p_178329_1_) {
			Map<String, String> map = Maps.newHashMap();

			if (p_178329_1_.has("textures")) {
				JsonObject jsonobject = p_178329_1_.getAsJsonObject("textures");

				for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
					map.put(entry.getKey(), entry.getValue().getAsString());
				}
			}

			return map;
		}

		private String getParent(JsonObject p_178326_1_) {
			return JsonUtils.getString(p_178326_1_, "parent", "");
		}

		protected boolean getAmbientOcclusionEnabled(JsonObject p_178328_1_) {
			return JsonUtils.getBoolean(p_178328_1_, "ambientocclusion", true);
		}

		protected List<BlockPart> getModelElements(JsonDeserializationContext ctx, JsonObject p_178325_2_) {
			List<BlockPart> list = new ArrayList<>();

			if (p_178325_2_.has("elements")) {
				for (JsonElement jsonelement : JsonUtils.getJsonArray(p_178325_2_, "elements")) {
					list.add(ctx.deserialize(jsonelement, BlockPart.class));
				}
			}

			return list;
		}

	}

	public static class LoopException extends RuntimeException {
	}

}
