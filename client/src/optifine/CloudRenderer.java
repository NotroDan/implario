package optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.G;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class CloudRenderer {
	private Minecraft mc;
	private boolean updated = false;
	private boolean renderFancy = false;
	int cloudTickCounter;
	float partialTicks;
	private int glListClouds;
	private int cloudTickCounterUpdate = 0;
	private double cloudPlayerX = 0.0D;
	private double cloudPlayerY = 0.0D;
	private double cloudPlayerZ = 0.0D;

	public CloudRenderer(Minecraft mc) {
		this.mc = mc;
		glListClouds = GLAllocation.generateDisplayLists(1);
	}

	public void prepareToRender(boolean renderFancy, int cloudTickCounter, float partialTicks) {
		if (this.renderFancy != renderFancy)
			updated = false;

		this.renderFancy = renderFancy;
		this.cloudTickCounter = cloudTickCounter;
		this.partialTicks = partialTicks;
	}

	public boolean shouldUpdateGlList() {
		if (!updated || cloudTickCounter >= cloudTickCounterUpdate + 20)
			return true;
		Entity entity = mc.getRenderViewEntity();
		boolean flag = cloudPlayerY + (double) entity.getEyeHeight() < 128.0D + (double) (Settings.CLOUD_HEIGHT.f() * 128.0F);
		boolean flag1 = entity.prevPosY + (double) entity.getEyeHeight() < 128.0D + (double) (Settings.CLOUD_HEIGHT.f() * 128.0F);
		return flag1 != flag;
	}

	public void startUpdateGlList() {
		GL11.glNewList(glListClouds, GL11.GL_COMPILE);
	}

	public void endUpdateGlList() {
		GL11.glEndList();
		cloudTickCounterUpdate = cloudTickCounter;
		cloudPlayerX = mc.getRenderViewEntity().prevPosX;
		cloudPlayerY = mc.getRenderViewEntity().prevPosY;
		cloudPlayerZ = mc.getRenderViewEntity().prevPosZ;
		updated = true;
		G.resetColor();
	}

	public void renderGlList() {
		Entity entity = mc.getRenderViewEntity();
		double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
		double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks;
		double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
		double d3 = (double) ((float) (cloudTickCounter - cloudTickCounterUpdate) + partialTicks);
		float f = (float) (d0 - cloudPlayerX + d3 * 0.03D);
		float f1 = (float) (d1 - cloudPlayerY);
		float f2 = (float) (d2 - cloudPlayerZ);
		G.pushMatrix();

		if (renderFancy) G.translate(-f / 12.0F, -f1, -f2 / 12.0F);
		else G.translate(-f, -f1, -f2);

		G.callList(glListClouds);
		G.popMatrix();
		G.resetColor();
	}

	public void reset() {
		updated = false;
	}
}
