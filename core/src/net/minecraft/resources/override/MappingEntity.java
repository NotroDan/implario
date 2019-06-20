package net.minecraft.resources.override;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public class MappingEntity extends Mapping<Class<Entity>> {

	public MappingEntity(int id, String address, Class<Entity> overridden, Class<Entity> actual) {
		super(id, address, overridden, actual);
	}

	@Override
	public void map(int id, String address, Class<Entity> element) {
		if (element == null) EntityList.removeMapping(id);
		else EntityList.addMapping(element, address, id);
	}

}
