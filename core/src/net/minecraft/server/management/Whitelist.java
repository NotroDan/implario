package net.minecraft.server.management;

import net.minecraft.database.Table;

public class Whitelist extends TableUserList{
	public Whitelist(Table table){
		super(table, "whitelist");
	}
}
