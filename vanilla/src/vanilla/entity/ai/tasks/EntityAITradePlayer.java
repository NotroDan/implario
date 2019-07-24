package vanilla.entity.ai.tasks;

import vanilla.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class EntityAITradePlayer extends EntityAIBase
{
    private EntityVillager villager;

    public EntityAITradePlayer(EntityVillager villagerIn)
    {
        this.villager = villagerIn;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.villager.isEntityAlive())
        {
            return false;
        }
		if (this.villager.isInWater())
		{
			return false;
		}
		if (!this.villager.onGround)
		{
			return false;
		}
		if (this.villager.velocityChanged)
		{
			return false;
		}
		EntityPlayer entityplayer = this.villager.getCustomer();
		return entityplayer == null ? false : this.villager.getDistanceSqToEntity(entityplayer) > 16.0D ? false : entityplayer.openContainer instanceof Container;
	}

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.villager.getNavigator().clearPathEntity();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.villager.setCustomer((EntityPlayer)null);
    }
}
