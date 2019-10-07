package net.minecraft.resources.mapping;

import net.minecraft.world.WorldType;

public class MappingWorldType extends AbstractMapping<WorldType> {

	private final WorldType type;

	public MappingWorldType(WorldType type) {
		super(type.getWorldTypeName(), null, type);
		this.type = type;
	}

	@Override
	protected void map(WorldType type) {
		if (type == null) WorldType.unregisterType(this.type);
		else WorldType.registerType(type);
	}

}
