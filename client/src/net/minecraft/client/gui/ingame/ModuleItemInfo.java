package net.minecraft.client.gui.ingame;

import net.minecraft.client.MC;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.ScaledResolution;

public class ModuleItemInfo implements Module {

	private final GuiSpectator guiSpectator = new GuiSpectator(MC.i());

	@Override
	public void render(GuiIngame gui, float partialTicks, ScaledResolution res) {
		if (MC.i().playerController.isSpectator()) guiSpectator.renderTooltip(res, partialTicks);
//		else this.renderTooltip(res, partialTicks);
	}

}
