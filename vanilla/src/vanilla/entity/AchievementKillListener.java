package vanilla.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.resources.event.Listener;
import net.minecraft.resources.event.events.EntityKilledEntityEvent;
import net.minecraft.stats.AchievementList;
import vanilla.entity.monster.IMob;

public class AchievementKillListener implements Listener<EntityKilledEntityEvent> {

	@Override
	public void handle(EntityKilledEntityEvent event) {
		if (event.getKiller() instanceof EntityPlayer && event.getKilled() instanceof IMob)
			((EntityPlayer) event.getKiller()).triggerAchievement(AchievementList.killEnemy);
	}

}
