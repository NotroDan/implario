package net.minecraft.resources.override;

import net.minecraft.block.Block;

public class BlockOverridden extends OverriddenEntry<Block> {

	public BlockOverridden(int id, String address, Block old, Block neo) {
		super(id, address, old, neo);
	}

	@Override
	public void override(int id, String address, Block element) {
		Block.registerBlock(id, address, element);
	}

}
