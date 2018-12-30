package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.Utils;
import net.minecraft.client.Logger;
import net.minecraft.client.MC;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiServers extends GuiScreen {
	
	private static final long SMOOTH_SCROLL_TIME = 150;
	private final List<Server> servers = new ArrayList<>();
	private final GuiScreen parent;
	private ServerList serverList;
	private int columns = -1;
	private int rows = -1;
	private int rightBorder;
	
	public GuiServers(GuiScreen parent) {
		this.parent = parent;
	}
	
	public GuiScreen getParent() {
		return parent;
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		
		if (columns == -1 || servers.isEmpty()) {
			serverList = new ServerList(mc);
			serverList.loadServerList();
			for (ServerData serverData : serverList.getServers()) servers.add(new Server(serverData));
		}
		columns = width / 72;
		rightBorder = width / 2 - columns * 36;
		columns--;
		int size = servers.size();
		rows = size / columns;
		if (size % columns != 0) rows++;
		scroll = 0;
		lastScroll = 0;
		lastScrolled = 0;
		
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		int wheel = Mouse.getEventDWheel();
		if (wheel != 0) {
			if (wheel < -1) wheel = -1;
			if (wheel >  1) wheel = 1;
			long time = System.currentTimeMillis();
			int n = wheel * H / 2;
			if (time - lastScrolled > SMOOTH_SCROLL_TIME){ //|| n > 0 != lastUpwards) {
				lastScroll = scroll;
				lastScrolled = time;
			}
			lastUpwards = n > 0;
			scroll -= n;
			if (scroll > rows * H - height) scroll = rows * H - height;
			if (scroll < 0) scroll = 0;
			
		}
	}
	
	public static int H = 84;
	public static int W = 72;
	protected volatile int scroll, lastScroll;
	protected volatile long lastScrolled;
	protected boolean lastUpwards;
	
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		Iterator<Server> iter = servers.iterator();
		if (columns < 1) return;
		
		// плавная прокрутка
		long time = System.currentTimeMillis();
		int scroll = this.scroll - lastScroll;
		if (lastScrolled != 0) {
			long diff = time - lastScrolled;
			if (diff < 0 || diff > SMOOTH_SCROLL_TIME) {
				lastScrolled = 0;
				lastScroll = 0;
				scroll = this.scroll;
			} else {
				float d = (float) diff / (float) SMOOTH_SCROLL_TIME;
				scroll = this.lastScroll + (int) (d * scroll);
			}
		}
		
		
		for (int row = 0; row < rows; row++) {
			int x = rightBorder + 4;
			int y0 = row * 84 - scroll, y1 = y0 + 4, y2 = y0 + 84;
			for (int i = 0; i < columns; i++) {
				Server server = iter.next();
				boolean hovered = mouseX >= x - 4 && mouseX < x + 68 && mouseY >= y0 && mouseY < y2;
				if (hovered != server.hovered) {
					server.hovered = hovered;
					server.hoverTime = time;
				}
				int color = 0xa0202020;
				long ago = time - server.hoverTime;
				if (server.hoverTime > 0) {
					if (ago < 250) {
						if (hovered) color = Utils.gradient(0xa07fff3f, 0xa0202020, (float) (ago % 250) / 250f);
						else color = Utils.gradient(0xa0202020, 0xa07fff3f, (float) (ago % 250) / 250f);
					}
					else if (hovered) color = 0xa07fff3f;
					else server.hoverTime = 0;
				}
				
				
				drawRect(x - 2, y0 + 2,
						x + 66, y2 - 2, color);
				server.prepareServerIcon();
				server.drawFavicon(x, y1);
				drawCenteredString(MC.FR, server.getTitle(), x + 32, y0 + 72, -1);
				x += 72;
				if (!iter.hasNext()) break;
			}
		}
		drawHUD(mouseX, mouseY);
	}
	
	private void drawHUD(int mouseX, int mouseY) {
		drawRect(width - 72, 0, width, height, 0x80505050);
	}
	
	
	public static class Server {
		
		private final ServerData serverData;
		private final ResourceLocation iconLoc;
		private boolean hovered;
		private DynamicTexture texture;
		private boolean prepared;
		long hoverTime;
		
		public Server(ServerData serverData) {
			this.serverData = serverData;
			this.iconLoc = new ResourceLocation("servers/" + serverData.serverIP + "/icon");
			this.texture = (DynamicTexture) MC.getTextureManager().getTexture(this.iconLoc);
		}
		
		private void prepareServerIcon() {
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
		
		private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
		
		public void drawFavicon(int x, int y) {
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
	}
	
	
	
}
