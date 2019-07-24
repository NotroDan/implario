package net.minecraft.client.renderer.block.model;

import __google_.util.ByteUnzip;
import __google_.util.ByteZip;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.Utils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

public class BlockPart {

	public final Vector3f positionFrom;
	public final Vector3f positionTo;
	public final Map<EnumFacing, BlockPartFace> mapFaces;
	public final BlockPartRotation partRotation;
	public final boolean shade;

	public BlockPart(Vector3f positionFromIn, Vector3f positionToIn, Map<EnumFacing, BlockPartFace> mapFacesIn, BlockPartRotation partRotationIn, boolean shadeIn) {
		this.positionFrom = positionFromIn;
		this.positionTo = positionToIn;
		this.mapFaces = mapFacesIn;
		this.partRotation = partRotationIn;
		this.shade = shadeIn;
		this.setDefaultUvs();
	}

	private void setDefaultUvs() {
		for (Entry<EnumFacing, BlockPartFace> entry : this.mapFaces.entrySet()) {
			float[] afloat = this.getFaceUvs((EnumFacing) entry.getKey());
			((BlockPartFace) entry.getValue()).blockFaceUV.setUvs(afloat);
		}
	}

	private float[] getFaceUvs(EnumFacing p_178236_1_) {
		float[] afloat;

		switch (p_178236_1_) {
			case DOWN:
			case UP:
				afloat = new float[] {this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
				break;
			case NORTH:
			case SOUTH:
				afloat = new float[] {this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
				break;
			case WEST:
			case EAST:
				afloat = new float[] {this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
				break;
			default:
				throw new NullPointerException();
		}

		return afloat;
	}

	public static BlockPart deserialize(byte array[]) {
		ByteUnzip unzip = new ByteUnzip(array);
		Vector3f vec3f = parse(unzip.getBytes());
		Vector3f vec3fl = parse(unzip.getBytes());

		BlockPartRotation rotation;
		byte next[] = unzip.getBytes();
		if (next.length == 0) rotation = null;
		else {
			ByteUnzip unz = new ByteUnzip(next);
			Vector3f v3f = parse(unz.getBytes());
			v3f.scale(0.0625F);
			EnumFacing.Axis axis = EnumFacing.Axis.values()[unz.getInt()];
			float f = Utils.toFloat(unz.getBytes());
			boolean flag = unz.getBoolean();
			rotation = new BlockPartRotation(v3f, axis, f, flag);
		}

		Map<EnumFacing, BlockPartFace> faces = Maps.newEnumMap(EnumFacing.class);
		int max = unzip.getInt();
		for (int i = 0; i < max; i++)
			faces.put(EnumFacing.VALUES[unzip.getInt()], BlockPartFace.deserialize(unzip.getBytes()));

		boolean flag = unzip.getBoolean();

		return new BlockPart(vec3f, vec3fl, faces, rotation, flag);
	}

	public static Vector3f parse(byte array[]) {
		ByteUnzip unzip = new ByteUnzip(array);
		return new Vector3f(
				Utils.toFloat(unzip.getBytes()),
				Utils.toFloat(unzip.getBytes()),
				Utils.toFloat(unzip.getBytes())
		);
	}

	public static byte[] serialize(BlockPart part) {
		ByteZip zip = new ByteZip();
		zip.add(encode(part.positionFrom)).add(encode(part.positionTo));
		BlockPartRotation rotation = part.partRotation;
		if (rotation == null) zip.add(new byte[] {});
		else {
			ByteZip z = new ByteZip();
			z.add(encode(rotation.origin));
			z.add(rotation.axis.ordinal());
			z.add(Utils.toBytes(rotation.angle));
			z.add(rotation.rescale);
			zip.add(z.build());
		}
		int i = 0;
		for (BlockPartFace face : part.mapFaces.values())
			if (face != null) i++;
		zip.add(i);
		for (Entry<EnumFacing, BlockPartFace> entry : part.mapFaces.entrySet()) {
			if (entry.getValue() == null) continue;
			zip.add(entry.getKey().ordinal()).add(BlockPartFace.serialize(entry.getValue()));
		}
		zip.add(part.shade);
		return zip.build();
	}

	public static byte[] encode(Vector3f vector3f) {
		return new ByteZip().add(Utils.toBytes(vector3f.x)).add(Utils.toBytes(vector3f.y)).add(Utils.toBytes(vector3f.z)).build();
	}

	static class Deserializer implements JsonDeserializer<BlockPart> {

		public BlockPart deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
			JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
			Vector3f vector3f = this.parsePositionFrom(jsonobject);
			Vector3f vector3f1 = this.parsePositionTo(jsonobject);
			BlockPartRotation blockpartrotation = this.parseRotation(jsonobject);
			Map<EnumFacing, BlockPartFace> map = this.parseFacesCheck(p_deserialize_3_, jsonobject);

			if (jsonobject.has("shade") && !JsonUtils.isBoolean(jsonobject, "shade")) {
				throw new JsonParseException("Expected shade to be a Boolean");
			}
			boolean flag = JsonUtils.getBoolean(jsonobject, "shade", true);
			return new BlockPart(vector3f, vector3f1, map, blockpartrotation, flag);
		}

		private BlockPartRotation parseRotation(JsonObject p_178256_1_) {
			BlockPartRotation blockpartrotation = null;

			if (p_178256_1_.has("rotation")) {
				JsonObject jsonobject = JsonUtils.getJsonObject(p_178256_1_, "rotation");
				Vector3f vector3f = this.parsePosition(jsonobject, "origin");
				vector3f.scale(0.0625F);
				EnumFacing.Axis enumfacing$axis = this.parseAxis(jsonobject);
				float f = this.parseAngle(jsonobject);
				boolean flag = JsonUtils.getBoolean(jsonobject, "rescale", false);
				blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
			}

			return blockpartrotation;
		}

		private float parseAngle(JsonObject p_178255_1_) {
			float f = JsonUtils.getFloat(p_178255_1_, "angle");

			if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
				throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
			}
			return f;
		}

		private EnumFacing.Axis parseAxis(JsonObject p_178252_1_) {
			String s = JsonUtils.getString(p_178252_1_, "axis");
			EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.byName(s.toLowerCase());

			if (enumfacing$axis == null) {
				throw new JsonParseException("Invalid rotation axis: " + s);
			}
			return enumfacing$axis;
		}

		private Map<EnumFacing, BlockPartFace> parseFacesCheck(JsonDeserializationContext p_178250_1_, JsonObject p_178250_2_) {
			Map<EnumFacing, BlockPartFace> map = this.parseFaces(p_178250_1_, p_178250_2_);

			if (map.isEmpty()) {
				throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
			}
			return map;
		}

		private Map<EnumFacing, BlockPartFace> parseFaces(JsonDeserializationContext p_178253_1_, JsonObject p_178253_2_) {
			Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
			JsonObject jsonobject = JsonUtils.getJsonObject(p_178253_2_, "faces");

			for (Entry<String, JsonElement> entry : jsonobject.entrySet()) {
				EnumFacing enumfacing = this.parseEnumFacing((String) entry.getKey());
				map.put(enumfacing, (BlockPartFace) p_178253_1_.deserialize((JsonElement) entry.getValue(), BlockPartFace.class));
			}

			return map;
		}

		private EnumFacing parseEnumFacing(String name) {
			EnumFacing enumfacing = EnumFacing.byName(name);

			if (enumfacing == null) {
				throw new JsonParseException("Unknown facing: " + name);
			}
			return enumfacing;
		}

		private Vector3f parsePositionTo(JsonObject p_178247_1_) {
			Vector3f vector3f = this.parsePosition(p_178247_1_, "to");

			if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
				return vector3f;
			}
			throw new JsonParseException("\'to\' specifier exceeds the allowed boundaries: " + vector3f);
		}

		private Vector3f parsePositionFrom(JsonObject p_178249_1_) {
			Vector3f vector3f = this.parsePosition(p_178249_1_, "from");

			if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
				return vector3f;
			}
			throw new JsonParseException("\'from\' specifier exceeds the allowed boundaries: " + vector3f);
		}

		private Vector3f parsePosition(JsonObject p_178251_1_, String p_178251_2_) {
			JsonArray jsonarray = JsonUtils.getJsonArray(p_178251_1_, p_178251_2_);

			if (jsonarray.size() != 3) {
				throw new JsonParseException("Expected 3 " + p_178251_2_ + " values, found: " + jsonarray.size());
			}
			float[] afloat = new float[3];

			for (int i = 0; i < afloat.length; ++i) {
				afloat[i] = JsonUtils.getFloat(jsonarray.get(i), p_178251_2_ + "[" + i + "]");
			}

			return new Vector3f(afloat[0], afloat[1], afloat[2]);
		}

	}

}
