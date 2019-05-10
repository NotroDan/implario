package vanilla.entity.ai;

import vanilla.entity.VanillaEntity;

public class EntityJumpHelper
{
    private VanillaEntity entity;
    protected boolean isJumping;

    public EntityJumpHelper(VanillaEntity entityIn)
    {
        this.entity = entityIn;
    }

    public void setJumping()
    {
        this.isJumping = true;
    }

    /**
     * Called to actually make the entity jump if isJumping is true.
     */
    public void doJump()
    {
        this.entity.setJumping(this.isJumping);
        this.isJumping = false;
    }
}
