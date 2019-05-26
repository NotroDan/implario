package vanilla.item;

import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class VanillaItems extends Items {

	public static Item saddle;
	public static Item spawn_egg;
	public static Item carrot_on_a_stick;
	public static Item lead;
	public static Item name_tag;


	public static void init() {
		saddle = getRegisteredItem("saddle");
		spawn_egg = getRegisteredItem("spawn_egg");
		carrot_on_a_stick = getRegisteredItem("carrot_on_a_stick");
		lead = getRegisteredItem("lead");
		name_tag = getRegisteredItem("name_tag");
	}

}
