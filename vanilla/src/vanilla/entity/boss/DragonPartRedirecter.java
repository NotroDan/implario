package vanilla.entity.boss;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.resources.event.Event;
import net.minecraft.resources.event.Handler;
import net.minecraft.resources.event.events.DamageByEntityEvent;
import vanilla.entity.IEntityMultiPart;

public class DragonPartRedirecter implements Handler<Event, DamageByEntityEvent> {

	@Override
	public void handle(DamageByEntityEvent event) {

		if (event.getDamagedEntity() instanceof EntityDragonPart) {
			IEntityMultiPart ientitymultipart = ((EntityDragonPart) event.getDamagedEntity()).entityDragonObj;
			if (ientitymultipart instanceof EntityLivingBase) {
				event.setDamagedEntity((EntityLivingBase) ientitymultipart);
			}
		}
	}

}
