package net.minecraft.server.management;

import lombok.RequiredArgsConstructor;
import net.minecraft.database.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class Whitelist {
	private final Table table;
	private List<String> whitelisted = new ArrayList<>();

	public void add(String nick){
		whitelisted.add(nick);
	}

	public void remove(String nick){
		whitelisted.remove(nick);
	}

	public boolean contains(String nick){
		return whitelisted.contains(nick);
	}

	public List<String> values(){
		return whitelisted;
	}

	public void save(){
		StringBuilder buffer = new StringBuilder();
		for(String nick : whitelisted)
			buffer.append(nick).append('\n');
		table.write("whitelist", buffer.toString().getBytes());
	}

	public void read(){
		byte[] read = table.read("whitelist");
		if(read == null)return;
		Collections.addAll(whitelisted, new String(read).split("\n"));
	}
}
