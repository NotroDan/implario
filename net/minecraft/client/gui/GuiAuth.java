package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.gui.element.GuiTextField;

public class GuiAuth extends GuiScreen {


	public GuiAuth() {
		mc = Minecraft.getMinecraft();
		loginText = new GuiTextField(3, mc.fontRendererObj, width / 2 - 50, );
		buttonList.add(new GuiButton(1, width / 2 - 50, height / 2 + 40, 48, 20, "Отмена"));
		buttonList.add(new GuiButton(2, width / 2 + 4, height / 2 + 40, 48, 20, "Войти"));
	}


}
