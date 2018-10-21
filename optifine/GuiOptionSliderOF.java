package optifine;

import net.minecraft.client.gui.element.GuiOptionSlider;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionSliderOF extends GuiOptionSlider implements IOptionControl
{
    private GameSettings.Options option = null;

    public GuiOptionSliderOF(int p_i50_1_, int p_i50_2_, int p_i50_3_) {
		super(p_i50_1_, p_i50_2_, p_i50_3_, null);
	}

    public GameSettings.Options getOption()
    {
        return this.option;
    }
}
