package net.minecraft.client.settings;

import java.util.HashMap;
import java.util.Map;

public class Setting {

	public static final Map<String, Setting> ALL = new HashMap<>();

	public final String name;
	public final String caption;

	public Setting(String name, String caption) {
		this.name = name;
		this.caption = caption;
		ALL.put(name, this);
	}

	public float floatValue() 		{throw null;}
	public boolean booleanValue() 	{throw null;}
	public void set(float f) 		{throw null;}
	public void set(boolean b) 		{throw null;}
	public void set(String arg) 	{throw null;}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public static void fromString(String s) {
		String[] args = s.split(": ");
		Setting setting = ALL.get(args[0]);
		if (setting == null) {
			System.out.println("Неизвестная настройка - " + s);
			return;
		}
		System.out.println("Настройке " + setting.name + " было присвоено значение " + args[1]);
		setting.set(args[1]);
	}


	public void reset() {}

}
