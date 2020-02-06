package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.minecraft.client.gui.font.MCFontRenderer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class TileEntityRendererDispatcher {

	@Getter
	private Map<Class<? extends TileEntity>, TileEntitySpecialRenderer<? extends TileEntity>> mapSpecialRenderers = new HashMap<>();
	public static TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
	private MCFontRenderer fontRenderer;

	/**
	 * The player's current X position (same as playerX)
	 */
	public static double staticPlayerX;

	/**
	 * The player's current Y position (same as playerY)
	 */
	public static double staticPlayerY;

	/**
	 * The player's current Z position (same as playerZ)
	 */
	public static double staticPlayerZ;
	public TextureManager renderEngine;
	public World worldObj;
	public Entity entity;
	public float entityYaw;
	public float entityPitch;
	public double entityX;
	public double entityY;
	public double entityZ;

	private TileEntityRendererDispatcher() {
		register(TileEntitySign.class, new TileEntitySignRenderer());
		register(TileEntityPiston.class, new TileEntityPistonRenderer());
		register(TileEntityChest.class, new TileEntityChestRenderer());
		register(TileEntityEnderChest.class, new TileEntityEnderChestRenderer());
		register(TileEntityEnchantmentTable.class, new TileEntityEnchantmentTableRenderer());
		register(TileEntityEndPortal.class, new TileEntityEndPortalRenderer());
		register(TileEntityBeacon.class, new TileEntityBeaconRenderer());
		register(TileEntitySkull.class, new TileEntitySkullRenderer());
		register(TileEntityBanner.class, new TileEntityBannerRenderer());

	}

	public <T extends TileEntity> void register(Class<T> c, TileEntitySpecialRenderer<T> renderer) {
		this.mapSpecialRenderers.put(c, renderer);
		renderer.setRendererDispatcher(this);
	}

	@SuppressWarnings ("unchecked")
	public <T extends TileEntity> TileEntitySpecialRenderer<T> getSpecialRendererByClass(Class<? extends TileEntity> teClass) {
		TileEntitySpecialRenderer<? extends TileEntity> tileentityspecialrenderer = this.mapSpecialRenderers.get(teClass);

		if (tileentityspecialrenderer == null && teClass != TileEntity.class) {
			tileentityspecialrenderer = this.getSpecialRendererByClass((Class<? extends TileEntity>) teClass.getSuperclass());
			if (tileentityspecialrenderer != null) this.mapSpecialRenderers.put(teClass, tileentityspecialrenderer);
		}

		return (TileEntitySpecialRenderer<T>) tileentityspecialrenderer;
	}

	public <T extends TileEntity> TileEntitySpecialRenderer<T> getSpecialRenderer(TileEntity tileEntityIn) {
		return (TileEntitySpecialRenderer<T>) (tileEntityIn == null ? null : this.getSpecialRendererByClass(tileEntityIn.getClass()));
	}

	public void cacheActiveRenderInfo(World worldIn, TextureManager textureManagerIn, MCFontRenderer fontrendererIn, Entity entityIn, float partialTicks) {
		if (this.worldObj != worldIn) {
			this.setWorld(worldIn);
		}

		this.renderEngine = textureManagerIn;
		this.entity = entityIn;
		this.fontRenderer = fontrendererIn;
		this.entityYaw = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
		this.entityPitch = entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks;
		this.entityX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
		this.entityY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
		this.entityZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
	}

	public void renderTileEntity(TileEntity tileentityIn, float partialTicks, int destroyStage) {
		if (tileentityIn.getDistanceSq(this.entityX, this.entityY, this.entityZ) < tileentityIn.getMaxRenderDistanceSquared()) {
			int i = this.worldObj.getCombinedLight(tileentityIn.getPos(), 0);
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
			G.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos blockpos = tileentityIn.getPos();
			this.renderTileEntityAt(tileentityIn, (double) blockpos.getX() - staticPlayerX, (double) blockpos.getY() - staticPlayerY, (double) blockpos.getZ() - staticPlayerZ, partialTicks,
					destroyStage);
		}
	}

	/**
	 * Render this TileEntity at a given set of coordinates
	 */
	public void renderTileEntityAt(TileEntity tileEntityIn, double x, double y, double z, float partialTicks) {
		this.renderTileEntityAt(tileEntityIn, x, y, z, partialTicks, -1);
	}

	public void renderTileEntityAt(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
		TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = this.getSpecialRenderer(tileEntityIn);

		if (tileentityspecialrenderer != null) {
			try {
				tileentityspecialrenderer.renderTileEntityAt(tileEntityIn, x, y, z, partialTicks, destroyStage);
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
				tileEntityIn.addInfoToCrashReport(crashreportcategory);
				throw new ReportedException(crashreport);
			}
		}
	}

	public void setWorld(World worldIn) {
		this.worldObj = worldIn;
	}

	public MCFontRenderer getFontRenderer() {
		return this.fontRenderer;
	}

}
