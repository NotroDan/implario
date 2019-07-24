package net.minecraft.client.game.particle;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import optifine.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

public class EffectRenderer {

	private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");

	/**
	 * Reference to the World object
	 */
	protected World worldObj;
	private List[][] fxLayers = new List[4][];
	private List particleEmitters = new ArrayList<>();
	private TextureManager renderer;

	/**
	 * RNG.
	 */
	private Random rand = new Random();
	private Map particleTypes = Maps.newHashMap();


	public EffectRenderer(World worldIn, TextureManager rendererIn) {
		this.worldObj = worldIn;
		this.renderer = rendererIn;

		for (int i = 0; i < 4; ++i) {
			this.fxLayers[i] = new List[2];

			for (int j = 0; j < 2; ++j) {
				this.fxLayers[i][j] = new ArrayList<>();
			}
		}

		this.registerVanillaParticles();
	}

	private void registerVanillaParticles() {
		this.registerParticle(ParticleType.EXPLOSION_NORMAL.getParticleID(), new EntityExplodeFX.Factory());
		this.registerParticle(ParticleType.WATER_BUBBLE.getParticleID(), new EntityBubbleFX.Factory());
		this.registerParticle(ParticleType.WATER_SPLASH.getParticleID(), new EntitySplashFX.Factory());
		this.registerParticle(ParticleType.WATER_WAKE.getParticleID(), new EntityFishWakeFX.Factory());
		this.registerParticle(ParticleType.WATER_DROP.getParticleID(), new EntityRainFX.Factory());
		this.registerParticle(ParticleType.SUSPENDED.getParticleID(), new EntitySuspendFX.Factory());
		this.registerParticle(ParticleType.SUSPENDED_DEPTH.getParticleID(), new EntityAuraFX.Factory());
		this.registerParticle(ParticleType.CRIT.getParticleID(), new EntityCrit2FX.Factory());
		this.registerParticle(ParticleType.CRIT_MAGIC.getParticleID(), new EntityCrit2FX.MagicFactory());
		this.registerParticle(ParticleType.SMOKE_NORMAL.getParticleID(), new EntitySmokeFX.Factory());
		this.registerParticle(ParticleType.SMOKE_LARGE.getParticleID(), new EntityCritFX.Factory());
		this.registerParticle(ParticleType.SPELL.getParticleID(), new EntitySpellParticleFX.Factory());
		this.registerParticle(ParticleType.SPELL_INSTANT.getParticleID(), new EntitySpellParticleFX.InstantFactory());
		this.registerParticle(ParticleType.SPELL_MOB.getParticleID(), new EntitySpellParticleFX.MobFactory());
		this.registerParticle(ParticleType.SPELL_MOB_AMBIENT.getParticleID(), new EntitySpellParticleFX.AmbientMobFactory());
		this.registerParticle(ParticleType.SPELL_WITCH.getParticleID(), new EntitySpellParticleFX.WitchFactory());
		this.registerParticle(ParticleType.DRIP_WATER.getParticleID(), new EntityDropParticleFX.WaterFactory());
		this.registerParticle(ParticleType.DRIP_LAVA.getParticleID(), new EntityDropParticleFX.LavaFactory());
		this.registerParticle(ParticleType.VILLAGER_ANGRY.getParticleID(), new EntityHeartFX.AngryVillagerFactory());
		this.registerParticle(ParticleType.VILLAGER_HAPPY.getParticleID(), new EntityAuraFX.HappyVillagerFactory());
		this.registerParticle(ParticleType.TOWN_AURA.getParticleID(), new EntityAuraFX.Factory());
		this.registerParticle(ParticleType.NOTE.getParticleID(), new EntityNoteFX.Factory());
		this.registerParticle(ParticleType.PORTAL.getParticleID(), new EntityPortalFX.Factory());
		this.registerParticle(ParticleType.ENCHANTMENT_TABLE.getParticleID(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
		this.registerParticle(ParticleType.FLAME.getParticleID(), new EntityFlameFX.Factory());
		this.registerParticle(ParticleType.LAVA.getParticleID(), new EntityLavaFX.Factory());
		this.registerParticle(ParticleType.FOOTSTEP.getParticleID(), new EntityFootStepFX.Factory());
		this.registerParticle(ParticleType.CLOUD.getParticleID(), new EntityCloudFX.Factory());
		this.registerParticle(ParticleType.REDSTONE.getParticleID(), new EntityReddustFX.Factory());
		this.registerParticle(ParticleType.SNOWBALL.getParticleID(), new EntityBreakingFX.SnowballFactory());
		this.registerParticle(ParticleType.SNOW_SHOVEL.getParticleID(), new EntitySnowShovelFX.Factory());
		this.registerParticle(ParticleType.SLIME.getParticleID(), new EntityBreakingFX.SlimeFactory());
		this.registerParticle(ParticleType.HEART.getParticleID(), new EntityHeartFX.Factory());
		this.registerParticle(ParticleType.BARRIER.getParticleID(), new Barrier.Factory());
		this.registerParticle(ParticleType.ITEM_CRACK.getParticleID(), new EntityBreakingFX.Factory());
		this.registerParticle(ParticleType.BLOCK_CRACK.getParticleID(), new EntityDiggingFX.Factory());
		this.registerParticle(ParticleType.BLOCK_DUST.getParticleID(), new EntityBlockDustFX.Factory());
		this.registerParticle(ParticleType.EXPLOSION_HUGE.getParticleID(), new EntityHugeExplodeFX.Factory());
		this.registerParticle(ParticleType.EXPLOSION_LARGE.getParticleID(), new EntityLargeExplodeFX.Factory());
		this.registerParticle(ParticleType.FIREWORKS_SPARK.getParticleID(), new EntityFirework.Factory());
	}

	public void registerParticle(int id, IParticleFactory particleFactory) {
		this.particleTypes.put(id, particleFactory);
	}
	public void unregisterParticle(int id) {
		this.particleTypes.remove(id);
	}
	public IParticleFactory getFactory(int id) {
		return (IParticleFactory) this.particleTypes.get(id);
	}

	public void emitParticleAtEntity(Entity entityIn, ParticleType particleTypes) {
		this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
	}

	/**
	 * Spawns the relevant particle according to the particle id.
	 */
	public EntityFX spawnEffectParticle(int particleId, double p_178927_2_, double p_178927_4_, double p_178927_6_, double p_178927_8_, double p_178927_10_, double p_178927_12_, int... p_178927_14_) {
		IParticleFactory iparticlefactory = getFactory(particleId);

		if (iparticlefactory != null) {
			EntityFX entityfx = iparticlefactory.getEntityFX(particleId, this.worldObj, p_178927_2_, p_178927_4_, p_178927_6_, p_178927_8_, p_178927_10_, p_178927_12_, p_178927_14_);

			if (entityfx != null) {
				this.addEffect(entityfx);
				return entityfx;
			}
		}

		return null;
	}

	public void addEffect(EntityFX effect) {
		if (effect != null) {
			if (!(effect instanceof EntityFirework.SparkFX) || Config.isFireworkParticles()) {
				int i = effect.getFXLayer();
				int j = effect.getAlpha() != 1.0F ? 0 : 1;

				if (this.fxLayers[i][j].size() >= 4000) {
					this.fxLayers[i][j].remove(0);
				}

				if (!(effect instanceof Barrier) || !this.reuseBarrierParticle(effect, this.fxLayers[i][j])) {
					this.fxLayers[i][j].add(effect);
				}
			}
		}
	}

	public void updateEffects() {
		for (int i = 0; i < 4; ++i) {
			this.updateEffectLayer(i);
		}

		ArrayList arraylist = new ArrayList<>();

		for (Object entityparticleemitter0 : this.particleEmitters) {
			EntityParticleEmitter entityparticleemitter = (EntityParticleEmitter) entityparticleemitter0;
			entityparticleemitter.onUpdate();

			if (entityparticleemitter.isDead) {
				arraylist.add(entityparticleemitter);
			}
		}

		this.particleEmitters.removeAll(arraylist);
	}

	private void updateEffectLayer(int p_178922_1_) {
		for (int i = 0; i < 2; ++i) {
			this.updateEffectAlphaLayer(this.fxLayers[p_178922_1_][i]);
		}
	}

	private void updateEffectAlphaLayer(List p_178925_1_) {
		ArrayList arraylist = new ArrayList<>();

		for (int i = 0; i < p_178925_1_.size(); ++i) {
			EntityFX entityfx = (EntityFX) p_178925_1_.get(i);
			this.tickParticle(entityfx);

			if (entityfx.isDead) {
				arraylist.add(entityfx);
			}
		}

		p_178925_1_.removeAll(arraylist);
	}

	private void tickParticle(final EntityFX p_178923_1_) {
		try {
			p_178923_1_.onUpdate();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
			final int i = p_178923_1_.getFXLayer();
			crashreportcategory.addCrashSectionCallable("Particle", new Callable() {


				public String call() throws Exception {
					return p_178923_1_.toString();
				}
			});
			crashreportcategory.addCrashSectionCallable("Particle Type", new Callable() {


				public String call() throws Exception {
					return i == 0 ? "MISC_TEXTURE" : i == 1 ? "TERRAIN_TEXTURE" : i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
				}
			});
			throw new ReportedException(crashreport);
		}
	}

	/**
	 * Renders all current particles. Args player, partialTickTime
	 */
	public void renderParticles(Entity entityIn, float partialTicks) {
		float f = ActiveRenderInfo.getRotationX();
		float f1 = ActiveRenderInfo.getRotationZ();
		float f2 = ActiveRenderInfo.getRotationYZ();
		float f3 = ActiveRenderInfo.getRotationXY();
		float f4 = ActiveRenderInfo.getRotationXZ();
		EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
		EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
		EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
		G.enableBlend();
		G.blendFunc(770, 771);
		G.alphaFunc(516, 0.003921569F);

		for (int i = 0; i < 3; ++i) {
			final int j = i;

			for (int k = 0; k < 2; ++k) {
				if (!this.fxLayers[j][k].isEmpty()) {
					switch (k) {
						case 0:
							G.depthMask(false);
							break;

						case 1:
							G.depthMask(true);
					}

					switch (j) {
						case 0:
						default:
							this.renderer.bindTexture(particleTextures);
							break;

						case 1:
							this.renderer.bindTexture(TextureMap.locationBlocksTexture);
					}

					G.color(1.0F, 1.0F, 1.0F, 1.0F);
					Tessellator tessellator = Tessellator.getInstance();
					WorldRenderer worldrenderer = tessellator.getWorldRenderer();
					worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

					for (int l = 0; l < this.fxLayers[j][k].size(); ++l) {
						final EntityFX entityfx = (EntityFX) this.fxLayers[j][k].get(l);

						try {
							entityfx.renderParticle(worldrenderer, entityIn, partialTicks, f, f4, f1, f2, f3);
						} catch (Throwable throwable) {
							CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
							CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
							crashreportcategory.addCrashSectionCallable("Particle", new Callable() {


								public String call() throws Exception {
									return entityfx.toString();
								}
							});
							crashreportcategory.addCrashSectionCallable("Particle Type", new Callable() {


								public String call() throws Exception {
									return j == 0 ? "MISC_TEXTURE" : j == 1 ? "TERRAIN_TEXTURE" : j == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + j;
								}
							});
							throw new ReportedException(crashreport);
						}
					}

					tessellator.draw();
				}
			}
		}

		G.depthMask(true);
		G.disableBlend();
		G.alphaFunc(516, 0.1F);
	}

	public void renderLitParticles(Entity entityIn, float p_78872_2_) {
		float f = 0.017453292F;
		float f1 = MathHelper.cos(entityIn.rotationYaw * 0.017453292F);
		float f2 = MathHelper.sin(entityIn.rotationYaw * 0.017453292F);
		float f3 = -f2 * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
		float f4 = f1 * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
		float f5 = MathHelper.cos(entityIn.rotationPitch * 0.017453292F);

		for (int i = 0; i < 2; ++i) {
			List list = this.fxLayers[3][i];

			if (!list.isEmpty()) {
				Tessellator tessellator = Tessellator.getInstance();
				WorldRenderer worldrenderer = tessellator.getWorldRenderer();

				for (int j = 0; j < list.size(); ++j) {
					EntityFX entityfx = (EntityFX) list.get(j);
					entityfx.renderParticle(worldrenderer, entityIn, p_78872_2_, f1, f5, f2, f3, f4);
				}
			}
		}
	}

	public void clearEffects(World worldIn) {
		this.worldObj = worldIn;

		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 2; ++j) {
				this.fxLayers[i][j].clear();
			}
		}

		this.particleEmitters.clear();
	}

	public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
		if (state.getBlock().getMaterial() != Material.air) {
			state = state.getBlock().getActualState(state, this.worldObj, pos);
			byte b0 = 4;

			for (int i = 0; i < b0; ++i) {
				for (int j = 0; j < b0; ++j) {
					for (int k = 0; k < b0; ++k) {
						double d0 = (double) pos.getX() + ((double) i + 0.5D) / (double) b0;
						double d1 = (double) pos.getY() + ((double) j + 0.5D) / (double) b0;
						double d2 = (double) pos.getZ() + ((double) k + 0.5D) / (double) b0;
						this.addEffect(new EntityDiggingFX(this.worldObj, d0, d1, d2, d0 - (double) pos.getX() - 0.5D, d1 - (double) pos.getY() - 0.5D, d2 - (double) pos.getZ() - 0.5D,
								state).func_174846_a(pos));
					}
				}
			}
		}
	}

	/**
	 * Adds block hit particles for the specified block
	 */
	public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = this.worldObj.getBlockState(pos);
		Block block = iblockstate.getBlock();

		if (block.getRenderType() != -1) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			float f = 0.1F;
			double d0 = (double) i + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinX();
			double d1 = (double) j + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinY();
			double d2 = (double) k + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinZ();

			if (side == EnumFacing.DOWN) {
				d1 = (double) j + block.getBlockBoundsMinY() - (double) f;
			}

			if (side == EnumFacing.UP) {
				d1 = (double) j + block.getBlockBoundsMaxY() + (double) f;
			}

			if (side == EnumFacing.NORTH) {
				d2 = (double) k + block.getBlockBoundsMinZ() - (double) f;
			}

			if (side == EnumFacing.SOUTH) {
				d2 = (double) k + block.getBlockBoundsMaxZ() + (double) f;
			}

			if (side == EnumFacing.WEST) {
				d0 = (double) i + block.getBlockBoundsMinX() - (double) f;
			}

			if (side == EnumFacing.EAST) {
				d0 = (double) i + block.getBlockBoundsMaxX() + (double) f;
			}

			this.addEffect(new EntityDiggingFX(this.worldObj, d0, d1, d2, 0.0D, 0.0D, 0.0D, iblockstate).func_174846_a(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
		}
	}

	public void moveToAlphaLayer(EntityFX effect) {
		this.moveToLayer(effect, 1, 0);
	}

	public void moveToNoAlphaLayer(EntityFX effect) {
		this.moveToLayer(effect, 0, 1);
	}

	private void moveToLayer(EntityFX effect, int p_178924_2_, int p_178924_3_) {
		for (int i = 0; i < 4; ++i) {
			if (this.fxLayers[i][p_178924_2_].contains(effect)) {
				this.fxLayers[i][p_178924_2_].remove(effect);
				this.fxLayers[i][p_178924_3_].add(effect);
			}
		}
	}

	public String getStatistics() {
		int i = 0;

		for (int j = 0; j < 4; ++j) {
			for (int k = 0; k < 2; ++k) {
				i += this.fxLayers[j][k].size();
			}
		}

		return "" + i;
	}

	private boolean reuseBarrierParticle(EntityFX p_reuseBarrierParticle_1_, List<EntityFX> p_reuseBarrierParticle_2_) {
		for (EntityFX entityfx : p_reuseBarrierParticle_2_) {
			if (entityfx instanceof Barrier && p_reuseBarrierParticle_1_.posX == entityfx.posX && p_reuseBarrierParticle_1_.posY == entityfx.posY && p_reuseBarrierParticle_1_.posZ == entityfx.posZ) {
				entityfx.particleAge = 0;
				return true;
			}
		}

		return false;
	}

}
