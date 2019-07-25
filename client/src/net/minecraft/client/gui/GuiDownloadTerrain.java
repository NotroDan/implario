package net.minecraft.client.gui;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.Lang;
import net.minecraft.network.play.client.C00PacketKeepAlive;

import javax.annotation.PostConstruct;
import java.io.IOException;

public class GuiDownloadTerrain extends GuiScreen {

	private NetHandlerPlayClient netHandlerPlayClient;
	private int progress;

	public GuiDownloadTerrain(NetHandlerPlayClient netHandler) {
		this.netHandlerPlayClient = netHandler;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	public void initGui() {
		this.buttonList.clear();
	}

	@Override
	public void updateScreen() {
		if (++progress == 20) {
			progress = 0;
			netHandlerPlayClient.addToSendQueue(new C00PacketKeepAlive());
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawBackground(0);
		drawCenteredString(fontRendererObj, Lang.format("multiplayer.downloadingTerrain"),
				width / 2, height / 2 - 50, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}
