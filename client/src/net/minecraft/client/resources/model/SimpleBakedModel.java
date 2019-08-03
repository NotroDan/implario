package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class SimpleBakedModel implements IBakedModel {
	protected final List<BakedQuad> generalQuads;
	protected final List<List<BakedQuad>> faceQuads;
	protected final boolean ambientOcclusion;
	protected final boolean gui3d;
	protected final TextureAtlasSprite texture;
	protected final ItemCameraTransforms cameraTransforms;

	public SimpleBakedModel(List<BakedQuad> generalQuads, List<List<BakedQuad>> faceQuads, boolean ambientOcclusion,
							boolean isGui3d, TextureAtlasSprite texture, ItemCameraTransforms cameraTransforms) {
		this.generalQuads = generalQuads;
		this.faceQuads = faceQuads;
		this.ambientOcclusion = ambientOcclusion;
		this.gui3d = isGui3d;
		this.texture = texture;
		this.cameraTransforms = cameraTransforms;
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing facing) {
		return faceQuads.get(facing.ordinal());
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return generalQuads;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return ambientOcclusion;
	}

	@Override
	public boolean isGui3d() {
		return gui3d;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return texture;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return cameraTransforms;
	}

	public static class Builder {
		private final List<BakedQuad> builderGeneralQuads;
		private final List<List<BakedQuad>> builderFaceQuads;
		private final boolean builderAmbientOcclusion;
		private TextureAtlasSprite builderTexture;
		private boolean builderGui3d;
		private ItemCameraTransforms builderCameraTransforms;

		public Builder(ModelBlock model) {
			this(model.isAmbientOcclusion(), model.isGui3d(), model.func_181682_g());
		}

		public Builder(IBakedModel model, TextureAtlasSprite texture) {
			this(model.isAmbientOcclusion(), model.isGui3d(), model.getItemCameraTransforms());
			builderTexture = model.getParticleTexture();

			for (EnumFacing enumfacing : EnumFacing.values())
				addFaceBreakingFours(model, texture, enumfacing);

			addGeneralBreakingFours(model, texture);
		}

		private void addFaceBreakingFours(IBakedModel model, TextureAtlasSprite texture, EnumFacing facing) {
			for (BakedQuad bakedquad : model.getFaceQuads(facing))
				addFaceQuad(facing, new BreakingFour(bakedquad, texture));
		}

		private void addGeneralBreakingFours(IBakedModel model, TextureAtlasSprite texture) {
			for (BakedQuad bakedquad : model.getGeneralQuads())
				addGeneralQuad(new BreakingFour(bakedquad, texture));
		}

		private Builder(boolean ambientOcclusion, boolean gui3d, ItemCameraTransforms cameraTransforms) {
			builderGeneralQuads = new ArrayList<>();
			builderFaceQuads = Lists.newArrayListWithCapacity(6);

			int size = EnumFacing.VALUES.length;
			for (int i = 0; i < size; i++)
				this.builderFaceQuads.add(new ArrayList<>());

			builderAmbientOcclusion = ambientOcclusion;
			builderGui3d = gui3d;
			builderCameraTransforms = cameraTransforms;
		}

		public SimpleBakedModel.Builder addFaceQuad(EnumFacing facing, BakedQuad quad) {
			builderFaceQuads.get(facing.ordinal()).add(quad);
			return this;
		}

		public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad) {
			builderGeneralQuads.add(quad);
			return this;
		}

		public SimpleBakedModel.Builder setTexture(TextureAtlasSprite texture) {
			this.builderTexture = texture;
			return this;
		}

		public IBakedModel makeBakedModel() {
			if (builderTexture == null)
				throw new RuntimeException("Missing particle!");
			return new SimpleBakedModel(builderGeneralQuads, builderFaceQuads, builderAmbientOcclusion,
					builderGui3d, builderTexture, builderCameraTransforms);
		}
	}
}
