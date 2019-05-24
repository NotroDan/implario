package net.minecraft.world;

import net.minecraft.util.OpenableGui;

@FunctionalInterface
public interface WorldCustomizer {

	void openCustomizationGui(OpenableGui parent, String chunkProviderSettingsJson);

}
