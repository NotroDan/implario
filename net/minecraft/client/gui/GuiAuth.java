package net.minecraft.client.gui;

import net.minecraft.Auth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.element.GuiButton;
import net.minecraft.client.gui.element.GuiTextField;

import java.io.IOException;

public class GuiAuth extends GuiScreen {


	private GuiTextField loginText;
	private GuiTextField passText;
	private final GuiScreen parent;
	private String popup = "";
	private long popupTime;
	private int rounds = 12;

	public GuiAuth(GuiScreen parent) {
		mc = Minecraft.getMinecraft();
		this.parent = parent;
	}

	@Override
	public void initGui() {
		loginText = new GuiTextField(3, mc.fontRendererObj, width / 2 - 50, height / 2 - 40, 100, 20);
		passText = new GuiTextField(4, mc.fontRendererObj, width / 2 - 50, height / 2 - 15, 100, 20);
		loginText.setText(Minecraft.getGlobalName());
		loginText.setEnabled(false);
		buttonList.add(new GuiButton(1, width / 2 - 50, height / 2 + 10, 48, 20, "Отмена"));
		buttonList.add(new GuiButton(2, width / 2 + 4, height / 2 + 10, 48, 20, "Войти"));
		buttonList.get(1).enabled = false;
		passText.setFocused(true);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		boolean b1 = loginText.getText().trim().length() == 0 && !loginText.isFocused();
		boolean b2 =  passText.getText().trim().length() == 0 && ! passText.isFocused();

		if (b1) loginText.setText("§7Логин");
		if (b2) passText.setText("§7Пароль");
		loginText.drawTextBox();
		passText.drawTextBox();
		if (b1) loginText.setText("");
		if (b2) passText.setText("");

		drawCenteredString(fontRendererObj, "Итерации: " + rounds, width / 2 + 100, height / 2 - 18, -1);

		if (popup.length() != 0) {
			long time = System.currentTimeMillis() - popupTime;
			if (time > 3000) {
				popup = "";
				popupTime = 0;
				return;
			}
			drawCenteredString(mc.fontRendererObj, popup, width / 2, loginText.yPosition - 13,0xffffff);
		}
	}

	@Override
	public void updateScreen() {
		loginText.updateCursorCounter();
		passText.updateCursorCounter();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		passText.mouseClicked(mouseX, mouseY, mouseButton);
		if (loginText.isEnabled) loginText.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
			case 1:
				mc.displayGuiScreen(parent);
				return;
			case 2:
				String login = loginText.getText().trim();
				String pass = passText.getText().trim();
				if (login.length() == 0 || pass.length() == 0) {
					popup("§cВы не заполнили все поля.");
					return;
				}
				try {
					Auth.setPassword(pass, rounds);
					Auth.reg();
					popup("§aРегистрация успешно завершена.");
				} catch (Throwable t) {
					popup("§cПроизошла ошибка.");
					t.printStackTrace();
				}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 200 && isCtrlKeyDown()) {
			rounds++;
			return;
		}
		if (keyCode == 208 && isCtrlKeyDown()) {
			rounds--;
			return;
		}
		if (loginText.isEnabled && loginText.isFocused()) loginText.textboxKeyTyped(typedChar, keyCode);
		if (passText.isFocused()) passText.textboxKeyTyped(typedChar, keyCode);
		this.buttonList.get(1).enabled = loginText.getText().trim().length() > 0 && passText.getText().trim().length() > 0;
		if (keyCode == 28 || keyCode == 156) this.actionPerformed(this.buttonList.get(1));
	}

	private void popup(String s) {
		popup = s;
		popupTime = System.currentTimeMillis();
	}

}
