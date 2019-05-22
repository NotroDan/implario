package vanilla.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.event.Event;
import net.minecraft.resources.event.Handler;
import net.minecraft.resources.event.events.TrySleepEvent;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import vanilla.entity.monster.EntityMob;

import java.util.List;

public class SleepChecker implements Handler<Event, TrySleepEvent> {

	@Override
	public void handle(TrySleepEvent event) {
		BlockPos p = event.getBedLocation();
		List<EntityMob> list = event.getPlayer().worldObj.getEntitiesWithinAABB(EntityMob.class,
				new AxisAlignedBB((double) p.getX() - 8, p.getY() - 5, p.getZ() - 8,
						p.getX() + 8, p.getY() + 5, p.getZ() + 8));

		if (!list.isEmpty()) event.setStatus(EntityPlayer.EnumStatus.NOT_SAFE);
	}

}
