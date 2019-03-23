package net.minecraft.client.gui.keymap;

import net.minecraft.client.MC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.util.Collection;
import java.util.HashSet;

import static net.minecraft.client.gui.keymap.IButtonRender.DEFAULT_SIZE;
import static net.minecraft.client.gui.keymap.Key.*;
import static net.minecraft.client.renderer.G.*;

public class KeyboardRender {

	final Collection<KeyboardButton> BUTTONS = new HashSet<>();
	final Collection<KeyboardButton> METASMALL = new HashSet<>();
	final Collection<KeyboardButton> METABIG = new HashSet<>();
	private final IButtonRender keyIconer = new ActionButtonRender();
	private final IButtonRender keyLetterer = new LetterButtonRender(true, 0x505050);
	int baseWidth, metaWidth, fullWidth;

	public KeyboardRender() {
		fullKeyboard();
	}

	private void fullKeyboard() {
		int x = 0, y = 0;
		translate(0, 0, 0);
		reg(ESCAPE, x, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F1, x += DEFAULT_SIZE * 2 + 4, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F2, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F3, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F4, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F5, x += DEFAULT_SIZE + 15, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F6, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F7, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F8, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F9, x += DEFAULT_SIZE + 15, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F10, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F11, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(F12, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);

		baseWidth = x + DEFAULT_SIZE;

		x = 0;
		y += DEFAULT_SIZE + DEFAULT_SIZE / 2;
		reg(Key.TILDA, x, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_1, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_2, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_3, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_4, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_5, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_6, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_7, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_8, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_9, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DIGIT_0, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.MINUS, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.EQUALS, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.BACKSPACE, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE * 2 + 1, DEFAULT_SIZE);


		x = 0;
		y += DEFAULT_SIZE + 2;
		int oneh = (int) ((float) DEFAULT_SIZE * 1.55f);
		reg(Key.TAB, x, y, oneh, DEFAULT_SIZE);
		reg(Key.Q, x += oneh + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.W, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.E, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.R, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.T, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.Y, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.U, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.I, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.O, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.P, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.LEFT_BRACKET, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.RIGHT_BRACKET, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.ENTER, x += DEFAULT_SIZE + 2, y, oneh, DEFAULT_SIZE * 2 + 2, DEFAULT_SIZE / 2 - 4);

		x = 0;
		y += DEFAULT_SIZE + 2;
		reg(Key.CAPS_LOCK, x, y, oneh, DEFAULT_SIZE);
		reg(Key.A, x += oneh + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.S, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.D, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.F, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.G, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.H, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.J, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.K, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.L, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.SEMILOCON, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.APOSTROPHE, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.BACKSLASH, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);

		int shiftsize = DEFAULT_SIZE * 2 + 2;
		x = 0;
		y += DEFAULT_SIZE + 2;
		reg(Key.LSHIFT, x, y, shiftsize, DEFAULT_SIZE);
		reg(Key.Z, x += shiftsize + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.X, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.C, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.V, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.B, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.N, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.M, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.COMMA, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.DOT, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.SLASH, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		reg(Key.RSHIFT, x += DEFAULT_SIZE + 2, y, shiftsize + DEFAULT_SIZE + 1, DEFAULT_SIZE);

		int metasize = (int) ((float) DEFAULT_SIZE * 1.5f);
		x = 0;
		y += DEFAULT_SIZE + 2;
		reg(Key.LCONTROL, x, y, metasize, DEFAULT_SIZE);
		reg(Key.LALT, x += metasize + 2, y, metasize, DEFAULT_SIZE);
		int spaceWidth = baseWidth - (metasize * 4 + 8);
		reg(Key.SPACE, x += metasize + 2, y, spaceWidth, DEFAULT_SIZE);
		reg(Key.RALT, x += spaceWidth + 2, y, metasize, DEFAULT_SIZE);
		reg(Key.RCONTROL, x += metasize + 2, y, metasize, DEFAULT_SIZE);


		x = 0;
		y += DEFAULT_SIZE + 2;
		regMeta(PAUSE, x, y, 52, DEFAULT_SIZE);
		regMeta(INSERT, x += 52 + 2, y, 29, DEFAULT_SIZE);
		regMeta(HOME, x += 29 + 2, y, 37, DEFAULT_SIZE);
		regMeta(END, x += 37 + 2, y, 28, DEFAULT_SIZE);
		regMeta(DELETE, x += 28 + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regMeta(PAGE_UP, x += DEFAULT_SIZE + 2, y, metasize, DEFAULT_SIZE);
		regMeta(PAGE_DOWN, x += metasize + 2, y, metasize, DEFAULT_SIZE);
		regMeta(ARROW_UP, x += metasize * 2 + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regMeta(ARROW_LEFT, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regMeta(ARROW_DOWN, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regMeta(ARROW_RIGHT, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);

		x = baseWidth + 15;
		y = 0;
		regSide(PRINTSCREEN, x, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(SCROLL_LOCK, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(PAUSE, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(INSERT, x = baseWidth + 15, y += DEFAULT_SIZE + DEFAULT_SIZE / 2, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(HOME, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(PAGE_UP, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(DELETE, x = baseWidth + 15, y += DEFAULT_SIZE + 2, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(END, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(PAGE_DOWN, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(ARROW_UP, 15 + baseWidth + DEFAULT_SIZE + 2, y += DEFAULT_SIZE * 2 + 4, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(ARROW_LEFT, x = baseWidth + 15, y += DEFAULT_SIZE + 2, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(ARROW_DOWN, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);
		regSide(ARROW_RIGHT, x += DEFAULT_SIZE + 2, y, DEFAULT_SIZE, DEFAULT_SIZE);

		metaWidth = DEFAULT_SIZE * 4;
		fullWidth = metaWidth + baseWidth;
		
	}

	private void reg(Key key, int x, int y, int width, int height) {
		reg(key, x, y, width, height, 0);
	}
	private void regMeta(Key key, int x, int y, int width, int height) {
		METASMALL.add(new KeyboardButton(width, height, key, x, y, 0));
	}
	private void regSide(Key key, int x, int y, int width, int height) {
		METABIG.add(new KeyboardButton(width, height, key, x, y, 0));
	}
	private void reg(Key key, int x, int y, int width, int height, int renderY) {
		BUTTONS.add(new KeyboardButton(width, height, key, x, y, renderY));
	}

	public void render(Gui gui, int mouseX, int mouseY, int screenWidth) {
		MetaKey metaKey = MetaKey.get(GuiScreen.isCtrlKeyDown(), GuiScreen.isShiftKeyDown(), GuiScreen.isAltKeyDown());
		boolean big = screenWidth >= fullWidth;
		int xTranslation = screenWidth / 2 - (big ? fullWidth : baseWidth) / 2;
		pushMatrix();
		translate(xTranslation, 0, 0);
		mouseX -= xTranslation;
		MC.bindTexture(GuiKeymap.actionTextures);
		for (KeyboardButton button : BUTTONS) render(gui, mouseX, mouseY, button, metaKey);
		for (KeyboardButton button : big ? METABIG : METASMALL) render(gui, mouseX, mouseY, button, metaKey);
		popMatrix();
	}

	private void render(Gui gui, int mouseX, int mouseY, KeyboardButton button, MetaKey metaKey) {
		pushMatrix();
		translate(button.getX(), button.getY(), 0);
		int color = 0x50505050;
		if (mouseX >= button.getX() && mouseY >= button.getY() && mouseX < button.getX() + button.getWidth() && mouseY < button.getY() + button.getHeight())
			color = 0x50ff4040;
		if (Keyboard.isKeyDown(button.getKey().getKey())) color = 0x5040ff40;
		Gui.drawRect(0, 0, button.getWidth(), button.getHeight(), color);
		GlStateManager.color(1, 1, 1, 1);
		MC.bindTexture(GuiKeymap.actionTextures);
		if (!keyIconer.render(gui, button, metaKey)) keyLetterer.render(gui, button, metaKey);
		popMatrix();
	}

}
