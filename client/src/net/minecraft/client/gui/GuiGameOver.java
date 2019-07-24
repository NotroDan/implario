package net.minecraft.client.gui;

import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.renderer.G;
import net.minecraft.client.resources.Lang;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class GuiGameOver extends GuiScreen implements GuiYesNoCallback {

	//After 20 ticks buttons enable
	private int enableButtonsTimer;

	@Override
	public void initGui() {
		buttonList.clear();

		if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
			if (mc.isIntegratedServerRunning()) buttonList.add(new GuiButton(1, width / 2 - 100,
					height / 4 + 96, Lang.format("deathScreen.deleteWorld")));
			else buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 96,
					Lang.format("deathScreen.leaveServer")));
		} else {
			buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 72,
					Lang.format("deathScreen.respawn")));
			buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 96,
					Lang.format("deathScreen.titleScreen")));

			if (mc.getSession() == null)
				buttonList.get(1).enabled = false;
		}

		for (GuiButton guibutton : buttonList)
			guibutton.enabled = false;
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
			case 0:
				mc.thePlayer.respawnPlayer();
				mc.displayGuiScreen(null);
				break;

			case 1:
				if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) this.mc.displayGuiScreen(new GuiMainMenu());
				else {
					GuiYesNo guiyesno = new GuiYesNo(this, Lang.format("deathScreen.quit.confirm"),
							"", Lang.format("deathScreen.titleScreen"),
							Lang.format("deathScreen.respawn"), 0);
					mc.displayGuiScreen(guiyesno);
					guiyesno.setButtonDelay(20);
				}
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if (result) {
			mc.theWorld.sendQuittingDisconnectingPacket();
			mc.worldController.loadWorld(null, mc);
			mc.displayGuiScreen(new GuiMainMenu());
		} else {
			mc.thePlayer.respawnPlayer();
			mc.displayGuiScreen(null);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawGradientRect(0, 0, width, height, 1615855616, -1602211792);
		G.pushMatrix();
		G.scale(2.0F, 2.0F, 2.0F);
		boolean flag = mc.theWorld.getWorldInfo().isHardcoreModeEnabled();
		String s = flag ? Lang.format("deathScreen.title.hardcore") : Lang.format("deathScreen.title");
		drawCenteredString(fontRendererObj, s, width >> 2, 30, 16777215);
		G.popMatrix();

		if (flag)
			drawCenteredString(fontRendererObj, Lang.format("deathScreen.hardcoreInfo"),
					width >> 1, 144, 16777215);

		drawCenteredString(fontRendererObj, Lang.format("deathScreen.score") + ": " +
				EnumChatFormatting.YELLOW + mc.thePlayer.getScore(), width >> 1, 100, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}


	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (++this.enableButtonsTimer == 20)
			for (GuiButton guibutton : buttonList)
				guibutton.enabled = true;
	}

}
