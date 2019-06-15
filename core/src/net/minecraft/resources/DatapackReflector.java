package net.minecraft.resources;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class DatapackReflector {

	public static Datapack enable(String packclass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		Class c = Class.forName(packclass);
		Object instance = c.newInstance();
		if (!(instance instanceof Datapack)) throw new ClassNotFoundException(packclass + " does not extend net.minecraft.resources.Datapack");
		Datapack p = (Datapack) instance;

		p.loadBlocks();
		Block.reloadBlockStates();
		Blocks.reload();

		p.loadItems();
		Items.reload();

		p.preinit();
		Blocks.reload();
		Block.reloadBlockStates();

		p.init();
		p.clientInit();

		return p;
	}

}
