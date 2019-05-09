package net.minecraft.entity.ai.tasks;

import net.minecraft.entity.VanillaEntity;
import net.minecraft.entity.ai.pathfinding.PathNavigateGround;

public class EntityAISwimming extends EntityAIBase
{
    private VanillaEntity theEntity;

    public EntityAISwimming(VanillaEntity entitylivingIn)
    {
        this.theEntity = entitylivingIn;
        this.setMutexBits(4);
        ((PathNavigateGround)entitylivingIn.getNavigator()).setCanSwim(true);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.theEntity.isInWater() || this.theEntity.isInLava();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        if (this.theEntity.getRNG().nextFloat() < 0.8F)
        {
            this.theEntity.getJumpHelper().setJumping();
        }
    }
}
