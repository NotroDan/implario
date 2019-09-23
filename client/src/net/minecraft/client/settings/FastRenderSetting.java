package net.minecraft.client.settings;

import shadersmod.client.Shaders;

public class FastRenderSetting extends ToggleSetting{
    public FastRenderSetting(String name){
        super(name, "Быстрый рендер", true);
    }

    @Override
    public boolean toggle() {
        if(!value) {
            Shaders.setShaderPack("OFF");
            Shaders.uninit();
        }
        return super.toggle();
    }
}
