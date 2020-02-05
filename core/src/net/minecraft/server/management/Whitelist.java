package net.minecraft.server.management;

import net.minecraft.io.FileRoot;

public class Whitelist extends TableUserList{
	public Whitelist(FileRoot root){
		super(root, "whitelist");
	}
}
