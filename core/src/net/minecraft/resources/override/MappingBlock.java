package net.minecraft.resources.override;

import net.minecraft.block.Block;

public class MappingBlock extends Mapping<Block> {

	public MappingBlock(int id, String address, Block old, Block neo) {
		super(id, address, old, neo);
	}

	@Override
	public void map(int id, String address, Block element) {
		if (element == null) Block.blockRegistry.remove(Block.blockRegistry.getNameForObject(element));
		else Block.registerBlock(id, address, element);
	}

}
