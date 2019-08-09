package net.minecraft.resources.mapping;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.stats.StatList;

public class MappingMob extends MappingEntity {

	private final EntityEggInfo eggOld;
	private final int baseColor, spotColor;
	private EntityEggInfo eggNew;

	public MappingMob(int id, String address, Class<? extends Entity> overridden, Class<? extends Entity> actual,
					  EntityEggInfo eggOld, int newBaseColor, int newSpotColor) {
		super(id, address, overridden, actual);
		this.eggOld = eggOld;
		this.baseColor = newBaseColor;
		this.spotColor = newSpotColor;
	}

	@Override
	public void apply() {
		super.apply();
		if (eggOld != null) {
			StatList.unregister(eggOld.statKills);
			StatList.unregister(eggOld.statKilledBy);
		}
		this.eggNew = EntityList.regEgg(id, baseColor, spotColor);
	}

	@Override
	public void revert() {
		super.revert();
		StatList.unregister(eggNew.statKills);
		StatList.unregister(eggNew.statKilledBy);
		if (eggOld == null) EntityList.entityEggs.remove(id);
		else EntityList.regEgg(id, eggOld.secondaryColor, eggOld.primaryColor);

	}

}
