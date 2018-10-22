package net.minecraft.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.Settings;
import net.minecraft.client.settings.SliderSetting;
import net.minecraft.util.MathHelper;

public class GuiOptionSlider extends GuiButton
{
    private float sliderValue;
    public boolean dragging;
    private SliderSetting options;
    private final float field_146132_r;
    private final float field_146131_s;

    public GuiOptionSlider(int p_i45016_1_, int p_i45016_2_, int p_i45016_3_, Settings settings)
    {
        this(p_i45016_1_, p_i45016_2_, p_i45016_3_, settings, 0.0F, 1.0F);
    }

    public GuiOptionSlider(int id, int x, int y, Settings settings, float p_i45017_5_, float p_i45017_6_)
    {
        super(id, x, y, 150, 20, "");
        sliderValue = 1.0F;
        options = (SliderSetting) settings.getBase();
        field_146132_r = p_i45017_5_;
        field_146131_s = p_i45017_6_;
        Minecraft minecraft = Minecraft.getMinecraft();
        sliderValue = options.normalizeValue(options.value);
        displayString = options.caption;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
    {
        if (visible)
        {
            if (dragging)
            {
                sliderValue = (float)(mouseX - (xPosition + 4)) / (float)(width - 8);
                sliderValue = MathHelper.clamp_float(sliderValue, 0.0F, 1.0F);
                float f = options.denormalizeValue(sliderValue);
                options.value = f;
                sliderValue = options.normalizeValue(f);
                displayString = options.caption;
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(width - 8)), yPosition, 0, 66, 4, 20);
            drawTexturedModalRect(xPosition + (int)(sliderValue * (float)(width - 8)) + 4, yPosition, 196, 66, 4, 20);
        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (super.mousePressed(mc, mouseX, mouseY))
        {
            sliderValue = (float)(mouseX - (xPosition + 4)) / (float)(width - 8);
            sliderValue = MathHelper.clamp_float(sliderValue, 0.0F, 1.0F);
			options.value = options.denormalizeValue(sliderValue);
            displayString = options.caption;
            dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int mouseX, int mouseY)
    {
        dragging = false;
    }
}
