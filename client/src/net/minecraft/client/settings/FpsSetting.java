package net.minecraft.client.settings;

import org.lwjgl.opengl.Display;

public class FpsSetting extends SliderSetting {

	public FpsSetting(String name) {
		super(name, "Частота кадров", 0, 260, 120, 5);
	}

	@Override
	public void set(float value) {
		if (this.value != 0 && value == 0) {
			Settings.ENABLE_VSYNC.set(true);
			Display.setVSyncEnabled(true);
		}
		if (this.value == 0 && value != 0) {
			Settings.ENABLE_VSYNC.set(false);
			Display.setVSyncEnabled(false);
		}
		super.set(value);
	}

}
