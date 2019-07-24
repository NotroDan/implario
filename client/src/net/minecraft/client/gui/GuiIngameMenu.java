package net.minecraft.client.gui;

import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.resources.Lang;

import java.io.IOException;

public class GuiIngameMenu extends GuiScreen {

	@Override
	public void initGui() {
		this.buttonList.clear();
		int cacheWidth = width >> 1, cacheWidth2 = cacheWidth - 100, cacheWidth3 = cacheWidth + 2;
		int cacheHeight = height >> 2, cacheHeight2 = cacheHeight + 80, cacheHeight3 = cacheHeight + 32;
		buttonList.add(new GuiButton(1, cacheWidth2, cacheHeight + 104, Lang.format("menu.returnToMenu")));

		if (!mc.isIntegratedServerRunning()) buttonList.get(0).displayString = Lang.format("menu.disconnect");

		buttonList.add(new GuiButton(4, cacheWidth2, cacheHeight + 8, Lang.format("menu.returnToGame")));
		buttonList.add(new GuiButton(0, cacheWidth2, cacheHeight2, 98, 20, Lang.format("menu.options")));
		GuiButton guibutton;
		buttonList.add(guibutton = new GuiButton(7, cacheWidth3, cacheHeight2, 98, 20, Lang.format("menu.shareToLan")));
		buttonList.add(new GuiButton(5, cacheWidth - 100, cacheHeight3, 98, 20, Lang.format("gui.achievements")));
		buttonList.add(new GuiButton(6, cacheWidth3, cacheHeight3, 98, 20, Lang.format("gui.stats")));
		guibutton.enabled = mc.isSingleplayer() && !mc.getIntegratedServer().getPublic();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
			case 0:
				mc.displayGuiScreen(new GuiOptions());
				break;

			case 1:
				boolean flag = mc.isIntegratedServerRunning();
				button.enabled = false;
				mc.theWorld.sendQuittingDisconnectingPacket();
				mc.worldController.loadWorld(null, mc);

				mc.displayGuiScreen(flag ? new GuiMainMenu() : new GuiMultiplayer(new GuiMainMenu()));

			case 2:
			case 3:
			default:
				break;

			case 4:
				mc.displayGuiScreen(null);
				mc.inputHandler.setIngameFocus();
				break;

			case 5:
				mc.displayGuiScreen(new GuiAchievements(this, mc.thePlayer.getStatFileWriter()));
				break;

			case 6:
				mc.displayGuiScreen(new GuiStats(this, mc.thePlayer.getStatFileWriter()));
				break;

			case 7:
				mc.displayGuiScreen(new GuiShareToLan(this));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, Lang.format("menu.game"), this.width / 2, 40, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

}
