package vanilla.client.gui.block;

import net.minecraft.inventory.Inventory;
import vanilla.entity.passive.EntityHorse;

public class HorseInv {

	public final EntityHorse horse;
	public final Inventory inv;

	public HorseInv(EntityHorse horse, Inventory inv) {
		this.horse = horse;
		this.inv = inv;
	}

}
