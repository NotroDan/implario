package vanilla.entity.ai.tasks.village;

import vanilla.entity.ai.tasks.EntityAIWatchClosest;
import vanilla.entity.passive.EntityVillager;
import net.minecraft.entity.player.Player;

public class EntityAILookAtTradePlayer extends EntityAIWatchClosest {

	private final EntityVillager theMerchant;

	public EntityAILookAtTradePlayer(EntityVillager theMerchantIn) {
		super(theMerchantIn, Player.class, 8.0F);
		this.theMerchant = theMerchantIn;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.theMerchant.isTrading()) {
			this.closestEntity = this.theMerchant.getCustomer();
			return true;
		}
		return false;
	}

}
