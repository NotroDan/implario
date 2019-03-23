package net.minecraft.client.gui.keymap;

import net.minecraft.client.MC;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Map;

import static net.minecraft.client.gui.keymap.KeyBinding.KEYMAP;

public class GuiKeymap extends GuiScreen {

	private final KeyboardRender keyboardRender = new KeyboardRender();//new LetterButtonRender(false));
	public static final ResourceLocation actionTextures = new ResourceLocation("textures/gui/actions.png");
	private final static int TOOLBAR_HEIGHT = 50;

	@Override
	public void initGui() {

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		drawDefaultBackground();
		keyboardRender.render(this, mouseX, mouseY, width);
		drawRect(0, height - TOOLBAR_HEIGHT, width, height, 0x60404040);
		MC.bindTexture(actionTextures);
		for (Map.Entry<Key, KeyBinding> e : KEYMAP.entrySet()) {

		}

	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
	}

}
