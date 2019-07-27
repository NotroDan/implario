package vanilla.entity;

import net.minecraft.entity.player.Player;
import net.minecraft.resources.event.SHandler;
import net.minecraft.resources.event.events.EntityKilledEntityEvent;
import net.minecraft.stats.AchievementList;
import vanilla.entity.monster.IMob;

public class AchievementKillListener implements SHandler<EntityKilledEntityEvent> {

	@Override
	public void handle(EntityKilledEntityEvent event) {
		if (event.getKiller() instanceof Player && event.getKilled() instanceof IMob)
			((Player) event.getKiller()).triggerAchievement(AchievementList.killEnemy);
	}

}
