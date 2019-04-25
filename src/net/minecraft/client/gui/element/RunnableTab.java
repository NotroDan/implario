package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.AssetsFontRenderer;

public class RunnableTab implements ITab {
    private final GuiButton button;
    private final Runnable runnable;

    public RunnableTab(String name, Runnable runnable, int id, int x, int y){
        AssetsFontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        this.button = new GuiButton(id, x, y, fr.getStringWidth(name) + 12, 18, name);
        this.runnable = runnable;
    }

    @Override
    public void focus() {
        runnable.run();
    }

    @Override
    public GuiButton getButton() {
        return button;
    }
}
