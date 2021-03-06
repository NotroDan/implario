package vanilla.client.game.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.game.particle.EntityFX;
import net.minecraft.client.game.particle.IParticleFactory;
import net.minecraft.client.renderer.G;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import vanilla.entity.monster.EntityGuardian;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MobAppearance extends EntityFX {

	private EntityLivingBase entity;

	protected MobAppearance(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.particleGravity = 0.0F;
		this.particleMaxAge = 30;
	}

	public int getFXLayer() {
		return 3;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();

		if (this.entity != null) {return;}
		EntityGuardian entityguardian = new EntityGuardian(this.worldObj);
		entityguardian.setElder();
		this.entity = entityguardian;
	}

	/**
	 * Renders the particle
	 */
	public void renderParticle(WorldRenderer worldRendererIn, Entity entityIn, float partialTicks, float p_180434_4_, float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
		if (this.entity != null) {
			RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
			rendermanager.setRenderPosition(EntityFX.interpPosX, EntityFX.interpPosY, EntityFX.interpPosZ);
			float f = 0.42553192F;
			float f1 = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge;
			G.depthMask(true);
			G.enableBlend();
			G.enableDepth();
			G.blendFunc(770, 771);
			float f2 = 240.0F;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f2, f2);
			G.pushMatrix();
			float f3 = 0.05F + 0.5F * MathHelper.sin(f1 * (float) Math.PI);
			G.color(1.0F, 1.0F, 1.0F, f3);
			G.translate(0.0F, 1.8F, 0.0F);
			G.rotate(180.0F - entityIn.rotationYaw, 0.0F, 1.0F, 0.0F);
			G.rotate(60.0F - 150.0F * f1 - entityIn.rotationPitch, 1.0F, 0.0F, 0.0F);
			G.translate(0.0F, -0.4F, -1.5F);
			G.scale(f, f, f);
			this.entity.rotationYaw = this.entity.prevRotationYaw = 0.0F;
			this.entity.rotationYawHead = this.entity.prevRotationYawHead = 0.0F;
			rendermanager.renderEntityWithPosYaw(this.entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
			G.popMatrix();
			G.enableDepth();
		}
	}

	public static class Factory implements IParticleFactory {

		public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
			return new MobAppearance(worldIn, xCoordIn, yCoordIn, zCoordIn);
		}

	}

}
