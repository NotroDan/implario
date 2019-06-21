package net.minecraft.resources.mapping;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class MappingItemBlock extends MappingItem {

	private final Block block;

	public MappingItemBlock(Block block) {
		super(Block.blockRegistry.getIDForObject(block), Block.blockRegistry.getNameForObject(block).getResourcePath(), Item.getItemFromBlock(block), new ItemBlock(block));
		this.block = block;
	}

	@Override
	public void map(Item element) {
		Item.unregisterItemBlock(block);
		if (element != null) Item.registerItemBlock(block, element);
	}

}
