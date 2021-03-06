package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.settings.Settings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import optifine.Config;
import shadersmod.client.SVertexBuilder;

public class BlockRendererDispatcher implements IResourceManagerReloadListener {

	private BlockModelShapes blockModelShapes;
	private final BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
	private final ChestRenderer chestRenderer = new ChestRenderer();
	private final BlockFluidRenderer fluidRenderer = new BlockFluidRenderer();


	public BlockRendererDispatcher(BlockModelShapes blockModelShapesIn) {
		this.blockModelShapes = blockModelShapesIn;
	}

	public BlockModelShapes getBlockModelShapes() {
		return this.blockModelShapes;
	}

	public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess) {
		Block block = state.getBlock();
		int renderType = block.getRenderType();
		if (renderType != 3) return;

		state = block.getActualState(state, blockAccess, pos);
		IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);
		IBakedModel ibakedmodel1 = new SimpleBakedModel.Builder(ibakedmodel, texture).makeBakedModel();
		this.blockModelRenderer.renderModel(blockAccess, ibakedmodel1, state, pos, Tessellator.getInstance().getWorldRenderer());
	}

	public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess world, WorldRenderer worldRendererIn) {
		try {
			int i = state.getBlock().getRenderType();

			if (i == -1) return false;
			switch (i) {
				case 1:
					if (Config.isShaders()) SVertexBuilder.pushEntity(state, worldRendererIn);
					boolean success = this.fluidRenderer.renderFluid(world, state, pos, worldRendererIn);
					if (Config.isShaders()) SVertexBuilder.popEntity(worldRendererIn);
					return success;

				case 3:
					IBakedModel ibakedmodel = this.getModelFromBlockState(state, world, pos);
					if (Config.isShaders()) SVertexBuilder.pushEntity(state, worldRendererIn);
					boolean flag = this.blockModelRenderer.renderModel(world, ibakedmodel, state, pos, worldRendererIn);
					if (Config.isShaders()) SVertexBuilder.popEntity(worldRendererIn);
					return flag;

				case 2:
				default:
					return false;
			}
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
			CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
			throw new ReportedException(crashreport);
		}
	}

	public BlockModelRenderer getBlockModelRenderer() {
		return this.blockModelRenderer;
	}

	private IBakedModel getBakedModel(IBlockState state, BlockPos pos) {
		IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);

		if (pos != null && Settings.BLOCK_ALTERNATIVES.b() && ibakedmodel instanceof WeightedBakedModel) {
			ibakedmodel = ((WeightedBakedModel) ibakedmodel).getAlternativeModel(MathHelper.getPositionRandom(pos));
		}

		return ibakedmodel;
	}

	public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		Block block = state.getBlock();

		if (worldIn.getWorldType() != WorldType.DEBUG) {
			try {
				state = block.getActualState(state, worldIn, pos);
			} catch (Exception ignored) {}
		}

		IBakedModel ibakedmodel = this.blockModelShapes.getModelForState(state);

		if (pos != null && Settings.BLOCK_ALTERNATIVES.b() && ibakedmodel instanceof WeightedBakedModel)
			ibakedmodel = ((WeightedBakedModel) ibakedmodel).getAlternativeModel(MathHelper.getPositionRandom(pos));

		return ibakedmodel;
	}

	public void renderBlockBrightness(IBlockState state, float brightness) {
		int i = state.getBlock().getRenderType();

		if (i != -1) {
			switch (i) {
				case 1:
				default:
					break;

				case 2:
					this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
					break;

				case 3:
					IBakedModel ibakedmodel = this.getBakedModel(state, null);
					this.blockModelRenderer.renderModelBrightness(ibakedmodel, state, brightness, true);
			}
		}
	}

	public boolean isRenderTypeChest(Block p_175021_1_, int p_175021_2_) {
		if (p_175021_1_ == null) {
			return false;
		}
		int i = p_175021_1_.getRenderType();
		return i != 3 && i == 2;
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.fluidRenderer.initAtlasSprites();
	}

}
