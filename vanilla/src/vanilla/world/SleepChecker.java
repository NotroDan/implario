package vanilla.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.event.Listener;
import net.minecraft.resources.event.events.player.PlayerSleepEvent;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.chat.ChatComponentTranslation;
import vanilla.entity.monster.EntityMob;

import java.util.List;

/**
 * Запрет на сон, пока рядом с кроватью есть мобы
 */
public class SleepChecker implements Listener<PlayerSleepEvent> {

	public static final EntityPlayer.SleepStatus NOT_SAFE = new EntityPlayer.SleepStatus(new ChatComponentTranslation("tile.bed.notSafe"));

	@Override
	public void process(PlayerSleepEvent event) {
		if (event.getSleepStatus() != null) return;
		BlockPos p = event.getBedLocation();
		List<EntityMob> list = event.getPlayer().worldObj.getEntitiesWithinAABB(EntityMob.class,
				new AxisAlignedBB((double) p.getX() - 8, p.getY() - 5, p.getZ() - 8,
						p.getX() + 8, p.getY() + 5, p.getZ() + 8));

		if (!list.isEmpty()) event.setSleepStatus(NOT_SAFE);
	}


}
