package net.minecraft.client.gui;

import __google_.util.FileIO;

public class GuiPassword {

	public static String password = "";

	public static void loadPassword() {
		password = FileIO.read("password");
		if (password == null) password = "";
	}

	public static void savePassword() {
		FileIO.write("password", password);
	}

}
