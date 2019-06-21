package net.minecraft.resources.mapping;

import net.minecraft.block.Block;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;

public class MappingBlock extends LegacyMapping<Block> {

	public MappingBlock(int id, String address, Block old, Block neo) {
		super(id, address, old, neo);
	}

	@Override
	public void map(Block element) {
		if (element == null) {
			RegistryNamespacedDefaultedByKey<ResourceLocation, Block> r = Block.blockRegistry;
			r.remove(r.getNameForObject(r.getObjectById(id)));
		} else Block.registerBlock(id, address, element);
	}

}
