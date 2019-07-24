package net.minecraft.client.gui.element;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RenderRunnable implements RenderElement{
    private final Runnable runnable;

    @Override
    public void render() {
        runnable.run();
    }
}
