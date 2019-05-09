package net.minecraft.client.gui.spectator;

import net.minecraft.util.IChatComponent;

public interface ISpectatorMenuObject
{
    void func_178661_a(SpectatorMenu menu);

    IChatComponent getSpectatorName();

    void render(float p_178663_1_, int alpha);

    boolean func_178662_A_();
}
