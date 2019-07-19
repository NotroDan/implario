package net.minecraft.client.gui.element;

import net.minecraft.client.gui.Gui;

public class RenderRec implements RenderElement {
    private final int x, y, height, width, color;

    public RenderRec(int x, int y, int height, int width, int color){
        this.x = x;
        this.y = y;
        this.height = x + height;
        this.width = y + width;
        this.color = color;
    }

    @Override
    public void render() {
        Gui.drawRect(x, y, height, width, color);
    }
}
