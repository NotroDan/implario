package net.minecraft.client.renderer.entity;

import net.minecraft.client.game.entity.AbstractClientPlayer;
import net.minecraft.client.game.model.ModelPlayer;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityBot;
import net.minecraft.util.ResourceLocation;

public class RenderBot extends RendererLivingEntity<EntityBot> {


	public RenderBot(RenderManager renderManager) {
		super(renderManager, new ModelPlayer(0.0F, false), 0.5F);
		this.addLayer(new LayerBipedArmor(this));
		this.addLayer(new LayerHeldItem(this));
		this.addLayer(new LayerArrow(this));
//		this.addLayer(new LayerCape(this));
		this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
	}

	public ModelPlayer getMainModel() {
		return (ModelPlayer) super.getMainModel();
	}

	protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
		float f = 0.9375F;
		G.scale(f, f, f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBot entity) {
		return DefaultPlayerSkin.getDefaultSkinLegacy();
	}

}
