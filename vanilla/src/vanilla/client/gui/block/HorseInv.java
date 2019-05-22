package vanilla.client.gui.block;

import net.minecraft.inventory.IInventory;
import vanilla.entity.passive.EntityHorse;

public class HorseInv {
	public final EntityHorse horse;
	public final IInventory inv;

	public HorseInv(EntityHorse horse, IInventory inv) {
		this.horse = horse;
		this.inv = inv;
	}

}
