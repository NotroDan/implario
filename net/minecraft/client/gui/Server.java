package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.client.Logger;
import net.minecraft.client.MC;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.awt.image.BufferedImage;

public class Server {
	
	final ServerData serverData;
	final ResourceLocation iconLoc;
	boolean hovered, prepared;
	DynamicTexture texture;
	long hoverTime;
	
	public Server(ServerData serverData) {
		this.serverData = serverData;
		this.iconLoc = new ResourceLocation("servers/" + serverData.serverIP + "/icon");
		this.texture = (DynamicTexture) MC.getTextureManager().getTexture(this.iconLoc);
	}
	
	void prepareServerIcon() {
		if (prepared) return;
		prepared = true;
		if (this.serverData.getBase64EncodedIconData() == null) {
			MC.getTextureManager().deleteTexture(this.iconLoc);
			this.texture = null;
		} else {
			ByteBuf bytebuf = Unpooled.copiedBuffer(this.serverData.getBase64EncodedIconData(), Charsets.UTF_8);
			ByteBuf bytebuf1 = Base64.decode(bytebuf);
			BufferedImage bufferedimage;
			label101: {
				try {
					bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
					Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
					Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
					break label101;
				} catch (Throwable throwable) {
					Logger.instance.error("Invalid icon for server " +
							this.serverData.serverName + " (" + this.serverData.serverIP + ")", throwable);
					this.serverData.setBase64EncodedIconData(null);
				} finally {
					bytebuf.release();
					bytebuf1.release();
				}
				
				return;
			}
			
			if (this.texture == null) {
				this.texture = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
				MC.getTextureManager().loadTexture(this.iconLoc, this.texture);
			}
			
			bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), this.texture.getTextureData(), 0, bufferedimage.getWidth());
			this.texture.updateDynamicTexture();
		}
	}
	
	static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
	
	void drawFavicon(int x, int y) {
		GlStateManager.pushMatrix();
		GlStateManager.color(1, 1, 1, 1);
		MC.getTextureManager().bindTexture(texture == null ? UNKNOWN_SERVER : iconLoc);
		GlStateManager.enableBlend();
		GlStateManager.scale(2, 2, 2);
		if (x % 2 == 1) GlStateManager.translate(0.5, 0,0);
		Gui.drawModalRectWithCustomSizedTexture(x / 2, y / 2, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.scale(0.5, 0.5, 0.5);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
	
	public String getTitle() {
		return serverData.serverName;
	}
	
	@Override
	public String toString() {
		return serverData.serverName + " | " + serverData.serverIP;
	}
}
