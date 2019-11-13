package net.minecraft.server;

import net.minecraft.resources.Datapack;

public class Todo {

	public static Todo instance = new Todo();

	public boolean isSmoothWorld() {
		return false;
	}

	public boolean isCullFacesLeaves() {
		return false;
	}

	public boolean shouldUseRomanianNotation(int level) {
		return level <= 100;
	}

	public boolean isServerSide() {
		return true;
	}

	public void clientInit(Datapack datapack) {}

	public boolean debugEnabled(){
		return false;
	}
}
