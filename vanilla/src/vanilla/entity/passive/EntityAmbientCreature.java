package vanilla.entity.passive;

import net.minecraft.entity.IAnimals;
import vanilla.entity.VanillaEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityAmbientCreature extends VanillaEntity implements IAnimals {

	public EntityAmbientCreature(World worldIn) {
		super(worldIn);
	}

	public boolean allowLeashing() {
		return false;
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
	 */
	protected boolean interact(EntityPlayer player) {
		return false;
	}

}
