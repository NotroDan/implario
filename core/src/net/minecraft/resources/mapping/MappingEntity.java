package net.minecraft.resources.mapping;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public class MappingEntity extends LegacyMapping<Class<? extends Entity>> {

	public MappingEntity(int id, String address, Class<? extends Entity> overridden, Class<? extends Entity> actual) {
		super(id, address, overridden, actual);
	}

	@Override
	public void map(Class<? extends Entity> element) {
		EntityList.removeMapping(id);
		if (element != null) EntityList.addMapping(element, address, id);
	}

}
