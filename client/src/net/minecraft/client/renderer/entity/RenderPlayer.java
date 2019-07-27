package net.minecraft.client.renderer.entity;

import net.minecraft.client.game.entity.AbstractClientPlayer;
import net.minecraft.client.game.entity.CPlayer;
import net.minecraft.client.game.model.ModelPlayer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class RenderPlayer extends RendererLivingEntity<AbstractClientPlayer> {

	public RenderPlayer(RenderManager renderManager) {
		this(renderManager, false);
	}

	public RenderPlayer(RenderManager renderManager, boolean useSmallArms) {
		super(renderManager, new ModelPlayer(0.0F, useSmallArms), 0.5F);
		this.addLayer(new LayerBipedArmor(this));
		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerArrow(this));
		this.addLayer(new LayerCape(this));
		this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
	}

	public ModelPlayer getMainModel() {
		return (ModelPlayer) super.getMainModel();
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doe
	 */
	public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (entity.isUser() && this.renderManager.livingPlayer != entity) return;
		double d0 = y;

		if (entity.isSneaking() && !(entity instanceof CPlayer)) d0 = y - 0.125D;

		this.setModelVisibilities(entity);
		super.doRender(entity, x, d0, z, entityYaw, partialTicks);
	}

	private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
		ModelPlayer modelplayer = this.getMainModel();

		if (clientPlayer.isSpectator()) {
			modelplayer.setInvisible(false);
			modelplayer.bipedHead.showModel = true;
			modelplayer.bipedHeadwear.showModel = true;
		} else {
			ItemStack itemstack = clientPlayer.inventory.getCurrentItem();
			modelplayer.setInvisible(true);
			modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
			modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
			modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
			modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
			modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
			modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
			modelplayer.heldItemLeft = 0;
			modelplayer.aimedBow = false;
			modelplayer.isSneak = clientPlayer.isSneaking();

			if (itemstack == null) modelplayer.heldItemRight = 0;
			else {
				modelplayer.heldItemRight = 1;

				if (clientPlayer.getItemInUseCount() > 0) {
					EnumAction enumaction = itemstack.getItemUseAction();

					if (enumaction == EnumAction.BLOCK) modelplayer.heldItemRight = 3;
					else if (enumaction == EnumAction.BOW) modelplayer.aimedBow = true;
				}
			}
		}
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
		return entity.getLocationSkin();
	}

	public void transformHeldFull3DItemLayer() {
		G.translate(0.0F, 0.1875F, 0.0F);
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
		float f = 0.9375F;
		G.scale(f, f, f);
	}

	protected void renderOffsetLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String str, float p_177069_9_, double p_177069_10_) {
		if (p_177069_10_ < 100.0D) {
			Scoreboard scoreboard = entityIn.getWorldScoreboard();
			ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
			//            Collection<NetworkPlayerInfo> name = Minecraft.getMinecraft().thePlayer.sendQueue.getPlayerInfoMap();
			//            name.iterator().next().getDisplayName()

			if (scoreobjective != null) {
				Score score = scoreboard.getValueFromObjective(entityIn.getName(), scoreobjective);
				this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y, z, 64);
				y += (double) ((float) this.getFontRendererFromRenderManager().getFontHeight() * 1.15F * p_177069_9_);
			}
		}

		super.renderOffsetLivingLabel(entityIn, x, y, z, str, p_177069_9_, p_177069_10_);
	}

	public void renderRightArm(AbstractClientPlayer clientPlayer) {
		float f = 1.0F;
		G.color(f, f, f);
		ModelPlayer modelplayer = this.getMainModel();
		this.setModelVisibilities(clientPlayer);
		modelplayer.swingProgress = 0.0F;
		modelplayer.isSneak = false;
		modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
		modelplayer.renderRightArm();
	}

	public void renderLeftArm(AbstractClientPlayer clientPlayer) {
		float f = 1.0F;
		G.color(f, f, f);
		ModelPlayer modelplayer = this.getMainModel();
		this.setModelVisibilities(clientPlayer);
		modelplayer.isSneak = false;
		modelplayer.swingProgress = 0.0F;
		modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
		modelplayer.renderLeftArm();
	}

	/**
	 * Sets a simple glTranslate on a LivingEntity.
	 */
	protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z) {
		if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping())
			super.renderLivingAt(entityLivingBaseIn, x + (double) entityLivingBaseIn.renderOffsetX, y + (double) entityLivingBaseIn.renderOffsetY, z + (double) entityLivingBaseIn.renderOffsetZ);
		else super.renderLivingAt(entityLivingBaseIn, x, y, z);
	}

	protected void rotateCorpse(AbstractClientPlayer e, float p_77043_2_, float p_77043_3_, float partialTicks) {
		if (e.isEntityAlive() && e.isPlayerSleeping()) {
			G.rotate(e.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
			G.rotate(this.getDeathMaxRotation(e), 0.0F, 0.0F, 1.0F);
			G.rotate(270.0F, 0.0F, 1.0F, 0.0F);
		} else super.rotateCorpse(e, p_77043_2_, p_77043_3_, partialTicks);
	}

}
