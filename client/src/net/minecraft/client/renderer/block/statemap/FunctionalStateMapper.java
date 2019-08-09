package net.minecraft.client.renderer.block.statemap;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

@RequiredArgsConstructor
public class FunctionalStateMapper extends StateMapperBase {

	private final Mapper mapper;
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		return mapper.get(state);
	}

	public interface Mapper {
		ModelResourceLocation get(IBlockState state);
	}

}
