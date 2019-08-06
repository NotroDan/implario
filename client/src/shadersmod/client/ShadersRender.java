package shadersmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public class ShadersRender {

	public static void setFrustrumPosition(Frustum frustrum, double x, double y, double z) {
		frustrum.setPosition(x, y, z);
	}

	public static void setupTerrain(RenderGlobal renderGlobal, Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
		renderGlobal.setupTerrain(viewEntity, partialTicks, camera, frameCount, playerSpectator);
	}

	public static void beginTerrainSolid() {
		if (Shaders.isRenderingWorld) {
			Shaders.fogEnabled = true;
			Shaders.useProgram(7);
		}
	}

	public static void beginTerrainCutoutMipped() {
		if (Shaders.isRenderingWorld) {
			Shaders.useProgram(7);
		}
	}

	public static void beginTerrainCutout() {
		if (Shaders.isRenderingWorld) {
			Shaders.useProgram(7);
		}
	}

	public static void endTerrain() {
		if (Shaders.isRenderingWorld) {
			Shaders.useProgram(3);
		}
	}

	public static void beginTranslucent() {
		if (Shaders.isRenderingWorld) {
			if (Shaders.usedDepthBuffers >= 2) {
				G.setActiveTexture(33995);
				Shaders.checkGLError("pre copy depth");
				GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
				Shaders.checkGLError("copy depth");
				G.setActiveTexture(33984);
			}

			Shaders.useProgram(12);
		}
	}

	public static void endTranslucent() {
		if (Shaders.isRenderingWorld) {
			Shaders.useProgram(3);
		}
	}

	public static void renderHand0(EntityRenderer er, float par1, int par2) {
		if (!Shaders.isShadowPass) {
			boolean flag = Shaders.isItemToRenderMainTranslucent();

			if (!flag) {
				Shaders.readCenterDepth();
				Shaders.beginHand();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				er.renderHand(par1, par2, true, false, false);
				Shaders.endHand();
				Shaders.setHandRenderedMain(true);
			}
		}
	}

	public static void renderHand1(EntityRenderer er, float par1, int par2) {
		if (!Shaders.isShadowPass && !Shaders.isHandRenderedMain()) {
			Shaders.readCenterDepth();
			G.enableBlend();
			Shaders.beginHand();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			er.renderHand(par1, par2, true, false, true);
			Shaders.endHand();
			Shaders.setHandRenderedMain(true);
		}
	}

	public static void renderItemFP(ItemRenderer itemRenderer, float par1, boolean renderTranslucent) {
		G.depthMask(true);

		if (renderTranslucent) {
			G.depthFunc(519);
			GL11.glPushMatrix();
			IntBuffer intbuffer = Shaders.activeDrawBuffers;
			Shaders.setDrawBuffers(Shaders.drawBuffersNone);
			Shaders.renderItemKeepDepthMask = true;
			itemRenderer.renderItemInFirstPerson(par1);
			Shaders.renderItemKeepDepthMask = false;
			Shaders.setDrawBuffers(intbuffer);
			GL11.glPopMatrix();
		}

		G.depthFunc(515);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		itemRenderer.renderItemInFirstPerson(par1);
	}

	public static void renderFPOverlay(EntityRenderer er, float par1, int par2) {
		if (!Shaders.isShadowPass) {
			Shaders.beginFPOverlay();
			er.renderHand(par1, par2, false, true, false);
			Shaders.endFPOverlay();
		}
	}

	public static void beginBlockDamage() {
		if (Shaders.isRenderingWorld) {
			Shaders.useProgram(11);

			if (Shaders.programsID[11] == Shaders.programsID[7]) {
				Shaders.setDrawBuffers(Shaders.drawBuffersColorAtt0);
				G.depthMask(false);
			}
		}
	}

	public static void endBlockDamage() {
		if (Shaders.isRenderingWorld) {
			G.depthMask(true);
			Shaders.useProgram(3);
		}
	}

	public static void renderShadowMap(EntityRenderer entityRenderer, int pass, float partialTicks, long finishTimeNano) {
		if (Shaders.usedShadowDepthBuffers > 0 && --Shaders.shadowPassCounter <= 0) {
			Minecraft minecraft = Minecraft.getMinecraft();
			minecraft.getProfiler().endStartSection("shadow pass");
			RenderGlobal renderglobal = minecraft.renderGlobal;
			Shaders.isShadowPass = true;
			Shaders.shadowPassCounter = Shaders.shadowPassInterval;
			Shaders.preShadowPassThirdPersonView = Settings.getPerspective();
			Settings.PERSPECTIVE.set(1);
			Shaders.checkGLError("pre shadow");
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPushMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			minecraft.getProfiler().endStartSection("shadow clear");
			EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.sfb);
			Shaders.checkGLError("shadow bind sfb");
			Shaders.useProgram(30);
			minecraft.getProfiler().endStartSection("shadow camera");
			entityRenderer.setupCameraTransform(partialTicks, 2);
			Shaders.setCameraShadow(partialTicks);
			ActiveRenderInfo.updateRenderInfo(minecraft.thePlayer, Settings.getPerspective() == 2);
			Shaders.checkGLError("shadow camera");
			GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
			Shaders.checkGLError("shadow drawbuffers");
			GL11.glReadBuffer(0);
			Shaders.checkGLError("shadow readbuffer");
			EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);

			if (Shaders.usedShadowColorBuffers != 0) {
				EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, Shaders.sfbColorTextures.get(0), 0);
			}

			Shaders.checkFramebufferStatus("shadow fb");
			GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glClear(Shaders.usedShadowColorBuffers != 0 ? GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT : GL11.GL_DEPTH_BUFFER_BIT);
			Shaders.checkGLError("shadow clear");
			minecraft.getProfiler().endStartSection("shadow frustum");
			ClippingHelper clippinghelper = ClippingHelperShadow.getInstance();
			minecraft.getProfiler().endStartSection("shadow culling");
			Frustum frustum = new Frustum(clippinghelper);
			Entity entity = minecraft.getRenderViewEntity();
			double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
			double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
			double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
			frustum.setPosition(d0, d1, d2);
			G.shadeModel(7425);
			G.enableDepth();
			G.depthFunc(515);
			G.depthMask(true);
			G.colorMask(true, true, true, true);
			G.disableCull();
			minecraft.getProfiler().endStartSection("shadow prepareterrain");
			minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			minecraft.getProfiler().endStartSection("shadow setupterrain");
			int i = entityRenderer.frameCount;
			entityRenderer.frameCount = i + 1;
			renderglobal.setupTerrain(entity, (double) partialTicks, frustum, i, minecraft.thePlayer.isSpectator());
			minecraft.getProfiler().endStartSection("shadow updatechunks");
			minecraft.getProfiler().endStartSection("shadow terrain");
			G.matrixMode(5888);
			G.pushMatrix();
			G.disableAlpha();
			renderglobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, (double) partialTicks, 2, entity);
			Shaders.checkGLError("shadow terrain solid");
			G.enableAlpha();
			renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, (double) partialTicks, 2, entity);
			Shaders.checkGLError("shadow terrain cutoutmipped");
			minecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
			renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, (double) partialTicks, 2, entity);
			Shaders.checkGLError("shadow terrain cutout");
			minecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
			G.shadeModel(7424);
			G.alphaFunc(516, 0.1F);
			G.matrixMode(5888);
			G.popMatrix();
			G.pushMatrix();
			minecraft.getProfiler().endStartSection("shadow entities");

			renderglobal.renderEntities(entity, frustum, partialTicks);
			Shaders.checkGLError("shadow entities");
			G.matrixMode(5888);
			G.popMatrix();
			G.depthMask(true);
			G.disableBlend();
			G.enableCull();
			G.tryBlendFuncSeparate(770, 771, 1, 0);
			G.alphaFunc(516, 0.1F);

			if (Shaders.usedShadowDepthBuffers >= 2) {
				G.setActiveTexture(33989);
				Shaders.checkGLError("pre copy shadow depth");
				GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
				Shaders.checkGLError("copy shadow depth");
				G.setActiveTexture(33984);
			}

			G.disableBlend();
			G.depthMask(true);
			minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			G.shadeModel(7425);
			Shaders.checkGLError("shadow pre-translucent");
			GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
			Shaders.checkGLError("shadow drawbuffers pre-translucent");
			Shaders.checkFramebufferStatus("shadow pre-translucent");

			if (Shaders.isRenderShadowTranslucent()) {
				minecraft.getProfiler().endStartSection("shadow translucent");
				renderglobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, (double) partialTicks, 2, entity);
				Shaders.checkGLError("shadow translucent");
			}

			G.shadeModel(7424);
			G.depthMask(true);
			G.enableCull();
			G.disableBlend();
			GL11.glFlush();
			Shaders.checkGLError("shadow flush");
			Shaders.isShadowPass = false;
			Settings.PERSPECTIVE.set(Shaders.preShadowPassThirdPersonView);
			minecraft.getProfiler().endStartSection("shadow postprocess");

			if (Shaders.hasGlGenMipmap) {
				if (Shaders.usedShadowDepthBuffers >= 1) {
					if (Shaders.shadowMipmapEnabled[0]) {
						G.setActiveTexture(33988);
						G.bindTexture(Shaders.sfbDepthTextures.get(0));
						GL30.glGenerateMipmap(3553);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowFilterNearest[0] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
					}

					if (Shaders.usedShadowDepthBuffers >= 2 && Shaders.shadowMipmapEnabled[1]) {
						G.setActiveTexture(33989);
						G.bindTexture(Shaders.sfbDepthTextures.get(1));
						GL30.glGenerateMipmap(3553);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowFilterNearest[1] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
					}

					G.setActiveTexture(33984);
				}

				if (Shaders.usedShadowColorBuffers >= 1) {
					if (Shaders.shadowColorMipmapEnabled[0]) {
						G.setActiveTexture(33997);
						G.bindTexture(Shaders.sfbColorTextures.get(0));
						GL30.glGenerateMipmap(3553);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowColorFilterNearest[0] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
					}

					if (Shaders.usedShadowColorBuffers >= 2 && Shaders.shadowColorMipmapEnabled[1]) {
						G.setActiveTexture(33998);
						G.bindTexture(Shaders.sfbColorTextures.get(1));
						GL30.glGenerateMipmap(3553);
						GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowColorFilterNearest[1] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
					}

					G.setActiveTexture(33984);
				}
			}

			Shaders.checkGLError("shadow postprocess");
			EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
			GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
			Shaders.activeDrawBuffers = null;
			minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			Shaders.useProgram(7);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			Shaders.checkGLError("shadow end");
		}
	}

	public static void preRenderChunkLayer(EnumWorldBlockLayer blockLayerIn) {
		if (Shaders.isRenderBackFace(blockLayerIn)) {
			G.disableCull();
		}

		if (OpenGlHelper.useVbo()) {
			GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
			GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
			GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
			GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
		}
	}

	public static void postRenderChunkLayer(EnumWorldBlockLayer blockLayerIn) {
		if (OpenGlHelper.useVbo()) {
			GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
			GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
			GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
			GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
		}

		if (Shaders.isRenderBackFace(blockLayerIn)) {
			G.enableCull();
		}
	}

	public static void setupArrayPointersVbo() {
		int i = 14;
		GL11.glVertexPointer(3, GL11.GL_FLOAT, 56, 0L);
		GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 56, 12L);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 56, 16L);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glTexCoordPointer(2, GL11.GL_SHORT, 56, 24L);
		OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glNormalPointer(GL11.GL_BYTE, 56, 28L);
		GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, GL11.GL_FLOAT, false, 56, 32L);
		GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, GL11.GL_SHORT, false, 56, 40L);
		GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, GL11.GL_SHORT, false, 56, 48L);
	}

	public static void beaconBeamBegin() {
		Shaders.useProgram(14);
	}

	public static void beaconBeamStartQuad1() {
	}

	public static void beaconBeamStartQuad2() {
	}

	public static void beaconBeamDraw1() {
	}

	public static void beaconBeamDraw2() {
		G.disableBlend();
	}

	public static void renderEnchantedGlintBegin() {
		Shaders.useProgram(17);
	}

	public static void renderEnchantedGlintEnd() {
		if (Shaders.isRenderingWorld) {
			if (Shaders.isRenderBothHands()) {
				Shaders.useProgram(19);
			} else {
				Shaders.useProgram(16);
			}
		} else {
			Shaders.useProgram(0);
		}
	}

}
