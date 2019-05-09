package net.minecraft.client.gui.ingame;

import net.minecraft.client.gui.ScaledResolution;

@FunctionalInterface
public interface Module {

	void render(ScaledResolution res, float partialTicks);

}
